package vars.annotation.ui.videofile.jfxmedia;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.videofile.VideoPlayerAccessUI;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;

import java.awt.*;

/**
 * Created by brian on 1/7/14.
 */
public class JFXAccessUI implements VideoPlayerAccessUI {

    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        return null;
    }
}
