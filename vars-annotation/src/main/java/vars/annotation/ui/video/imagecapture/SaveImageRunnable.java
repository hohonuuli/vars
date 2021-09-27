package vars.annotation.ui.video.imagecapture;

import mbarix4j.awt.image.ImageUtilities;
import vars.avplayer.ImageCaptureException;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Brian Schlining
 * @since 2013-02-15
 */
public class SaveImageRunnable implements Runnable {

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

        try {
            ImageUtilities.saveImage(renderedImage, target);
        }
        catch (IOException ex) {
            throw new ImageCaptureException("An error occured while trying to write to " +
                    target.getAbsolutePath(), ex);
        }
    }
}
