package vars.shared.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.CompletableFuture;

/**
 * Created by brian on 5/1/14.
 */
public class ImageUtilities {

    /**
     * AVFoundation and JavaFX write images asynchronously. We need to block and watch for them to be created.
     * Lame, but even using Java Future's forces us to block. This method blocks for a maximum of 3 seconds.
     * TODO modify method to return CompleteableFuture
     *
     * @param file The file to watch
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static BufferedImage watchForAndReadNewImage(File file) throws IOException, InterruptedException {
        return watchForAndReadNewImage(file, Duration.ofSeconds(3L));
    }

    public static BufferedImage watchForAndReadNewImage(File file, Duration timeout) throws IOException, InterruptedException {
        BufferedImage image = null;
        long timeoutNanos = timeout.toNanos();
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

    public static CompletableFuture<BufferedImage> readImageAsync(File file, Duration timeout) throws IOException, InterruptedException {
        CompletableFuture<BufferedImage> future = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            int readAttempts = 10;
            for (int i = 0; i < readAttempts; i++) {
                try {
                    if (i > 0) {
                        Thread.sleep(timeout.toMillis() / readAttempts);
                    }
                    BufferedImage bufferedImage = watchForAndReadNewImage(file, timeout.dividedBy(readAttempts));
                    if (bufferedImage != null) {
                        future.complete(bufferedImage);
                        break;
                    }
                }
                catch (IndexOutOfBoundsException | InterruptedException | IOException e) {
                    /*
                        We are writing the image asynchronously (e.g. Platform.runLater()). Since we're not using any RX
                        frameworks, we have to poll the image to see if it's done being written. If we try to read it while
                        it's in a partially written state then we will get:

                        java.lang.IndexOutOfBoundsException: null
                            at java.io.RandomAccessFile.readBytes(Native Method) ~[na:1.8.0_05]
                            at java.io.RandomAccessFile.read(RandomAccessFile.java:349) ~[na:1.8.0_05]
                            at javax.imageio.stream.FileImageInputStream.read(FileImageInputStream.java:117) ~[na:1.8.0_05]
                            at ...

                     */
                }
            }
        }, ImageUtilities.class.getSimpleName() + "-" + Instant.now().toString());
        thread.start();
        return future;

    }
}
