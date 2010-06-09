package vars.annotation

import java.text.SimpleDateFormat
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.LinkedBlockingQueue
import org.slf4j.LoggerFactory
import vars.integration.MergeStatusDAO
import org.mbari.vars.integration.MergeEXPDAnnotations
import vars.integration.MergeFunction
import org.mbari.expd.UberDatum
import org.mbari.expd.DiveDAO


class DatabaseUtility {

    final log = LoggerFactory.getLogger(DatabaseUtility.class)
    final toolBox = new vars.ToolBox()


    def DatabaseUtility() {
    }

    void merge() {

        log.debug("----- Merging Annotations with EXPD data ----")

        def ids = new TreeSet()
        MergeStatusDAO mergeStatusDAO = toolBox.mergeStatusDAO
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()

        ids.addAll(mergeStatusDAO.findUnmergedSets())
        ids.addAll(mergeStatusDAO.findFailedSets())
        ids.addAll(mergeStatusDAO.findSetsWithEditedNav())
        ids.addAll(mergeStatusDAO.findUpdatedSets())

        for (id in ids) {

            if (id == null) { continue }

            // DAOTX
            dao.startTransaction()
            def videoArchiveSet = dao.findByPrimaryKey(id)

            if (videoArchiveSet == null) { continue }

            def platform = videoArchiveSet.platformName

            // A VideoArchiveSet must have exactly 1 cameraPlatformDeployment in order to merge it
            def cds = videoArchiveSet.cameraDeployments
            def n = cds.size()
            if (n == 0) {
                // ---- Can't merge but update the status to reflect no-merge
                def s = "${videoArchiveSet} does not have any dives associated " +
                        "with it. Unable to merge"
                def action = new MergeEXPDAnnotations(platform, 0, false)
                action.mergeStatus.statusMessage = action.mergeStatus.statusMessage + ";" + s
                try {
                    action.update(new HashMap<VideoFrame, UberDatum>(), MergeFunction.MergeType.PRAGMATIC)
                }
                catch (Exception e) {
                    log.debug("Failed to update EXPDMergeStatus for ${id}", e)
                }
            }
            else if (n > 1) {
                // ---- Can't merge but update the status to reflect no-merge
                def s = "${videoArchiveSet} has more that one dive (${n}) associated " +
                        "with it. Unable to merge"
                def action = new MergeEXPDAnnotations(platform, cds.iterator().next().sequenceNumber, false)
                action.mergeStatus.statusMessage = action.mergeStatus.statusMessage + ";" + s
                try {
                    action.update(new HashMap<VideoFrame, UberDatum>(), MergeFunction.MergeType.PRAGMATIC)
                }
                catch (Exception e) {
                    log.debug("Failed to update EXPDMergeStatus for ${id}", e)
                }
            }
            else {
                def seqNumber = cds.iterator().next().sequenceNumber
                // Merge both both SD and HD tapes
                for (isHD in [false, true]) {
                    def action = new MergeEXPDAnnotations(platform, seqNumber, isHD)
                    try {
                        action.apply(MergeFunction.MergeType.PRAGMATIC)
                    }
                    catch (Exception e) {
                        log.debug("Failed to merge ${platform} #${seqNumber}. It's " +
                                "likely that this failure was not recorded in the " +
                                "EXPDMergeStatus table in the VARS database")
                    }
                }

            }
            dao.endTransaction()
        }
    }

    /**
     * Fixes CameraPlatformDeployments without ChiefScientists
     */
    void fixChiefScientists() {
        log.info("----- Fixing CameraPlatformDeployments with no ChiefScientists -----")
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()

        def badVas = dao.findAllWithNoChiefScientist()
        log.debug("Found ${badVas.size()} VideoArchiveSets to fix.")
        def platforms = ['Ventana':"vnta", 'Tiburon':"tibr", 'Doc Ricketts':'docr']
        def diveDao = toolBox.daoFactory.newDiveDAO()
        for (VideoArchiveSet vas in badVas) {
            for (cpd in vas.cameraDeployments) {
                def dive = diveDao.findByPlatformAndDiveNumber(platforms[vas.platformName], cpd.sequenceNumber)
                def chiefScientist = dive.chiefScientist
                if (chiefScientist) {
                    log.debug("Updating ${vas}.chiefScientist = ${chiefScientist} for dive ${vas.platformName} ${cpd.sequenceNumber} ")
                    dao.startTransaction()
                    cpd = dao.find(cpd)
                    cpd.chiefScientistName = chiefScientist
                    dao.endTransaction()
                }
                else {
                    log.debug("Unable to find chiefScientist for ${vas.platformName} #${cpd.sequenceNumber}")
                }
            }

        }

    }

    void fixDiveDates() {
        log.debug("---- Fixing CameraPlatformDeployments and VideoArchiveSets without start or end dates -----")
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def badVas = dao.findAllWithoutDates()
        badVas.each { videoArchiveSet ->
            updateDiveDates(videoArchiveSet)
        }
    }
    
    void fixAllDiveDates() {
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def allVas = dao.findAll()
        allVas.each { videoArchiveSet ->
            updateDiveDates(videoArchiveSet)
        }
    }
    
    void updateDiveDates(VideoArchiveSet videoArchiveSet) {
        Date start = null
        Date end = null

        def diveDao = toolBox.daoFactory.newDiveDAO()
        def dao = toolBox.toolBelt.annotationDAOFactory.newDAO()
        dao.startTransaction()
        videoArchiveSet = dao.find(videoArchiveSet)
        videoArchiveSet.cameraDeployments.each { deployment ->

            def dive = diveDao.findByPlatformAndDiveNumber(videoArchiveSet.platformName, deployment.sequenceNumber)

            if (dive) {
                // Set values in CameraPlatformDeployment
                deployment.startDate = dive.startDate
                deployment.endDate = dive.endDate


                if (start == null) {
                    start = dive.startDate
                }
                else {
                    start = (start.before(dive.startDate)) ? start : dive.startDate
                }

                if (end == null) {
                    end = dive.endDate
                }
                else {
                    end = (end.after(dive.endDate)) ? end : dive.endDate
                }
            }
            else {
                log.info("No start and end dates for ${videoArchiveSet.platformName} #${deployment.seqNumber}")
            }

        }

        videoArchiveSet.startDate = start
        videoArchiveSet.endDate = end
        dao.endTransaction()

    }
    

    static void fixTrackingNumbers() {
        log.debug("----- Fixing VideoArchiveSets with no trackingNumbers ----")
    }

    void showMergeStatus(String platform, def seqNumber) {
        def dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        MergeStatusDAO mergeStatusDao = toolBox.mergeStatusDAO
        def mergeStatus = mergeStatusDao.findByPlatformAndSequenceNumber(platform, seqNumber)

        // ----- TODO 20100609 - Continue fixing from this line on

        def id = mergeStatusDao.findByPlatformAndSequenceNumber(platform, seqNumber)
        def vas = null
        def va = null
        if (id) {
            vas = VideoArchiveSetDAO.instance.findByPK("${id}")
            va = vas.videoArchives
            va.each { v ->
                v.videoFrames.size()
            }
        }
        def mergeStatus = EXPDMergeStatusDAO.find(id)
        println "==========================================================================="
        println " ROV:                ${platform}"
        println " Dive Number:        ${seqNumber}"
        println ""
        println "==== Status of Data Merge with EXPD ===="

        // Show merge information
        if (id) {

            if (!mergeStatus.merged) {
                println " THIS DIVE HAS NOT BEEN MERGED!!"
            }
            else {
                println " Merged On:          ${dateFormat.format(mergeStatus.mergeDate)}"
                def navSrc = mergeStatus.navigationEdited ? "edited" : "raw"
                println "                     Merged using ${navSrc} navigation data"
                println "                     Recorded Dates are from '${mergeStatus.dateSource}' database(s)"
                println " Video-frame count:  ${mergeStatus.videoFrameCount}"
                println " Status Message:     ${mergeStatus.statusMessage}"
            }

            // Show VideoArchiveSet information
            //def vas = VideoArchiveSetDAO.instance.findByPK("${id}")
            def cpds = vas.cameraPlatformDeployments
            println ""
            println "==== Details About this VideoArchiveSet ===="
            println " Tracking Number:    ${vas.trackingNumber ?: ''}"
            println " Number of dives:    ${cpds.size()}"
            cpds.inject(1) { n, c ->
                println "\t${n}) Dive Number:       ${c.seqNumber}"
                println "\t   Chief Scientist:   ${c.chiefScientist.name}"
                n += 1
            }

            //def va = vas.videoArchiveColl
            println " Number of Tapes:    ${va.size()}"
            va.inject(1) { n, v ->
                println "\t${n}) Name:              ${v.videoArchiveName}"
                println "\t   Video-frame count: ${v.videoFrames.size()}"
                n += 1
            }


        }
        else {
            println "NO MERGE INFORMATION WAS FOUND IN THE DATABASE!"
        }


        println "==========================================================================="
    }

	/**
     * Loops through all the images in the database to see if they exist. If they don't we
     * write the ouput onto the console.
     * @return A collection of bogus image URL's that are linked to annotations in the VARS database.
     */
    static Collection<URL> listMissingStillImages() {

        def badUrls = new Vector<URL>()

        /*
         * Grab all the URL's into a list
         */
        def sql = "SELECT StillImageURL FROM CameraData WHERE StillImageURL LIKE 'http%'"
        def urls = []
        def handler = { resultSet ->
            while(resultSet.next()) {
                def s = resultSet.getString(1)
                try {
                    
                    def url = new URL(s)
                    urls << url
                }
                catch (Exception e) {
                    // bogus URL
                    log.debug("ERROR: Can't process ${s}")
                }
            }
        }
        DAO.query(sql, handler)
        log.debug("----- Found ${urls.size()} images ----")
        
        /*
         * We'll use 5 threads to check if the images exists
         */
        def queue = new LinkedBlockingQueue(100)
        def imageCheckers = []
        (0..9).each {
            def ic = new ImageChecker(queue, badUrls)
            imageCheckers << ic
            def thread = new Thread(ic)
            thread.start()
        }
        urls.each { url ->
            queue.put(url) 
        }

        /*
         * When queue is empty, terminate the threads
         */
        while(!queue.isEmpty()) {
            Thread.sleep(250)
        }
        imageCheckers.each { it.terminate = true }

        return badUrls

    }
}

/**
 * Support class for listMissingStillImages method. Pulls URLs from the
 * supplied queue and checks to see if the file exists. If not then
 * it adds the ULR to the badUrls collection.
 */
private class ImageChecker implements Runnable {

    static log = LoggerFactory.getLogger(ImageChecker.class)

    final queue
    def terminate = false

    /**
     * @param queue A queue full of URL's to process
     * @param badUrls Where the bad URL's are stored (SHould be a synchronized collecion)
     */
    ImageChecker(BlockingQueue<URL> queue, Collection<URL> badUrls) {
        this.queue = queue
    }

    public void run() {
        while(!terminate) {
            def url = queue.poll(500L, TimeUnit.MILLISECONDS)
            // Check that image exists
            if (url != null && !imageExists(url)) {
                badUrls << url
            }
        }
    }

    /**
     * Checks to see if an image was found at the given URL
     */
    boolean imageExists(URL url) {
        def exists = false
        try {
            // If the image doesn't exist Java will throw an IOException
            def inputStream = url.openStream()
            def buf = new byte[8]
            inputStream.read(buf)
            inputStream.close()
            exists = true
        }
        catch (Exception e) {
            log.info("Unable to read ${url}")
        }
        return exists
    }

}
