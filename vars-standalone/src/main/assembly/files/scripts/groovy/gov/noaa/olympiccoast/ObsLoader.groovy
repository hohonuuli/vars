package gov.noaa.olympiccoast

import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.integration.MergeFunction
import java.text.SimpleDateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import vars.annotation.ui.PersistenceController
import vars.LinkBean
import vars.ILink
import vars.annotation.VideoFrame
import vars.annotation.VideoArchive

class ObsLoader {
    
    private toolBox = new ToolBox()
    private annotationFactory = toolBox.toolBelt.annotationFactory
    private videoArchiveDAO
    private final log = LoggerFactory.getLogger(getClass())
    private observer = getClass().simpleName
    private final NumberFormat f0123 = new DecimalFormat("0000");
    private final link = new LinkBean("camera view", ILink.VALUE_SELF, ILink.VALUE_NIL)
    
    void load(List<ObsDatum> data, String platform, Integer sequenceNumber) {
        def date = new Date()
        def videoArchiveName = "${platform[0].toUpperCase()}${f0123.format(sequenceNumber)}-OBS"
        videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
        videoArchiveDAO.startTransaction()
        def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
        data.each { obsDatum ->
            if (obsDatum.dive == sequenceNumber) {
                def videoFrame = findVideoFrame(videoArchive, obsDatum)
                def observation = annotationFactory.newObservation()
                observation.conceptName = obsDatum.conceptName
                observation.observer = observer
                observation.observationDate = date
                videoFrame.addObservation(observation)
                videoArchiveDAO.persist(observation)
                def association = annotationFactory.newAssociation(link)
                association.linkValue = obsDatum.cameraView
                observation.addAssociation(association)
                videoArchiveDAO.persist(association)
            }
            else {
                log.warn("Observation at ${obsDatum.logDate} is from dive ${obsDatum.dive}; not from ${sequenceNumber}. Skipping this record")   
            }
        }
        videoArchiveDAO.endTransaction()
        videoArchiveDAO.close()
           
    }
    
    /**
     * Find a video frame
     */
    private findVideoFrame(VideoArchive videoArchive, ObsDatum obsDatum) {
        def videoFrame = videoArchive.findVideoFrameByTimeCode(obsDatum.timecode)
        if (!videoFrame) {
            videoFrame = annotationFactory.newVideoFrame()
            videoFrame.timecode = obsDatum.timecode
            videoFrame.recordedDate = obsDatum.logDate
            videoArchive.addVideoFrame(videoFrame)
            videoArchiveDAO.persist(videoFrame)
        }
        
        return videoFrame
    }
    
}
