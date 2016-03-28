package vars.avplayer.jfx;

import org.mbari.util.Tuple2;
import vars.ToolBelt;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.avplayer.AbstractAccessUI;
import vars.avplayer.VideoPlayerDialogUI;

import java.awt.*;
import java.util.Optional;

/**
 * Created by brian on 1/7/14.
 */
public class JFXAccessUI extends AbstractAccessUI {

    private VideoPlayerDialogUI dialog;
    private Window currentParent;
    private JFXVideoControlServiceImpl controller = new JFXVideoControlServiceImpl();


    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        // dispose of old dialog if the parent window reference changes
        if (dialog != null && parent != currentParent) {
            //dialog.dispose();
            dialog = null;
        }

        // create new dialog if needed
        if (dialog == null) {
            dialog = new DefaultVideoPlayerDialogUI(parent, toolBelt, this);
            currentParent = parent;
        }
        return dialog;
    }

    @Override
    public Tuple2<VideoArchive, VideoPlayerController> openMoviePlayer(VideoParams videoParams, AnnotationDAOFactory daoFactory) {
        Optional<VideoArchive> videoArchiveOpt = findByLocation(videoParams.getMovieLocation(), daoFactory);
        VideoArchive videoArchive = videoArchiveOpt.orElseGet(() -> createVideoArchive(videoParams, daoFactory));
        controller.getVideoControlService().connect(videoArchive.getName());
        return new Tuple2<>(videoArchive, controller);
    }

    @Override
    public String getName() {
        return "Java FX";
    }
}
