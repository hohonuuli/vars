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

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.io.FileUtilities;
import org.mbari.vcr4j.VideoIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.VARSException;
import vars.annotation.CameraDirections;
import vars.annotation.VideoArchive;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationCmd;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoController;

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
        VideoController videoController = StateLookup.getVideoController();
        if (videoController == null) {
            EventBus.publish(StateLookup.TOPIC_WARNING, "You are not connected to a video. Unable to capture a frame.");

            return;
        }

        ImageCaptureService imageCaptureService = videoController.getImageCaptureService();
        if (imageCaptureService == null) {
            EventBus.publish(StateLookup.TOPIC_WARNING, "No image capture service is available for frame capture");

            return;
        }

        // Get the image archive we're adding to.
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        if (videoArchive == null) {
            EventBus.publish(StateLookup.TOPIC_WARNING,
                             "No video-archive is open for annotating. Unable to capture an image.");

            return;
        }

        // --- Step 2: Get Video timecode and recordedDate
        final SnapTime snapTime = getSnapTime(videoController);


        // --- Step 3: Get location to write original PNG file to
        File png = null;
        try {
            png = new File(imageDirectory.getImageDirectory(), snapTime.getFramegrabName() + ".png");
        }
        catch (final Exception e) {
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            log.error("Unable to create or write to the image directory", e);
            return;
        }

        // --- Step 4: Save PNG
        final Optional<BufferedImage> bufferedImageOpt = grabImageAndSavePng(imageCaptureService, png, snapTime);
        if (!bufferedImageOpt.isPresent()) {
            EventBus.publish(StateLookup.TOPIC_WARNING, "No image was captured");
            return;
        }

        // --- Step 5: Create JPG and add annotation off main thread
        final File pngFile = png;
        Runnable runnable = () -> {
            JPGPreviewUtilities.createCommentFile(pngFile, snapTime);
            final String[] overlayText = JPGPreviewUtilities.createOverlayText(pngFile, snapTime);
            JPGPreviewUtilities.createOverlayFile(pngFile, overlayText);
            File jpg = null;
            try {
                jpg = FileUtilities.changeExtension(pngFile, ".jpg");
                JPGPreviewUtilities.createJpgWithOverlay(bufferedImageOpt.get(), jpg, overlayText);
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                log.error("Failed to write jpg preview file to " + jpg.getAbsolutePath(), e);

                return;
            }
            updateVideoArchive(snapTime, jpg);
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

    private SnapTime getSnapTime(VideoController videoController) {

        Instant observationTimestamp = Instant.now();

        try {
            VideoIndex videoIndex = (VideoIndex) videoController.getVideoIndex().get(2, TimeUnit.SECONDS);
            return new SnapTime(observationTimestamp, videoIndex);
        }
        catch (Exception e) {
            throw new VARSException("Failed to acquire SnapTime", e);
        }

    }

    private Optional<BufferedImage> grabImageAndSavePng(ImageCaptureService imageCaptureService, File png, SnapTime snapTime) {

        if (log.isDebugEnabled()) {
            log.debug("Attempting to save an image to " + png.getAbsolutePath());
        }

        final Optional<BufferedImage> image = imageCaptureService.capture(png)
                .map(ImageUtilities::toBufferedImage);

        image.ifPresent(i -> {
            Runnable saveRunnable = new SaveImageRunnable(i, png);
            (new Thread(saveRunnable, "ImageIO-" + png.getName())).run();
        });

        return image;

    }

    /**
     *  Gets the available attribute of the FrameCaptureAction object
     *
     * @return  true if a frame-capture card is installed and accessable
     */
    public boolean isAvailable() {

        return StateLookup.getVideoController() != null;
    }

    /**
     * Populate a videoFrame with the correct information
     */
    private void updateVideoArchive(SnapTime snapTime, File jpg) {

        VideoArchive videoArchive = StateLookup.getVideoArchive();
        if (videoArchive != null) {

            CameraDirections cameraDirections = StateLookup.getCameraDirection();
            final String cameraDirection = cameraDirections.getDirection();

            UserAccount userAccount = StateLookup.getUserAccount();
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

            // FIXME recodedDate may not always be present. null should be inserted if not found
            Date recordedDate = snapTime.getVideoIndex()
                    .getTimestamp()
                    .map(Date::from)
                    .orElse(null);

            // FIXME physical object is hardcoded. Should be set in preferences
            Command command = new AddObservationCmd("physical object",
                    snapTime.getTimecodeString(),
                    recordedDate,
                    videoArchive.getName(),
                    user,
                    cameraDirection,
                    null,
                    imageReference,
                    true);

            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);
        }

    }

}
