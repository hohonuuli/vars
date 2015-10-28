package vars.avplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2015-10-08T08:45:00
 */
public class EmptyLookupServiceImpl implements LookupService {

    @Override
    public CompletableFuture<List<String>> findCameraPlatforms() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Collection<VideoMetadata>> findVideos(String platformName, Integer sequenceNumber, Integer videoNumber) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}
