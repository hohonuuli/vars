package org.mbari.vars.biauv

import org.mbari.biauv.integration.MergeData
import vars.ToolBox
import vars.annotation.CameraDeployment
import vars.annotation.VideoArchiveDAO
import java.text.SimpleDateFormat
import vars.annotation.VideoArchive
import vars.annotation.VideoFrame
import vars.DAO
import org.slf4j.LoggerFactory
import org.mbari.biauv.integration.MergeDatum
import org.mbari.math.CoallateFunction

/**
 * 
 * @author Brian Schlining
 * @since Sep 13, 2010
 */
class AUVLoader {

    private final toolBox
    private final timecodeFormat
    private final annotationFactory
    private conceptNameAsString
    private final log = LoggerFactory.getLogger(getClass())

    def AUVLoader() {
        toolBox = new ToolBox()
        timecodeFormat = new SimpleDateFormat("HH:mm:ss")
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
        annotationFactory = toolBox.toolBelt.annotationFactory

        def conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
        conceptDao.startTransaction()
        conceptNameAsString = conceptDao.findRoot().primaryConceptName.name
        conceptDao.endTransaction()
        conceptDao.close()
    }

    /**
     * Loads an AUV mission into VARS
     * @param videoArchiveName The name, I'm using "biauv-2009.173.01" as the
     *  names where the number matches the directory containing the nav and
     *  camera logs
     * @param platform Normally we'll use 'Benthic Imaging AUV'
     * @param diveNumber I'm using the mission directory sans the '.'. So
     *  2009.173.01 would become 200917301
     * @param images This is a Map<Date, URL> where the date is the GPS
     *  time of the image and the URL points to the location of the image
     *  on a web server.
     * @param mergeData The merge data object to be used to supply position
     *  and camera info. {@see org.mbari.biauv.integration.LogRecordReader}
     */
    void load(String videoArchiveName, String platform, int diveNumber,
             Map<Date, URL> images, List<MergeDatum> mergeData) {
                 
        // -- Coallate the mergedata with the images
        def dates = new ArrayList(images.keySet())
        def data = CoallateFunction.coallate(dates, {Date d -> d.time}, 
                mergeData, {MergeDatum d -> (d.time * 1000) as Long }, 5000)

        def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        videoArchiveDAO.startTransaction()
        def videoArchive = getVideoArchive(videoArchiveDAO, videoArchiveName, platform,
                diveNumber)
        images.each { date, image ->
            try {
                def mergeDatum = data[date]
                if (mergeDatum) {
                    makeVideoFrame(videoArchive, videoArchiveDAO, date, image, mergeDatum)
                    log.debug("Found image at ${date}")
                }
                else {
                    log.debug("Ignoring image at ${date}")   
                }
            }
            catch (Exception e) {
                log.info("Failed to add VideoFrame for $image")
            }

        }
        videoArchiveDAO.endTransaction()
        videoArchiveDAO.close()
        
    }

    /**
     * Get a persisted video archive of the given name and parameters
     * @param dao
     * @param videoArchiveName
     * @return
     */
    private getVideoArchive(VideoArchiveDAO dao, String videoArchiveName,
                            String platform, int sequenceNumber) {

        def videoArchive = dao.findByName(videoArchiveName)
        if (!videoArchive) {
            def annotationFactory = toolBox.toolBelt.annotationFactory
            def videoArchiveSet = annotationFactory.newVideoArchiveSet()
            dao.persist(videoArchiveSet)
            videoArchiveSet.setPlatformName(platform)
            CameraDeployment cameraDeployment = annotationFactory.newCameraDeployment()
            cameraDeployment.setSequenceNumber(sequenceNumber)
            videoArchiveSet.addCameraDeployment(cameraDeployment)
            dao.persist(cameraDeployment)
            videoArchive = annotationFactory.newVideoArchive()
            videoArchive.setName(videoArchiveName)
            videoArchiveSet.addVideoArchive(videoArchive)
            dao.persist(videoArchive)
        }
        log.info("Using ${videoArchive.videoArchiveSet} and \t\t ${videoArchive}")
        return videoArchive
    }

    private void makeVideoFrame(VideoArchive videoArchive, DAO dao, Date date, URL image, MergeDatum mergeDatum) {
        def s = date.time / 1000D
        def ff = Math.round((s - Math.floor(s)) * 30) // millsec portion expressed as seconds
        def timecode = "${timecodeFormat.format(date)}:${ff}"
        def videoFrame = videoArchive.findVideoFrameByTimeCode(timecode)
        if (!videoFrame) {
            videoFrame = annotationFactory.newVideoFrame()
            videoFrame.timecode = timecode
            videoFrame.recordedDate = date
            videoArchive.addVideoFrame(videoFrame)
            dao.persist(videoFrame)
        }
        //  -- IF a matching videoframe already exists. Change the image URL if none is yet set.
        if (videoFrame.cameraData.imageReference) {
            println("${videoFrame} already exists and contains an image reference. Not modifying it")
        }
        else {
            videoFrame.cameraData.setImageReference(image.toExternalForm())
            if (videoFrame.observations.size() == 0) {
                def observation = annotationFactory.newObservation()
                observation.conceptName = conceptNameAsString
                observation.observer = getClass().simpleName
                observation.observationDate = new Date()
                videoFrame.addObservation(observation)
                dao.persist(observation)
            }
        }

        try {
            merge(dao, videoFrame, mergeDatum)
        }
        catch (Exception e) {
            log.info("Failed to merge ${videoFrame} for $image with position information ", e)
        }
    }

    private void merge(DAO dao, VideoFrame videoFrame, MergeDatum mergeDatum) {
        def cameraData = videoFrame.cameraData
        if (!dao.isPersistent(cameraData)) {
            dao.persist(cameraData)
        }
        cameraData.with {
            viewWidth = mergeDatum.viewWidth
            viewHeight = mergeDatum.viewHeight
            viewUnits = "meters"
            pitch = mergeDatum.pitch
            roll = mergeDatum.roll
            heading = mergeDatum.yaw
        }

        def phyicalData = videoFrame.physicalData
        if (!dao.isPersistent(phyicalData)) {
            dao.persist(phyicalData)
        }
        phyicalData.with {
            latitude = mergeDatum.latitude
            longitude = mergeDatum.longitude
            depth = mergeDatum.depth
        }
    }


}
