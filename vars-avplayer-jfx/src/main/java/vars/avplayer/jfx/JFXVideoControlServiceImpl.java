package vars.avplayer.jfx;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import org.mbari.vcr4j.*;
import org.mbari.vcr4j.time.Timecode;
import org.mbari.vcr4j.timer.AnnotationQueueVCR;
import vars.avplayer.EventBus;
import vars.avplayer.VideoPlayerController;
import vars.avplayer.jfx.vcr.VCR;
import vars.avplayer.AbstractVideoControlService;
import vars.avplayer.ImageCaptureException;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoControlService;
import vars.avplayer.VideoControlStatus;
import vars.avplayer.VideoTime;
import vars.shared.rx.messages.NonFatalExceptionMsg;

/**
 * Created by brian on 12/16/13.
 */
public class JFXVideoControlServiceImpl  extends AbstractVideoControlService
        implements ImageCaptureService, VideoPlayerController {

    private JFXMovieFrame movieFrame = new JFXMovieFrame();
    private final JFXTimecode vcrTimcode = new JFXTimecode();
    private final JFXState vcrState = new JFXState();
    private static final List<String> ACCEPTABLE_MIMETYPES = Arrays.asList("video/mp4");

    public JFXVideoControlServiceImpl() {
        movieFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public boolean isPngAutosaved() {
        return true;
    }

    @Override
    public Image capture(File file) throws ImageCaptureException {
        Image image = null;
        try {
           image = movieFrame.getController().frameCapture(file);
        }
        catch (Exception e) {
            EventBus.send(new NonFatalExceptionMsg("Failed to capture image to " + file.getAbsolutePath(), e));
        }
        return image;
    }

    @Override
    public Image capture(String timecode) throws ImageCaptureException {

        return null;
    }

    @Override
    public void dispose() {
        // TODO ???
    }

    @Override
    public void showSettingsDialog() {

    }

    @Override
    public void connect(Object... args) {
        disconnect();
        if ((args.length != 1) && (args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. The argument is the " +
                    "string URL to the movie to open with JavaFX");
        }

        String movieLocation = (String) args[0];
        try {
            movieFrame.setMovieLocation(movieLocation);
            Thread.sleep(1500); // HACK! setMovieLocation has to set the movie location on JavaFX thread. We must wait
            VCR fxVcr = new VCR(movieFrame.getController().getMediaView().getMediaPlayer());
            IVCR vcr = new AnnotationQueueVCR(fxVcr);
            setVcr(vcr);
            setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.CONNECTED));
            movieFrame.setVisible(true);
            ((VCR) fxVcr).triggerStateNotification();
        }
        catch (Exception e) {
            setVcr(new VCRAdapter());
            setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.ERROR));
            //throw new VARSException("Failed to open " + movieLocation + " with JavaFX", e);
        }

    }

    @Override
    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void seek(String timecode) {
        getVcr().seekTimecode(new Timecode(timecode));
    }

    @Override
    public VideoTime requestVideoTime() {
        final String tc = getVcr().getVcrTimecode().getTimecode().toString();
        return new VideoTime() {
            @Override
            public Date getDate() {
                return null;
            }

            @Override
            public String getTimecode() {
                return tc;
            }
        };
    }

    @Override
    public VideoControlService getVideoControlService() {
        return this;
    }

    @Override
    public ImageCaptureService getImageCaptureService() {
        return this;
    }

    @Override
    public String getMovieLocation() {
        return movieFrame.getController().getMediaView().getMediaPlayer().getMedia().getSource();
    }

    @Override
    public void close() {
        // TODO ???
    }

    @Override
    public IVCRTimecode getVcrTimecode() {
        return vcrTimcode;
    }

    @Override
    public IVCRState getVcrState() {
        return vcrState;
    }

    @Override
    public void setVcr(IVCR vcr) {
        vcrTimcode.setVCR(vcr);
        vcrState.setVcr(vcr);
        super.setVcr(vcr);
    }

    @Override
    public boolean canPlay(String mimeType) {
        return ACCEPTABLE_MIMETYPES.stream()
                .filter(s -> s.equalsIgnoreCase(mimeType))
                .count() > 0;
    }

    @Override
    public String getName() {
        return "JavaFX";
    }
}
