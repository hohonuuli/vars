package vars.annotation.ui.videofile.quicktime;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.videofile.VideoPlayerAccessUI;
import vars.annotation.ui.videofile.VideoPlayerDialogUI;

import java.awt.*;

/**
 * Created by brian on 1/7/14.
 */
public class QTAccessUI implements VideoPlayerAccessUI {

    private QTOpenVideoArchiveDialog dialog;
    private Window currentParent;

    @Override
    public VideoPlayerDialogUI getOpenDialog(Window parent, ToolBelt toolBelt) {
        if (dialog != null && parent != currentParent) {
            dialog.dispose();
            dialog = null;
        }

        if (dialog == null) {
            dialog = new QTOpenVideoArchiveDialog(parent, toolBelt);
            currentParent = parent;
        }
        return dialog;
    }
}
