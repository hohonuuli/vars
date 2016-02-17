package vars.avplayer.v2;

import vars.avplayer.ImageCaptureService;


/**
 * @author Brian Schlining
 * @since 2016-02-15T18:43:00
 */
public interface VideoPlayerController {

    /**
     *
     * @return A service used for controlling video
     */
    VideoControlService getVideoControlService();

    /**
     *
     * @return A service for grabbing images from video
     */
    ImageCaptureService getImageCaptureService();

    String getMovieLocation();

    void close();

    /**
     *
     * @param mimeType The mimetype of the media to be played
     * @return true if this controller can play that mimetype. Unfortunatly, video mime-types
     *  generally only capture the container and not the codec, so true sometimes means 'maybe'.
     *  false is returned if the mimetype is not supported by this controller
     */
    boolean canPlay(String mimeType);

    /**
     *
     * @return An identifier for this controller
     */
    String getName();
}
