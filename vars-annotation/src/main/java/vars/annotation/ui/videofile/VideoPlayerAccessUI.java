package vars.annotation.ui.videofile;

import org.mbari.util.Tuple2;
import vars.annotation.VideoArchive;
import vars.annotation.ui.ToolBelt;

import javax.swing.*;
import java.awt.*;

/**
 * Created by brian on 1/7/14.
 */
public interface VideoPlayerAccessUI {

    VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt);

    Tuple2<VideoArchive, VideoPlayerController> openMoviePlayer(VideoParams videoParams);

}
