package vars.avplayer;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Controller that allows interaction with a video.
 *
 * Note that the VideoIO object should already be appropriately decorated for the particular video stream.
 * VARS is not going to modify it.
 *
 * @author Brian Schlining
 * @since 2016-03-24T11:04:00
 */
public class VideoController<S extends VideoState, E extends VideoError> extends org.mbari.vcr4j.VideoController<S, E> {

    private final ImageCaptureService imageCaptureService;

    public VideoController(ImageCaptureService imageCaptureService, VideoIO<S, E> videoIO) {
        super(videoIO);
        this.imageCaptureService = imageCaptureService;
    }

    public ImageCaptureService getImageCaptureService() {
        return imageCaptureService;
    }


    /**
     * This may need to be overridden in some cases
     * @return
     */
    public String getConnectionID() {
        return getVideoIO().getConnectionID();
    }

    public Future<Boolean> isStopped() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getVideoIO().getStateObservable().take(1).forEach(s -> future.complete(s.isStopped()));
        getVideoIO().send(VideoCommands.REQUEST_STATUS);
        return future;
    }

    public Future<Boolean> isPlaying() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getVideoIO().getStateObservable().take(1).forEach(s -> future.complete(s.isPlaying()));
        getVideoIO().send(VideoCommands.REQUEST_STATUS);
        return future;
    }

    public Future<VideoIndex> getVideoIndex() {
        CompletableFuture<VideoIndex> future = new CompletableFuture<>();
        // TODO: VARS currently requires timecode. This will not be true in future versions; will need to drop filter below
        getVideoIO().getIndexObservable()
                .filter(vi -> vi.getTimecode().isPresent())
                .take(1).forEach(future::complete);
        getVideoIO().send(VideoCommands.REQUEST_INDEX);
        return future;
    }

    public void close() {
        getVideoIO().close();
        imageCaptureService.dispose();
    }

}
