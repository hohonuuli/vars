package vars.avplayer;


import java.net.URL;

/**
 * Descriptive information about a video file.
 * @author Brian Schlining
 * @since 2015-10-06T09:23:00
 * @deprecated This doens't make sense with our new video workflows.
 */
public interface VideoMetadata {

    /**
     *
     * @return The URL used to read the video
     */
    URL getURL();

    /**
     *
     * @return The mimetype of the video
     */
    String getMimeType();

    int getWidth();

    int getHeight();


}
