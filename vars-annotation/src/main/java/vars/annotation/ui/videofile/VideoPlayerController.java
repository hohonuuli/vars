package vars.annotation.ui.videofile;

import vars.annotation.VideoArchive;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

import javax.swing.*;
import java.awt.*;

/**
 * Created by brian on 1/6/14.
 */
public interface VideoPlayerController {

    VideoControlService getVideoControlService();

    ImageCaptureService getImageCaptureService();

    String getMovieLocation();

    void close();

}
