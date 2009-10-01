/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.knowledgebase.ui.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swing.ProgressDialog;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.dao.ConceptDAO;
import org.mbari.vars.knowledgebase.model.dao.ConceptDelegateDAO;
import org.mbari.vars.knowledgebase.model.dao.ConceptNameDAO;
import org.mbari.vars.knowledgebase.model.dao.IKnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.KnowledgebaseApp;
import org.mbari.vars.util.AppFrameDispatcher;
import foxtrot.Task;
import foxtrot.Worker;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptDelegate;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.LinkRealization;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.Media;
import vars.IUserAccount;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;
import vars.knowledgebase.IHistory;

//~--- classes ----------------------------------------------------------------
/**
 * <p>Handles the steps need to reject a History. NOTE: For some operations it's important to 
 * clear the knowledgebase after rejecting a History (Such as rejecting the addition of a concept name or concept</p>
 *
 * @version    $Id: RejectHistoryTask.java 3 2005-10-27 16:20:12Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class RejectHistoryTask extends AbstractHistoryTask {

    private static final Logger log = LoggerFactory.getLogger(RejectHistoryTask.class);

    /*
     * Map<String, IAction>
     */
    private static final Map actionMap = new HashMap();

    private static final GenericRejectTask DEFAULT_TASK = new GenericRejectTask();
    static {

        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_*
         * for acceptabe values. The Map holds that process the approval
         */
        final Map addMap = new HashMap();
        actionMap.put(IHistory.ACTION_ADD, addMap);
        addMap.put(IHistory.FIELD_CONCEPT, DEFAULT_TASK);
        addMap.put(IHistory.FIELD_CONCEPT_CHILD, new AddConceptTask());
        addMap.put(IHistory.FIELD_CONCEPTNAME, new AddConceptNameTask());
        addMap.put(IHistory.FIELD_LINKREALIZATION, new AddLinkRealizationTask());
        addMap.put(IHistory.FIELD_LINKTEMPLATE, new AddLinkTemplateTask());
        addMap.put(IHistory.FIELD_MEDIA, new AddMediaTask());
        addMap.put(IHistory.FIELD_SECTIONINFO, DEFAULT_TASK);

        /*
         * This map holds actions that process the approval of remove actions
         * deleteMap<String, IAction> String defines which field was Added
         */
        final Map deleteMap = new HashMap();
        actionMap.put(IHistory.ACTION_DELETE, deleteMap);
        /*
         * A concept is never deleted directly. It's deleted from the parent
         * concept. This allows us to track IHistory better.
         * deleteMap.put(IHistory.FIELD_CONCEPT, new RemoveConceptAction());
         */
        deleteMap.put(IHistory.FIELD_CONCEPT_CHILD, DEFAULT_TASK);
        deleteMap.put(IHistory.FIELD_CONCEPTNAME, DEFAULT_TASK);
        deleteMap.put(IHistory.FIELD_LINKREALIZATION, DEFAULT_TASK);
        deleteMap.put(IHistory.FIELD_LINKTEMPLATE, DEFAULT_TASK);
        deleteMap.put(IHistory.FIELD_MEDIA, DEFAULT_TASK);
        deleteMap.put(IHistory.FIELD_SECTIONINFO, DEFAULT_TASK);

        final Map replaceMap = new HashMap();
        actionMap.put(IHistory.ACTION_REPLACE, replaceMap);
        replaceMap.put(IHistory.FIELD_CONCEPT_PARENT, new ReplaceParentConceptTask());
        replaceMap.put(IHistory.FIELD_CONCEPT_NODCCODE, new ReplaceNodcCodeTask());
        replaceMap.put(IHistory.FIELD_CONCEPT_RANKNAME, new ReplaceRankNameTask());
        replaceMap.put(IHistory.FIELD_CONCEPT_RANKLEVEL, new ReplaceRankLevelTask());
        replaceMap.put(IHistory.FIELD_CONCEPT_REFERENCE, new ReplaceReferenceTask());
        actionMap.put(IHistory.FIELD_CONCEPTNAME, new ReplaceConceptNameTask());
    }

    private RejectHistoryTask() {
        // DO nothing. DO not allow instatiation
    }

    public void doTask(final IUserAccount userAccount, final IHistory history) {
        reject(userAccount, history);
    }

    public static void reject(final IUserAccount userAccount, final IHistory history) {
        if ((history != null) && !history.isApproved() && !history.isRejected()) {
            if (log.isDebugEnabled()) {
                log.debug("Rejecting " + history);
            }
            final Map rejectActions = (Map) actionMap.get(history.getAction());
            GenericRejectTask processer = (GenericRejectTask) rejectActions.get(history.getField());
            if (processer == null) {
                processer = DEFAULT_TASK;
            }
            processer.reject(userAccount, history);
        }
    }


    private static class GenericRejectTask extends AbstractHistoryTask {

        public void doTask(final IUserAccount userAccount, final IHistory history) {
            reject(userAccount, history);
        }

        public void reject(final IUserAccount userAccount, final IHistory history) {
            if (canDo(userAccount, history)) {
                history.setApprovalDate(new Date());
                history.setRejected(true);
                history.setApproverName(userAccount.getUserName());
                try {
                    ConceptDelegateDAO.getInstance().update((ConceptDelegate) history.getConceptDelegate());
                }
                catch (DAOException e) {
                    final TaskException re = new TaskException("Unable to update history in database");
                    re.initCause(e);
                    throw re;
                }
            }
        }
    }

    private static class AddConceptNameTask extends GenericRejectTask {


        @Override
        public void reject(final IUserAccount userAccount, final IHistory history) {

            if (!canDo(userAccount, history)) {
                return;
            }


            final ProgressDialog dialog = AppFrameDispatcher.getProgressDialog();
            final JProgressBar progressBar = dialog.getProgressBar();
            progressBar.setIndeterminate(false);
            progressBar.setMinimum(0);
            progressBar.setMaximum(6);
            progressBar.setStringPainted(true);
            progressBar.setValue(0);
            progressBar.setString("Initializing...");
            dialog.setSize(350, 40);
            dialog.setVisible(true);

            /*
             * Verify that the concept name still exists in the knowledgebase
             */
            final String name = history.getNewValue();
            progressBar.setString("Fetching '" + name + "' from database");
            progressBar.setValue(1);
            Concept thatConcept = null;
            try {
                thatConcept = KnowledgeBaseCache.getInstance().findConceptByName(name);
            }
            catch (DAOException e1) {
                AppFrameDispatcher.showErrorDialog("There is a problem with the database connection. Error message given is: " + e1.getMessage());
            }
            if (thatConcept == null) {
                dropHistory(history, "The concept-name, '" + name + "' no longer exists in the knowledgebase. I'll remove this history from the database");
                return;
            }


            /*
             * We need to check that the concept-name that we're dropping is still associated with the correct
             * concept.
             */
            final IConcept thisConcept = history.getConceptDelegate().getConcept();
            if (thisConcept.equals(thatConcept)) {
                /*
                 * A primary conceptname can not be dropped. If it's a primary name we remove the history.
                 */
                if (!thisConcept.getPrimaryConceptNameAsString().equals(name)) {

                    /*
                     * When dropping a concept-name we first want to make sure that no annotations are
                     * using it.
                     */
                    boolean ok = true;
                    progressBar.setString("Removing uses of '" + name + "' from database");
                    progressBar.setValue(2);
                    try {
                        Worker.post(new Task() {

                            public Object run() throws Exception {
                                ConceptDAO.getInstance().updateConceptNameUsedByAnnotations((Concept) thisConcept);
                                return null;
                            }
                        });
                    }
                    catch (Exception e) {
                        ok = false;
                        progressBar.setIndeterminate(false);
                        dialog.setVisible(false);
                        AppFrameDispatcher.showErrorDialog("Failed to remove uses of '" + name + "' from the database. Reason: " + e.getMessage());
                    }

                    if (ok) {
                        final IConceptName conceptName = thisConcept.getConceptName(name);
                        if (conceptName != null) {

                            // Delete the name
                            progressBar.setString("Deleting '" + name + "' from the knowledgebase");
                            progressBar.setValue(3);
                            try {
                                Worker.post(new Task() {

                                    public Object run() throws Exception {
                                        ConceptNameDAO.getInstance().delete((ConceptName) conceptName);
                                        return null;
                                    }
                                });
                            }
                            catch (Exception e) {
                                ok = false;
                                progressBar.setIndeterminate(false);
                                dialog.setVisible(false);
                                AppFrameDispatcher.showErrorDialog("Unable to delete '" + name + "' from the database. Reason: " + e.getMessage());
                            }

                            if (ok) {
                                thisConcept.removeConceptName(conceptName);
                                progressBar.setString("Deleting '" + name + "' from the knowledgebase");
                                progressBar.setValue(4);
                                try {
                                    Worker.post(new Task() {

                                        public Object run() throws Exception {
                                            history.setApprovalDate(new Date());
                                            history.setRejected(true);
                                            ConceptDAO.getInstance().update((Concept) thisConcept);
                                            return null;
                                        }
                                    });
                                }
                                catch (Exception e) {
                                    progressBar.setIndeterminate(false);
                                    dialog.setVisible(false);
                                    AppFrameDispatcher.showWarningDialog("Failed to update '" + thisConcept.getPrimaryConceptNameAsString() + "'. Reason: " + e.getMessage() + ". I'll refresh the knowledgebase to resynchronize it.");
                                }
                            }
                        }
                    }

                    progressBar.setString("Refreshing");
                    progressBar.setValue(5);
                    ((KnowledgebaseApp) KnowledgebaseApp.DISPATCHER.getValueObject()).getKnowledgebaseFrame().refreshTreeAndOpenNode(thisConcept.getPrimaryConceptNameAsString());
                }
                else {
                    dropHistory(history, "Unable to delete a primary concept name! I'll remove this history from the database.");
                }
            }
            else {
                dropHistory(history, "This History refers to a concept name that has been moved to '" + thatConcept.getPrimaryConceptNameAsString() + "'. I'll remove this history from the database.");
            }

            // Get count of usages in the Annotation database. Collection<Observation>
            progressBar.setValue(6);
            progressBar.setIndeterminate(false);
            dialog.setVisible(false);
        }
    }

/**
     * Handles the Rejection of a Concept that was added to the knowledgebase
     * @author brian
     *
     */
    private static class AddConceptTask extends GenericRejectTask {

        @Override
        public void reject(final IUserAccount userAccount, final IHistory history) {
            boolean okToProceed = canDo(userAccount, history);
            final String rejectedName = history.getNewValue();

            /*
             * Look up the concept that we're deleting. Make sure it exists and it's not the root concept
             */
            Concept rejectedConcept = null;
            if (okToProceed) {
                try {
                    rejectedConcept = findRejectedConcept(history);
                    okToProceed = (rejectedConcept != null && rejectedConcept.getConceptName(ConceptName.NAME_DEFAULT) == null);
                }
                catch (DAOException e) {
                    log.error("Problem occurred when looking up '" + rejectedName + "'", e);
                    okToProceed = false;
                }
            }

            if (okToProceed) {
                okToProceed = DeleteConceptTask.delete(rejectedConcept);
            }

            /*
             * Mark the history as processed
             */
            if (okToProceed) {
                super.reject(userAccount, history);
            }
        }

        private Concept findRejectedConcept(final IHistory history) throws DAOException {
            final String rejectedName = history.getNewValue();
            final IConcept parentConcept = history.getConceptDelegate().getConcept();
            Concept rejectedConcept = KnowledgeBaseCache.getInstance().findConceptByName(rejectedName);
            if (rejectedConcept == null) {
                dropHistory(history, "Unable to find a concept with the name '" + rejectedName + "' in the knowledgebase. I'll drop the history reference");
            }
            if (!parentConcept.getChildConceptColl().contains(rejectedConcept)) {
                dropHistory(history, "The concept with the name '" + rejectedName + "' is no longer a child of '" + parentConcept.getPrimaryConceptNameAsString() + "'. Unable to process the history reference.");
                rejectedConcept = null;
            }

            return rejectedConcept;
        }
    }

    private static class AddLinkTemplateTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {

            if (canDo(userAccount, history)) {
                // Convienet means to parse the string stored in the history
                final LinkTemplate exampleTemplate = LinkTemplate.createFromString(history.getNewValue());
                final IConcept concept = history.getConceptDelegate().getConcept();

                /*
                 * Find the matching linkTemplate
                 */
                Set linkTemplates = concept.getLinkTemplateSet();
                LinkTemplate linkTemplate = null;
                for (Iterator i = linkTemplates.iterator(); i.hasNext();) {
                    LinkTemplate t = (LinkTemplate) i.next();
                    if (t.getLinkName().equals(exampleTemplate.getLinkName()) && t.getToConcept().equals(exampleTemplate.getToConcept()) && t.getLinkValue().equals(exampleTemplate.getLinkValue())) {

                        linkTemplate = t;
                        break;
                    }
                }

                if (linkTemplate == null) {
                    dropHistory(history, "Unable to locate '" + history.getNewValue() + "'. It may have been moved. I'll remove the History reference.");
                }
                else {
                    DeleteLinkTemplateTask.delete(linkTemplate);
                    super.reject(userAccount, history);
                }
            }
        }
    }

    private static class AddLinkRealizationTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {

            if (canDo(userAccount, history)) {
                // Convienet means to parse the string stored in the history
                final LinkRealization exampleTemplate = LinkRealization.createFromString(history.getNewValue());
                final IConcept concept = history.getConceptDelegate().getConcept();

                /*
                 * Find the matching linkRealization
                 */
                Set<LinkRealization> linkRealizations = concept.getLinkRealizationSet();
                LinkRealization linkRealization = null;
                for (LinkRealization t : linkRealizations) {
                    if (t.getLinkName().equals(exampleTemplate.getLinkName()) && t.getToConcept().equals(exampleTemplate.getToConcept()) && t.getLinkValue().equals(exampleTemplate.getLinkValue())) {

                        linkRealization = t;
                        break;
                    }
                }

                if (linkRealization == null) {
                    dropHistory(history, "Unable to locate \'" + history.getNewValue() + "\'. It may have been moved. I\'ll remove the History reference.");
                }
                else {
                    DeleteLinkRealizationTask.delete(linkRealization);
                    super.reject(userAccount, history);
                }
            }
        }
    }

    private static class AddMediaTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IConcept concept = history.getConceptDelegate().getConcept();
                final Set mediaSet = concept.getMediaSet();
                for (Iterator i = mediaSet.iterator(); i.hasNext();) {
                    Media media = (Media) i.next();
                    if (media.getUrl().equals(history.getNewValue())) {
                        concept.removeMedia(media);
                        // Don't break out. Remove ALL matching references.
                    }
                }

                try {
                    ConceptDAO.getInstance().update((Concept) concept);
                    super.reject(userAccount, history);
                }
                catch (DAOException e) {
                    AppFrameDispatcher.showErrorDialog("Failed to upate database!");
                }
                
            }
        }
    }

    private static class ReplaceParentConceptTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IKnowledgeBaseCache cache = KnowledgeBaseCache.getInstance();
                final IConcept concept = history.getConceptDelegate().getConcept();
                final IConcept currentParent = concept.getParentConcept();

                /*
                 * Need to do a little database lookup.
                 */
                Concept newParent = null;
                Concept oldParent = null;
                try {
                    newParent = cache.findConceptByName(history.getNewValue());
                    oldParent = cache.findConceptByName(history.getOldValue());
                }
                catch (DAOException e) {
                    AppFrameDispatcher.showErrorDialog("An error occured while fetching information from the database. " + "Unable to process your request.");
                }

                if (currentParent.equals(newParent)) {
                    if (oldParent != null) {

                        if (oldParent.hasDescendent(concept.getPrimaryConceptNameAsString())) {
                            AppFrameDispatcher.showWarningDialog("\'" + oldParent.getPrimaryConceptNameAsString() + "\' already has a child named \'" + concept.getPrimaryConceptNameAsString() + "\'. Unable to process your request.");
                            return;
                        }

                        /*
                         * UPdate the database.
                         */
                        currentParent.removeChildConcept(concept);
                        super.reject(userAccount, history);
                        try {
                            ConceptDAO.getInstance().update((Concept) currentParent);
                            oldParent.addChildConcept(concept);
                            ConceptDAO.getInstance().update(oldParent);
                        }
                        catch (DAOException e) {
                            currentParent.addChildConcept(concept);
                            oldParent.removeChildConcept(concept);
                            AppFrameDispatcher.showErrorDialog("An error occured while processing updating the database." + " Your change was not successful");
                        }
                    }
                    else {
                        /*
                         * We can't reject the change if we can't find the original parent
                         */
                        AppFrameDispatcher.showWarningDialog("Unable to find the original parent, \'" + oldParent.getPrimaryConceptNameAsString() + "\'. Unable to move \'" + concept.getPrimaryConceptNameAsString() + "\' back to it\'s previous state.");
                    }
                }
                else {
                    /*
                     * If the expected new parent does not match the current parent we can't reject it. Ultimatly,
                     * the user will have to accept the change since there is no way to roll back.
                     */
                    String message = null;
                    if (newParent != null) {
                        message = "The concept, \'" + concept.getPrimaryConceptNameAsString() + "\' was a child of \'" + newParent.getPrimaryConceptNameAsString() + "\' when this history was created. However, it\'s now a child of \'" + currentParent.getPrimaryConceptNameAsString() + "\'. Unable to move it back to it\'s previous state.";
                    }
                    else {
                        message = "The concept, \'" + history.getNewValue() + "\' was not found in the knowledgebase. It may " + "have been deleted or renamed. Unable to move \'" + concept.getPrimaryConceptNameAsString() + "\' back to it\'s previous state.";
                    }
                    AppFrameDispatcher.showWarningDialog(message);
                }
            }
        }
    }

    private static class ReplaceNodcCodeTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IConcept concept = history.getConceptDelegate().getConcept();
                String currentValue = concept.getNodcCode();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setNodcCode(history.getOldValue());
                    try {
                        ConceptDAO.getInstance().update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (DAOException e) {
                        AppFrameDispatcher.showErrorDialog("Failed to upate database!");
                    }
                    
                }
                else {
                    AppFrameDispatcher.showWarningDialog("Unable to reject this history. The NODC Code has been modified" + " since this history was created.");
                }
            }
        }
    }

    private static class ReplaceRankNameTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IConcept concept = history.getConceptDelegate().getConcept();
                String currentValue = concept.getRankName();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setRankName(history.getOldValue());
                    try {
                        ConceptDAO.getInstance().update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (DAOException e) {
                        AppFrameDispatcher.showErrorDialog("Failed to upate database!");
                    }
                    
                }
                else {
                    AppFrameDispatcher.showWarningDialog("Unable to reject this history. The Rank Name has been modified" + " since this history was created.");
                }
            }
        }
    }

    private static class ReplaceRankLevelTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IConcept concept = history.getConceptDelegate().getConcept();
                String currentValue = concept.getRankLevel();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setRankLevel(history.getOldValue());
                    try {
                        ConceptDAO.getInstance().update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (DAOException e) {
                        AppFrameDispatcher.showErrorDialog("Failed to upate database!");
                    }
                    
                }
                else {
                    AppFrameDispatcher.showWarningDialog("Unable to reject this history. The Rank Level has been modified" + " since this history was created.");
                }
            }
        }
    }

    private static class ReplaceReferenceTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                final IConcept concept = history.getConceptDelegate().getConcept();
                String currentValue = concept.getReference();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setReference(history.getOldValue());
                    try {
                        ConceptDAO.getInstance().update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (DAOException e) {
                        AppFrameDispatcher.showErrorDialog("Failed to upate database!");
                    }
                    
                }
                else {
                    AppFrameDispatcher.showWarningDialog("Unable to reject this history. The Reference has been modified" + " since this history was created.");
                }
            }
        }
    }

    private static class ReplaceConceptNameTask extends GenericRejectTask {

        @Override
        public void reject(IUserAccount userAccount, IHistory history) {
            if (canDo(userAccount, history)) {
                
                final IConcept concept = history.getConceptDelegate().getConcept();
                final IConceptName conceptName = concept.getConceptName(history.getNewValue());
                
                if (conceptName == null) {
                    AppFrameDispatcher.showWarningDialog("Unable to find a concept named '" + history.getNewValue() + "'" + "associated with '" + concept.getPrimaryConceptNameAsString() + "'. Unable to reject this history.");
                }
                else {
                    
                    // Verify that the old name is not being used by another concept in the database. If it is return
                    Concept duplicate = null;
                    try {
                        duplicate = KnowledgeBaseCache.getInstance().findConceptByName(history.getOldValue());
                    }
                    catch (DAOException e1) {
                        AppFrameDispatcher.showErrorDialog("An error occured while attempting to look up '" + conceptName.getName() + "' from the database.");
                        log.error("A database error occured while looking up '" + conceptName.getName() + "'", e1);
                    }
                    
                    if (duplicate == null) {
                            conceptName.setName(history.getOldValue());
                        try {
                            ConceptDAO.getInstance().updateConceptNameUsedByAnnotations((Concept) concept);
                            ConceptDAO.getInstance().update((Concept) concept);
                            super.reject(userAccount, history);
                        }
                        catch (DAOException e) {
                            String s = "The attempt to change " + history.getNewValue() + 
                                    " to " + history.getOldValue() + " failed.";
                            conceptName.setName(history.getNewValue());
                            AppFrameDispatcher.showErrorDialog(s);
                            log.error(s, e);
                        }
                        
                    }
                    else {
                        AppFrameDispatcher.showWarningDialog("Unable to reject this history. The name '" + history.getOldValue() + "' exists in the knowledgebase.");
                    }
                }
            }
        }
    }
}
