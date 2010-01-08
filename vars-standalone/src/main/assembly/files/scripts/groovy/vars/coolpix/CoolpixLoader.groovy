package vars.coolpix


import org.slf4j.LoggerFactory
import vars.ToolBox
import java.text.SimpleDateFormat
import vars.annotation.ui.PersistenceController
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.tiff.constants.TiffConstants

/**
 * Class used by MBARI for loading coolpix images into a VARS database
 *
 */
class CoolpixLoader {

    def toolBox
    private final log = LoggerFactory.getLogger(getClass())
    private final dateFormat
    private final timecodeFormat
    
    def CoolpixLoader() {
        toolBox = new ToolBox()
        dateFormat = new SimpleDateFormat('yyyy:MM:dd HH:mm:ss')
        dateFormat.timeZone = TimeZone.getTimeZone('UTC')
        timecodeFormat = new SimpleDateFormat("HH:mm:ss:'00'")
        timecodeFormat.timeZone = TimeZone.getTimeZone('UTC')
    }
    
    def load(List<URL> images, String platform, Integer sequenceNumber) {

        /*
         *  -- Step 1: Ensure there is a VideoFrame for each image
         */
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "")
        try {
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            videoArchiveDAO.startTransaction()
            
            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            def videoFrames = videoArchive.videoFrames
            
            // Loop through all the files updating or creating VideoFrames
            images.each { url ->

                //  -- Get the time from the image EXIF data using Sanselan
                def inputStream = new BufferedInputStream(url.openStream());
                def metadata = Sanselan.getMetadata(inputStream)
                if (metadata) {
                    def field = metadata.findEXIFValue(TiffConstants.EXIF_TAG_CREATE_DATE)
                    if (field) {

                        def date = dateFormat.parse(field.valueDescription)
                        def timecode = timecodeFormat.format(date)
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
                            videoFrame.setImageReference(url.toExternalForm())  
                            if (videoFrame.observations.size() == 0) {
                                observation = annotationFactory.newObservation()
                                observation.conceptName = conceptNameAsString
                                observation.observer = getClass().simpleName
                                observation.observationDate = new Date()
                                videoFrame.addObservation(observation)
                                videoArchiveDAO.persist(observation)
                            }
                        }
                    }
                }
                else {
                    log.warn("${TiffConstants.EXIF_TAG_CREATE_DATE.name} was not found")
                }
            }
            videoArchiveDAO.endTransaction()
            
            

            
            // After data is loaded run a merge on it. Use the new expd project
        }
        catch (Exception e) {
            
        }
    }

    

}