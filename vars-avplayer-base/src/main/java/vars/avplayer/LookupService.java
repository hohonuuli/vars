package vars.avplayer;

import org.mbari.util.stream.StreamUtilities;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Base interface with default methods for looking up all SPI's for implementations of VideoPlayerOld
 * @author Brian Schlining
 * @since 2016-03-24T12:55:00
 */
public interface LookupService {

    default ServiceLoader<VideoPlayer> loadVideoPlayers() {
        return ServiceLoader.load(VideoPlayer.class);
    }

    default List<VideoPlayer> findVideoPlayers(String mimeType) {
        return StreamUtilities.toStream(loadVideoPlayers().iterator())
                .filter(v -> v.canPlay(mimeType))
                .collect(Collectors.toList());
    }
}
