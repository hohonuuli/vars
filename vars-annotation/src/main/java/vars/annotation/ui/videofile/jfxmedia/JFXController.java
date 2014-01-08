package vars.annotation.ui.videofile.jfxmedia;

import vars.annotation.ui.videofile.VideoPlayerController;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

/**
 * Created by brian on 1/7/14.
 */
public class JFXController implements VideoPlayerController {
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
}
