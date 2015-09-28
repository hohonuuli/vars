package vars.annotation.ui.videofile;

import com.google.common.collect.ImmutableList;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.ui.videofile.jfxmedia.JFXAccessUI;
import vars.annotation.ui.videofile.quicktime.QTAccessUI;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


/**
 * This enumeration provides access to
 * Created by brian on 1/6/14.
 */
public class VideoPlayers {

    private final List<VideoPlayer> playerList; //ImmutableList.of(new VideoPlayer("Built-in",));

    @Inject
    public VideoPlayers(AnnotationDAOFactory daoFactory) {
        List<VideoPlayer> vps = new ArrayList<>();
        buildVideoPlayer("Built-in", () -> new JFXAccessUI(daoFactory)).ifPresent(vps::add);
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
