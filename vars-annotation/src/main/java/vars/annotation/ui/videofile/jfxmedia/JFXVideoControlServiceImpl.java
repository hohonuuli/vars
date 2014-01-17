package vars.annotation.ui.videofile.jfxmedia;

import java.awt.*;
import java.io.File;
import java.util.Date;
import javafx.util.Duration;
import javax.swing.*;
import org.mbari.movie.Timecode;
import vars.annotation.ui.videofile.jfxmedia.vcr.VCR;
import vars.shared.ui.video.*;
import vars.VARSException;

/**
 * Created by brian on 12/16/13.
 */
public class JFXVideoControlServiceImpl  extends AbstractVideoControlService implements ImageCaptureService {

    private JFXMovieFrame movieFrame = new JFXMovieFrame();

    public JFXVideoControlServiceImpl() {
        movieFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public boolean isPngAutosaved() {
        return true;
    }

    @Override
    public Image capture(File file) throws ImageCaptureException {
        return null;
    }

    @Override
    public Image capture(String timecode) throws ImageCaptureException {

        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void showSettingsDialog() {

    }

    @Override
    public void connect(Object... args) {
        disconnect();
        if ((args.length != 1) && (args[0] instanceof String)) {
            throw new IllegalArgumentException("YOu didn't call this method correctly. The argument is the " +
                    "string URL to the movie to open with JavaFX");
        }

        String movieLocation = (String) args[0];
        try {
            movieFrame.setMovieLocation(movieLocation);
            setVcr(new VCR(movieFrame.getController().getMediaView().getMediaPlayer()));
            setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.CONNECTED));
        }
        catch (Exception e) {
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(movieLocation, VideoControlStatus.ERROR));
            throw new VARSException("Failed to open " + movieLocation + " with JavaFX", e);
        }

    }

    @Override
    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void seek(String timecode) {
        Timecode tc = new Timecode(timecode);
        double seconds = tc.getFrames() * tc.getFrameRate();
        movieFrame.getController().getMediaView().getMediaPlayer().seek(Duration.seconds(seconds));
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

}
