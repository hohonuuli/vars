package vars.avplayer.jfx;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.javafx.JFXVideoIO;
import org.mbari.vcr4j.javafx.JFXVideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2016-03-25T13:19:00
 */
public class JFXVideoPlayer implements VideoPlayer<JFXVideoState, SimpleVideoError> {

    private static final List<String> ACCEPTABLE_MIMETYPES = Arrays.asList("video/mp4");
    private volatile JFXMovieJFrame movieFrame = new JFXMovieJFrame();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private volatile JFXMovieJFrameController controller;

    @Override
    public boolean canPlay(String mimeType) {
        return ACCEPTABLE_MIMETYPES.stream()
                .filter(s -> s.equalsIgnoreCase(mimeType))
                .count() > 0;
    }

    @Override
    public Optional<VideoController<JFXVideoState, SimpleVideoError>> connect(Object... args) {
        if ((args.length != 1) && (args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. The argument is the " +
                    "string URL to the movie to open with JavaFX");
        }

        AtomicReference<VideoController<JFXVideoState, SimpleVideoError>> videoControllerRef =
                new AtomicReference<>();

        String movieLocation = (String) args[0];
        try {

            movieFrame.setVisible(false);
            movieFrame.setMediaLocation(movieLocation, c -> {
                controller = c;

                MediaPlayer mediaPlayer = c.getMediaView().getMediaPlayer();
                Media media = mediaPlayer.getMedia();

                // -- Configure VideoIO
                JFXVideoIO videoIO = new JFXVideoIO(mediaPlayer);
                new StatusDecorator<>(videoIO);   // Some commands should immediatly send a status request
                new VCRSyncDecorator<>(videoIO);  // Send state/index commands at regular intervals

                videoControllerRef.set(new VideoController<>(new JFXImageCaptureService(controller), videoIO));
                SwingUtilities.invokeLater(() -> {
                    movieFrame.setSize(media.getWidth(), media.getHeight());
                    movieFrame.setVisible(true);
                });

            });

        }
        catch (Exception e) {
            log.error("Failed to create JFXMovieJFrame", e);
        }

        return Optional.ofNullable(videoControllerRef.get());
    }

    @Override
    public VideoPlayerDialogUI getConnectionDialog() {
        return null;
    }
}
