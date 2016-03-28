package vars.avplayer;

import org.mbari.util.Tuple2;
import org.mbari.vcr4j.*;
import vars.ToolBelt;
import vars.annotation.VideoArchive;

/**
 * Created by brian on 1/7/14.
 */
public interface VideoPlayerDialogUI<S extends VideoState, E extends VideoError> {

    /**
     * Same behavior as swing's setVisible methods
     * @param visible
     */
    void setVisible(boolean visible);

    /**
     * Same behavior as JDialog's setLocation method
     * @param x
     * @param y
     */
    void setLocation(int x, int y);

    int getHeight();

    /**
     *
     * @param fn A function that handles all the needed steps when the Okay button of the dialog is pressed.
     */
    void onOkay(Runnable fn);

    /**
     * This will be called by the onOkay method. It should return the open VideoArchive object as well as
     * controller to manage video playback.
     * @return
     */
    Tuple2<VideoArchive, VideoController<S, E>> openVideoArchive();


}
