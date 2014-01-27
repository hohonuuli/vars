package vars.annotation.ui.videofile.jfxmedia;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.movie.Timecode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * TODO scrubber does not work
 *
 * TODO fix teh play button
 *
 * TODO screen needs to resize to size of video when it's open.
 *
 * TODO add resizing of video based on user changing the frame size
 *
 * Created by brian on 12/16/13.
 */
public class JFXMovieFrameController implements Initializable {

    public static final Double DEFAULT_FRAME_RATE = 30D;

    @FXML
    private TextField timecodeTextField;

    /**
     * To get the mediaPlayer just call mediaView.getMediaPlayer()
     */
    @FXML
    private MediaView mediaView;

    @FXML
    private Slider scrubber;

    @FXML
    private ImageView buttonImage;

    private MediaPlayer mediaPlayer;
    private final StringProperty timecodeProperty = new SimpleStringProperty("--:--:--:--");
    private final Timecode timecode = new Timecode();
    private ChangeListener<Duration> timeListener = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timecodeTextField.textProperty().bind(timecodeProperty);
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public TextField getTimecodeTextField() {
        return timecodeTextField;
    }

    public Slider getScrubber() { return scrubber; }

    public void setMediaLocation(String mediaLocation) {
        resetMediaView();

        Media media = new Media(mediaLocation);
        final ObservableMap<String,Object> metadata = media.getMetadata();
        timecode.setFrameRate((Double) metadata.getOrDefault("framerate", DEFAULT_FRAME_RATE));
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.setFitHeight(media.getHeight());
        mediaView.setFitWidth(media.getWidth());

        scrubber.setMin(0);
        scrubber.setMax(media.getDuration().toSeconds());

        timeListener = (ChangeListener<Duration>) (observableValue, duration, duration2) -> {
            double frames = duration2.toSeconds() * timecode.getFrameRate();
            timecode.setFrames(frames);
            timecodeProperty.setValue(timecode.toString());
            scrubber.adjustValue(duration2.toSeconds());
        };


        mediaPlayer.currentTimeProperty().addListener(timeListener);

        mediaPlayer.onPlayingProperty().addListener(new ChangeListener<Runnable>() {
            @Override
            public void changed(ObservableValue<? extends Runnable> observableValue, Runnable runnable, Runnable runnable2) {

            }
        });

    }

    private void resetMediaView() {
        if (mediaPlayer != null) {
            if (timeListener != null) {
                mediaPlayer.currentTimeProperty().removeListener(timeListener);
            }
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
                    System.out.println("Failed to write " + target.getAbsolutePath());
                }
            });

        }
    }

}
