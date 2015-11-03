package vars.avplayer.jfx.vcr;

/**
 * Created by brian on 12/16/13.
 */

import javafx.beans.value.ChangeListener;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.time.Timecode;


public class VCR extends VCRAdapter {

    /**
     * To best handle proxies. We use fractional seconds as frames. This lets us swap videos
     * and likely be close enough (1/100th sec) to the actual frame of interest between
     * alternate proxies.
     */
    public static final double DEFAULT_FRAME_RATE = 100D;
    public static final double FAST_FORWARD_RATE = 5D;
    public static final double MAX_RATE = 8D; // According to JavaFX docs this is the max rate

    private final MediaPlayer mediaPlayer;

    private final ChangeListener<Duration> timeListener;

    public VCR(final MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        vcrReply = new Reply(this);

        timeListener = (observableValue, oldDuration, newDuration) -> {
            double frames = newDuration.toSeconds() * VCR.DEFAULT_FRAME_RATE;
            Timecode timecode = new Timecode(frames, VCR.DEFAULT_FRAME_RATE);
            vcrReply.getVcrTimecode().timecodeProperty().set(timecode);
        };
        mediaPlayer.currentTimeProperty().addListener(timeListener);
        triggerStateNotification();
    }

    @Override
    public void shuttleForward(int speed) {
        double rate = speed / 255D * MAX_RATE;
        mediaPlayer.setRate(rate);
        //mediaPlayer.play();
        super.shuttleForward(speed);
        triggerStateNotification();
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
        triggerStateNotification();
    }

    @Override
    public void play() {
        mediaPlayer.setRate(1.0);
        mediaPlayer.play();
        triggerStateNotification();
    }


    @Override
    public void rewind() {
        // See https://javafx-jira.kenai.com/browse/RT-5238
        // Negative rate playback is not supported
    }

    @Override
    public void stop() {
        mediaPlayer.pause();
        triggerStateNotification();
    }

    @Override
    public void disconnect() {
        if (mediaPlayer != null) {
            mediaPlayer.currentTimeProperty().removeListener(timeListener);
        }
        super.disconnect();
        triggerStateNotification();
    }

    @Override
    public void fastForward() {
        mediaPlayer.setRate(FAST_FORWARD_RATE);
        mediaPlayer.play();
        super.fastForward();
        triggerStateNotification();
    }

    public void triggerStateNotification() {
        State state = (State) getVcrState();
        state.notifyObserversFX();
    }

    @Override
    public void requestStatus() {
        super.requestStatus();
        triggerStateNotification();
    }

    @Override
    public void seekTimecode(Timecode timecode) {
        //Timecode currentTimecode = getVcrTimecode().getTimecode();
        Timecode tc = timecode;
        if (!timecode.isComplete()) {
            tc = new Timecode(timecode.toString(), DEFAULT_FRAME_RATE);
        }

        //double seconds = tc.getFrames() / currentTimecode.getFrameRate();
        double seconds = tc.getSeconds();
        Duration duration = new Duration(seconds * 1000D);
        System.out.println(duration);
        mediaPlayer.seek(duration);
        super.seekTimecode(timecode);
        triggerStateNotification();
    }
}
