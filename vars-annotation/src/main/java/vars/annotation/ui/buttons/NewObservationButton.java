/*
 * @(#)NewVideoFrameButton.java   2009.11.15 at 08:33:19 PST
 *
 * Copyright 2009 MBARI
 *
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
import org.mbari.swing.SwingUtils;
import vars.UserAccount;
import vars.annotation.VideoArchive;

import vars.annotation.ui.StateLookup;
import vars.annotation.ui.actions.NewObservationAction;
import vars.annotation.ui.ToolBelt;
import vars.avplayer.VideoController;
import vars.shared.ui.FancyButton;


/**
 * <p>A button that calls the <code>NewVideoFrameAction</code> </p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class NewObservationButton extends FancyButton {

    private final Action action;


    /**
     * Constructor for the NewVideoFrameButton object
     */
    public NewObservationButton(ToolBelt toolBelt) {
        super();
        action = new NewObservationAction(toolBelt);
        setAction(action);
        setToolTipText("Create an Observation with a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) action.getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_new.png")));


        StateLookup.videoControllerProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));
        StateLookup.videoArchiveProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));
        StateLookup.userAccountProperty().addListener((obs, oldVal, newVal) -> setEnabled(checkEnable()));

        setEnabled(checkEnable());

        setText("");

    }

    /**
     * <p>Enable this button if someone is logged in AND a videoarchvie set is
     * open and the VCR exists.</p>
     */
    public boolean checkEnable() {
        VideoController videoController = StateLookup.getVideoController();
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        UserAccount userAccount = StateLookup.getUserAccount();
        return videoController != null && videoArchive != null && userAccount != null;
    }
}
