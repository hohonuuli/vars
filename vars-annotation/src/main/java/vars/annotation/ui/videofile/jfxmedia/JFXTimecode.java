package vars.annotation.ui.videofile.jfxmedia;

import org.mbari.movie.Timecode;
import org.mbari.util.IObserver;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.VCRTimecodeAdapter;

/**
 * Because we keep reusing JFXVideoControlServiceImpl as THE vcr control, (rather than disposing of it
 * everytime we open a new movie) mayn UI parts bind/observe it only when it's first created.
 * So in order for things to be properly updated we needa proxy timecode that UI parts can bind to
 * and still get correctly updated when we switch out the underlying VCR control instance (using
 * jfxmedia.vcr.VCR)
 *
 * Created by brian on 5/1/14.
 */
public class JFXTimecode extends VCRTimecodeAdapter {

    private IVCR vcr;
    private IObserver observer = (obj, changeCode) ->  {
        if (obj != null && obj instanceof Timecode) {
            Timecode that = (Timecode) obj;
            timecode.setFrameRate(that.getFrameRate());
            timecode.setFrames(that.getFrames());
        }
    };


    protected void setVCR(IVCR vcr) {
        if (this.vcr != null && this.vcr != vcr) {
            this.vcr.getVcrTimecode().getTimecode().removeObserver(observer);
        }

        if (vcr != null) {
            vcr.getVcrTimecode().getTimecode().addObserver(observer);
        }
        this.vcr = vcr;
    }
}
