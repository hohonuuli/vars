package vars.avplayer;


import java.net.URL;

/**
 * Descriptive information about a video file.
 * @author Brian Schlining
 * @since 2015-10-06T09:23:00
 * @param <T> the type of the location reference. May be a URL, String, File, Path, etc.
 */
public interface VideoMetadata<T> {

    /**
     *
     * @return The location of the video.
     */
    T get();

    /**
     *
     * @return The mimetype of the video
     */
    String getMimeType();

    /**
     *
     * @return The width of the video
     */
    int getWidth();

    /**
     *
     * @return The height of the video.
     */
    int getHeight();


}
