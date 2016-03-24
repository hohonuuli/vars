package vars.avplayer;

import com.google.common.collect.ImmutableList;
import org.mbari.util.stream.StreamUtilities;
import vars.annotation.AnnotationDAOFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * This enumeration provides access to
 * Created by brian on 1/6/14.
 * @deprecated
 */
public class VideoPlayers {

    private final List<VideoPlayerOld> playerList; //ImmutableList.of(new VideoPlayerOld("Built-in",));

    @Inject
    public VideoPlayers(AnnotationDAOFactory daoFactory) {
        ServiceLoader<VideoPlayerAccessUI> serviceLoader = ServiceLoader.load(VideoPlayerAccessUI.class);
        List<VideoPlayerOld> vps =  StreamUtilities.toStream(serviceLoader.iterator())
                .map(vp -> new VideoPlayerOld(vp.getName(), vp))
                .collect(Collectors.toList());
        playerList = ImmutableList.copyOf(vps);
    }


    public List<VideoPlayerOld> get() { return playerList; }
}
