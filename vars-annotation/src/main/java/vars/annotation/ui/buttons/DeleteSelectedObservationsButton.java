/*
 * @(#)DeleteSelectedObservationsButton.java   2009.11.13 at 03:21:06 PST
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

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.SwingUtils;
import vars.UserAccount;
import vars.annotation.ui.actions.DeleteSelectedObservationsWithConfirmAction;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.shared.ui.FancyButton;

import java.util.Collections;

/**
 * <p>Deletes the observations selected in the Table. This button will bring
 * up a dialog prompting the user to confirm the delete.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class DeleteSelectedObservationsButton extends FancyButton {


    /**
     * Constructor
     */
    public DeleteSelectedObservationsButton(ToolBelt toolBelt) {
        super();
        setAction(new DeleteSelectedObservationsWithConfirmAction(toolBelt.getPersistenceController()));
        setToolTipText("Delete selected observations [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_delete.png")));
        setText("");
        AnnotationProcessor.process(this);
        onSelectedObservationsEvent(new ObservationsSelectedEvent(null, Collections.EMPTY_LIST));
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void onSelectedObservationsEvent(ObservationsSelectedEvent selectionEvent) {
        final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        boolean enabled = (userAccount != null) && selectionEvent.get().size() > 0;
        setEnabled(enabled);
        getAction().setEnabled(enabled);
    }
}
