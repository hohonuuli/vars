package vars.annotation.ui.eventbus;

import vars.annotation.VideoArchive;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class VideoArchiveSelectedEvent extends UISelectionEvent<VideoArchive> {
    public VideoArchiveSelectedEvent(Object selectionSource, VideoArchive refs) {
        super(selectionSource, refs);
    }
}
