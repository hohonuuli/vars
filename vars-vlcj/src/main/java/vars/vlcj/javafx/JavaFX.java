package vars.vlcj.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Brian Schlining
 * @since 2015-07-01T14:15:00
 */
public class JavaFX {

    private static int TIMEOUT = 5000;
    private static final Logger log = LoggerFactory.getLogger(JavaFX.class);

    private static Application myApp;

    // Used to launch the application before running any test
    private static final CountDownLatch launchLatch = new CountDownLatch(1);
    private static Map<String, VideoStage> namedWindows = new HashMap<>();

    /**
     * JavaFX requires one and only one Application instance per JVM. This class
     * provides a simple default App to use if you want to use JavaFX as a
     * standalone image dispay utility. Otherwise you can can call
     * setJavaFXApplication(app) to use a different one.
     */
    public static class MyApp extends Application {

        Stage primaryStage;

        @Override
        public void init() throws Exception {
            JavaFX.myApp = this;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle(getClass().getSimpleName());
            this.primaryStage = primaryStage;
            launchLatch.countDown();
        }

        @Override
        public void stop() throws Exception {
            JavaFX.myApp = null;
            super.stop();
        }
    }

    /**
     * If you are using this from an already running JavaFX Application. You'll
     * need to set the parent app so that it doesn't try to create one for you.
     *
     * @param app The JavaFX application
     */
    public static void setJavaFXApplication(Application app) {
        myApp = app;
    }

    public static Application getJavaFXApplication() {
        return myApp;
    }

    /**
     * Manages the JavaFX life-cycle. We need to ensure a JavaFX app is running
     * before we try to start opening new Stages.
     */
    private static void startJavaFX() {
        if (myApp == null) {
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

    /**
     * Opens a resizable window with the given title and image.
     * @param pathToVideo The path to the video.
     * @return A future that will contain a reference to the VideoStage when the
     *         stage has finished initialization.
     *
     */
    public static CompletableFuture<VideoStage> namedWindow(final String pathToVideo) {
        startJavaFX();
        final CompletableFuture<VideoStage> videoStageFuture = new CompletableFuture<>();
        runOnJavaFXThread(() -> {
            VideoStage stage;
            if (namedWindows.containsKey(pathToVideo)) {
                stage = namedWindows.get(pathToVideo);
            }
            else {
                stage = new VideoStage(pathToVideo);
                stage.setTitle(pathToVideo);
                namedWindows.put(pathToVideo, stage);
                stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
                    namedWindows.remove(pathToVideo);
                    stage.close();
                });
            }
            videoStageFuture.complete(stage);
        });
        return videoStageFuture;
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
