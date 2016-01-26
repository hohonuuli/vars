package vars.avplayer.jfx;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.mbari.vcr4j.*;
import org.mbari.vcr4j.time.Timecode;
import org.mbari.vcr4j.timer.AnnotationQueueVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private volatile JFXMovieJFrame movieFrame = new JFXMovieJFrame();
    private final JFXTimecode vcrTimcode = new JFXTimecode();
    private final JFXState vcrState = new JFXState();
    private static final List<String> ACCEPTABLE_MIMETYPES = Arrays.asList("video/mp4");
    private final Logger log = LoggerFactory.getLogger(getClass());
    private volatile JFXMovieJFrameController controller;

    public JFXVideoControlServiceImpl() {
    }

    @Override
    public boolean isPngAutosaved() {
        return true;
    }

    @Override
    public Image capture(File file) throws ImageCaptureException {
        Image image = null;
        if (controller != null) {
            try {
                image = controller.frameCapture(file);
            } catch (Exception e) {
                EventBus.send(new NonFatalExceptionMsg("Failed to capture image to " + file.getAbsolutePath(), e));
            }
        }
        return image;
    }

    @Override
    public Image capture(String timecode) throws ImageCaptureException {

        return null;
    }

    @Override
    public void dispose() {
        // We can't dispose of the JFXPanel in the JFXMovieJFrame or we have issues with JavaFX
        movieFrame.setVisible(false);
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

            movieFrame.setVisible(false);
            movieFrame.setMediaLocation(movieLocation, c -> {
                controller = c;
                VCR fxVcr = new VCR(c.getMediaView().getMediaPlayer());
                IVCR vcr = new AnnotationQueueVCR(fxVcr);
                setVcr(vcr);
                setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.CONNECTED));
                fxVcr.triggerStateNotification();
                MediaPlayer mediaPlayer = c.getMediaView().getMediaPlayer();
                Media media = mediaPlayer.getMedia();
                Timecode timecode = new Timecode(mediaPlayer.getCurrentTime().toSeconds(), 100D);
                vcr.getVcrTimecode().timecodeProperty().setValue(timecode);
                SwingUtilities.invokeLater(() -> {
                    movieFrame.setSize(media.getWidth(), media.getHeight());
                    movieFrame.setVisible(true);
                });

            });

        }
        catch (Exception e) {
            log.error("Failed to create JFXMovieJFrame", e);
            setVcr(new VCRAdapter());
            setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.ERROR));
        }

    }


    @Override
    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void seek(String timecode) {
        IVCR vcr = getVcr();
        if (vcr != null) {
            //getVcr().play();
            getVcr().seekTimecode(new Timecode(timecode));
            //getVcr().stop();
        }
    }

    @Override
    public VideoTime requestVideoTime() {
        IVCR vcr = getVcr();
        final String tc = vcr == null ? Timecode.EMPTY_TIMECODE_STRING :
                vcr.getVcrTimecode().getTimecode().toString();
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
        String location = null;
        if (controller != null) {
            location = controller.getMediaView().getMediaPlayer().getMedia().getSource();
        }
        return location;
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
