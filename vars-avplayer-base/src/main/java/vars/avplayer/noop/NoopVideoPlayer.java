package vars.avplayer.noop;

import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayerDialogUI;

import javax.swing.*;
import java.util.Optional;

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
    public VideoPlayerDialogUI getConnectionDialog(ToolBelt toolBelt) {
        // TODO return a dialog with only a close button
        return null;
    }

    @Override
    public String getName() {
        return "Disconnected";
    }

    @Override
    public Optional<Tuple2<VideoArchive, VideoController>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        return Optional.empty();
    }
}
