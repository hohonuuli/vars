package vars.avplayer.v2;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoIO;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.VideoState;
import org.mbari.vcr4j.commands.VideoCommands;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brian Schlining
 * @since 2016-02-15T18:54:00
 */
public class VideoIOState<S extends VideoState, E extends VideoError> {

    private final VideoIO<S, E> videoIO;

    public VideoIOState(VideoIO<S, E> videoIO) {
        this.videoIO = videoIO;
    }

    public Future<Boolean> isStopped() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        videoIO.getStateObservable().take(1).forEach(s -> future.complete(s.isStopped()));
        videoIO.send(VideoCommands.REQUEST_STATUS);
        return future;
    }

    public Future<Boolean> isPlaying() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        videoIO.getStateObservable().take(1).forEach(s -> future.complete(s.isPlaying()));
        videoIO.send(VideoCommands.REQUEST_STATUS);
        return future;
    }

    public Future<VideoIndex> requestVideoIndex() {
        CompletableFuture<VideoIndex> future = new CompletableFuture<>();
        videoIO.getIndexObservable().take(1).forEach(future::complete);
        videoIO.send(VideoCommands.REQUEST_INDEX);
        return future;
    }
}
