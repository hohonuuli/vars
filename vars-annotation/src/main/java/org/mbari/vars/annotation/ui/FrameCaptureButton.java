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


package org.mbari.vars.annotation.ui;

import foxtrot.Job;
import foxtrot.Worker;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.ui.actions.FrameCaptureAction2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Button to grab a video frame directly using the Quicktime for Java API. Uses
 * {@link org.mbari.framegrab.GrabFrame org.mbari.framegrab.GrabFrame}to
 * capture a .png file with a .jpg file containing text overlay.
 * </p>
 *
 *
 * @author  : $Author: hohonuuli $
 * @version  : $Id: FrameCaptureButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class FrameCaptureButton extends JFancyButton {

    /**
     *
     */
    private static final long serialVersionUID = -7034181609667604680L;
    private static final Logger log = LoggerFactory.getLogger(FrameCaptureButton.class);

    /**
     * Constructor for the FrameCaptureButton object
     */
    public FrameCaptureButton() {
        super();

        try {
            setAction(new WorkerAction());
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
     * @author digitaladmin
     *
     */
    private class WorkerAction extends FrameCaptureAction2 {

        /**
         *
         */
        private static final long serialVersionUID = 899661232182758639L;

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
