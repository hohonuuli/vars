package vars.avplayer;

import com.google.common.collect.ImmutableList;
import org.mbari.util.stream.StreamUtilities;
import vars.annotation.AnnotationDAOFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * This enumeration provides access to
 * Created by brian on 1/6/14.
 */
public class VideoPlayers {

    private final List<VideoPlayer> playerList; //ImmutableList.of(new VideoPlayer("Built-in",));

    @Inject
    public VideoPlayers(AnnotationDAOFactory daoFactory) {
        ServiceLoader<VideoPlayerAccessUI> serviceLoader = ServiceLoader.load(VideoPlayerAccessUI.class);
        List<VideoPlayer> vps =  StreamUtilities.toStream(serviceLoader.iterator())
                .map(vp -> new VideoPlayer(vp.getName(), vp))
                .collect(Collectors.toList());
        playerList = ImmutableList.copyOf(vps);
    }


    public List<VideoPlayer> get() { return playerList; }
}
