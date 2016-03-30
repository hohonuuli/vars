/*
 * @(#)AssociationEditorPanelLiteController.java   2009.12.15 at 02:17:42 PST
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



package vars.annotation.ui.roweditor;

import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.EventBus;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddAssociationCmd;
import vars.annotation.ui.commandqueue.impl.ChangeAssociationsCmd;

/**
 *
 *
 * @version        Enter version here..., 2009.12.15 at 02:17:42 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AssociationEditorPanelController {

    private Association association;
    private Observation observation;
    private final AssociationEditorPanel panel;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     * @param panel
     */
    public AssociationEditorPanelController(ToolBelt toolBelt, AssociationEditorPanel panel) {
        super();
        this.toolBelt = toolBelt;
        this.panel = panel;
    }

    /**
     *
     * @param observation
     * @param association
     */
    public void setTarget(Observation observation, Association association) {
        this.observation = observation;
        this.association = association;
    }

    public Observation getObservation() {
        return observation;
    }

    public Association getAssociation() {
        return association;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }


    public void doOkay(ILink link) {
        if (association == null) {
            addAssociation(link);
        }
        else {
            updateAssociation(link);
        }
    }

    private void addAssociation(ILink link) {
        if (observation != null) {
            Association newAssociation = toolBelt.getAnnotationFactory().newAssociation(link.getLinkName(),
                    link.getToConcept(), link.getLinkValue());
            Command command = new AddAssociationCmd(newAssociation, ImmutableList.of(observation));
            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);
        }
        observation = null;
        association = null;
    }

    private void updateAssociation(ILink link) {
        if (association != null) {
            ILink copyOfLink = toolBelt.getAnnotationFactory().newAssociation(link);
            Command command = new ChangeAssociationsCmd(copyOfLink, ImmutableList.of(association));
            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);
        }
        observation = null;
        association = null;
    }
}
