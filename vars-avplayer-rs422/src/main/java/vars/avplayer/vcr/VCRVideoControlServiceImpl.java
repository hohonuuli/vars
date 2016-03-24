package vars.avplayer.vcr;

import vars.avplayer.AbstractVideoControlService;
import vars.avplayer.ImageCaptureException;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoControlService;
import vars.avplayer.VideoPlayerController;
import vars.avplayer.VideoTime;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Brian Schlining
 * @since 2016-01-21T16:27:00
 */
public class VCRVideoControlServiceImpl extends AbstractVideoControlService
        implements ImageCaptureService, VideoPlayerController {
    
    @Override
    public Image capture(File file) throws ImageCaptureException {
        return null;
    }

    @Override
    public boolean isPngAutosaved() {
        return false;
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

    }

    @Override
    public JDialog getConnectionDialog() {
        return null;
    }

    @Override
    public void seek(String timecode) {

    }

    @Override
    public VideoTime requestVideoTime() {
        return null;
    }

    @Override
    public VideoControlService getVideoControlService() {
        return null;
    }

    @Override
    public ImageCaptureService getImageCaptureService() {
        return null;
    }

    @Override
    public String getMovieLocation() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }
}
