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
import java.util.Collection;
import org.bushe.swing.event.EventBus;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

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
            // DAOTX
            AssociationDAO dao = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
            dao.startTransaction();
            observation = dao.find(observation);
            observation.addAssociation(newAssociation);
            dao.persist(newAssociation);
            dao.validateName(newAssociation);
            dao.endTransaction();

            Collection<Observation> changedObservations = ImmutableList.of(newAssociation.getObservation());
            toolBelt.getPersistenceController().updateUI(changedObservations);
            //EventBus.publish(Lookup.TOPIC_OBSERVATION_CHANGED, newAssociation.getObservation());
        }
        observation = null;
        association = null;
    }

    private void updateAssociation(ILink link) {
        if (association != null) {
            AssociationDAO  associationDAO = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
            // DAOTX
            associationDAO.startTransaction();
            association = associationDAO.find(association);
            association.setLinkName(link.getLinkName());
            association.setLinkValue(link.getLinkValue());
            association.setToConcept(link.getToConcept());
            associationDAO.validateName(association);
            associationDAO.endTransaction();

            Collection<Observation> changedObservations = ImmutableList.of(association.getObservation());
            toolBelt.getPersistenceController().updateUI(changedObservations);
            //EventBus.publish(Lookup.TOPIC_OBSERVATION_CHANGED, association.getObservation());
        }
        observation = null;
        association = null;
    }
}
