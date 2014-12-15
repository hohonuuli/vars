package vars.avfoundation;

import org.bushe.swing.event.EventBus;
import org.mbari.nativelib.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.dialogs.StandardDialog;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created with IntelliJ IDEA.
 * User: brian
 * Date: 11/25/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class AVFImageCaptureServiceImpl implements ImageCaptureService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String LIBRARY_NAME = "avfimagesnap";
    private volatile boolean isStarted = false;
    private StandardDialog dialog;
    File tempDir = new File(System.getProperty("java.io.tmpdir"));

    public AVFImageCaptureServiceImpl() {
        try {
            System.loadLibrary(LIBRARY_NAME);
            log.info(LIBRARY_NAME + " was found on the java.library.path and loaded");
        }
        catch (UnsatisfiedLinkError e) {
            extractAndLoadNativeLibraries();
        }
        stopDevice();
    }

    ////////////////////////
    // Start of Natives   //
    ////////////////////////
    /**
     * This is a JNI method that returns an array of strings that are the names of
     * the various video devices available on the underlying platform
     *
     * @return An array of <code>Strings</code> that echos back the names of
     * video devices available.
     */
    public native String[] videoDevicesAsStrings();

    /**
     * This is a JNI method that provides a device Name to begin a Capture Session
     * with on the underlying platform.
     *
     * @param deviceName is a String that specifies the device that is to be
     * used for the capture session
     *
     * @return A <code>String</code> that echos back the device name that is
     * to be used for the session.
     */
    public native String startSessionWithNamedDevice(String deviceName);


    /**
     * This is a JNI method that provides a Path to save an image to as a file.
     * Available on the underlying platform.
     *
     * @param specifiedPath is a String that specifies the path/filename to save
     * the captured image to.
     *
     * @return A <code>String</code> that echos back the path/filename that the
     * image is to be saved as.
     */
    public native String saveSnapshotToSpecifiedPath(String specifiedPath);


    /**
     * This is a JNI method that stops the Capture Session currently running.
     * Available on the underlying platform.
     *
     * @return
     */
    public native void stopSession();

    /////////////////////
    // End of Natives  //
    /////////////////////

    @Override
    public boolean isPngAutosaved() {
        return true;
    }

    @Override
    public Image capture(File file) throws ImageCaptureException {
        if (!isStarted) {
            startDevice();
        }

        saveSnapshotToSpecifiedPath(file.getAbsolutePath());

        // -- Read file as image
        BufferedImage image = null;
        try {
            image = watchForAndReadNewImage(file);
        } catch (Exception e) {
            EventBus.publish(GlobalLookup.TOPIC_WARNING, e);
        }

        return image;
    }

    @Override
    public Image capture(String timecode) throws ImageCaptureException {
        if (!isStarted) {
            startDevice();
        }

        // -- Save to temp file png.
        final File tempFile = new File(tempDir, LIBRARY_NAME + (new Date()).getTime() + ".png");
        Image image = capture(tempFile);

        // -- Delete the temp file in the background
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                tempFile.delete();
            }
        }, "DELETE-" + tempFile.getName());
        thread.run();

        return image;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void showSettingsDialog() {
        Preferences preferences = Preferences.userRoot();
        String originalVideoSrc = preferences.get(LIBRARY_NAME, "");
        if (dialog == null) {
            Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();

            // --- start HACK: Always add "Blackmagic" to video device list (brian 2014-03-04)
            // In Mac OS X 10.9.2, Blackmagic stopped showing up in the videodevice list
            String[] listedDevices = videoDevicesAsStrings();
            boolean hasBlackmagic = false;
            for (String s: listedDevices) {    // Check if list contains Blackmagic
                if (s.equals("Blackmagic")) {
                    hasBlackmagic = true;
                    break;
                }
            }

            String[] devices;                  // If needed, add Blackmagic to list
            if (!hasBlackmagic) {
                devices = new String[listedDevices.length + 1];
                System.arraycopy(listedDevices, 0, devices, 0, listedDevices.length);
                devices[devices.length - 1] = "Blackmagic";
            }
            else {
                devices = listedDevices;
            }
            Arrays.sort(devices);
            // --- end HACK

            dialog = new AVFImageCaptureDialog(frame, devices);
        }
        dialog.setVisible(true);

        // Stop session if the video source has been changed
        String newVideoSrc = preferences.get(LIBRARY_NAME, "");
        if (!newVideoSrc.equals(originalVideoSrc)) {
            stopDevice();
            startDevice();
        }

    }

    private void startDevice() {
        Preferences preferences = Preferences.userRoot();
        String videoSource = preferences.get(LIBRARY_NAME, "");
        if (isStarted) {
            EventBus.publish(GlobalLookup.TOPIC_WARNING, "The video device '" +
                    videoSource + "' is already opened");
        }
        else if (!videoSource.isEmpty()) {
            log.debug("Starting image capture service, {}, using {}", getClass().getName(), videoSource);
            startSessionWithNamedDevice(videoSource);
            isStarted = true;
        }
        else {
            EventBus.publish(GlobalLookup.TOPIC_WARNING, "A video source has not been selected");
        }
    }

    private void stopDevice() {
        log.debug("Stopping image capture service, {}", getClass().getName());
        stopSession();
        isStarted = false;
    }

    private void extractAndLoadNativeLibraries() {

        String libraryName = System.mapLibraryName(LIBRARY_NAME);
        String os = System.getProperty("os.name");

        if (libraryName != null) {

            // Location to extract the native libraries into $HOME/.vars/native/Mac
            File libraryHome = new File(new File(GlobalLookup.getSettingsDirectory(), "native"), os.substring(0, 3));
            if (!libraryHome.exists()) {
                libraryHome.mkdirs();
            }

            if (!libraryHome.canWrite()) {
                throw new VARSException("Unable to extract native '" + LIBRARY_NAME +
                        "' library to " + libraryHome +
                        ". Verify that you have write access to that directory");
            }

            // This finds the native library, extracts it and hacks the java.library.path if needed
            new Native(LIBRARY_NAME, "native", libraryHome, getClass().getClassLoader());

        }
        else {
            log.error( "A native '" + LIBRARY_NAME + "' library for your platform is not available. " +
                    "You will not be able to use AVFoundation to capture images");
        }

    }

    /**
     * AVFoundation writes the image asynchronously. We need to block and watch for them to be created.
     * Lame, but even using Java Future's forces us to block.
     * @param file
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private BufferedImage watchForAndReadNewImage(File file) throws IOException, InterruptedException {
        BufferedImage image = null;
        long timeoutNanos = 3000000000L; // 3 seconds
        long elapsedNanos = 0L;
        long startNanos = System.nanoTime();
        while (elapsedNanos < timeoutNanos) {
            if (file.exists()) {
                break;
            }
            Thread.sleep(50L);
            elapsedNanos = System.nanoTime() - startNanos;
        }
        if (file.exists()) {
            image = ImageIO.read(file);
        }
        return image;
    }
}
