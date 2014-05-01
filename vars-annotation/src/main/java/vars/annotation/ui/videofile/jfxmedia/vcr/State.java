package vars.annotation.ui.videofile.jfxmedia.vcr;

import org.mbari.vcr.VCRStateAdapter;

/**
 * Created by brian on 12/16/13.
 */
public class State extends VCRStateAdapter {

    private final VCR vcr;

    public State(VCR vcr) {
        this.vcr = vcr;
        vcr.getMediaPlayer().currentRateProperty().addListener((obs, rOld, rNew) ->  notifyObservers());
        vcr.getMediaPlayer().currentTimeProperty().addListener((obs, rOld, rNew) ->  notifyObservers());
    }

    @Override
    public boolean isFastForwarding() {
        return vcr.getMediaPlayer().getCurrentRate() > 1.0;
    }

    @Override
    public boolean isJogging() {
        // jog is when you are not playing but still moving forward
        double rate = vcr.getMediaPlayer().getCurrentRate();
        return rate > 0.0 &&  Math.abs(1D - rate) > 0.0001;
    }

    @Override
    public boolean isPlaying() {
        return vcr.getMediaPlayer().getCurrentRate() == 1.0;
    }

    @Override
    public boolean isShuttling() {
        return vcr.getMediaPlayer().getCurrentRate() > 1.0;
    }

    @Override
    public boolean isStandingBy() {
        return isStopped();
    }

    @Override
    public boolean isStill() {
        return isStopped();
    }

    @Override
    public boolean isStopped() {
        return vcr.getMediaPlayer().getCurrentRate() == 0.0;
    }

    @Override
    public boolean isTapeEnd() {
        return vcr.getMediaPlayer().getCurrentTime().toMillis() >= vcr.getMediaPlayer().getMedia().getDuration().toMillis();
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
