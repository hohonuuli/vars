package vars.avplayer.noop;

import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoController;

import javax.swing.*;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-03-24T13:08:00
 */
public class NoopVideoPlayer implements VideoPlayer {

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    @Override
    public Optional<VideoController> connect(Object... args) {
        return Optional.empty();
    }

    @Override
    public JDialog getConnectionDialog() {
        // TODO return a dialog with only a close button
        return null;
    }
}
