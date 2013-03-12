package vars.annotation

import org.mbari.vars.integration.MergeEXPDAnnotations2
import vars.integration.MergeHistoryDAO

import java.text.SimpleDateFormat
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.LinkedBlockingQueue
import org.slf4j.LoggerFactory
import org.mbari.expd.UberDatum
import vars.RawSQLQueryFunction
import vars.integration.MergeType

class DatabaseUtility2 {

    final log = LoggerFactory.getLogger(getClass())
    final toolBox = new vars.ToolBox()


    def DatabaseUtility2() {
    }

    /**
     * Run a raw SQL Query against the VARS Annotation Database
     * @param sql THe query to run
     * @return A string of the data, suitable for writing to a file
     */
    String sqlquery(sql) {
        def sb = new StringBuilder("# Query Results from VARS Annotation database\n")
        sb << "# SQL: ${sql.replaceAll(/\n/) { "\n# " }}\n"
        sb << toolBox.toolBelt.annotationPersistenceService.executeQueryFunction(sql, new RawSQLQueryFunction())
        return sb.toString()
    }

    void merge() {

        log.debug("----- Merging Annotations with EXPD data ----")

        def idSet = new TreeSet()
        MergeHistoryDAO mergeHistoryDAO = toolBox.mergeHistoryDAO
        def cache = toolBox.toolBelt.persistenceCache

        idSet.addAll(mergeHistoryDAO.findUnmergedSets())
        idSet.addAll(mergeHistoryDAO.findSetsWithEditedNav())
        idSet.addAll(mergeHistoryDAO.findUpdatedSets())

        def ids = []
        ids.addAll(idSet)
        ids = ids.sort().reverse(); // Do most recently added first.

        for (id in ids) {

            if (id != null) {
                try {
                    doMerge(id)
                    cache.clear()
                }
                catch (Exception e) {
                    log.error("An error occurred while trying to merge VideoArchiveSet with id = ${id}", e)
                }
            }
        }

    }

    /**
     * Merges a {@link VideoArchiveSet} by it's primary-key (id)
     *
     * @param id
     */
    private void doMerge(id, boolean isHD) {
        // DAOTX - Create new DAO with each loop or 1st level cache causes memory leak
        VideoArchiveSetDAO dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        dao.startTransaction()
        def videoArchiveSet = dao.findByPrimaryKey(id)

        if (videoArchiveSet != null) {

            def mergeHistories = toolBox.mergeHistoryDAO.find(id, isHD)
            def pessimisticMerge = mergeHistories.find { it.mergeType == MergeType.PESSIMISTIC.name() }
            def mergeType = pessimisticMerge == null ? MergeType.PRAGMATIC : MergeType.PESSIMISTIC

            def platform = videoArchiveSet.platformName

            // A VideoArchiveSet must have exactly 1 cameraPlatformDeployment in order to merge it
            def cds = videoArchiveSet.cameraDeployments
            def n = cds.size()
            if (n == 0) {
                // ---- Can't merge but update the status to reflect no-merge
                log.info("${videoArchiveSet} does not have any dives associated " +
                        "with it. Unable to merge")
            }
            else if (n > 1) {
                // ---- Can't merge but update the status to reflect no-merge
                log.info("${videoArchiveSet} has more that one dive (${n}) associated " +
                        "with it. Unable to merge")
            }
            else {
                def seqNumber = cds.iterator().next().sequenceNumber
                log.debug("Merging ${platform} #${seqNumber} [isHD = ${isHD}]")
                def action = new MergeEXPDAnnotations2(platform, seqNumber, isHD)
                try {
                    action.apply(mergeType)
                }
                catch (Exception e) {
                    log.debug("Failed to merge ${platform} #${seqNumber} [isHD = ${isHD}].", e)
                }


            }
        }
        dao.endTransaction()
        dao.close()
    }

    private void doMerge(id) {
        // Merge both both SD and HD tapes
        for (doHD in [false, true]) {
            doMerge(id, doHD)
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
                def chiefScientist = dive?.chiefScientist
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
        dao.close()

    }

    void fixDiveDates() {
        log.debug("---- Fixing CameraPlatformDeployments and VideoArchiveSets without start or end dates -----")
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def badVas = dao.findAllWithoutDates()
        badVas.each { videoArchiveSet ->
            try {
                updateDiveDates(videoArchiveSet)
            }
            catch (Exception e) {
                log.warn("Something unexpected happened", e)
            }
        }
        dao.close()
    }

    void fixAllDiveDates() {
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        def allVas = dao.findAll()
        allVas.each { videoArchiveSet ->
            try {
                updateDiveDates(videoArchiveSet)
            }
            catch (Exception e) {
                log.warn("Something unexpected happened", e)
            }
        }
        dao.close()
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
                log.info("No start and end dates for ${videoArchiveSet.platformName} #${deployment.sequenceNumber}")
            }

        }

        videoArchiveSet.startDate = start
        videoArchiveSet.endDate = end
        dao.endTransaction()
        dao.close()

    }


    void fixTrackingNumbers() {
        log.debug("----- Fixing VideoArchiveSets with no trackingNumbers ----")
        def dateFormat = new SimpleDateFormat('yyyyDDD')
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
        dao.startTransaction()
        def badVas = dao.findAllWithoutTrackingNumber()
        badVas.each { vas ->
            def startDate = vas.startDate
            if (startDate) {
                vas.trackingNumber = dateFormat.format(startDate)
            }
        }
        dao.endTransaction()
    }

    void showMergeStatus(String platform, def seqNumber) {
        def dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        MergeHistoryDAO mergeHistoryDAO = toolBox.mergeHistoryDAO
        // TODO next line returns a list
        def mergeHistories = mergeHistoryDAO.findByPlatformAndSequenceNumber(platform, seqNumber, true)
        mergeHistories.addAll(mergeHistoryDAO.findByPlatformAndSequenceNumber(platform, seqNumber, false))
        mergeHistories.sort {it.mergeDate}
        def dao = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()

        println "==========================================================================="
        println " ROV:                ${platform}"
        println " Dive Number:        ${seqNumber}"
        println ""
        println "==== Status of Data Merge with EXPD ===="

        // Show merge information
        if (mergeHistories) {

            for (mergeHistory in mergeHistories) {
                def tapeType = mergeHistory.hd ? "High-Definition" : "Standard-Definition"
                println "-----------------------------------------------------------------"
                println " Merged On:          ${dateFormat.format(mergeHistory.mergeDate)}"
                println " Merge Type:         ${mergeHistory.mergeType}"
                def navSrc = mergeHistory.navigationEdited ? "edited" : "raw"
                println "                     Merged using ${navSrc} navigation data"
                println "                     Recorded Dates are from '${mergeHistory.dateSource}' database(s)"
                println "                     Tapes are ${tapeType}"
                println " Video-frame count:  ${mergeHistory.videoFrameCount}"
                println " Status Message:     ${mergeHistory.statusMessage}\n"
            }

            // Show VideoArchiveSet information

            dao.startTransaction()
            def vas = dao.findByPrimaryKey(mergeHistories[0].videoArchiveSetID)
            def cpds = vas.cameraDeployments
            println ""
            println "==== Details About this VideoArchiveSet ===="
            println " Tracking Number:    ${vas.trackingNumber ?: ''}"
            println " Number of dives:    ${cpds.size()}"
            cpds.inject(1) { n, c ->
                println "\t${n}) Dive Number:       ${c.sequenceNumber}"
                println "\t   Chief Scientist:   ${c.chiefScientistName}"
                n += 1
            }

            //def va = vas.videoArchiveColl
            def va = vas.videoArchives.sort { it.name }
            println " Number of Tapes:    ${va.size()}"
            va.inject(1) { n, v ->
                println "\t${n}) Name:              ${v.name}"
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
    Collection<URL> listMissingStillImages() {

        def badUrls = new Vector<URL>()

        /*
         * Grab all the URL's into a list
         */
        def sql = "SELECT StillImageURL FROM CameraData WHERE StillImageURL LIKE 'http%'"
        def dao = toolBox.toolBelt.annotationDAOFactory.newDAO()
        def query = dao.entityManager.createNativeQuery(sql)
        def urls = query.resultList
        log.debug("----- Found ${urls.size()} images ----")

        /*
         * We'll use 10 threads to check if the images exists
         */
        def queue = new LinkedBlockingQueue(100)
        def imageCheckers = []
        (0..9).each {
            def ic = new ImageChecker2(queue, badUrls)
            imageCheckers << ic
            def thread = new Thread(ic)
            thread.start()
        }
        urls.each { url ->
            try {
                queue.put(new URL(url))
            }
            catch (Exception e) {
                log.info("${url} is not a valid URL", e)
            }
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
class ImageChecker2 implements Runnable {

    static log = LoggerFactory.getLogger(ImageChecker2.class)

    final queue
    final badUrls
    def terminate = false

    /**
     * @param queue A queue full of URL's to process
     * @param badUrls Where the bad URL's are stored (Should be a synchronized collection)
     */
    ImageChecker2(BlockingQueue<URL> queue, Collection<URL> badUrls) {
        this.queue = queue
        this.badUrls = badUrls
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
