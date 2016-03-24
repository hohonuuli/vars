package vars.avplayer;

import vars.avplayer.VideoController;

import javax.swing.JDialog;
import java.util.Optional;

/**
 * Implementations of this class can open videos. Use `canPlay` to see if a mimetype is playable.
 *
 * @author Brian Schlining
 * @since 2016-03-24T11:04:00
 */
public interface VideoPlayer {

    /**
     * Connect to your video controller. This method accepts a varargs
     * as the argument so you can feed in whatever params you need.
     *
     * @param args The arguments need to connect to you video control service
     * @return A resulting VideoController or nothing if the connection failed
     *
     */
    Optional<VideoController> connect(Object... args);

    /**
     * Connect to your video service with input from a User Interface. This
     * method should implement the UI needed to collect the parameters, then
     * call the connect method. The returned dialog may be requested once and then
     * subsequently reused by UI components so you should write it accordely.
     *
     * @return A JDialog that can be used to connect to your video service
     */
    JDialog getConnectionDialog();

    /**
     * Used to determine if this service can open this mimetype.
     *
     * @param mimeType The mimetype of the media to be played
     * @return true if this controller can play that mimetype. Unfortunatly, video mime-types
     *  generally only capture the container and not the codec, so true sometimes means 'maybe'.
     *  false is returned if the mimetype is not supported by this controller
     */
    boolean canPlay(String mimeType);



}
