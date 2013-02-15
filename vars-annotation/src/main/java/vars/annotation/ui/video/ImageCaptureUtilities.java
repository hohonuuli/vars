/*
 * @(#)ImageCaptureUtilities.java   2010.05.03 at 10:09:02 PDT
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



package vars.annotation.ui.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import org.mbari.awt.image.ImageUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;

/**
 *
 *
 * @author         Brian Schlining [brian@mbari.org]
 */
@Deprecated
public class ImageCaptureUtilities {

    private static final Logger log = LoggerFactory.getLogger(ImageCaptureUtilities.class);

    /**
     * Capture and image from the grabber. The image is written to disk in a
     * background thread so the method doesn't block while an image is written.
     *
     * @param captureService The grabber to use to capture the image
     * @param timecode
     * @param file The name of the file to save the image to.
     * @return An AWT image object of the captured image.
     *
     *
     * @throws ImageCaptureException
     */
    public static Image capture(final ImageCaptureService captureService, String timecode, final File file)
            throws ImageCaptureException {

        Image image = null;

        if (log.isDebugEnabled()) {
            log.debug("Grabbing a frame");
        }

        try {

            /*
             * Grab an Image from the videoChannel
             */
            image = captureService.capture(timecode);
            Runnable saveRunnable = new SaveImageRunnable(ImageUtilities.toBufferedImage(image), file);
            (new Thread(saveRunnable, "ImageIO-" + file.getName())).run();

        }
        catch (Exception ex) {
            throw new ImageCaptureException("Failed to create " + file.getAbsolutePath(), ex);
        }

        return image;
    }

    /**
     * Add overlay text to the image and save as a .jpg file.
     *
     * @param  image        a java.awt.Image to add the text overlay to
     * @param  jpg          Target file to save jpeg to
     * @param  overlayText  The text to overlay onto the image
     * @return
     */
    public static BufferedImage createJpgWithOverlay(final Image image, final File jpg, final String[] overlayText) {

        // Copy BufferedImage and set .jpg file name
        final BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null),
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.drawImage(image, 0, 0, null);
        final Font font = new Font("Monospaced", Font.PLAIN, 14);
        g.setFont(font);
        g.setColor(Color.CYAN);
        final FontRenderContext frc = g.getFontRenderContext();
        int x = 1;
        int n = 1;
        for (String s : overlayText) {
            LineMetrics lineMetrics = font.getLineMetrics(s, frc);
            float y = (lineMetrics.getHeight() + 1) * n + lineMetrics.getHeight();
            g.drawString(s, x, y);
            n++;
        }

        g.dispose();

        Runnable saveRunnable = new SaveImageRunnable(bi, jpg);
        (new Thread(saveRunnable, "ImageIO-" + jpg.getName())).run();

        return bi;
    }

    /**
     * Runnable for saving images in the background off of the current thread
     */
    private static class SaveImageRunnable implements Runnable {

        private final RenderedImage renderedImage;
        private final File target;

        /**
         * Constructs ...
         *
         * @param renderedImage
         * @param target
         */
        public SaveImageRunnable(RenderedImage renderedImage, File target) {
            this.renderedImage = renderedImage;
            this.target = target;
        }

        /**
         */
        public void run() {
            if (log.isDebugEnabled()) {
                log.debug("Saving image to " + target.getAbsolutePath());
            }

            try {
                ImageUtilities.saveImage(renderedImage, target);
            }
            catch (IOException ex) {
                throw new ImageCaptureException("An error occured while trying to write to " +
                                                target.getAbsolutePath(), ex);
            }
        }
    }

}
