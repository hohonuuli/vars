package vars.annotation.ui.videofile.jfxmedia;

import vars.annotation.ui.videofile.VideoPlayerController;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

/**
 * Created by brian on 1/7/14.
 */
public class JFXController implements VideoPlayerController {

    private JFXVideoControlServiceImpl controlService;

    @Override
    public VideoControlService getVideoControlService() {
        return getControlService();
    }

    @Override
    public ImageCaptureService getImageCaptureService() {
        return getControlService();
    }

    private JFXVideoControlServiceImpl getControlService() {
        if (controlService == null) {
            controlService = new JFXVideoControlServiceImpl();
        }
        return controlService;
    }

    @Override
    public String getMovieLocation() {
        return null;
    }

    @Override
    public void close() {
        // TODO implement me
    }
}
