package vars.quicktime;

import org.mbari.movie.Timecode;
import org.mbari.qt.VideoStandard;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.qt.TimeSource;
import org.mbari.vcr.qt.VCRFactory;
import org.mbari.vcr.qt.VCRWithDisplay;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import vars.VARSException;
import vars.shared.ui.video.AbstractVideoControlService;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlStatus;
import vars.shared.ui.video.VideoTime;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author Brian Schlining
 * @since 2010-11-29
 */
public class QTVideoControlServiceImpl extends AbstractVideoControlService implements ImageCaptureService  {



    public QTVideoControlServiceImpl() {

    }

    public void connect(Object... args) {

        disconnect();

        if (args.length != 1 && !(args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. The argument is the " +
                    "string URL to the movie to open with QuickTime");
        }

        String movieName = (String) args[0];

        IVCR vcr;
        try {
            vcr = new AnnotationMonitoringVCR(new VCRWithDisplay(movieName, TimeSource.AUTO));
        } catch (Exception e) {
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.ERROR));
            throw new VARSException("Failed to open " + movieName + " with QuickTime for Java", e);
        }

        setVcr(vcr);
        setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.CONNECTED));
    }

    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void seek(String timecode) {
         //getVcr().seekTimecode(new Timecode(timecode, frameRate));
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public VideoTime requestVideoTime() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Image capture(String timecode) throws ImageCaptureException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void showSettingsDialog() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
