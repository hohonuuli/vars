package vars.avplayer.vlcj.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    private static Map<String, VideoStage> namedWindows = new ConcurrentHashMap<>();

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

    /**
     * A demo url to try: http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8
     *
     * My local test files on external SSD:
     *  - /Volumes/LBD2/i2map/4k_ProRes_HQ_original.mov (playback is jerky)
     *  - /Volumes/LBD2/i2map/h264_test.mp4 (decent playback)
     *  - /Volumes/LBD2/i2map/h265_test.mp4 (actually seems really good)
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //System.setProperty("VLC_PLUGIN_PATH", "/Applications/VLC.app/Contents/MacOS/plugins/");
        //System.setProperty("jna.library.path", "/Applications/VLC.app/Contents/MacOS/lib/");
        boolean found = new NativeDiscovery().discover();
        if (found) {
            System.out.println("Using VLC version: " + LibVlc.INSTANCE.libvlc_get_version());
        }

        String a = args[0];

        Consumer<VideoStage> fn = videoStage -> {
            videoStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
                System.exit(0);
            });
            videoStage.show();
        };

        if (a.startsWith("http:")) {
            namedWindow(a).thenAccept(fn);
        }
        else {
            File f = new File(a);
            namedWindow(f.getAbsolutePath()).thenAccept(fn);
        }
    }

}
