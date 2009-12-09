package vars.annotation.ui.video;

import ij.IJ;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageCaptureUtilities {
    
    private static final Logger log = LoggerFactory.getLogger(ImageCaptureUtilities.class);
    
    
    /**
     * Add overlay text to the image and save as a .jpg file.
     * Show the image in a popup frame too.  Uses ImageJ API
     * for the text overlay code, specifically {@link
     * ij.process.ImageProcessor#drawString ij.process.ImageProcessor#drawString}.
     *
     * @param  image        a java.awt.Image to add the text overlay to
     * @param  jpg          Description of the Parameter
     * @param  overlayText  Description of the Parameter
     */
    public static void createJpgWithOverlay(final Image image, final File jpg,
            final String[] overlayText) {
        if (IJ.versionLessThan("1.17s")) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "Unable to complete this operation. You are running a " +
                        "version of imagej less than 1.17s. Upgrade imagej!");
            }
            
            return;
        }
        
        final ImageProcessor ip = new ColorProcessor(image);
        ip.setColor(Color.cyan);
        int x = 1;
        int y = 1;
        ip.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        for (int i = 0; i < overlayText.length; i++) {
            y += 14;
            ip.moveTo(x, y);
            ip.drawString(overlayText[i] + "");
        }
        
        // Get BufferedImage and set .jpg file name
        final BufferedImage bi = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        final Graphics g = bi.getGraphics();
        g.drawImage(ip.createImage(), 0, 0, null);
        g.dispose();
        
        // Save as a jpg using ImageIO
        try {
            ImageIO.write(bi, "jpg", jpg);
            if (log.isDebugEnabled()) {
                log.debug("Created " + jpg.getAbsolutePath());
            }
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to create " + jpg.getAbsolutePath(), e);
            }
        }
    }

}
