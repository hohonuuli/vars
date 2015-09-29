package vars.annotation.ui.videofile.jfxmedia;

import javafx.beans.value.ChangeListener;
import org.mbari.util.IObserver;
import org.mbari.vcr4j.IVCR;
import org.mbari.vcr4j.VCRTimecodeAdapter;
import org.mbari.vcr4j.time.Timecode;

/**
 * Because we keep reusing JFXVideoControlServiceImpl as THE vcr control, (rather than disposing of it
 * everytime we open a new movie) mayn UI parts bind/observe it only when it's first created.
 * So in order for things to be properly updated we need a proxy timecode that UI parts can bind to
 * and still get correctly updated when we switch out the underlying VCR control instance (using
 * jfxmedia.vcr.VCR)
 *
 * Created by brian on 5/1/14.
 */
public class JFXTimecode extends VCRTimecodeAdapter {

    private IVCR vcr;


    protected void setVCR(IVCR vcr) {
        if (timecodeProperty.isBound()) {
            timecodeProperty.unbind();
        }

        if (vcr != null) {
            timecodeProperty.bind(vcr.getVcrTimecode().timecodeProperty());
        }
        this.vcr = vcr;
    }
}
