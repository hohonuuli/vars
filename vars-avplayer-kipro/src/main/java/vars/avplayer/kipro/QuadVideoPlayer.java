package vars.avplayer.kipro;

import org.bushe.swing.event.EventBus;
import org.mbari.util.Tuple2;
import org.mbari.vcr4j.SimpleVideoIO;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.decorators.LoggingDecorator;
import org.mbari.vcr4j.decorators.SchedulerVideoIO;
import org.mbari.vcr4j.decorators.VCRSyncDecorator;
import org.mbari.vcr4j.kipro.QuadError;
import org.mbari.vcr4j.kipro.QuadState;
import org.mbari.vcr4j.kipro.QuadVideoIO;
import rx.Observable;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.noop.NoopImageCaptureService;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * @author Brian Schlining
 * @since 2016-04-20T11:47:00
 */
public class QuadVideoPlayer implements VideoPlayer<QuadState, QuadError> {

    private final ImageCaptureService imageCaptureService = new NoopImageCaptureService();
    private QuadVideoPlayerDialogUI dialogUI;


    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    @Override
    public Optional<Tuple2<VideoArchive, VideoController<QuadState, QuadError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        return null;
    }

    @Override
    public VideoPlayerDialogUI<QuadState, QuadError> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new QuadVideoPlayerDialogUI(window, toolBelt);
            dialogUI.onOkay(() -> {
                dialogUI.setVisible(false);
                VideoArchive videoArchive = dialogUI.openVideoArchive();
                String httpAddress = dialogUI.getQuadHTTPAddress();
                VideoController<QuadState, QuadError> videoController =
                        new VideoController<>(imageCaptureService, newVideoIO(httpAddress));
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(videoController));
            });
        }
        return dialogUI;
    }

    @Override
    public String getName() {
        return "AJA KiPro Quad";
    }

    private VideoIO<QuadState, QuadError> newVideoIO(String httpAddress) {
        VideoIO<QuadState, QuadError> io = null;
        try {
            QuadVideoIO rawIO = QuadVideoIO.open(httpAddress);
            new VCRSyncDecorator<>(rawIO);
            //new LoggingDecorator<>(rawIO);
            VideoIO<QuadState, QuadError> scheduledIO = new SchedulerVideoIO<>(rawIO, Executors.newSingleThreadExecutor());

            // For the UI we need to filter videoIndices that don't have timecode (or the UI
            // show --:--:--:-- every few seconds
            Observable<VideoIndex> indexObservable = scheduledIO.getIndexObservable()
                    .filter(vi -> vi.getTimecode().isPresent());
            io = new SimpleVideoIO<>(scheduledIO.getConnectionID(),
                    scheduledIO.getCommandSubject(),
                    scheduledIO.getStateObservable(),
                    scheduledIO.getErrorObservable(),
                    indexObservable);
        }
        catch (Exception e) {
            EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
        }
        return io;
    }

}
