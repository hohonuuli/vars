package vars.annotation.jfxmedia.vcr;

import javafx.collections.ObservableMap;
import javafx.scene.media.MediaPlayer;
import org.mbari.movie.Timecode;
import org.mbari.vcr.VCRReplyAdapter;

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
        final Timecode timecode = getVcrTimecode().getTimecode();
        final MediaPlayer mediaPlayer = vcr.getMediaPlayer();
        final ObservableMap<String,Object> metadata = mediaPlayer.getMedia().getMetadata();
        timecode.setFrameRate((Double) metadata.getOrDefault("framerate", VCR.DEFAULT_FRAME_RATE));
        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, duration2) -> {
            double frames = duration2.toSeconds() * timecode.getFrameRate();
            timecode.setFrames(frames);
        });
    }
}
