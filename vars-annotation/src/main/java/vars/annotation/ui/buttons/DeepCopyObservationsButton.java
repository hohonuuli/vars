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

import java.util.Collections;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;
import org.mbari.vcr4j.IVCR;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.DeepCopyObservationsAction;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.shared.ui.FancyButton;

/**
 * <p>Performs a deep copy of an selected observation to a new time code.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class DeepCopyObservationsButton extends FancyButton {


    /**
     * Constructor
     *
     * @param toolBelt
     */
    public DeepCopyObservationsButton(ToolBelt toolBelt) {
        super();
        setAction(new DeepCopyObservationsAction(toolBelt));
        AnnotationProcessor.process(this); // wire up the EventBus
        setToolTipText("Copy an observation to a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copyanno.png")));
        setText("");
        respondTo(new ObservationsSelectedEvent(null, Collections.EMPTY_LIST));

    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondTo(ObservationsSelectedEvent event) {
        final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        final VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        final IVCR vcr = videoService;
        boolean enabled = (userAccount != null) && (event.get().size() > 0) && (vcr != null);
        setEnabled(enabled);
        getAction().setEnabled(enabled);
    }
}
