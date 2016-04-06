package vars.avplayer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import vars.annotation.VideoArchive;

/**
 * @author Brian Schlining
 * @since 2016-04-06T09:05:00
 */
public class AVPlayerState {

    private static final ObjectProperty<VideoArchive> videoArchive = new SimpleObjectProperty<>();
    private static final ObjectProperty<VideoController> videoController = new SimpleObjectProperty<>();


    public static VideoArchive getVideoArchive() {
        return videoArchive.get();
    }

    public static ObjectProperty<VideoArchive> videoArchiveProperty() {
        return videoArchive;
    }

    public static void setVideoArchive(VideoArchive videoArchive) {
        AVPlayerState.videoArchive.set(videoArchive);
    }

    public static VideoController getVideoController() {
        return videoController.get();
    }

    public static ObjectProperty<VideoController> videoControllerProperty() {
        return videoController;
    }

    public static void setVideoController(VideoController videoController) {
        AVPlayerState.videoController.set(videoController);
    }
}
