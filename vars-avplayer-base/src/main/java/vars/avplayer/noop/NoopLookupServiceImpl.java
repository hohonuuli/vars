package vars.avplayer.noop;


import vars.avplayer.VideoMetadata;
import vars.avplayer.LookupService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2015-10-08T08:45:00
 */
public class NoopLookupServiceImpl implements LookupService {

    /**
     * @deprecated
     * @return
     */
    public CompletableFuture<List<String>> findCameraPlatforms() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    /**
     * @deprecated
     * @param platformName
     * @param sequenceNumber
     * @param videoNumber
     * @return
     */
    public CompletableFuture<Collection<VideoMetadata>> findVideos(String platformName, Integer sequenceNumber, Integer videoNumber) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}
