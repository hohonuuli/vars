package vars.annotation.ui.videofile;

import vars.annotation.ui.ToolBelt;

import javax.swing.*;
import java.awt.*;

/**
 * Created by brian on 1/7/14.
 */
public interface VideoPlayerAccessUI {

    VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt);

}
