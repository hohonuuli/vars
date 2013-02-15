/*
 * @(#)JPGPreviewUtilities.java   2013.02.15 at 09:39:15 PST
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.mbari.io.FileUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.VARSProperties;

/**
 * @author Brian Schlining
 * @since 2013-02-15
 */
public class JPGPreviewUtilities {

    private static final Logger log = LoggerFactory.getLogger(JPGPreviewUtilities.class);
    private static final String imageCopyrightOwner = VARSProperties.getImageCopyrightOwner();

    /**
     * Create .comment file for the image file name
     *
     * @param  png Description of the Parameter
     * @param  snapTime Description of the Parameter
     */
    public static void createCommentFile(final File png, final SnapTime snapTime) {
        final String strAnnoText = "";

        // File comments
        final StringBuffer s = new StringBuffer("Copyright ").append(snapTime.getYear());
        s.append(imageCopyrightOwner).append("\n");
        s.append("File      = ").append(png.getAbsolutePath()).append(" (MAIN)\n");
        s.append("EPOCHsecs = ").append(snapTime.getTimeInSecs()).append("\n");
        s.append("LocalTime = ").append(snapTime.getFormattedLocalTime()).append("\n");
        s.append("UTCTime   = ").append(snapTime.getFormattedGmtTime()).append("\n");
        s.append("YYYYJJJ   = ").append(snapTime.getTrackingNumber()).append("\n");
        s.append("AnnTC     = ").append(snapTime.getTimeCodeAsString()).append("\n");
        s.append("SnapTC    = ").append(snapTime.getTimeCodeAsName()).append("\n");
        s.append("Annotation= ").append(strAnnoText).append("\n");

        if (log.isInfoEnabled()) {
            log.info("strComment = \n" + s.toString());
        }

        /*
         *  Write the comment to a file.
         */
        final File comment = FileUtilities.changeExtension(png, ".comment");
        try {
            final BufferedWriter bwCmt = new BufferedWriter(new FileWriter(comment));
            bwCmt.write(s.toString());
            bwCmt.close();
        }
        catch (final IOException e2) {
            if (log.isErrorEnabled()) {
                log.error("Failed to write " + comment.getAbsolutePath(), e2);
            }
        }
    }

    /**
     * Create .overlay file for the .png file name
     *
     * @param  png Description of the Parameter
     * @param  overlayText Description of the Parameter
     * @return  The File object representing the overlay file. null is returned if
     *          the fiel was not created.
     */
    public static File createOverlayFile(final File png, final String[] overlayText) {
        final StringBuffer text = new StringBuffer("");
        for (int i = 0; i < overlayText.length; i++) {
            text.append(overlayText[i]).append("\n");
        }

        if (log.isInfoEnabled()) {
            log.info("strOverlay = \n" + text.toString());
        }

        /*
         *  Write the overlay file
         */
        File overlay = FileUtilities.changeExtension(png, ".overlay");
        try {
            final BufferedWriter bwOvly = new BufferedWriter(new FileWriter(overlay));
            bwOvly.write(text.toString());
            bwOvly.close();
        }
        catch (final IOException e2) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create overlay file", e2);
            }

            overlay = null;
        }

        return overlay;
    }

    /**
     * Creates the textual overlay for the preview image
     *
     * @param  png Description of the Parameter
     * @param  snapTime Description of the Parameter
     * @return  A string array of ext to be overlaid onto an image.
     */
    public static String[] createOverlayText(final File png, final SnapTime snapTime) {
        final String[] s = new String[4];
        s[0] = "Copyright " + snapTime.getYear() + " " + imageCopyrightOwner;
        s[1] = png.getAbsolutePath() + " (MAIN)";
        s[2] = snapTime.getFormattedGmtTime() + " GMT (local +" +
                snapTime.getGmtOffset().replaceFirst("-", "").replaceAll("0", "") + ")";
        s[3] = "";

        return s;
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


}
