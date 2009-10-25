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

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptMetadataDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.HistoryFactory;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;

/**
 *
 * @author brian
 */
public class NamesEditorPanelController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public NamesEditorPanelController(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
    }

    /**
     * Update a conceptName
     *
     * @param concept
     * @param name
     * @param author
     * @param nameType
     * @param userAccount
     * @return
     */
    public boolean updateConceptName(final Concept concept, final String newName, final String author,
                                     final String nameType, final String oldName, final UserAccount userAccount) {

        log.error("Entering updateConceptName method");
        boolean okToProceed = true;

        ApproveHistoryTask approveHistoryTask = toolBelt.getApproveHistoryTask();
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        ConceptNameDAO conceptNameDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptNameDAO();
        HistoryDAO historyDAO = toolBelt.getKnowledgebaseDAOFactory().newHistoryDAO();
        HistoryFactory historyFactory = toolBelt.getHistoryFactory();
        KnowledgebaseDAO knowledgebaseDAO = toolBelt.getKnowledgebaseDAO();
        KnowledgebaseFactory knowledgebaseFactory = toolBelt.getKnowledgebaseFactory();


        /*
         * Check that the name does not already exist in the database
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
            if ((matchingConcept != null) && conceptDAO.equalInDatastore(matchingConcept, concept)) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "A concept with " + "the name '" + newName + "' already exists.");
                okToProceed = false;
            }
            else {
                log.debug("'" + newName + "' does not yet exist in the knowledgebase");
            }
        }

        ConceptName oldConceptName = concept.getConceptName(oldName);
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

            /*
             * Add a History object to track the change.
             */
            History history = historyFactory.replaceConceptName(userAccount, oldConceptName, newConceptName);
            conceptMetadata.addHistory(history);
            historyDAO.makePersistent(history);

            /*
             * When updating a primary name we want to keep the older
             * name, so we add a new Concept with the old values.
             */


            if (nameType.equals(ConceptNameTypes.PRIMARY.toString())) {
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
                conceptNameDAO.makePersistent(copyCn);
            }
            else {
                oldConceptName.setName(newName);
            }

            oldConceptName.setAuthor(author);
            oldConceptName.setNameType(nameType);

            okToProceed = false;

            try {
                conceptDAO.update(concept);
                okToProceed = true;
            }
            catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to update " + concept, e);
                }

                EventBus.publish(Lookup.TOPIC_FATAL_ERROR, e);
            }

            if (okToProceed) {

                /*
                 * Update the annotations that might use the name. Ideally, the database would only contain
                 * primary names. But just in case we'll update common names and synonyms.
                 */
                if (log.isDebugEnabled()) {
                    log.debug("Changing all Observations that use '" + oldName + "' to use '" + newName + "'");
                }

                okToProceed = false;

                try {
                    knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept);
                    okToProceed = true;
                }
                catch (Exception e) {
                    String msg = "Failed to change primary names of annotations from '" + oldName + "' to '" +
                                 newName + "'.";
                    log.error(msg);
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
                }

                /*
                 * If the annotation update was successful we can drop the old conceptname that we
                 * might have created if changing a primary name
                 */
                if (okToProceed) {
                    ConceptName oldPrimaryName = concept.getConceptName(history.getOldValue());
                    if ((oldPrimaryName != null) &&
                            !oldPrimaryName.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                        concept.removeConceptName(oldPrimaryName);

                        try {
                            conceptNameDAO.makeTransient(oldPrimaryName);
                        }
                        catch (Exception ex) {
                            log.error("Failed to remove " + oldPrimaryName +
                                      " from the database. This will need to be done manually!!");
                        }
                    }
                }


                /*
                 * If the user is an admin go ahead and approve the change. Do this BEFORE you refresh the tree
                 * or your database transaction will fail because of a timestamp mismatch. (ie. Cache does not
                 * match you instance)
                 */
                if ((userAccount != null) && userAccount.isAdministrator()) {
                    approveHistoryTask.approve(userAccount, history);
                }


            }
        }

        log.debug("Exiting updateConceptName method");

        return okToProceed;
    }
}
