package vars.annotation.ui.videofile.jfxmedia.vcr;

import org.mbari.vcr.VCRStateAdapter;

/**
 * Created by brian on 12/16/13.
 */
public class State extends VCRStateAdapter {

    private final VCR vcr;
    private static final double eps = 0.01;

    public State(VCR vcr) {
        this.vcr = vcr;
        vcr.getMediaPlayer().currentRateProperty().addListener((obs, rOld, rNew) ->  notifyObservers());
        vcr.getMediaPlayer().currentTimeProperty().addListener((obs, rOld, rNew) ->  notifyObservers());
    }

    @Override
    public boolean isFastForwarding() {
        return Math.abs(VCR.FAST_FORWARD_RATE - vcr.getMediaPlayer().getCurrentRate()) <= eps;
    }

    @Override
    public boolean isJogging() {
        // jog is when you are not playing but still moving forward
        return isShuttling();
    }

    @Override
    public boolean isPlaying() {
        return Math.abs(1D - vcr.getMediaPlayer().getCurrentRate()) <= eps;
    }

    @Override
    public boolean isShuttling() {
        return vcr.getMediaPlayer().getCurrentRate() > 0.0 && Math.abs(1D - vcr.getMediaPlayer().getRate()) > eps;
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
        return Math.abs(0D - vcr.getMediaPlayer().getCurrentRate()) <= eps;
    }

    @Override
    public boolean isTapeEnd() {
        return vcr.getMediaPlayer().getCurrentTime().toMillis() >= vcr.getMediaPlayer().getMedia().getDuration().toMillis();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    /**
     * Allow the JavaFX VCR implementation to trigger a state notification
     */
    protected void notifyObserversFX() {
        notifyObservers();
    }


}
