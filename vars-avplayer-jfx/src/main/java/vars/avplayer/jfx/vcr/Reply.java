package vars.avplayer.jfx.vcr;

import javafx.scene.media.MediaPlayer;
import org.mbari.vcr4j.VCRReplyAdapter;
import org.mbari.vcr4j.time.Timecode;

/**
 * Created by brian on 12/16/13.
 */
public class Reply extends VCRReplyAdapter {

    private final VCR vcr;

    public Reply(final VCR vcr) {
        this.vcr = vcr;
        vcrState = new State(vcr);

        bindTimecode();

    }

    private void bindTimecode() {
        final MediaPlayer mediaPlayer = vcr.getMediaPlayer();

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double frames = newValue.toSeconds() * VCR.DEFAULT_FRAME_RATE;
            Timecode timecode = new Timecode(frames, VCR.DEFAULT_FRAME_RATE);
            getVcrTimecode().timecodeProperty().set(timecode);
        });

    }
}
