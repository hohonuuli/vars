package vars.shared.javafx.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.javafx.stage.ImageStage;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brian Schlining
 * @since 2015-07-17T10:49:00
 */
public class ImageFX {
    private static int TIMEOUT = 5000;
    private static final Logger log = LoggerFactory.getLogger(ImageFX.class);

    private static boolean isJavaFXRunning = false;

    // Used to launch the application before running any test
    private static final CountDownLatch launchLatch = new CountDownLatch(1);
    private static Map<String, ImageStage> namedWindows = new ConcurrentHashMap<>();

    /**
     * ImageFX requires one and only one Application instance per JVM. This class
     * provides a simple default App to use if you want to use ImageFX as a
     * standalone image dispay utility. Otherwise you can can call
     * setJavaFXApplication(app) to use a different one.
     */
    public static class MyApp extends Application {

        Stage primaryStage;

        @Override
        public void init() throws Exception {
            isJavaFXRunning = true;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle(getClass().getSimpleName());
            this.primaryStage = primaryStage;
            launchLatch.countDown();
        }

        @Override
        public void stop() throws Exception {
            isJavaFXRunning = false;
            super.stop();
        }
    }

    public static void setIsJavaFXRunning(boolean isJavaFXRunning) {
        ImageFX.isJavaFXRunning = isJavaFXRunning;
    }

    /**
     * Manages the JavaFX life-cycle. We need to ensure a JavaFX app is running
     * before we try to start opening new Stages.
     */
    private static void startJavaFX() {
        if (isJavaFXRunning == false) {
            new Thread(() -> {
                try {
                    Application.launch(MyApp.class, "");
                }
                catch (Exception e) {
                    log.info("JavaFX did not launch app. A JavaFX app may already be running", e);
                }
            }).start();

            try {
                if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Timeout waiting for Application to launch");
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Timeout waiting for Application to launch");
            }
        }
    }

    public static CompletableFuture<ImageStage> namedWindow(String title, Image image) {
        return namedWindow(title, image, false);
    }

    public static CompletableFuture<ImageStage> namedWindow(String title, String imageLocation, boolean showTooltip) {
        startJavaFX();
        final CompletableFuture<ImageStage> imageStageFuture = new CompletableFuture<>();
        runOnJavaFXThread(() -> {
            try {
                Image image = new Image(imageLocation);
                CompletableFuture<ImageStage> isF = namedWindow(title, image, showTooltip);
                isF.thenAccept(imageStageFuture::complete);
            } catch (Exception e) {
                // TODO Set a failed to load image
                imageStageFuture.completeExceptionally(e);
                log.warn("Failed to open " + imageLocation, e);
            }
        });
        return imageStageFuture;
    }

    public static CompletableFuture<ImageStage> namedWindow(String title, String imageLocation) {
        return namedWindow(title, imageLocation, false);
    }

    public static CompletableFuture<ImageStage> namedWindow(String title, Image image, boolean showTooltip) {
        startJavaFX();
        final CompletableFuture<ImageStage> imageStageFuture = new CompletableFuture<>();
        runOnJavaFXThread(() -> {
            ImageStage stage;
            if (namedWindows.containsKey(title)) {
                stage = namedWindows.get(title);
            }
            else {
                stage = new ImageStage();
                namedWindows.put(title, stage);
            }

            stage.setTitle(title);
            stage.setImage(image);

            if (showTooltip) {

                BorderPane root =  stage.getRoot();
                final Text text = new Text("100, 100");
                text.setLayoutX(100);
                text.setLayoutX(100);
                text.setFill(Color.WHITE); // WHITE + BlendMode.DIFFERENCE = XOR
                text.setBlendMode(BlendMode.DIFFERENCE);

                root.getChildren().add(text);

                root.setOnMouseMoved(e -> {
                    String msg = "";
                    Point2D scenePoint = new Point2D(e.getSceneX(), e.getSceneY());
                    Point2D imagePoint = stage.convertToImage(scenePoint);
                    msg = String.format("%.1f, %.1f", imagePoint.getX(), imagePoint.getY());

                    text.setLayoutX(e.getX());
                    text.setLayoutY(e.getY());
                    text.setText(msg);
                });

            }

            imageStageFuture.complete(stage);
        });
        return imageStageFuture;
    }

    /**
     * Get a handle to a Stage that has already been created. This call does not create
     * a new Stage, it only grabs a reference to ones that have already been created.
     * @param title The named window to find
     * @return If the window exists, the optional will contain a reference to the iamge
     */
    public static Optional<ImageStage> getNamedWindow(String title) {
        return Optional.ofNullable(namedWindows.get(title));
    }


    private static void runOnJavaFXThread(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        }
        else {
            Platform.runLater(r);
        }
    }

}
