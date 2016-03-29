package vars.avfoundation;

import org.mbari.util.TimeUtilities;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: brian
 * Date: 12/3/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class DemoApp {
/*
    public static void main(String[] args) {
        File saveDirectory = new File(args[0]);
        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }
        AVFImageCaptureServiceImpl imageCaptureService = new AVFImageCaptureServiceImpl();
        String[] videoDeviceNames = imageCaptureService.videoDevicesAsStrings();
        String videoDevice = videoDeviceNames[0];
        imageCaptureService.startSessionWithNamedDevice(videoDevice);
        for (int i = 0; i < 5; i++) {
            File target = new File(saveDirectory, "snapshot" + i + ".png");
            Image image = imageCaptureService.capture(target);
            if (image == null) {
                System.out.println("Failed to write " + target.getAbsolutePath());
            }
            else {
                System.out.println("Wrote " + target.getAbsolutePath());
            }
        }

    } */

    public static void main(String[] args) {

        File saveDirectory;
        if (args.length == 0) saveDirectory = new File("target");
        else saveDirectory = new File(args[0]);

        if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
        }
        AVFImageCaptureServiceImpl imageCaptureService = new AVFImageCaptureServiceImpl();
        imageCaptureService.startSessionWithNamedDevice("FaceTime HD Camera");
        for (int i = 0; i < 5; i++) {
            File target = new File(saveDirectory, "snapshot" + i + ".png");
            Optional<Image> image = imageCaptureService.capture(target);
            if (!image.isPresent()) {
                System.out.println("Failed to write " + target.getAbsolutePath());
            }
            else {
                System.out.println("Wrote " + target.getAbsolutePath());
            }
        }

    }
}
