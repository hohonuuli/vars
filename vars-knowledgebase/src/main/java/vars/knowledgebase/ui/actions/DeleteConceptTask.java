/*
 * @(#)DeleteConceptTask.java   2009.10.29 at 01:10:32 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.actions;

import java.awt.Frame;
import java.util.Collection;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.ui.Lookup;

/**
 * Class description
 * @version        $date$, 2009.10.01 at 01:47:48 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class DeleteConceptTask {

    private static final Logger log = LoggerFactory.getLogger(DeleteConceptTask.class);
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final AnnotationDAOFactory annotationDAOFactory;

    /**
     * Constructs ...
     *
     * @param conceptDAO
     * @param observationDAO
     */
    public DeleteConceptTask(AnnotationDAOFactory annotationDAOFactory, KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.annotationDAOFactory = annotationDAOFactory;

    }

    public boolean delete(Concept concept) {
        boolean okToProceed = (concept != null);
        final String rejectedName = concept.getPrimaryConceptName().getName();
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();

        /*
         * Look up the concept that we're deleting. Make sure it exists and it's not the root concept
         */
        if (okToProceed) {
            okToProceed = ((concept != null) && (concept.getConceptName(ConceptName.NAME_DEFAULT) == null));
        }

        /*
         * Let the user know jsut how much damage their about to do to the database
         */
        if (okToProceed) {
            Collection<Concept> deletedConcepts;
            try {
                
                conceptDAO.startTransaction();
                deletedConcepts = conceptDAO.findDescendents(concept);
                conceptDAO.endTransaction();
                Dispatcher dispatcher = Lookup.getApplicationFrameDispatcher();
                Frame frame = (Frame) dispatcher.getValueObject();
                final int option = JOptionPane .showConfirmDialog( frame,
                        "You are about to delete " + deletedConcepts.size() +
                        " concept(s) from the \nknowledgebase. Are you sure you want to continue?",
                        "VARS - Delete Concepts",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                okToProceed = (option == JOptionPane.YES_OPTION);
            }
            catch (Exception e) {
                log.error("Failed to fetch concepts from the database", e);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to fetch concepts from database");
                okToProceed = false;
            }

        }

        /*
         * Get all concept-names that will be deleted. Use those to find all the Observations that
         * will be affected.
         */
        Collection<Observation> observations = null;
        if (okToProceed) {
            try {
                ObservationDAO observationDAO = annotationDAOFactory.newObservationDAO();
                observationDAO.startTransaction();
                observations = observationDAO.findAllByConcept(concept, true);
                observationDAO.endTransaction();
            }
            catch (Exception e) {
                log.error("Failed to fetch observations from database", e);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Unable to look up Observations from database");
                okToProceed = false;
            }
        }

        /*
         * If observations were found that should be modified, give the user a
         * choice about how to modify them.. i.e. leave them alone or change to the name of the parentConcept
         */
        if (okToProceed && (observations.size() > 0)) {
            okToProceed = handleObservations(observations, concept);
        }

        /*
         * Delete the concept
         */
        if (okToProceed) {
            try {
                conceptDAO.cascadeRemove(concept);
            }
            catch (Exception e) {
                final String msg = "Failed to delete '" + rejectedName + "'";
                log.error(msg, e);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                okToProceed = false;
            }
        }

        return okToProceed;
    }

    private boolean handleObservations(final Collection<Observation> observations, final Concept concept) {
        boolean okToProceed = true;
        final String deletedName = concept.getPrimaryConceptName().getName();
        final Concept parentConcept = concept.getParentConcept();
        final String newName = parentConcept.getPrimaryConceptName().getName();

        final String msg = observations.size() + " Observations were found using '" + deletedName +
                           "' or one of it's \nchildren. Do you want to update the names to '" + newName +
                           "' or \nignore them and leave them as is?";

        /*
         * Report the usages to the user. Allow them to replace with parent concept or leave as is.
         */
        final Object[] options = { "Update", "Ignore", "Cancel" };
        final Dispatcher dispatcher = Lookup.getApplicationFrameDispatcher();
        Frame frame = (Frame) dispatcher.getValueObject();
        final int option = JOptionPane.showOptionDialog(frame, msg, "VARS - Removing '" + deletedName + "'",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);


        switch (option) {
        case JOptionPane.YES_OPTION:
            okToProceed = update(observations, newName);

            break;

        case JOptionPane.NO_OPTION:
            break;

        default:
            okToProceed = false;

            break;
        }

        return okToProceed;
    }

    private boolean update(final Collection<Observation> observations, final String newName) {
        boolean success = true;
        ObservationDAO observationDAO = annotationDAOFactory.newObservationDAO();
        observationDAO.startTransaction();
        for (Observation observation : observations) {
            final String oldName = observation.getConceptName();
            observation.setConceptName(newName);

            try {
                observationDAO.merge(observation);
            }
            catch (Exception e) {
                observation.setConceptName(oldName);
                log.error("Failed to change the name of " + observation + " to '" + newName + "'", e);
                success = false;

                break;
            }
        }
        observationDAO.endTransaction();

        return success;
    }
}
