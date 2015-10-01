package vars.avplayer;

/**
 * Created by brian on 1/22/14.
 */
public class VideoPlayer {

    private final String name;
    private final VideoPlayerAccessUI accessUI;

    public VideoPlayer(String name, VideoPlayerAccessUI accessUI) {
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
