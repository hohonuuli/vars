/*
 * @(#)ImageCaptureAction.java   2013.02.15 at 11:04:20 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.video.imagecapture;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.io.FileUtilities;
import org.mbari.util.NumberUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.CameraDirections;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationCmd;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

/**
 * @author Brian Schlining
 * @since 2013-02-15
 */
public class ImageCaptureAction extends ActionAdapter {

    private Logger log = LoggerFactory.getLogger(getClass());
    private final ImageDirectory imageDirectory;

    /**
     * This class does all the heavy lifting.
     */
    private final ToolBelt toolBelt;

    /**
     * Constructor for the FrameCaptureAction object
     *
     * @param toolBelt
     */
    public ImageCaptureAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        this.imageDirectory = new ImageDirectory();
        putValue(Action.NAME, "Frame Capture");
        putValue(Action.ACTION_COMMAND_KEY, "frame capture");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     */
    public void capture() {

        // --- Step 1: Verify that all the needed services and objects exist
        // Verify that we have the services needed to capture an image
        VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher()
            .getValueObject();
        if (videoControlService == null) {
            EventBus.publish(Lookup.TOPIC_WARNING, "You are not connected to the VCR. Unable to capture a frame.");

            return;
        }

        ImageCaptureService imageCaptureService = (ImageCaptureService) Lookup.getImageCaptureServiceDispatcher()
            .getValueObject();
        if (imageCaptureService == null) {
            EventBus.publish(Lookup.TOPIC_WARNING, "No image capture service is available for frame capture");

            return;
        }

        // Get the image archive we're adding to.
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive == null) {
            EventBus.publish(Lookup.TOPIC_WARNING,
                             "No video-archive is open for annotating. Unable to capture an image.");

            return;
        }

        // --- Step 2: Get Video timecode and recordedDate
        final SnapTime snapTime = getSnapTime(videoControlService);

        // --- Step 3: Get location to write original PNG file to
        File png = null;
        try {
            png = new File(imageDirectory.getImageDirectory(), snapTime.getTimeCodeAsName() + ".png");
        }
        catch (final Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            log.error("Unable to create or write to the image directory", e);

            return;
        }

        // --- Step 4: Save PNG
        final BufferedImage bufferedImage = grabImageAndSavePng(imageCaptureService, png, snapTime);

        // --- Step 5: Create JPG and add annotation off main thread
        final File pngFile = png;
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                JPGPreviewUtilities.createCommentFile(pngFile, snapTime);
                final String[] overlayText = JPGPreviewUtilities.createOverlayText(pngFile, snapTime);
                JPGPreviewUtilities.createOverlayFile(pngFile, overlayText);
                File jpg = null;
                try {
                    jpg = FileUtilities.changeExtension(pngFile, ".jpg");
                    JPGPreviewUtilities.createJpgWithOverlay(bufferedImage, jpg, overlayText);
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    log.error("Failed to write jpg preview file to " + jpg.getAbsolutePath(), e);

                    return;
                }
                updateVideoArchive(snapTime, jpg);
            }
        };
        (new Thread(runnable, "JPEG-Generation-" + snapTime.getObservationDate().getTime())).run();

    }

    /**
     */
    @Override
    public void doAction() {
        if (isAvailable()) {
            capture();
        }
    }

    private SnapTime getSnapTime(VideoControlService videoControlService) {
        final String timecode = videoControlService.getVcrTimecode().toString();

        Date recordedDate;
        if (videoControlService.getVcrState().isRecording()) {
            recordedDate = new Date();
        }
        else {
            /*
             *  Try to grab the userbits off of the tape. The userbits
             *  may have the time that the frame was recorded stored as a
             *  little-endian 4-byte int.
             */
            videoControlService.requestUserbits();
            final int epicSeconds = NumberUtilities.toInt(videoControlService.getVcrUserbits().getUserbits(), true);
            recordedDate = new Date((long) epicSeconds * 1000L);
        }

        return new SnapTime(new Date(), recordedDate, timecode);

    }

    private BufferedImage grabImageAndSavePng(ImageCaptureService imageCaptureService, File png, SnapTime snapTime) {

        if (log.isDebugEnabled()) {
            log.debug("Attempting to save an image to " + png.getAbsolutePath());
        }

        Image image;
        BufferedImage bufferedImage;

        if (imageCaptureService.isPngAutosaved()) {
            image = imageCaptureService.capture(png);
            bufferedImage = ImageUtilities.toBufferedImage(image);
        }
        else {
            image = imageCaptureService.capture(snapTime.getTimeCodeAsString());
            bufferedImage = ImageUtilities.toBufferedImage(image);
            Runnable saveRunnable = new SaveImageRunnable(bufferedImage, png);
            (new Thread(saveRunnable, "ImageIO-" + png.getName())).run();
        }

        return bufferedImage;

    }

    /**
     *  Gets the available attribute of the FrameCaptureAction object
     *
     * @return  true if a frame-capture card is installed and accessable
     */
    public boolean isAvailable() {
        return Lookup.getVideoControlServiceDispatcher().getValueObject() != null;
    }

    /**
     * Populate a videoFrame with the correct information
     */
    private void updateVideoArchive(SnapTime snapTime, File jpg) {

        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive != null) {

            CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher()
                .getValueObject();
            final String cameraDirection = cameraDirections.getDirection();

            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            String user = (userAccount == null) ? UserAccount.USERNAME_DEFAULT : userAccount.getUserName();
            String imageReference = null;

            try {
                imageReference = jpg.toURI().toURL().toExternalForm();
            }
            catch (final MalformedURLException e) {
                if (log.isErrorEnabled()) {
                    log.error("Problem creating a URL.", e);
                }
            }

            // FIXME physical object is hardcoded. Should be set in preferences
            Command command = new AddObservationCmd("physical object", snapTime.getTimeCodeAsString(),
                snapTime.getRecordedDate(), videoArchive.getName(), user, cameraDirection, null, imageReference, true);
            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);
        }

    }

}
