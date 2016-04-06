package vars.avplayer.jfx;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.mbari.util.Tuple2;
import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.javafx.JFXVideoIO;
import org.mbari.vcr4j.javafx.JFXVideoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import javax.swing.*;
import java.awt.*;
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

    private VideoPlayerDialogUI dialogUI;

    @Override
    public boolean canPlay(String mimeType) {
        return ACCEPTABLE_MIMETYPES.stream()
                .filter(s -> s.equalsIgnoreCase(mimeType))
                .count() > 0;
    }

    public Optional<VideoController<JFXVideoState, SimpleVideoError>> createVideoController(String movieLocation) {

        AtomicReference<VideoController<JFXVideoState, SimpleVideoError>> videoControllerRef =
                new AtomicReference<>();

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
    public VideoPlayerDialogUI getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new JFXVideoPlayerDialogUI(window, toolBelt, this);
        }
        return dialogUI;
    }

    /**
     *
     * @param toolBelt
     * @param args The arguments need to connect to you video control service. They are the following: movieLocation: String,
     *             platformName: String, sequenceNumber: Integer
     * @return A tuple of the videoarchve and the corresponding videocontroller
     */
    @Override
    public Optional<Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String movieLocation = (String) args[0];
        String platformName = (String) args[1];
        Integer sequenceNumber = (Integer) args[2];
        return openVideoArchive(toolBelt, movieLocation, platformName, sequenceNumber);
    }

    private Optional<Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>>> openVideoArchive(ToolBelt toolBelt, String movieLocation,
            String platformName,
            Integer sequenceNumber) {

        VideoArchive videoArchive = getOrCreateVideoArchive(
                new VideoParams(movieLocation, platformName, sequenceNumber),
                toolBelt.getAnnotationDAOFactory());

        Optional<VideoController<JFXVideoState, SimpleVideoError>> controller = createVideoController(movieLocation);

        if (controller.isPresent()) {
            return Optional.of(new Tuple2<>(videoArchive, controller.get()));
        }
        else {
            return Optional.empty();
        }

    }

    public Optional<VideoArchive> findByLocation(String location, AnnotationDAOFactory daoFactory) {
        VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
        dao.startTransaction();
        VideoArchive videoArchive = dao.findByName(location);
        dao.endTransaction();
        return Optional.ofNullable(videoArchive);
    }

    private VideoArchive createVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        VideoArchive videoArchive = null;
        if (videoParams.getPlatformName().isPresent() && videoParams.getSequenceNumber().isPresent()) {
            String location = videoParams.getMovieLocation();
            int sequenceNumber = videoParams.getSequenceNumber().get();
            String platform = videoParams.getPlatformName().get();
            VideoArchiveDAO dao = daoFactory.newVideoArchiveDAO();
            dao.startTransaction();
            videoArchive = dao.findOrCreateByParameters(platform, sequenceNumber, location);
            dao.endTransaction();
        }
        return videoArchive;
    }

    private VideoArchive getOrCreateVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        return findByLocation(videoParams.getMovieLocation(), daoFactory)
                .orElseGet(() -> createVideoArchive(videoParams, daoFactory));
    }

    @Override
    public String getName() {
        return "Java FX";
    }
}
