/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vars.knowledgebase.ui;

import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptDelegate;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.History;
import org.mbari.vars.knowledgebase.model.HistoryFactory;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.actions.ApproveHistoryTask;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.IUserAccount;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDelegate;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;

/**
 *
 * @author brian
 */
public class NamesEditorPanelController {

    private static final Logger log = LoggerFactory.getLogger(NamesEditorPanelController.class);

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
            final String nameType, final String oldName, final IUserAccount userAccount) {
        
        log.error("Entering updateConceptName method");
        boolean okToProceed = true;

        /*
         * Check that the name does not already exist in the database
         */
        IConcept matchingConcept = null;
        if (okToProceed) {
            log.debug("Verifying that '" + newName + "' does not already exist in the knowledgebase");
            try {
                matchingConcept = KnowledgeBaseCache.getInstance().findConceptByName(newName);
            }
            catch (DAOException e1) {
                if (log.isErrorEnabled()) {
                    log.error("A search for '" + newName + "' in the database failed", e1);
                }
                AppFrameDispatcher.showErrorDialog("Failed to connect to the database");
                okToProceed = false;
            }
        }

        if (okToProceed) {
            if ((matchingConcept != null) && (matchingConcept.getId() != concept.getId())) {
                AppFrameDispatcher.showWarningDialog("A concept with " + "the name '" + newName +
                        "' already exists.");
                okToProceed = false;
            }
            else {
                log.debug("'" + newName + "' does not yet exist in the knowledgebase");
            }
        }

        IConceptName oldConceptName = concept.getConceptName(oldName);
        if (okToProceed) {
            
            log.debug("Updating the conceptName");

            if (concept.isConceptDelegateLoaded()) {
                log.debug("ConceptDelegate is not yet loaded");
            }

            IConceptDelegate conceptDelegate = concept.getConceptDelegate();
            log.debug("Inspecting " + conceptDelegate + "\n" +
                    conceptDelegate.getHistorySet().toString() + "\n");

            /*
             * Make the changes and update the database
             */
            
            ConceptName newConceptName = new ConceptName();
            newConceptName.setName(newName);
            newConceptName.setAuthor(author);
            newConceptName.setNameType(nameType);

            /*
             * Add a History object to track the change.
             */
            IHistory history = HistoryFactory.replaceConceptName(userAccount, oldConceptName, newConceptName);
            concept.addHistory(history);

            /*
             * When updating a primary name we want to keep the older
             * name, so we add a new Concept with the old values.
             */

            if (nameType.equals(ConceptName.NAMETYPE_PRIMARY)) {
                ConceptName copyCn = new ConceptName();
                copyCn.setName(oldConceptName.getName());
                copyCn.setAuthor(oldConceptName.getAuthor());
                copyCn.setNameType(ConceptName.NAMETYPE_SYNONYM);

                /*
                 * Have to update the original concept before adding the
                 * copy. Otherwise they will have the same names and the
                 * concept won't allow duplicate names to be added.
                 */
                oldConceptName.setName(newName);
                concept.addConceptName(copyCn);
            }
            else {
                oldConceptName.setName(newName);
            }

            oldConceptName.setAuthor(author);
            oldConceptName.setNameType(nameType);
            
            okToProceed = false;
            try {
                ConceptDAO.getInstance().update(concept);
                okToProceed = true;
            }
            catch (DAOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to update " + concept, e);
                }
                AppFrameDispatcher.showErrorDialog("Update failed!! (Database error)");
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
                    ConceptDAO.getInstance().updateConceptNameUsedByAnnotations(concept);
                    okToProceed = true;
                }
                catch (DAOException e) {
                    String msg = "Failed to change primary names of annotations from '" +
                            oldName + "' to '" + newName + "'.";
                    log.error(msg);
                    AppFrameDispatcher.showErrorDialog(msg);
                }

                /*
                 * If the annotation update was successful we can drop the old conceptname that we
                 * might have created if changing a primary name
                 */
                if (okToProceed) {
                    IConceptName oldPrimaryName = concept.getConceptName(history.getOldValue());
                    if (oldPrimaryName != null && !oldPrimaryName.getNameType().equalsIgnoreCase(ConceptName.NAMETYPE_PRIMARY)) {
                        concept.removeConceptName(oldPrimaryName);
                        try {
                            ConceptDAO.getInstance().update(concept);
                        }
                        catch (DAOException ex) {
                            log.error("Failed to remove " + oldPrimaryName + " from the database. This will need to be done manually!!");
                        }
                    }
                }


                /*
                 * If the user is an admin go ahead and approve the change. Do this BEFORE you refresh the tree
                 * or your database transaction will fail because of a timestamp mismatch. (ie. Cache does not
                 * match you instance)
                 */
                if (userAccount != null && userAccount.isAdmin()) {
                    ApproveHistoryTask.approve(userAccount, history);
                }
                

            }
        }
        log.debug("Exiting updateConceptName method");
        return okToProceed;
    }
}

