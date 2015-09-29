package vars.annotation.ui.videofile.jfxmedia.vcr;

import javafx.scene.media.MediaPlayer;
import org.mbari.vcr4j.VCRReplyAdapter;
import org.mbari.vcr4j.time.Timecode;

import java.sql.Time;

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
        //final ObservableMap<String,Object> metadata = mediaPlayer.getMedia().getMetadata();
//        timecode.setFrameRate((Double) metadata.getOrDefault("framerate", VCR.DEFAULT_FRAME_RATE));
//        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, duration2) -> {
//            double frames = duration2.toSeconds() * timecode.getFrameRate();
//            timecode.setFrames(frames);
//        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double frames = newValue.toSeconds() * VCR.DEFAULT_FRAME_RATE;
            Timecode timecode = new Timecode(frames, VCR.DEFAULT_FRAME_RATE);
            getVcrTimecode().timecodeProperty().set(timecode);
        });

    }
}
