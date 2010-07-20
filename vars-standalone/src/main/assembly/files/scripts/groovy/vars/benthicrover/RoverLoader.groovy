package vars.benthicrover

import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.PersistenceController
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jun 29, 2010
 * Time: 11:44:26 AM
 * To change this template use File | Settings | File Templates.
 */
class RoverLoader {

    def toolBox
    private final log = LoggerFactory.getLogger(getClass())
    private final timecodeFormat
    private observer = getClass().simpleName


    def RoverLoader() {
        toolBox = new ToolBox()
        // Time is only resolved to the nearest second so we don't worry about 'frames'
        timecodeFormat = new SimpleDateFormat("HH:mm:ss:'00'")
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
    }

    def load(List<URL> images, String platform, Integer sequenceNumber) {
        def re = /_\d+\./
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "-rover")
        try {
            def conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDao.startTransaction()
            def conceptNameAsString = conceptDao.findRoot().primaryConceptName.name
            conceptDao.endTransaction()

            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            videoArchiveDAO.startTransaction()

            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            images.each { url ->

                // -- Parse date and timecode from image name (transit_1255656931.53.jpg)
                def m = url.toExternalForm() =~ re
                def t = (m[0][1..-2] as Long) * 1000L
                def date = new Date(t)
                def timecode = timecodeFormat.format(date)

                // -- Find or create a videoframe
                def videoFrame = videoArchive.findVideoFrameByTimeCode(timecode)
                if (!videoFrame) {
                    videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode
                    videoFrame.recordedDate = date
                    videoArchive.addVideoFrame(videoFrame)
                    videoArchiveDAO.persist(videoFrame)
                }

                //  -- IF a matching videoframe already exists. Change the image URL if none is yet set.
                if (videoFrame.cameraData.imageReference) {
                    log.warn("${videoFrame} already exists and contains an image reference. Not modifying it")
                }
                else {
                    videoFrame.cameraData.setImageReference(url.toExternalForm())
                }

                // -- Make sure there is at least one observation
                if (videoFrame.observations.size() == 0) {
                    def observation = annotationFactory.newObservation()
                    observation.conceptName = conceptNameAsString
                    observation.observer = observer
                    observation.observationDate = new Date()
                    videoFrame.addObservation(observation)
                    videoArchiveDAO.persist(observation)
                }

            }
            videoArchiveDAO.endTransaction()

        }
        catch (Exception e) {
          log.error("ChuckNorrisException ----> Round House kick to the face!", e)
        }
    }

}
