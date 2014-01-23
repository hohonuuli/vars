package vars.annotation.ui.videofile.jfxmedia;

import vars.annotation.ui.videofile.VideoPlayerController;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

/**
 * Created by brian on 1/7/14.
 */
public class JFXController implements VideoPlayerController {

    private JFXVideoControlServiceImpl controlService = new JFXVideoControlServiceImpl();

    @Override
    public VideoControlService getVideoControlService() {
        return controlService;
    }

    @Override
    public ImageCaptureService getImageCaptureService() {
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
