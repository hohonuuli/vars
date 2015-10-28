package vars.avplayer.jfx;

import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.VCRStateAdapter;

/**
 * Created by brian on 5/1/14.
 */
public class JFXState extends VCRStateAdapter {

    private IVCR vcr;
    private IObserver observer = (obj, changeCode) -> {
        if (obj != null && obj instanceof IVCRState) {
            notifyObservers();
        }
    };

    protected void setVcr(IVCR vcr) {
        if (this.vcr != null && this.vcr != vcr) {
            this.vcr.getVcrState().removeObserver(observer);
        }

        if (vcr != null) {
            vcr.getVcrState().addObserver(observer);
        }
        this.vcr = vcr;
    }

    @Override
    public boolean isBadCommunication() {
        return vcr == null || vcr.getVcrState().isBadCommunication();
    }

    @Override
    public boolean isConnected() {
        return vcr != null && vcr.getVcrState().isConnected();
    }

    @Override
    public boolean isCueingUp() {
        return vcr != null && vcr.getVcrState().isCueingUp();
    }

    @Override
    public boolean isFastForwarding() {
        return vcr != null && vcr.getVcrState().isFastForwarding();
    }

    @Override
    public boolean isHardwareError() {
        return vcr != null && vcr.getVcrState().isHardwareError();
    }

    @Override
    public boolean isJogging() {
        return vcr != null && vcr.getVcrState().isJogging();
    }

    @Override
    public boolean isLocal() {
        return vcr == null || vcr.getVcrState().isLocal();
    }

    @Override
    public boolean isPlaying() {
        return vcr != null && vcr.getVcrState().isPlaying();
    }

    @Override
    public boolean isRecording() {
        return vcr != null && vcr.getVcrState().isRecording();
    }

    @Override
    public boolean isReverseDirection() {
        return vcr != null && vcr.getVcrState().isReverseDirection();
    }

    @Override
    public boolean isRewinding() {
        return vcr != null && vcr.getVcrState().isRewinding();
    }

    @Override
    public boolean isServoLock() {
        return vcr != null && vcr.getVcrState().isServoLock();
    }

    @Override
    public boolean isServoRef() {
        return vcr != null && vcr.getVcrState().isServoRef();
    }

    @Override
    public boolean isShuttling() {
        return vcr != null && vcr.getVcrState().isShuttling();
    }

    @Override
    public boolean isStandingBy() {
        return vcr != null && vcr.getVcrState().isStandingBy();
    }

    @Override
    public boolean isStill() {
        return vcr == null || vcr.getVcrState().isStill();
    }

    @Override
    public boolean isStopped() {
        return vcr == null || vcr.getVcrState().isStill();
    }

    @Override
    public boolean isTapeEnd() {
        return vcr == null || vcr.getVcrState().isTapeEnd();
    }

    @Override
    public boolean isTapeTrouble() {
        return vcr == null || vcr.getVcrState().isTapeTrouble();
    }

    @Override
    public boolean isTso() {
        return vcr != null && vcr.getVcrState().isTso();
    }

    @Override
    public boolean isUnthreaded() {
        return vcr == null && vcr.getVcrState().isUnthreaded();
    }

    @Override
    public boolean isVarSpeed() {
        return vcr != null && vcr.getVcrState().isVarSpeed();
    }

}
