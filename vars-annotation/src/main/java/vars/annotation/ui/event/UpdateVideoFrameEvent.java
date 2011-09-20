package vars.annotation.ui.event;

import vars.annotation.VideoFrame;

/**
 * When a videoframe is updated this event can be occur. Components can listed for the update
 * and redraw themselves.
 * 
 * @author Brian Schlining
 * @since 2011-09-20
 */
public class UpdateVideoFrameEvent {

    private final Object updateSource;
    private final VideoFrame videoFrame;

    public UpdateVideoFrameEvent(Object updateSource, VideoFrame videoFrame) {
        this.updateSource = updateSource;
        this.videoFrame = videoFrame;
    }

    public Object getUpdateSource() {
        return updateSource;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }
}
