package vars.annotation.ui.videofile.jfxmedia;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.videofile.VideoPlayerAccessUI;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;
import vars.annotation.ui.videofile.quicktime.QTOpenVideoArchiveDialog;

import java.awt.*;

/**
 * Created by brian on 1/7/14.
 */
public class JFXAccessUI implements VideoPlayerAccessUI {

    private JFXOpenVideoArchiveDialog dialog;
    private Window currentParent;

    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        // dispose of old dialog if the parent window reference changes
        if (dialog != null && parent != currentParent) {
            dialog.dispose();
            dialog = null;
        }

        // create new dialog if needed
        if (dialog == null) {
            dialog = new JFXOpenVideoArchiveDialog(parent, toolBelt);
            currentParent = parent;
        }
        return dialog;
    }
}
