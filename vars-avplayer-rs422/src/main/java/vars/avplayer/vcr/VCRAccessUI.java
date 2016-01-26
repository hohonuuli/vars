package vars.avplayer.vcr;

import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.avplayer.VideoParams;
import vars.avplayer.VideoPlayerAccessUI;
import vars.avplayer.VideoPlayerController;
import vars.avplayer.VideoPlayerDialogUI;

import java.awt.*;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2016-01-21T16:27:00
 */
public class VCRAccessUI implements VideoPlayerAccessUI {

    @Override
    public VideoArchive createVideoArchive(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        return null;
    }

    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        return null;
    }

    @Override
    public Tuple2<VideoArchive, VideoPlayerController> openMoviePlayer(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        return null;
    }

    @Override
    public Optional<VideoArchive> findByLocation(String location, AnnotationDAOFactory daoFactory) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}