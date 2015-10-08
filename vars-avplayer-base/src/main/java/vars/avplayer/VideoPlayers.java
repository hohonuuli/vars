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


/**
 * This enumeration provides access to
 * Created by brian on 1/6/14.
 */
public class VideoPlayers {

    private final List<VideoPlayer> playerList; //ImmutableList.of(new VideoPlayer("Built-in",));

    @Inject
    public VideoPlayers(AnnotationDAOFactory daoFactory) {
        ServiceLoader<VideoPlayerAccessUI> serviceLoader = ServiceLoader.load(VideoPlayerAccessUI.class);
        StreamUtilities.toStream(serviceLoader)
                .map(vp -> buildVideoPlayer())
        List<VideoPlayer> vps = new ArrayList<>();
        //buildVideoPlayer("Built-in", () -> new JFXAccessUI(daoFactory)).ifPresent(vps::add);
        //buildVideoPlayer("QuickTime", () -> new QTAccessUI(daoFactory)).ifPresent(vps::add);
        //buildVideoPlayer("Mac OS X", () -> new AVFAccessUI(daoFactory)).ifPresent(vps::add);
        playerList = ImmutableList.copyOf(vps);
    }

    private Optional<VideoPlayer> buildVideoPlayer(String name, Supplier<VideoPlayerAccessUI> factory) {
        Optional<VideoPlayer> vpOpt;
        try {
            vpOpt = Optional.of(new VideoPlayer(name, factory.get()));
        }
        catch (Exception e) {
            vpOpt = Optional.empty();
        }
        return vpOpt;
    }

    public List<VideoPlayer> get() { return playerList; }
}
