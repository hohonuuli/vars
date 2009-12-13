/*
 * @(#)DeepCopyObservationsButton.java   2009.12.10 at 12:09:45 PST
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
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.vcr.IVCR;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.DeepCopyObservationsAction;
import vars.annotation.ui.video.VideoControlService;

/**
 * <p>Performs a deep copy of an selected observation to a new time code.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class DeepCopyObservationsButton extends JFancyButton {


    /**
     * Constructor
     *
     * @param toolBelt
     */
    public DeepCopyObservationsButton(ToolBelt toolBelt) {
        super();
        setAction(new DeepCopyObservationsAction(toolBelt));
        setToolTipText("Copy an observation to a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copyanno.png")));
        setEnabled(false);
        setText("");

        /*
         * Enable this button if someone is logged in AND the Observation
         * in the ObservationDispather is not null and the VCR is enabled.
         */
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                Collection<Observation> obs = (Collection<Observation>) evt.getNewValue();

                final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                final VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
                final IVCR vcr = videoService;
                if ((userAccount != null) && (obs.size() > 0) && (vcr != null)) {
                    setEnabled(true);
                }
                else {
                    setEnabled(false);
                }

            }
        });

    }
}
