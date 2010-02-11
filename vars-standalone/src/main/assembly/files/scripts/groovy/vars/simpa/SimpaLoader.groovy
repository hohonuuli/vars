package vars.simpa

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Date
import java.util.List
import java.util.TimeZone
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.PersistenceController
import org.mbari.expd.actions.CoallateByDateFunction
import org.mbari.vcr.rs422.VCR
import org.mbari.vcr.VCRUtil
import org.mbari.movie.Timecode
import vars.annotation.CameraData

class SimpaLoader {

    private final toolBox;
    private final dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final offsetSecs = 7.5D;
    private final coallateFunction = new CoallateByDateFunction()
    private final targetRootDirectory
    private final targetRootUrl
    private final vcr
    private final log = LoggerFactory.getLogger(getClass())

    /**
     * @param targetDir is the root of the directory to write images into
     * @param targetUrl is the mapping of the targetDirectory onto a web server 
     */
    def SimpaLoader(String targetDir, String targetUrl, String commport) {
        targetRootDirectory = new File(targetDir)
        targetRootUrl = new URL(targetUrl);
        vcr = new VCR(commport);

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        toolBox = new ToolBox()
    }

    def read(URL url) {
        def reader = new BufferedReader(new InputStreamReader(url.openStream()));
        def lineCount = 0
        def line = null
        def data = []

        while((line = reader.readLine())) {
            data << parse(line)
        }

        reader.close()
        return data
    }

    def load(List<SimpaDatum> simpaData, String platform, Integer sequenceNumber) {

        // Need the dates for simpaData
        def simpaDates = simpaData.collect { it.date }

        // Fetch CTD data to get timecode
        def dive = toolBox.daoFactory.newDiveDAO().findByPlatformAndDiveNumber(platform, sequenceNumber)
        def uberData = toolBox.daoFactory.newUberDatumDAO().fetchData(dive, true, offsetSecs)
        def dataMap = coallateFunction.apply(simpaDates, uberData, offsetSecs * 1000)

        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, 1, "-coolpix")
        try {
            def cameraDataDAO = toolBox.daoFactory.newCameraDatumDAO()
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            videoArchiveDAO.startTransaction()

            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            def parentDir = new File("${targetRootDirectory.getAbsolutePath()}/${platform}/images/${sequenceNumber}/")
            def parentUrlString = "${targetRootUrl.toExternalForm()}/${platform}/images/${sequenceNumber}/"
            parentDir.mkdirs()

            simpaData.each { simpaDatum ->
                def uberDatum = dataMap[simpaDatum.date]
                def cameraDatum = uberDatum?.cameraDatum
                if (cameraDatum) {
                    def videoFrame = videoArchive.findVideoFrameByTimeCode(cameraDatum.timecode)
                    def videoTime = cameraDataDAO.interpolateTimecodeToDate(platform, simpaDatum.date, offsetSecs * 1000, 29.97)

                    // Create a VideoFrame if needed
                    if (!videoFrame) {
                        if (videoTime) {
                            videoFrame = annotationFactory.newVideoFrame()
                            videoFrame.timecode = videoTime.timecode
                            videoFrame.recordedDate = simpaDatum.date
                            videoArchive.addVideoFrame(videoFrame)
                            videoArchiveDAO.persist(videoFrame)
                        }
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
                        def imageName = "${videoTime.timecode.replace(":", "_")}.png"
                        def targetFile = new File(parentDir, imageName)
                        if (captureFrame(videoTime.timecode, targetFile)) {
                            def targetUrl = new URL("${parentUrlString}${targetFile}")
                            CameraData cameraData = videoFrame.cameraData

                            // et videoFrame and cameraData fields
                            cameraData.imageReference = targetUrl.toExternalForm()
                            cameraData.x = simpaDatum.x
                            cameraData.y = simpaDatum.y
                            cameraData.setXYUnits("meters from origin")
                            cameraData.z = simpaDatum.z
                            cameraData.setZUnits("meters from origin")
                            cameraData.viewHeight = simpaDatum.height
                            cameraData.viewWidth = simpaDatum.width
                            cameraData.viewUnits = "meters"

                            // The following are in radians
                            cameraData.roll = simpaDatum.roll
                            cameraData.pitch = simpaDatum.pitch
                            cameraData.heading = simpaDatum.heading

                        }
                    }
                }
            }

            videoArchiveDAO.endTransaction()
        }
        catch (Exception e) {

        }

    }

    private boolean captureFrame(String timecode, File target) {
        def captured = false
        def targetTimecode = new Timecode(timecode)
        def toleranceInFrames = 2
        def count = 0
        def maxCount = 480 // 2 minutes (or 480 iterations at 250 ms per iteration).

        // Use RXTX to seek to the correct timecode
        vcr.seekTimecode(VCRUtil.timecodeToTime(targetTimecode))
        while(true) {
            Thread.sleep(250)
            vcr.requestStatus()
            if (vcr.vcrState.isStopped) {
                vcr.requestTimeCode()
                def currentTimecode = vcr.vcrTimecode.timecode
                if (Math.abs(targetTimecode.diffFrames(currentTimecode)) <= toleranceInFrames) {
                    count++
                    log.debug("Saving image to ${target.absolutePath}")

                    // TODO Use Imagesnap to grab the image from the video capture card
                    def snapCommand = "imagesnap -d videocard ${target.absolutePath}" as String
                    snapCommand.execute()
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

    def parse(String line) {
        /*
         * Split the line by white space. Drop the empty spaces in
         * the resulting array.
         */
        def simpaDatum = new SimpaDatum()
        def values = line.split('\t');
        if (values.size() == 11) {
            def tileIndex = Double.parseDouble(values[0])
            def date = dateFormat.parse("${values[1]} ${values[2]}")
            def x = Double.parseDouble(values[4]); // Circle swaps his axes (he uses +x = forward, +y = right)
            def y = Double.parseDouble(values[3]);
            def z = Double.parseDouble(values[5]);
            def roll = Double.parseDouble(values[6]);
            def pitch = Double.parseDouble(values[7]);
            def heading = Double.parseDouble(values[8]);
            def width = Double.parseDouble(values[10]);    // Swap width and height from file too.
            def height = Double.parseDouble(values[9]);
            simpaDatum = new SimpaDatum(tileIndex, date, x, y, z, roll, pitch, heading,
                    width, height)
        }
        return simpaDatum;

    }


    private class SimpaDatum {
        int tileIndex
        Date date
        double x, y, z, roll, pitch, heading, width, height

        def SimpaDatum() { }

        def SimpaDatum(tileIndex, date, x, y, z, roll, pitch, heading, width, height) {
            this.x = x
            this.y = y
            this.z = z
            this.roll = roll
            this.pitch = pitch
            this.heading = heading
            this.width = width
            this.height = height
        }


    }

}

