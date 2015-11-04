package vars.testing;


import java.text.NumberFormat;
import java.util.Date;

import org.mbari.movie.Timecode;
import vars.annotation.VideoFrame;
import vars.annotation.*;

/**
 * Factory for generating prepopulated annotation objects for use in Unit tests
 */
public class AnnotationTestObjectFactory {

    private final AnnotationFactory factory;
    public static final long TEST_EPOCH = 1063391106296L;

    public AnnotationTestObjectFactory(AnnotationFactory factory) {
        this.factory = factory;
    }

    private static long randomNumber(long min, long max) {
        long range = max - min;
        long value = (long) (Math.random() * range + min);
        return value;
    }

    private static Timecode makeTimecode() {
        long hour = randomNumber(0, 12);
        long minute = randomNumber(0, 59);
        long second = randomNumber(0, 59);
        long frame = randomNumber(0, 29);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        return new Timecode(
            nf.format(hour)
                + ":"
                + nf.format(minute)
                + ":"
                + nf.format(second)
                + ":"
                + nf.format(frame));
    }

    public VideoArchiveSet makeVideoArchiveSet() {
         // VideoArchiveSet represents a collection of Related Tapes
        String trackingNumber = randomNumber(0, 10000) + ""; // what is this?
        String shipName = "TEST";
        String platformName = "TEST";
        char formatCode = FormatCodes.DETAILED.getCode();
        VideoArchiveSet vas = factory.newVideoArchiveSet();
        vas.setTrackingNumber(trackingNumber);
        vas.setShipName(shipName);
        vas.setPlatformName(platformName);
        vas.setFormatCode(formatCode);
        vas.setStartDate(new Date(randomNumber(0, TEST_EPOCH)));
        vas.setEndDate(
            new Date(randomNumber(TEST_EPOCH, (new Date().getTime()))));
        return vas;
    }

    public VideoArchive makeVideoArchive() {
        //      An VideoArchive represents a single Tape
        String name = "T" + Integer.valueOf((int) randomNumber(1, 1500000));
        // 1-based not 0-based
        Timecode timeCode = makeTimecode();
        VideoArchive va = factory.newVideoArchive();
        va.setStartTimecode(timeCode.toString());
        va.setName(name);
        return va;
    }

    public CameraDeployment makeCameraPlatformDeployment() {
        //      CameraPlatformDeployment describes the Camera platform.
        int seqNumber = (int) randomNumber(1, 3000); // Dive Number
        Date start = new Date(randomNumber(0, TEST_EPOCH));
        // Start of Dive in epic seconds
        Date end = new Date(randomNumber(TEST_EPOCH, (new Date().getTime())));
        // End of Dive in epic seconds
        CameraDeployment cd = factory.newCameraDeployment();
        cd.setChiefScientistName("MBARI Scientest");
        cd.setStartDate(start);
        cd.setEndDate(end);
        cd.setSequenceNumber(seqNumber);
        return cd;
    }

    public VideoFrame makeVideoFrame() {
        
        //		An annotation represents desriptions of a Frame
        long recordedEpoch = randomNumber(TEST_EPOCH, new Date().getTime());
        // UTC time in epic seconds of Frame.
        VideoFrame videoFrame = factory.newVideoFrame();
        videoFrame.setRecordedDate(new Date(recordedEpoch));
        videoFrame.setTimecode(makeTimecode().toString());
        videoFrame.setInSequence(true);
        videoFrame.setAlternateTimecode(makeTimecode().toString());

        // Populate the physicalData
        PhysicalData physicalData = videoFrame.getPhysicalData();
        physicalData.setDepth(new Float(randomNumber(0, 90000) / 100F));
        physicalData.setLatitude(new Double(randomNumber(0, 9000) / 100F));
        physicalData.setLongitude(new Double(randomNumber(0, -18000) / 100F));
        physicalData.setLight(new Float(randomNumber(0, 10000) / 100F));
        physicalData.setOxygen(new Float(randomNumber(0, 1000) / 100F));
        physicalData.setSalinity(new Float(randomNumber(320000, 360000) / 10000F));
        physicalData.setTemperature(new Float(randomNumber(30000, 150000) / 10000F));
        physicalData.setAltitude(new Float(randomNumber(0, 90000) / 100F));

        // populate the cameraData
        CameraData camera = videoFrame.getCameraData();
        camera.setName("Test");
        camera.setDirection("cruise");
        camera.setFieldWidth(new Double(randomNumber(0, 100)));
        camera.setFocus(Integer.valueOf((int) randomNumber(0, 10)));
        camera.setIris(Integer.valueOf((int) randomNumber(0, 5)));
        camera.setImageReference("http://www.mbari.org/IMG" + randomNumber(0, 10000) + ".jpg");
        camera.setZoom(Integer.valueOf((int) randomNumber(0, 10)));

        return videoFrame;
    }

    public Observation makeObservation(String conceptName) {
        Observation observation = factory.newObservation();
        observation.setObserver("Testy the Tester");
        observation.setConceptName(conceptName);
        observation.setObservationDate(new Date());
        observation.setNotes("Test tes test test");
        return observation;
    }

    public Association makeAssociation() {
        //      An association associaties an Observatoin or Association with
        // Another Object
        Association association = factory.newAssociation();
        association.setLinkName("pop-count");
        association.setLinkValue(randomNumber(1, 1000) + "");
        association.setToConcept("nil");
        return association;
    }

    public VideoArchiveSet makeObjectGraph(String prefix) {
	    return makeObjectGraph(prefix, 2);
	}

    public VideoArchiveSet makeObjectGraph(String prefix, int depth) {

           VideoArchiveSet vas = makeVideoArchiveSet();
           String shortPrefix = prefix;
           vas.setShipName(shortPrefix);
           vas.setPlatformName(shortPrefix);

           for (int n = 0; n < depth; n++) {

               VideoArchive va = makeVideoArchive();
               va = makeVideoArchive();
               vas.addVideoArchive(va);

               for (int i = 0; i < depth; i++) {
                   CameraDeployment cpd = makeCameraPlatformDeployment();
                   cpd.setChiefScientistName(prefix + "_scientist_" + n + "_" + i);
                   vas.addCameraDeployment(cpd);
               }

               for (int i = 0; i < depth; i++) {
                   VideoFrame vf = makeVideoFrame();
                   va.addVideoFrame(vf);

                   for (int j = 0; j < depth; j++) {
                       Observation obs = makeObservation(prefix + "_observation_" + n + "_" + i + "_" + j);
                       vf.addObservation(obs);
                       for (int k = 0; k < depth; k++) {
                           Association a = makeAssociation();
                           a.setLinkName(prefix);
                           a.setLinkValue(n + "_" + i + "_" + j + "_" + k);
                           obs.addAssociation(a);
                       }
                   }
               }
           }

           return vas;
       }

}

