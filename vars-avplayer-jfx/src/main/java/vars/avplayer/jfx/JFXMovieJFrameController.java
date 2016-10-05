package vars.avplayer.jfx;

/**
 * Created by brian on 4/29/14.
 */
import com.google.common.base.Preconditions;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.vcr4j.time.Timecode;
import vars.shared.ui.GlobalStateLookup;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class JFXMovieJFrameController implements Initializable {

    public static final Double DEFAULT_FRAME_RATE = 100D;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField maxTimecodeTextField;

    @FXML
    private Slider scrubber;

    @FXML
    private MediaView mediaView;

    @FXML
    private TextField timecodeTextField;

    @FXML
    private Button playButton;



    private MediaPlayer mediaPlayer;

    private BooleanProperty readyProperty = new SimpleBooleanProperty(false);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaLocation(String mediaLocation, Consumer<JFXMovieJFrameController> onReadyRunnable) {

        Preconditions.checkNotNull(mediaLocation, "The medialocation can not be null");

        scrubber.setDisable(true);
        Media media = null;
        try {
            media = new Media(mediaLocation);
        }
        catch (MediaException e) {
            EventBus.publish(GlobalStateLookup.TOPIC_WARNING, "Media type is not supported: " + mediaLocation);
        }
        //final ObservableMap<String,Object> metadata = media.getMetadata();
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        // --- Configure MediaPlayer
        mediaPlayer.currentTimeProperty().addListener(observable -> updateValues());

        mediaPlayer.setOnPlaying(() -> { playButton.setText("||"); });

        mediaPlayer.setOnPaused(() -> { playButton.setText(">"); });

        mediaPlayer.setOnReady(() -> {
            Media m = mediaPlayer.getMedia();
            Timecode timecode = JFXUtilities.jfxDurationToTimecode(m.getDuration());
            SwingUtilities.invokeLater(() -> maxTimecodeTextField.setText(timecode.toString()));
            updateValues();
            onReadyRunnable.accept(this);
            readyProperty.setValue(true);
        });

        mediaPlayer.setOnEndOfMedia(() -> { playButton.setText(">"); });

        // ---  Configure play button
        playButton.setOnAction((e) -> {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            System.out.println(status);
            if (status == MediaPlayer.Status.UNKNOWN ||  status == MediaPlayer.Status.HALTED) {
                // Do nothing
                return;
            }

            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            }
            else if (status == MediaPlayer.Status.PAUSED
                    || status == MediaPlayer.Status.READY
                    || status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            }

        });

        // --- Configure Scrubber
        scrubber.valueProperty().addListener(observable -> {
            if (scrubber.isValueChanging()) {
                Media m = mediaPlayer.getMedia();
                // multiply duration by percentage calculated by slider position
                mediaPlayer.seek(m.getDuration().multiply(scrubber.getValue() / 100D));
            }
            else {
                updateValues();
            }

        });

    }


    public boolean isReady() {
        return readyProperty.get();
    }

    public BooleanProperty readyProperty() {
        return readyProperty;
    }


    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public BufferedImage frameCapture(File target) throws IOException, InterruptedException, TimeoutException, ExecutionException {

        BufferedImage bufferedImage = null;
        if (mediaView != null) {
            Platform.runLater(() -> {
                WritableImage image = mediaView.snapshot(new SnapshotParameters(), null);
                System.out.println("Saving image to " + target.getAbsolutePath());
                try {
                    ImageUtilities.saveImage(SwingFXUtils.fromFXImage(image, null), target);
                } catch (IOException e) {
                    // TODO fix exception handling
                    System.out.println("Failed to write " + target.getAbsolutePath());
                }
            });

            /*
                We are writing the image asynchronously (i.e. Platform.runLater()). Since we're not using any RX
                frameworks, we have to poll the image to see if it's done being written. If we try to read it while
                it's in a partially written state then we will get:

                java.lang.IndexOutOfBoundsException: null
                    at java.io.RandomAccessFile.readBytes(Native Method) ~[na:1.8.0_05]
                    at java.io.RandomAccessFile.read(RandomAccessFile.java:349) ~[na:1.8.0_05]
                    at javax.imageio.stream.FileImageInputStream.read(FileImageInputStream.java:117) ~[na:1.8.0_05]
                    at ...

             */
            java.time.Duration timeout = java.time.Duration.ofSeconds(8);
            CompletableFuture<BufferedImage> imageFuture = vars.shared.ui.ImageUtilities.readImageAsync(target, timeout);
            bufferedImage = imageFuture.get(timeout.getSeconds(), TimeUnit.SECONDS);

            if (bufferedImage == null) {
                // TODO report error
                //EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, failure);
            }


        }

        return bufferedImage;
    }

    protected  void updateValues() {
        if (timecodeTextField != null && scrubber != null && mediaPlayer != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                Duration totalTime = mediaPlayer.getMedia().getDuration();
                Timecode timecode = JFXUtilities.jfxDurationToTimecode(currentTime);
                timecodeTextField.setText(timecode.toString());
                scrubber.setDisable(totalTime.isUnknown());
                if (!scrubber.isDisabled() && totalTime.greaterThan(Duration.ZERO) && !scrubber.isValueChanging()) {
                    scrubber.setValue(currentTime.divide(totalTime.toMillis()).toMillis() * 100D);
                }
            });
        }
    }
}

