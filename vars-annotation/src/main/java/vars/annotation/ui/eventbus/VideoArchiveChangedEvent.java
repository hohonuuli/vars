package vars.annotation.ui.eventbus;

import vars.annotation.VideoArchive;

/**
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class VideoArchiveChangedEvent extends UIChangeEvent<VideoArchive> {

    public VideoArchiveChangedEvent(Object updateSource, VideoArchive videoArchive) {
        super(updateSource, videoArchive);
    }

}
