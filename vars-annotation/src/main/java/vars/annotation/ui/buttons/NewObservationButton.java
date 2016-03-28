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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.SwingUtils;
import vars.UserAccount;
import vars.annotation.VideoArchive;

import vars.annotation.ui.actions.NewObservationAction;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;
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

        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setEnabled(checkEnable());
            }
        };

        Lookup.getVideoControlServiceDispatcher().addPropertyChangeListener(listener);
        Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(listener);
        Lookup.getUserAccountDispatcher().addPropertyChangeListener(listener);

        setEnabled(checkEnable());

        setText("");

    }

    /**
     * <p>Enable this button if someone is logged in AND a videoarchvie set is
     * open and the VCR exists.</p>
     */
    public boolean checkEnable() {
        VideoControlService vcs = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        UserAccount ua = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        return vcs != null && va != null && ua != null;
    }
}
