package vars.avplayer.rx;

import org.mbari.vcr4j.VideoError;
import org.mbari.vcr4j.VideoState;
import vars.avplayer.VideoController;
import vars.shared.rx.messages.Msg;

/**
 * @author Brian Schlining
 * @since 2016-04-06T09:11:00
 */
public class SetVideoControllerMsg<S extends VideoState, E extends VideoError> implements Msg {


    private final VideoController<S, E> videoController;

    public SetVideoControllerMsg(VideoController<S, E> videoController) {
        this.videoController = videoController;
    }

    public VideoController<S, E> getVideoController() {
        return videoController;
    }
}
