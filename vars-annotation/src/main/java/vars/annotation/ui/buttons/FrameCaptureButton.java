/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.annotation.ui.buttons;

import foxtrot.Job;
import foxtrot.Worker;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.ui.video.ImageCaptureAction;
import vars.annotation.ui.ToolBelt;

/**
 * <p>
 * Button to grab a video frame directly using the Quicktime for Java API. Uses
 * {@link org.mbari.framegrab.GrabFrame org.mbari.framegrab.GrabFrame}to
 * capture a .png file with a .jpg file containing text overlay.
 * </p>
 *
 *
 * @author  : $Author: hohonuuli $
 */
public class FrameCaptureButton extends JFancyButton {


    private static final Logger log = LoggerFactory.getLogger(FrameCaptureButton.class);

    /**
     * Constructor for the FrameCaptureButton object
     */
    public FrameCaptureButton(ToolBelt toolBelt) {
        super();

        try {
            setAction(new WorkerAction(toolBelt));
        }
        catch (final Exception e) {
            log.warn("Unable to set-up frame-grabbing. You may not have Quicktime installed.", e);
        }

        // setEnabled(false);
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/fgbutton.png")));
        setToolTipText("Grab an image from the video-stream [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setText("");
    }

    /**
     * Don't hhang the UI when we grab a Frame. Use Foxtrot.
     *
     */
    private class WorkerAction extends ImageCaptureAction {


        public WorkerAction(ToolBelt toolBelt) {
            super(toolBelt);
        }

        /**
         * Method description
         *
         */
        public void doAction() {
            Worker.post(new Job() {
                public Object run() {
                    doActionInThread();
                    return null;
                }

            });
        }

        private void doActionInThread() {
            super.doAction();
        }
    }
}
