package vars.avplayer.noop;

import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayerDialogUI;
import vars.shared.rx.RXEventBus;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Noop player can't play anything, it's a fake placeholder though.
 * @author Brian Schlining
 * @since 2016-03-24T13:08:00
 */
public class NoopVideoPlayer implements VideoPlayer {


    @Override
    public boolean canPlay(String mimeType) {
        return true;
    }


    @Override
    public VideoPlayerDialogUI getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        // TODO return a dialog with only a close button
        return null;
    }

    @Override
    public String getName() {
        return "Disconnected";
    }

    @Override
    public CompletableFuture<Tuple2<VideoArchive, VideoController>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        CompletableFuture<Tuple2<VideoArchive, VideoController>> cf = new CompletableFuture<>();
        cf.completeExceptionally(new RuntimeException("openVideoArchive is not implemented in " + getClass().getName()));
        return cf;
    }
}
