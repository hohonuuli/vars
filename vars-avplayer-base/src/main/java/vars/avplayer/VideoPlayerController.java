package vars.avplayer;


/**
 * Created by brian on 1/6/14.
 */
public interface VideoPlayerController {

    VideoControlService getVideoControlService();

    ImageCaptureService getImageCaptureService();

    String getMovieLocation();

    void close();

}

