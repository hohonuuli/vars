package vars.annotation.ui.videofile.quicktime;

import vars.annotation.ui.videofile.VideoPlayerController;
import vars.quicktime.QTVideoControlServiceImpl;

import javax.swing.*;

/**
 * Created by brian on 1/6/14.
 */
public class QTController implements VideoPlayerController {
    private QTVideoControlServiceImpl controlService;

    @Override
    public QTVideoControlServiceImpl getVideoControlService() {
        return getControlService();
    }

    @Override
    public QTVideoControlServiceImpl getImageCaptureService() {
        return getControlService();
    }

    @Override
    public String getMovieLocation() {
        return null;
    }

    private QTVideoControlServiceImpl getControlService() {
        if (controlService == null) {
            controlService = new QTVideoControlServiceImpl();
        }
        return controlService;
    }

    @Override
    public void close() {
        if (controlService != null) {
            controlService.dispose();
        }
    }
}
