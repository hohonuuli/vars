package vars.annotation.ui.videofile.jfxmedia.vcr;

/**
 * Created by brian on 12/16/13.
 */

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.mbari.movie.Timecode;
import org.mbari.vcr.VCRAdapter;


public class VCR extends VCRAdapter {

    public static final Double DEFAULT_FRAME_RATE = 30D;

    private final MediaPlayer mediaPlayer;

    private final ChangeListener<Duration> timeListener;

    public VCR(final MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        vcrReply = new Reply(this);

        // Bind timecode
        final Timecode timecode = getVcrReply().getVcrTimecode().getTimecode();
        final ObservableMap<String,Object> metadata = mediaPlayer.getMedia().getMetadata();
        timecode.setFrameRate((Double) metadata.getOrDefault("framerate", DEFAULT_FRAME_RATE));
        timeListener = (observableValue, oldDuration, newDuration) -> {
            double frames = newDuration.toSeconds() * timecode.getFrameRate();
            timecode.setFrames(frames);
        };
        mediaPlayer.currentTimeProperty().addListener(timeListener);

    }

    @Override
    public void shuttleForward(int speed) {
        double rate = speed / 256D * 3;
        super.shuttleForward(speed);
    }

    @Override
    public String getConnectionName() {
        return mediaPlayer.getMedia().getSource();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void play() {
        mediaPlayer.play();
    }


    @Override
    public void rewind() {
        // See https://javafx-jira.kenai.com/browse/RT-5238
        // Negative rate playback is not supported
    }

    @Override
    public void stop() {
        mediaPlayer.pause();
    }

    @Override
    public void disconnect() {
        mediaPlayer.currentTimeProperty().removeListener(timeListener);
        super.disconnect();
    }
}
