package vars.avplayer.rx;

import vars.annotation.VideoArchive;
import vars.shared.rx.messages.Msg;

/**
 * @author Brian Schlining
 * @since 2016-04-06T09:10:00
 */
public class SetVideoArchiveMsg implements Msg {
    private final VideoArchive videoArchive;

    public SetVideoArchiveMsg(VideoArchive videoArchive) {
        this.videoArchive = videoArchive;
    }

    public VideoArchive getVideoArchive() {
        return videoArchive;
    }
}
