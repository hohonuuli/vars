package vars.shared.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by brian on 5/1/14.
 */
public class ImageUtilities {

    /**
     * AVFoundation and JavaFX write images asynchronously. We need to block and watch for them to be created.
     * Lame, but even using Java Future's forces us to block. This method blocks for a maximum of 3 seconds.
     *
     * @param file The file to watch
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static BufferedImage watchForAndReadNewImage(File file) throws IOException, InterruptedException {
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
