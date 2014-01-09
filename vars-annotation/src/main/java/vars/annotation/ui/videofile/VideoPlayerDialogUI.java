package vars.annotation.ui.videofile;

import org.mbari.util.Tuple2;
import vars.annotation.VideoArchive;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

import java.util.function.Consumer;

/**
 * Created by brian on 1/7/14.
 */
public interface VideoPlayerDialogUI {

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
     * @param fn A function that handles all the needed steps when the Okay button of the dialog is pressed. A reference
     *           to <i>this</i> VideoPlayerDialogUI instance should be passed in.
     */
    void onOkay(Consumer<Void> fn);

    /**
     * This will be called by the onOkay method. It should return the open VideoArchive object as well as
     * controller to manage video playback.
     * @return
     */
    Tuple2<VideoArchive, VideoPlayerController> openVideoArchive();
}
