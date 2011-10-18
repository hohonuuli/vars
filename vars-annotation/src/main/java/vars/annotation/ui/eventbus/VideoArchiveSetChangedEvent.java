package vars.annotation.ui.eventbus;

import vars.annotation.VideoArchiveSet;

/**
 * @author Brian Schlining
 * @since 2011-10-18
 */
public class VideoArchiveSetChangedEvent extends UIChangeEvent<VideoArchiveSet> {
    public VideoArchiveSetChangedEvent(Object changeSource, VideoArchiveSet refs) {
        super(changeSource, refs);
    }
}
