package vars.avplayer.jfx;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mbarix4j.util.Tuple2;
import org.mbari.vcr4j.SimpleVideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.javafx.JFXVideoIO;
import org.mbari.vcr4j.javafx.JFXVideoState;
import org.mbari.vcr4j.javafx.decorators.FauxTimecodeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public CompletableFuture<VideoController<JFXVideoState, SimpleVideoError>> createVideoController(String movieLocation) {

        CompletableFuture<VideoController<JFXVideoState, SimpleVideoError>> cf = new CompletableFuture<>();

        try {

            movieFrame.setVisible(false);
            movieFrame.setMediaLocation(movieLocation, c -> {
                controller = c;

                MediaPlayer mediaPlayer = c.getMediaView().getMediaPlayer();
                Media media = mediaPlayer.getMedia();

                // -- Configure VideoIO
                JFXVideoIO videoIO = new JFXVideoIO(mediaPlayer);
                new StatusDecorator<>(videoIO);   // Some commands should immediately send a status request
                new VCRSyncDecorator<>(videoIO);  // Send state/index commands at regular intervals
                //new LoggingDecorator<>(videoIO);
                new FauxTimecodeDecorator(videoIO); // Convert elapsed-time to timecode
                VideoIO<JFXVideoState, SimpleVideoError> io =
                        new SchedulerVideoIO<JFXVideoState, SimpleVideoError>(videoIO, Executors.newCachedThreadPool());

                cf.complete(new VideoController<>(new JFXImageCaptureService(controller), io));
                SwingUtilities.invokeLater(() -> {
                    movieFrame.setSize(media.getWidth(), media.getHeight());
                    movieFrame.setVisible(true);
                });

            });

        }
        catch (Exception e) {
            log.error("Failed to create JFXMovieJFrame", e);
            cf.completeExceptionally(e);
        }

        return cf;
    }

    @Override
    public VideoPlayerDialogUI getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new JFXVideoPlayerDialogUI(window, toolBelt, this);
            dialogUI.onOkay(() -> {
                dialogUI.setVisible(false);
                Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>> data = ((JFXVideoPlayerDialogUI) dialogUI).openVideoArchive();
                VideoArchive videoArchive = data.getA();
                VideoController<JFXVideoState, SimpleVideoError> videoController = data.getB();
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(videoController));
            });
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
    public CompletableFuture<Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String movieLocation = (String) args[0];
        String platformName = (String) args[1];
        Integer sequenceNumber = (Integer) args[2];
        return openVideoArchive(toolBelt, movieLocation, platformName, sequenceNumber);
    }

    protected CompletableFuture<Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>>> openVideoArchive(ToolBelt toolBelt, String movieLocation,
            String platformName,
            Integer sequenceNumber) {

        return openVideoArchive(toolBelt, new VideoParams(movieLocation, platformName, sequenceNumber));

    }


    protected CompletableFuture<Tuple2<VideoArchive, VideoController<JFXVideoState, SimpleVideoError>>>
            openVideoArchive(ToolBelt toolBelt, VideoParams videoParams) {

        VideoArchive videoArchive = getOrCreateVideoArchive(
                videoParams,
                toolBelt.getAnnotationDAOFactory());

        return createVideoController(videoArchive.getName())
                .thenCompose(c -> CompletableFuture.supplyAsync(() -> new Tuple2<>(videoArchive, c)));
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

    protected VideoArchive getOrCreateVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        return findByLocation(videoParams.getMovieLocation(), daoFactory)
                .orElseGet(() -> createVideoArchive(videoParams, daoFactory));
    }

    @Override
    public String getName() {
        return "MP4 using Java";
    }
}
