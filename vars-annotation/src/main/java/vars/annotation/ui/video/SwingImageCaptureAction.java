package vars.annotation.ui.video;

import foxtrot.Job;
import foxtrot.Worker;
import vars.annotation.ui.ToolBelt;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.Toolkit;

/**
 * Image capture action that does the heavy lifting off of the EDT.
 *
 * @author Brian Schlining
 * @since 2011-12-08
 */
public class SwingImageCaptureAction extends ImageCaptureAction {


        public SwingImageCaptureAction(ToolBelt toolBelt) {
            super(toolBelt);
            putValue(Action.NAME, "Frame Capture");
            putValue(Action.ACTION_COMMAND_KEY, "frame capture");
            putValue(Action.ACCELERATOR_KEY,
                  KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        /**
         * Method description
         *
         */
        @Override
        public void doAction() {
            Worker.post(new Job() {
                public Object run() {
                    SwingImageCaptureAction.super.doAction();
                    return null;
                }
            });
        }
    }