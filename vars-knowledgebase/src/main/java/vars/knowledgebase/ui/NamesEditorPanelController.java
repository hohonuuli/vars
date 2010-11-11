/*
 * @(#)NamesEditorPanelController.java   2009.10.24 at 08:40:08 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.WaitIndicator;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebasePersistenceService;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;
import vars.knowledgebase.ui.dialogs.AddConceptNameDialog2;

/**
 *
 * @author brian
 */
class NamesEditorPanelController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;
    private final NamesEditorPanel namesEditorPanel;
    private NewAction newAction;

    private final HistoryFactory historyFactory;

    /**
     * Constructs ...
     *
     * @param namesEditorPanel The panel to be controlled.
     */
    NamesEditorPanelController(NamesEditorPanel namesEditorPanel) {
        this.namesEditorPanel = namesEditorPanel;
        this.toolBelt = namesEditorPanel.getToolBelt();
        historyFactory = toolBelt.getHistoryFactory();
    }


    void newConceptName() {
        if (newAction == null) {
            newAction = new NewAction();
        }
        newAction.doAction();
    }

    void deleteConceptName(ConceptName conceptName) {

            int value = JOptionPane.showConfirmDialog(namesEditorPanel,
                "Do you want to mark '" + conceptName.getName() + "' for deletion?", "VARS - Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            if (value == JOptionPane.YES_OPTION) {
                WaitIndicator waitIndicator = new WaitIndicator(namesEditorPanel);
                History history = historyFactory.delete(userAccount, conceptName);
                DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
                dao.startTransaction();
                conceptName = dao.merge(conceptName);
                conceptName.getConcept().getConceptMetadata().addHistory(history);
                dao.persist(history);
                dao.endTransaction();
                dao.close();
                waitIndicator.dispose();
                EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);
                
            }

    }
    
        
    

    /**
     * Update a conceptName
     *
     * @param concept
     * @param newName
     * @param author
     * @param nameType
     * @param userAccount
     * @return
     */
    public boolean updateConceptName(Concept concept, final String newName, final String author,
            final String nameType, final String oldName, final UserAccount userAccount) {


        boolean okToProceed = true;

        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        KnowledgebasePersistenceService kbPersistenceService = toolBelt.getKnowledgebasePersistenceService();
        AnnotationPersistenceService annoPersistenceService = toolBelt.getAnnotationPersistenceService();
        KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();


        /*
         * ---- Step 1: Check that the name does not already exist in the database
         */
        Concept matchingConcept = null;
        if (okToProceed) {
            log.debug("Verifying that '" + newName + "' does not already exist in the knowledgebase");

            try {
                matchingConcept = conceptDAO.findByName(newName);
            }
            catch (Exception e1) {
                if (log.isErrorEnabled()) {
                    log.error("A search for '" + newName + "' in the database failed", e1);
                }

                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                okToProceed = false;
            }
        }

        if (okToProceed) {
            if ((matchingConcept != null) && !conceptDAO.equalInDatastore(matchingConcept, concept)) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "A concept with " + "the name '" + newName + "' already exists.");
                okToProceed = false;
            }
            else {
                log.debug("'" + newName + "' does not yet exist in the knowledgebase");
            }
        }

        if (okToProceed) {

            log.debug("Updating the conceptName");

            // TODO may need to load lazy relations.
            ConceptMetadata conceptMetadata = concept.getConceptMetadata();
            log.debug("Inspecting " + conceptMetadata + "\n" + conceptMetadata.getHistories().toString() + "\n");

            /*
             * Make the changes and update the database
             */
            ConceptName newConceptName = knowledgebaseFactory.newConceptName();
            newConceptName.setName(newName);
            newConceptName.setAuthor(author);
            newConceptName.setNameType(nameType);

            DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
            dao.startTransaction();
            concept = dao.merge(concept);
            ConceptName oldConceptName = concept.getConceptName(oldName);

            /*
             * Add a History object to track the change.
             */
            History history = historyFactory.replaceConceptName(userAccount, oldConceptName, newConceptName);
            concept.getConceptMetadata().addHistory(history);
            dao.persist(history);

            /*
             * When updating a primary name we want to keep the older
             * name, so we add a new Concept with the old values.
             */
            
            if (nameType.equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                ConceptName copyCn = knowledgebaseFactory.newConceptName();
                copyCn.setName(oldConceptName.getName());
                copyCn.setAuthor(oldConceptName.getAuthor());
                copyCn.setNameType(ConceptNameTypes.SYNONYM.toString());

                /*
                 * Have to update the original concept before adding the
                 * copy. Otherwise they will have the same names and the
                 * concept won't allow duplicate names to be added.
                 */
                oldConceptName.setName(newName);
                concept.addConceptName(copyCn);
                dao.persist(copyCn);
            }
            else {
                oldConceptName.setName(newName);
            }

            oldConceptName.setAuthor(author);
            oldConceptName.setNameType(nameType);
            dao.endTransaction();


            try {
                /*
               * Update the annotations that might use the name. Ideally, the database would only contain
               * primary names. But just in case we'll update common names and synonyms.
               */
              if (log.isDebugEnabled()) {
                  log.debug("Changing all Observations that use '" + oldName + "' to use '" + newName + "'");
              }
                kbPersistenceService.updateConceptNameUsedByLinkTemplates(concept);
                annoPersistenceService.updateConceptNameUsedByAnnotations(concept);
            }
            catch (Exception e) {
                String msg = "Failed to change primary names of annotations from '" + oldName + "' to '" +
                             newName + "'.";
                log.error(msg);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            }

            /*
             * If the user is an admin go ahead and approve the change. Do this BEFORE you refresh the tree
             * or your database transaction will fail because of a timestamp mismatch. (ie. Cache does not
             * match you instance)
             */
            EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);

        }

        conceptDAO.close();

        return okToProceed;
    }
    
    /**
     * This action sets up the AddConceptNameDialog. Most of the work is done in the dialog, not here.
     */
    private class NewAction extends ActionAdapter {

 
        private AddConceptNameDialog2 dialog;

        public void doAction() {
            getDialog().setVisible(true);
        }

        private AddConceptNameDialog2 getDialog() {
            if (dialog == null) {
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                dialog = new AddConceptNameDialog2(frame, true, toolBelt);

                /*
                 * Set the currently selected concept
                 */
                Dispatcher dispatcher = Lookup.getSelectedConceptDispatcher();
                dialog.setConcept((Concept) dispatcher.getValueObject());

                /*
                 * The dialog needs a reference to the currently selected
                 * concept. We do that by listening to the appropriate
                 * dispatcher.
                 */
                dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        dialog.setConcept((Concept) evt.getNewValue());
                    }
                });
            }

            return dialog;
        }
    }

}
