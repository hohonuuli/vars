package vars.avplayer.noop;

import org.mbari.util.Tuple2;
import org.mbari.vcr4j.adapter.noop.NoopVideoError;
import org.mbari.vcr4j.adapter.noop.NoopVideoIO;
import org.mbari.vcr4j.adapter.noop.NoopVideoState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.SimpleVideoParams;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Noop player can't play anything, it's a fake placeholder though.
 * @author Brian Schlining
 * @since 2016-03-24T13:08:00
 */
public class NoopVideoPlayer implements VideoPlayer {

    private NoopVideoPlayerDialogUI dialogUI;
    private VideoController<NoopVideoState, NoopVideoError> videoController;

    @Override
    public boolean canPlay(String mimeType) {
        return true;
    }


    @Override
    public VideoPlayerDialogUI getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new NoopVideoPlayerDialogUI(window, toolBelt);
            dialogUI.onOkay(() -> {
                dialogUI.setVisible(false);
                VideoArchive videoArchive = dialogUI.openVideoArchive();
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(getVideoController()));
            });
        }
        return dialogUI;
    }

    @Override
    public String getName() {
        return "Disconnected";
    }

    @Override
    public CompletableFuture<Tuple2<VideoArchive, VideoController<NoopVideoState, NoopVideoError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String platformName = (String) args[0];
        Integer sequenceNumber = (Integer) args[1];
        Integer tapeNumber = (Integer) args[2];
        Boolean isHD = (Boolean) args[3];
        SimpleVideoParams videoParams = new SimpleVideoParams(platformName, sequenceNumber, tapeNumber, isHD);
        return CompletableFuture.supplyAsync(() -> openVideoArchive(toolBelt, videoParams));
    }

    public Tuple2<VideoArchive, VideoController<NoopVideoState, NoopVideoError>> openVideoArchive(ToolBelt toolBelt, SimpleVideoParams videoParams) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoArchive videoArchive = dao.findOrCreateByParameters(videoParams.getPlatformName(),
                videoParams.getSequenceNumber(),
                videoParams.getVideoArchiveName());
        dao.close();

        return new Tuple2<>(videoArchive, getVideoController());
    }

    public VideoController<NoopVideoState, NoopVideoError> getVideoController() {
        if (videoController == null) {
            videoController = new VideoController<>(new NoopImageCaptureService(), new NoopVideoIO());
        }
        return videoController;
    }
}
