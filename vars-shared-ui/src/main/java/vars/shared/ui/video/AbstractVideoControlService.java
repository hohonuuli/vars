/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.video;

import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.IVCRError;
import org.mbari.vcr4j.IVCRReply;
import org.mbari.vcr4j.IVCRState;
import org.mbari.vcr4j.IVCRTimecode;
import org.mbari.vcr4j.IVCRUserbits;
import org.mbari.vcr4j.VCRAdapter;
import org.mbari.vcr4j.time.Timecode;

/**
 * Abstract class that delegates calls to an underlying VCR implementation. Ensures
 * that the VCR object will never be null, avoiding the dreaded {@link NullPointerException}
 * @author brian
 */
public abstract class AbstractVideoControlService implements VideoControlService {

    private IVCR vcr = new VCRAdapter();
    private VideoControlInformation videoControlInformation;

    public IVCR getVcr() {
        return vcr;
    }

    public void setVcr(IVCR vcr) {
        if (vcr == null) {
            vcr = new VCRAdapter();
            videoControlInformation = new NullVideoControlInformation();
        }
        this.vcr = vcr;
    }
    
    public VideoControlInformation getVideoControlInformation() {
        if (videoControlInformation == null) {
            videoControlInformation = new NullVideoControlInformation();
        }
        return videoControlInformation;
    }

    public void setVideoControlInformation(VideoControlInformation videoControlInformation) {
        this.videoControlInformation = videoControlInformation;
    }

    public void stop() {
        vcr.stop();
    }

    public void shuttleReverse(int speed) {
        vcr.shuttleReverse(speed);
    }

    public void shuttleForward(int speed) {
        vcr.shuttleForward(speed);
    }

    public void seekTimecode(Timecode timecode) {
        vcr.seekTimecode(timecode);
    }

    public void seekTimecode(int timecode) {
        vcr.seekTimecode(timecode);
    }

    public void seekTimecode(byte[] timecode) {
        vcr.seekTimecode(timecode);
    }

    public void rewind() {
        vcr.rewind();
    }

    public void requestVUserbits() {
        vcr.requestVUserbits();
    }

    public void requestVTimeCode() {
        vcr.requestVTimeCode();
    }

    public void requestUserbits() {
        vcr.requestUserbits();
    }

    public void requestTimeCode() {
        vcr.requestTimeCode();
    }

    public void requestStatus() {
        vcr.requestStatus();
    }

    public void requestLocalEnable() {
        vcr.requestLocalEnable();
    }

    public void requestLocalDisable() {
        vcr.requestLocalDisable();
    }

    public void requestLUserbits() {
        vcr.requestLUserbits();
    }

    public void requestLTimeCode() {
        vcr.requestLTimeCode();
    }

    public void requestDeviceType() {
        vcr.requestDeviceType();
    }

    public void removeAllObservers() {
        vcr.removeAllObservers();
    }

    public void releaseTape() {
        vcr.releaseTape();
    }

    public void record() {
        vcr.record();
    }

    public void presetUserbits(byte[] userbits) {
        vcr.presetUserbits(userbits);
    }

    public void presetTimecode(byte[] timecode) {
        vcr.presetTimecode(timecode);
    }

    public void play() {
        vcr.play();
    }

    public void pause() {
        vcr.pause();
    }

    public IVCRUserbits getVcrUserbits() {
        return vcr.getVcrUserbits();
    }

    public IVCRTimecode getVcrTimecode() {
        return vcr.getVcrTimecode();
    }

    public IVCRState getVcrState() {
        return vcr.getVcrState();
    }

    public IVCRReply getVcrReply() {
        return vcr.getVcrReply();
    }

    public IVCRError getVcrError() {
        return vcr.getVcrError();
    }

    public String getConnectionName() {
        return vcr.getConnectionName();
    }

    public void fastForward() {
        vcr.fastForward();
    }

    public void eject() {
        vcr.eject();
    }

    public void kill() {
        vcr.kill();
    }

    public void disconnect() {
        vcr.disconnect();
        VideoControlInformation oldInfo = getVideoControlInformation();
        videoControlInformation = new VideoControlInformationImpl(oldInfo.getVideoConnectionID(), VideoControlStatus.DISCONNECTED);
    }

    public boolean isConnected() {
        return getVideoControlInformation().getVideoControlStatus().equals(VideoControlStatus.CONNECTED);
    }


    public boolean isPlaying() {
        vcr.requestStatus();
        return vcr.getVcrState().isPlaying();
    }

    public boolean isStopped() {
        vcr.requestStatus();
        return vcr.getVcrState().isStopped();
    }

    protected class VideoControlInformationImpl implements VideoControlInformation {
        private final String videoConnectionID;
        private final VideoControlStatus videoControlStatus;

        public VideoControlInformationImpl(String videoConnectionID, VideoControlStatus videoControlStatus) {
            this.videoConnectionID = videoConnectionID;
            this.videoControlStatus = videoControlStatus;
        }

        public String getVideoConnectionID() {
            return videoConnectionID;
        }

        public VideoControlStatus getVideoControlStatus() {
            return videoControlStatus;
        }

    }

    /**
     * Represents an empty video connection
     */
    protected class NullVideoControlInformation extends VideoControlInformationImpl {

        public NullVideoControlInformation() {
            super("UNKNOWN", VideoControlStatus.DISCONNECTED);
        }
    }

}
