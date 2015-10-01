package vars.avplayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2015-10-01T16:21:00
 */
public interface LookupService {

    CompletableFuture<List<String>> findCameraPlatforms();

}
