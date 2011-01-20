package vars.quicktime;

import org.mbari.framegrab.FakeGrabber;
import org.mbari.framegrab.IGrabber;
import org.mbari.movie.Timecode;
import org.mbari.movie.VideoTimeBean;
import org.mbari.qt.VideoStandard;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.qt.TimeSource;
import org.mbari.vcr.qt.VCRWithDisplay;
import org.mbari.vcr.timer.AnnotationQueueVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.shared.ui.video.AbstractVideoControlService;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlStatus;
import vars.shared.ui.video.VideoTime;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2010-11-29
 */
public class QTVideoControlServiceImpl extends AbstractVideoControlService implements ImageCaptureService  {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private IGrabber grabber;

    public QTVideoControlServiceImpl() {
        // TODO make sure we've intialized everything correctly
    }

    /**
     * @param args The first argument is the string URL of the movie to open. The 2nd argument is a 
     *  {@link TimeSource} object that indicates which timecode track to read from while annotating
     *  the movie file.
     */
    public void connect(Object... args) {

        disconnect();
        if (grabber != null) {
            grabber.dispose();
        }

        if (args.length != 1 && !(args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. The argument is the " +
                    "string URL to the movie to open with QuickTime");
        }

        String movieName = (String) args[0];
        TimeSource timeSource = (TimeSource) args[1];

        try {
            VCRWithDisplay vcr0 = new VCRWithDisplay(movieName, timeSource);
            IVCR vcr = new AnnotationQueueVCR(vcr0);
            grabber = vcr0.getGrabber();
            setVcr(vcr);
            setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.CONNECTED));
        } catch (Exception e) {
            grabber = new FakeGrabber();
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.ERROR));
            throw new VARSException("Failed to open " + movieName + " with QuickTime for Java", e);
        }

    }

    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void seek(String timecode) {
        getVcr().seekTimecode(new Timecode(timecode));
    }

    public VideoTime requestVideoTime() {
        IVCR vcr = getVcr();
        vcr.requestTimeCode();
        Timecode timecode = vcr.getVcrTimecode().getTimecode();
        final String tc = timecode.toString();
        return new VideoTime() {
            public Date getDate() {
                return null;
            }

            public String getTimecode() {
                return tc;
            }
        };
    }

    public Image capture(String timecode) throws ImageCaptureException {
        return grabber.grab();
    }

    public void dispose() {
        getVcr().disconnect();
        grabber.dispose();
    }

    public void showSettingsDialog() {
        // TODO Implement this
    }
}
