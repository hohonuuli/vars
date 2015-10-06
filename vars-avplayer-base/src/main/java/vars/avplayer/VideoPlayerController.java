package vars.avplayer;


/**
 * This is an SPI service.
 */
public interface VideoPlayerController {

    VideoControlService getVideoControlService();

    ImageCaptureService getImageCaptureService();

    String getMovieLocation();

    void close();

    boolean canPlay(String mimeType);

}

