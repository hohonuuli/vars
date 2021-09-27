package vars.avplayer;

import mbarix4j.util.Tuple2;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.shared.rx.RXEventBus;

import java.util.concurrent.CompletableFuture;

/**
 * Implementations of this class can open videos. Use `canPlay` to see if a mimetype is playable.
 *
 * @author Brian Schlining
 * @since 2016-03-24T11:04:00
 */
public interface VideoPlayer<S extends VideoState, E extends VideoError> {

    /**
     * Connect to your video controller. This method accepts a varargs
     * as the argument so you can feed in whatever params you need.
     *
     * @param args The arguments need to connect to you video control service
     * @return A resulting pair of VideoArchive and a VideoController to manage playback
     *         of the videoarchive, or nothing if the connection failed.
     *
     */
    CompletableFuture<Tuple2<VideoArchive, VideoController<S, E>>> openVideoArchive(ToolBelt toolBelt, Object... args);

    /**
     * Connect to your video service with input from a User Interface. This
     * method should implement the UI needed to collect the parameters, then
     * call the connect method. The returned dialog may be requested once and then
     * subsequently reused by UI components so you should write it accordely.
     *
     * @param toolBelt ToolBelt for accessign the various factories
     * @param eventBus Updates to the VideoArchive and VideoController will be sent as messages
     *                 on the eventbus
     * @return A JDialog that can be used to connect to your video service
     *
     */
    VideoPlayerDialogUI<S, E> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus);

    /**
     * Used to determine if this service can open this mimetype.
     *
     * @param mimeType The mimetype of the media to be played
     * @return true if this controller can play that mimetype. Unfortunatly, video mime-types
     *  generally only capture the container and not the codec, so true sometimes means 'maybe'.
     *  false is returned if the mimetype is not supported by this controller
     */
    boolean canPlay(String mimeType);

    String getName();


}
