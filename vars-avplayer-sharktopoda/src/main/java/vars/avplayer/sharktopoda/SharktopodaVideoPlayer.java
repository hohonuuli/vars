package vars.avplayer.sharktopoda;

import org.mbari.util.Tuple2;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.decorators.StatusDecorator;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd;
import org.mbari.vcr4j.sharktopoda.commands.SharkCommands;
import org.mbari.vcr4j.sharktopoda.decorators.FauxTimecodeDecorator;
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

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by brian on 9/1/16.
 */
public class SharktopodaVideoPlayer implements VideoPlayer<SharktopodaState, SharktopodaError> {

    private SharktopodaDialogUI dialogUI;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicReference<VideoController<SharktopodaState, SharktopodaError>> currentVideoController = new AtomicReference<>();

    protected CompletableFuture<VideoController<SharktopodaState, SharktopodaError>> createVideoController(String movieLocation,
            int sharktopodaPort,
            int framecapturePort) {

        CompletableFuture<VideoController<SharktopodaState, SharktopodaError>> cf = new CompletableFuture<>();

        try {
            SharktopodaVideoIO videoIO = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", sharktopodaPort);
            videoIO.send(new OpenCmd(new URL(movieLocation)));
            new StatusDecorator<>(videoIO);
            new VCRSyncDecorator<>(videoIO, 1000, 100, 3000000);
            new FauxTimecodeDecorator(videoIO); // Convert elapsed-time to timecode
            VideoIO<SharktopodaState, SharktopodaError> io = new SchedulerVideoIO<>(videoIO, Executors.newCachedThreadPool());
            VideoController<SharktopodaState, SharktopodaError> oldVc = currentVideoController.get();
            if (oldVc != null) {
                oldVc.getVideoIO().send(SharkCommands.CLOSE);
                oldVc.getImageCaptureService().dispose();
            }
            VideoController<SharktopodaState, SharktopodaError> newVc = new VideoController<>(new SharktopodaImageCaptureService(videoIO, framecapturePort), io);
            currentVideoController.set(newVc);
            cf.complete(newVc);
            io.send(SharkCommands.SHOW);
        }
        catch (Exception e) {
            log.error("Failed to create SharktopodaVideoIO", e);
            cf.completeExceptionally(e);
        }

        return cf;
    }


    @Override
    public CompletableFuture<Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String movieLocation = (String) args[0];
        String platformName = (String) args[1];
        Integer sequenceNumber = (Integer) args[2];
        return openVideoArchive(toolBelt, movieLocation, platformName, sequenceNumber);
    }

    public CompletableFuture<Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>>> openVideoArchive(ToolBelt toolBelt, String movieLocation,
             String platformName,
             Integer sequenceNumber,
            int sharktopodaPort,
            int framecapturePort) {
        return openVideoArchive(toolBelt,
                new VideoParams(movieLocation, platformName, sequenceNumber, sharktopodaPort, framecapturePort));
    }

    @Override
    public VideoPlayerDialogUI<SharktopodaState, SharktopodaError> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new SharktopodaDialogUI(window, toolBelt, this);
            dialogUI.onOkay(() -> {
                dialogUI.setVisible(false);
                final Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>> data = dialogUI.openVideoArchive();
                VideoArchive videoArchive = data.getA();
                final VideoController<SharktopodaState, SharktopodaError> videoController = data.getB();
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(videoController));
            });
        }
        return dialogUI;
    }

    protected CompletableFuture<Tuple2<VideoArchive, VideoController<SharktopodaState, SharktopodaError>>>
    openVideoArchive(ToolBelt toolBelt, VideoParams videoParams) {

        VideoArchive videoArchive = getOrCreateVideoArchive(
                videoParams,
                toolBelt.getAnnotationDAOFactory());

        return createVideoController(videoArchive.getName(),
                videoParams.getSharktopodaPort(),
                videoParams.getFramecapturePort())
                .thenCompose(c -> CompletableFuture.supplyAsync(() -> new Tuple2<>(videoArchive, c)));
    }

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    @Override
    public String getName() {
        return "Sharktopoda";
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
}
