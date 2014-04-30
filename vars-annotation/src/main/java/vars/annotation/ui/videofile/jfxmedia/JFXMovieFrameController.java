package vars.annotation.ui.videofile.jfxmedia;

/**
 * Created by brian on 4/29/14.
 */
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.movie.Timecode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class JFXMovieFrameController implements Initializable {

    public static final Double DEFAULT_FRAME_RATE = 30D;

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

    private volatile MediaPlayer mediaPlayer;
    private final Timecode timecode = new Timecode();
    private Duration duration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public void setMediaLocation(String mediaLocation) {
        resetMediaView();

        Media media = new Media(mediaLocation);
        final ObservableMap<String,Object> metadata = media.getMetadata();
        timecode.setFrameRate((Double) metadata.getOrDefault("framerate", DEFAULT_FRAME_RATE));
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

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
            else if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY ||
                    status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            }

        });

        // --- Configure MediaPlayer
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(() -> { playButton.setText("||"); });

        mediaPlayer.setOnPaused(() -> { playButton.setText(">"); });

        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            Timecode tc = new Timecode(duration.toSeconds() * timecode.getFrameRate());
            maxTimecodeTextField.setText(tc.toString());
            updateValues();
        });


        mediaPlayer.setOnEndOfMedia(() -> { playButton.setText(">"); });

        // --- Configure Scrubber
        scrubber.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (scrubber.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mediaPlayer.seek(duration.multiply(scrubber.getValue() / 100D));
                }
            }
        });

    }

    private void resetMediaView() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaView.setMediaPlayer(null);
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public void frameCapture(File target) throws IOException {

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

        }
    }

    protected  void updateValues() {
        if (timecodeTextField != null && scrubber != null && mediaPlayer != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                double frames = currentTime.toSeconds() * timecode.getFrameRate();
                timecode.setFrames(frames);
                timecodeTextField.setText(timecode.toString());
                scrubber.setDisable(duration.isUnknown());
                if (!scrubber.isDisabled() && duration.greaterThan(Duration.ZERO) && !scrubber.isValueChanging()) {
                    scrubber.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100D);
                }
            });
        }
    }
}

