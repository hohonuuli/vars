package vars.annotation.ui.eventbus;

import vars.annotation.VideoFrame;

import java.util.Collection;

/**
 * Event that contains videoframes that have changed. Implementations may need to check that
 * the videoFrames were not moved to a difference VideoArchive.
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class VideoFramesChangedEvent extends UIChangeEvent<Collection<VideoFrame>> {

    public VideoFramesChangedEvent(Object updateSource, Collection<VideoFrame> refs) {
        super(updateSource, refs);
    }

}
