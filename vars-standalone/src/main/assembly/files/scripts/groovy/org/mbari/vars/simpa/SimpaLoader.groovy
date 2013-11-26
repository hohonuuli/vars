package org.mbari.vars.simpa

import java.text.SimpleDateFormat

import org.mbari.framegrab.GrabUtil

import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.ui.PersistenceController
import org.mbari.expd.actions.CoallateByDateFunction

import org.mbari.vcr.VCRAdapter
import org.mbari.vcr.VCRUtil
import org.mbari.movie.Timecode
import org.mbari.movie.VideoTimeBean
import vars.annotation.CameraData
import vars.annotation.ui.Lookup
import vars.annotation.ui.video.RS422VideoControlService
import vars.shared.ui.video.ImageCaptureService

class SimpaLoader {

    private toolBox = new ToolBox();
    private dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private offsetSecs = 7.5D;
    private coallateFunction = new CoallateByDateFunction()
    private targetRootDirectory
    private targetRootUrl
    private vcr
    private log = LoggerFactory.getLogger("SimpaLoader")
    private frameGrabber

    /**
     * @param targetDir is the root of the directory to write images into
     * @param targetUrl is the mapping of the targetDirectory onto a web server 
     */
    def SimpaLoader(String targetDir, String targetUrl, String commport) {
        targetRootDirectory = new File(targetDir)
        targetRootUrl = new URL(targetUrl);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

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
        def imageCaptureService = Lookup.guiceInjectorDispatcher.valueObject.getInstance(ImageCaptureService.class);
        frameGrabber = imageCaptureService.grabber
        
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

    List<SimpaDatum> read(URL url) {
		log.info("Reading data from ${url.toExternalForm()}")
        def reader = new BufferedReader(new InputStreamReader(url.openStream()));
        def lineCount = 0
        def line = null
        def data = []

        while((line = reader.readLine())) {
            data << parse(line)
            lineCount++
        }

        reader.close()
		log.info("Read ${lineCount} records")
        return data
    }

    def load(List<SimpaDatum> simpaData, String platform, Integer sequenceNumber, String startTimecodeS, String endTimecodeS, int tapeNumber) {

		log.info("Loading records into VARS for ${platform} #${sequenceNumber}")
		def startTimecode = new Timecode(startTimecodeS)
		def endTimecode = new Timecode(endTimecodeS)
 
        // Need the dates for simpaData
        def simpaDates = simpaData.collect { it.date }

        // Fetch CTD data to get timecode
        def dive = toolBox.daoFactory.newDiveDAO().findByPlatformAndDiveNumber(platform, sequenceNumber)
        def uberData = toolBox.daoFactory.newUberDatumDAO().fetchData(dive, true, offsetSecs)
        def dataMap = coallateFunction.apply(simpaDates, uberData, Math.round(offsetSecs * 1000) as Long)
		dataMap.each { key, value -> println("${dateFormat.format(key)} - ${dateFormat.format(value.cameraDatum.date)} ${value.cameraDatum.timecode} ${value.cameraDatum.alternativeTimecode}")}

        def annotationFactory = toolBox.toolBelt.annotationFactory
        def videoArchiveName = PersistenceController.makeVideoArchiveName(platform, sequenceNumber, tapeNumber, "-simpa")
        try {
			
            def cameraDataDAO = toolBox.daoFactory.newCameraDatumDAO()
            def conceptDAO = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
            conceptDAO.startTransaction()
            def conceptNameAsString = conceptDAO.findRoot().primaryConceptName.name
            def videoArchiveDAO = toolBox.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
            videoArchiveDAO.startTransaction()

            def videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, sequenceNumber, videoArchiveName)
            def parentDir = new File("${targetRootDirectory.getAbsolutePath()}/${platform}/images/${sequenceNumber}/")
            def parentUrlString = "${targetRootUrl.toExternalForm()}${platform}/images/${sequenceNumber}/"
            parentDir.mkdirs()

            simpaData.each { simpaDatum ->
				log.info("Examining SIMPA data at ${dateFormat.format(simpaDatum.date)}")
                def uberDatum = dataMap[simpaDatum.date]
                def cameraDatum = uberDatum?.cameraDatum
                if (cameraDatum) {
					log.info("Processing SIMPA data at ${dateFormat.format(simpaDatum.date)}")
                    def videoFrame = videoArchive.findVideoFrameByTimeCode(cameraDatum.alternativeTimecode)
                    //def videoTime = cameraDataDAO.interpolateTimecodeToDate(platform, simpaDatum.date, offsetSecs * 1000 as Integer, 29.97 as Double)
					def videoTime = new VideoTimeBean(uberDatum.cameraDatum.date, uberDatum.cameraDatum.alternativeTimecode)
					def videoTimecode = new Timecode(videoTime.timecode)
					if (videoTimecode.frames >= startTimecode.frames && videoTimecode.frames <= endTimecode.frames) {

						log.info("Processing SIMPA data at ${videoTime.timecode} / ${dateFormat.format(simpaDatum.date)}")	

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
							def gotImage = targetFile.exists()
							if (!gotImage) {
								gotImage = captureFrame(videoTime.timecode, targetFile)
							}
	                        if (gotImage) {
	                            def targetUrl = new URL("${parentUrlString}${imageName}")
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
					else {
						log.info("${videoTime.timecode} is not between ${startTimecode} and ${endTimecode}. Skipping it")
					}
                }
            }

            videoArchiveDAO.endTransaction()
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
					GrabUtil.capture(frameGrabber, target)
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

    private parse(String line) {
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




}

