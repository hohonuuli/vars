package vars.annotation.ui.imagepanel;


import vars.annotation.VideoFrame;

import vars.annotation.ui.ToolBelt;


/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class ImageAnnotationFrameController {

    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;

    public ImageAnnotationFrameController(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }
}
