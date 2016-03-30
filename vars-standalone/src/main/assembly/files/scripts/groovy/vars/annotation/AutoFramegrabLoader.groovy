package vars.annotation

import vars.ToolBox
import org.slf4j.LoggerFactory
import vars.annotation.ui.Lookup
import vars.avplayer.ImageCaptureService
import vars.annotation.ui.video.RS422VideoControlService
import org.mbari.vcr.VCRAdapter
import org.mbari.movie.Timecode
import org.mbari.vcr.VCRUtil
import vars.annotation.ui.PersistenceController
import org.mbari.vcr.IVCR

/**
 *
 * @author Brian Schlining
 * @since 2012-09-04
 */
class AutoFramegrabLoader {

    private toolBox = new ToolBox();
    private targetRootDirectory
    private targetRootUrl
    private IVCR vcr
    private log = LoggerFactory.getLogger("AutoFramegrabLoader")
    private imageCaptureService
    private frameGrabber

    /**
     * @param targetDir is the root of the directory to write images into
     * @param targetUrl is the mapping of the targetDirectory onto a web server
     * @param commport The RS422 port connected to the VCR
     * @param secondInterval The time interval between frame grabs
     */
    def AutoFramegrabLoader(String targetDir, String targetUrl, String commport, Integer secondInterval) {
        targetRootDirectory = new File(targetDir)
        targetRootUrl = new URL(targetUrl);

        // Intialize image capture
        /*
           FIXME - This fails with
           010-06-08 21:42:53,555 [main] WARN  vars.quicktime.QTImageCaptureServiceImpl  - Failed to initialize QuickTime for Java components.
                org.mbari.framegrab.GrabberException: Failed to initialize QuickTime components
                at org.mbari.framegrab.VideoChannelGrabber.<init>(VideoChannelGrabber.java:57)
                at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
                ...
           Caused by: java.lang.SecurityException: Only able to capture media with security settings when class is signed
                at quicktime.std.sg.SequenceGrabber.initialize(SequenceGrabber.java:90)
                at quicktime.std.sg.SequenceGrabber.<init>(SequenceGrabber.java:58)
                at quicktime.std.sg.SequenceGrabber.<init>(SequenceGrabber.java:46)
                at org.mbari.framegrab.VideoChannelGrabber.<init>(VideoChannelGrabber.java:53)
                ... 111 more

           I found a note at http://www.oreillynet.com/mac/blog/2006/12/explaining_the_quartz_composer.html
           that says I need to sign my classes for this to work. I need to add
           some code to sign the jars when building the standalone app
        */
        imageCaptureService = Lookup.guiceInjectorDispatcher.valueObject.getInstance(ImageCaptureService.class);
        // frameGrabber = imageCaptureService.grabber // using QT4J

        // Initialize video control service
        try {
            def videoControlService = new RS422VideoControlService()
            videoControlService.connect(commport, 29.97D)
            vcr = videoControlService.vcr
        }
        catch (Exception e) {
            log.warn("Could not connect to VCR. VCR control is disabled", e)
            vcr = new VCRAdapter()
        }
    }

    def close() {
        vcr.disconnect()
        frameGrabber.dispose()
    }

    def load(String platform, Integer sequenceNumber, int secondsInterval, int tapeNumber) {
        log.info("Loading records into VARS for ${platform} #${sequenceNumber}")
        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, tapeNumber, "HD")

        vcr.requestTimeCode()
        def timecode = vcr.getVcrTimecode().timecode
        def failedCaptureCount = 0
        try {

            // Need the root selectedConcept
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            conceptDAO.endTransaction()
            conceptDAO.close()

            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()

            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            def parentDir = new File("${targetRootDirectory.getAbsolutePath()}/${platform}/images/${sequenceNumber}/")
            def parentUrlString = "${targetRootUrl.toExternalForm()}${platform}/images/${sequenceNumber}/"
            parentDir.mkdirs()

            log.info("Starting auto-framegrabs at ${timecode}")
            while (failedCaptureCount < 3) {
                videoArchiveDAO.startTransaction()
                def videoFrame = videoArchive.findVideoFrameByTimeCode(timecode.toString())
                log.info("Processing data at ${timecode}")
                // Create a VideoFrame if needed
                if (!videoFrame) {
                    videoFrame = annotationFactory.newVideoFrame()
                    videoFrame.timecode = timecode
                    videoArchive.addVideoFrame(videoFrame)
                    videoArchiveDAO.persist(videoFrame)

                }

                // Create an observation if needed
                if (videoFrame.observations.size() == 0) {
                    def observation = annotationFactory.newObservation()
                    observation.conceptName = conceptNameAsString
                    observation.observer = getClass().simpleName
                    observation.observationDate = new Date()
                    videoFrame.addObservation(observation)
                    videoArchiveDAO.persist(observation)
                }

                // Make sure that there is an image associated with the videoframe
                if (videoFrame.cameraData.imageReference) {
                    log.warn("${videoFrame} already exists and contains an image reference. Not modifying it")
                }
                else {

                    def imageName = "${timecode.toString().replace(":", "_")}.png"
                    def targetFile = new File(parentDir, imageName)
                    def gotImage = targetFile.exists()
                    if (!gotImage) {
                        gotImage = captureFrame(timecode.toString(), targetFile)
                    }

                    if (gotImage) {
                        def targetUrl = new URL("${parentUrlString}${imageName}")
                        videoFrame.cameraData.imageReference = targetUrl.toExternalForm()
                        failedCaptureCount = 0
                    }
                    else {
                        failedCaptureCount = failedCaptureCount + 1
                    }
                }
                videoArchiveDAO.endTransaction()

                // increment the timecode by our interval
                timecode = new Timecode(timecode.frames + timecode.frameRate * secondsInterval)

            }
            

        }
        catch (Exception e) {
            log.error("Load failed", e)
        }



    }

    private boolean captureFrame(String timecode, File target) {
        def captured = false
        def targetTimecode = new Timecode(timecode)
        def toleranceInFrames = 2
        def count = 0
        def maxCount = 120 // 30 seconds

        // Use RXTX to seek to the correct timecode
        vcr.seekTimecode(VCRUtil.timecodeToTime(targetTimecode))
        while(true) {
            Thread.sleep(250)
            vcr.requestStatus()
            if (vcr.vcrState.isStopped()) {
                vcr.requestTimeCode()
                def currentTimecode = vcr.vcrTimecode.timecode
                if (Math.abs(targetTimecode.diffFrames(currentTimecode)) <= toleranceInFrames) {
                    count++
                    log.debug("Saving image to ${target.absolutePath}")

                    // TODO Use Imagesnap to grab the image from the video capture card
                    //def snapCommand = "imagesnap -d videocard ${target.absolutePath}" as String
                    //snapCommand.execute()
                    //GrabUtil.capture(frameGrabber, target)  // Using QT4J
                    imageCaptureService.capture(target)       // Using QTKit
                    captured = true
                }
                else {
                    log.debug("Expected the VCR to be stopped at " +
                            timecode + " but found " + vcr.vcrTimecode.timecode + " instead. No frame " +
                            " will be captured for " + timecode)
                }
                break
            }

            if (count == maxCount) {
                break;
            }
            count++
        }

        return captured
    }

}
