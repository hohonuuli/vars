package vars.avplayer;

/**
 * Created by brian on 1/22/14.
 * @deprecated Use VideoController instead
 */
public class VideoPlayerOld {

    private final String name;
    private final VideoPlayerAccessUI accessUI;

    public VideoPlayerOld(String name, VideoPlayerAccessUI accessUI) {
        this.name = name;
        this.accessUI = accessUI;
    }

    public String getName() {
        return name;
    }

    public VideoPlayerAccessUI getAccessUI() {
        return accessUI;
    }


}
