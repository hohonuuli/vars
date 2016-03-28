package vars.avplayer;

import com.google.common.collect.ImmutableList;
import org.mbari.util.stream.StreamUtilities;
import vars.annotation.AnnotationDAOFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * Loads available VideoPlayer SPI's
 * Created by brian on 1/6/14.
 */
public class VideoPlayers {


    public List<VideoPlayer> get() {
        return StreamUtilities.toStream(loadVideoPlayers().iterator())
            .collect(Collectors.toList());
    }

    public ServiceLoader<VideoPlayer> loadVideoPlayers() {
        return ServiceLoader.load(VideoPlayer.class);
    }

    public List<VideoPlayer> findVideoPlayers(String mimeType) {
        return StreamUtilities.toStream(loadVideoPlayers().iterator())
                .filter(v -> v.canPlay(mimeType))
                .collect(Collectors.toList());
    }
}
