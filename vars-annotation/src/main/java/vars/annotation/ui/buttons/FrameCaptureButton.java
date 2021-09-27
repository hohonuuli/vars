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

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import mbarix4j.swing.SwingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.VideoArchive;

import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.video.SwingImageCaptureAction;
import vars.avplayer.VideoController;
import vars.shared.ui.DoNothingAction;
import vars.shared.ui.FancyButton;
import vars.avplayer.ImageCaptureService;

/**
 * <p>
 * Button to grab a video frame directly using the Quicktime for Java API.
 * Captures a .png file with a .jpg file containing text overlay.
 * </p>
 *
 *
 * @author  : $Author: hohonuuli $
 */
public class FrameCaptureButton extends FancyButton {


    private static final Logger log = LoggerFactory.getLogger(FrameCaptureButton.class);
    private Action action;

    /**
     * Constructor for the FrameCaptureButton object
     */
    public FrameCaptureButton(ToolBelt toolBelt) {
        super();

        try {
            action = new SwingImageCaptureAction(toolBelt);
            setAction(action);
        }
        catch (final Exception e) {
            action = new DoNothingAction();
            log.warn("Unable to set-up frame-grabbing. You may not have Quicktime installed.", e);
        }

        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/fgbutton.png")));
        setToolTipText("Grab an image from the video-stream [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setText("");

        StateLookup.videoControllerProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));
        StateLookup.videoArchiveProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));
        StateLookup.userAccountProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));

        setEnabled(checkEnable());
    }

    public boolean checkEnable() {
        VideoController videoController = StateLookup.getVideoController();
        boolean enabled = false;
        if (videoController != null) {
            ImageCaptureService ics = videoController.getImageCaptureService();
            VideoArchive videoArchive = StateLookup.getVideoArchive();
            UserAccount userAccount = StateLookup.getUserAccount();
            enabled = ics != null && videoArchive != null && userAccount != null;
        }
        return enabled;
    }


}
