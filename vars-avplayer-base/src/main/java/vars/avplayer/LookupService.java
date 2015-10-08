package vars.avplayer;

import org.mbari.util.stream.StreamUtilities;

import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-10-01T16:21:00
 */
public interface LookupService {

    default ServiceLoader<VideoPlayerController> loadVideoPlayerControllers(){
        return ServiceLoader.load(VideoPlayerController.class);
    }

    default List<VideoPlayerController> findVideoControlServices(String mimeType) {
        return StreamUtilities.toStream(loadVideoPlayerControllers().iterator())
                .filter(v -> v.canPlay(mimeType))
                .collect(Collectors.toList());
    }

    default ServiceLoader<VideoPlayerAccessUI> loadVideoPlayerAccessUIs() {
        return ServiceLoader.load(VideoPlayerAccessUI.class);
    }

    CompletableFuture<List<String>> findCameraPlatforms();

    CompletableFuture<Collection<VideoMetadata>> findVideos(String platformName, Integer sequenceNumber, Integer videoNumber);

}
