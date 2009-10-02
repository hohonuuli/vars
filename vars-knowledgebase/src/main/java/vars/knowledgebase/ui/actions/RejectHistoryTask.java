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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JProgressBar;
import org.mbari.swing.ProgressDialog;
import foxtrot.Task;
import foxtrot.Worker;

import java.util.Collection;
import java.util.HashSet;
import org.bushe.swing.event.EventBus;
import vars.LinkBean;
import vars.LinkComparator;
import vars.UserAccount;
import vars.VARSException;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.ObservationDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.KnowledgebaseDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.KnowledgebaseFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.ui.KnowledgebaseApp;
import vars.knowledgebase.ui.Lookup; 

//~--- classes ----------------------------------------------------------------
/**
 * <p>Handles the steps need to reject a History. NOTE: For some operations it's important to 
 * clear the knowledgebase after rejecting a History (Such as rejecting the addition of a concept name or concept</p>
 *
 * @version    $Id: RejectHistoryTask.java 3 2005-10-27 16:20:12Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class RejectHistoryTask extends AbstractHistoryTask {

    /*
     * Map<String, IAction>
     */
    private final Map actionMap = new HashMap();

    private final GenericRejectTask DEFAULT_TASK;

    @Inject
    public RejectHistoryTask(AnnotationDAOFactory annotationDAOFactory, 
            KnowledgebaseDAO knowledgebaseDAO,
            KnowledgebaseDAOFactory knowledgebaseDAOFactory,
            KnowledgebaseFactory knowledgebaseFactory) {
        super(knowledgebaseDAOFactory);
        DEFAULT_TASK = new GenericRejectTask(knowledgebaseDAOFactory);


        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_* 
         * for acceptabe values. The Map holds that process the approval
         */
        final Map addMap = new HashMap();
        actionMap.put(History.ACTION_ADD, addMap);
        addMap.put(History.FIELD_CONCEPT, DEFAULT_TASK);
        addMap.put(History.FIELD_CONCEPT_CHILD, new AddConceptTask(knowledgebaseDAOFactory, annotationDAOFactory.newObservationDAO()));
        addMap.put(History.FIELD_CONCEPTNAME, new AddConceptNameTask(knowledgebaseDAOFactory, knowledgebaseDAO));
        addMap.put(History.FIELD_LINKREALIZATION, new AddLinkRealizationTask(knowledgebaseDAOFactory));
        addMap.put(History.FIELD_LINKTEMPLATE, new AddLinkTemplateTask(knowledgebaseFactory, knowledgebaseDAOFactory));
        addMap.put(History.FIELD_MEDIA, new AddMediaTask(knowledgebaseDAOFactory));
        addMap.put(History.FIELD_SECTIONINFO, DEFAULT_TASK);

        /*
         * This map holds actions that process the approval of remove actions
         * deleteMap<String, IAction> String defines which field was Added
         */
        final Map deleteMap = new HashMap();
        actionMap.put(History.ACTION_DELETE, deleteMap);
        /*
         * A concept is never deleted directly. It's deleted from the parent
         * concept. This allows us to track History better.
         * deleteMap.put(History.FIELD_CONCEPT, new RemoveConceptAction());
         */
        deleteMap.put(History.FIELD_CONCEPT_CHILD, DEFAULT_TASK);
        deleteMap.put(History.FIELD_CONCEPTNAME, DEFAULT_TASK);
        deleteMap.put(History.FIELD_LINKREALIZATION, DEFAULT_TASK);
        deleteMap.put(History.FIELD_LINKTEMPLATE, DEFAULT_TASK);
        deleteMap.put(History.FIELD_MEDIA, DEFAULT_TASK);
        deleteMap.put(History.FIELD_SECTIONINFO, DEFAULT_TASK);

        final Map replaceMap = new HashMap();
        actionMap.put(History.ACTION_REPLACE, replaceMap);
        replaceMap.put(History.FIELD_CONCEPT_PARENT, new ReplaceParentConceptTask(knowledgebaseDAOFactory));
        replaceMap.put(History.FIELD_CONCEPT_NODCCODE, new ReplaceNodcCodeTask(knowledgebaseDAOFactory));
        replaceMap.put(History.FIELD_CONCEPT_RANKNAME, new ReplaceRankNameTask(knowledgebaseDAOFactory));
        replaceMap.put(History.FIELD_CONCEPT_RANKLEVEL, new ReplaceRankLevelTask(knowledgebaseDAOFactory));
        replaceMap.put(History.FIELD_CONCEPT_REFERENCE, new ReplaceReferenceTask(knowledgebaseDAOFactory));
        actionMap.put(History.FIELD_CONCEPTNAME, new ReplaceConceptNameTask(knowledgebaseDAO, knowledgebaseDAOFactory));

    }

    public void doTask(final UserAccount userAccount, final History history) {
        reject(userAccount, history);
    }

    public void reject(final UserAccount userAccount, final History history) {
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


    private class GenericRejectTask extends AbstractHistoryTask {

        public GenericRejectTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        public void doTask(final UserAccount userAccount, final History history) {
            reject(userAccount, history);
        }

        public void reject(final UserAccount userAccount, final History history) {
            if (canDo(userAccount, history)) {
                history.setApprovalDate(new Date());
                history.setRejected(true);
                history.setApproverName(userAccount.getUserName());
                try {
                    HistoryDAO historyDAO = knowledgebaseDAOFactory.newHistoryDAO();
                    historyDAO.update(history);
                }
                catch (Exception e) {
                    throw new TaskException("Unable to update history in database", e);
                }
            }
        }
    }

    private class AddConceptNameTask extends GenericRejectTask {

        private final KnowledgebaseDAO knowledgebaseDAO;

        public AddConceptNameTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory, KnowledgebaseDAO knowledgebaseDAO) {
            super(knowledgebaseDAOFactory);
            this.knowledgebaseDAO = knowledgebaseDAO;
        }


        @Override
        public void reject(final UserAccount userAccount, final History history) {

            if (!canDo(userAccount, history)) {
                return;
            }

            final ProgressDialog dialog = Lookup.getProgressDialog();
            final JProgressBar progressBar = dialog.getProgressBar();
            progressBar.setIndeterminate(false);
            progressBar.setMinimum(0);
            progressBar.setMaximum(6);
            progressBar.setStringPainted(true);
            progressBar.setValue(0);
            progressBar.setString("Initializing...");
            dialog.setSize(350, 40);
            dialog.setVisible(true);

            final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
            final ConceptNameDAO conceptNameDAO = getKnowledgebaseDAOFactory().newConceptNameDAO();
            final HistoryDAO historyDAO = getKnowledgebaseDAOFactory().newHistoryDAO();

            /*
             * Verify that the concept name still exists in the knowledgebase
             */
            final String name = history.getNewValue();
            progressBar.setString("Fetching '" + name + "' from database");
            progressBar.setValue(1);
            Concept thatConcept = null;
            try {
                thatConcept = conceptDAO.findByName(name);
            }
            catch (Exception e1) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
            }

            if (thatConcept == null) {
                dropHistory(history, "The concept-name, '" + name + "' no longer exists in the knowledgebase. I'll remove this history from the database");
                return;
            }


            /*
             * We need to check that the concept-name that we're dropping is still associated with the correct
             * concept.
             */
            final Concept thisConcept = history.getConceptMetadata().getConcept();
            if (thisConcept.equals(thatConcept)) {
                /*
                 * A primary conceptname can not be dropped. If it's a primary name we remove the history.
                 */
                if (!thisConcept.getPrimaryConceptName().getName().equals(name)) {

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
                                knowledgebaseDAO.updateConceptNameUsedByAnnotations(thisConcept);
                                return null;
                            }
                        });
                    }
                    catch (Exception e) {
                        ok = false;
                        progressBar.setIndeterminate(false);
                        dialog.setVisible(false);
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to remove uses of '" + name + "' from the database. Reason: " + e.getMessage());
                    }

                    if (ok) {
                        final ConceptName conceptName = thisConcept.getConceptName(name);
                        if (conceptName != null) {

                            // Delete the name
                            progressBar.setString("Deleting '" + name + "' from the knowledgebase");
                            progressBar.setValue(3);
                            try {
                                Worker.post(new Task() {

                                    public Object run() throws Exception {
                                        conceptNameDAO.makeTransient(conceptName);
                                        return null;
                                    }
                                });
                            }
                            catch (Exception e) {
                                ok = false;
                                progressBar.setIndeterminate(false);
                                dialog.setVisible(false);
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Unable to delete '" +
                                        name + "' from the database. Reason: " + e.getMessage());
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
                                            historyDAO.update(history);
                                            return null;
                                        }
                                    });
                                }
                                catch (Exception e) {
                                    progressBar.setIndeterminate(false);
                                    dialog.setVisible(false);
                                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                            "Failed to update '" +
                                            thisConcept.getPrimaryConceptName().getName() +
                                            "'. Reason: " + e.getMessage() +
                                            ". I'll refresh the knowledgebase to resynchronize it.");
                                }
                            }
                        }
                    }

                    progressBar.setString("Refreshing");
                    progressBar.setValue(5);
                    KnowledgebaseApp app = (KnowledgebaseApp) Lookup.getApplicationDispatcher().getValueObject();
                    app.getKnowledgebaseFrame().refreshTreeAndOpenNode(thisConcept.getPrimaryConceptName().getName());
                }
                else {
                    dropHistory(history, "Unable to delete a primary concept name! I'll remove this history from the database.");
                }
            }
            else {
                dropHistory(history, "This History refers to a concept name that has been moved to '" + 
                        thatConcept.getPrimaryConceptName().getName() + "'. I'll remove this history from the database.");
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
    private class AddConceptTask extends GenericRejectTask {

        final DeleteConceptTask deleteConceptTask;

        public AddConceptTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory, ObservationDAO observationDAO) {
            super(knowledgebaseDAOFactory);
            this.deleteConceptTask = new DeleteConceptTask(knowledgebaseDAOFactory.newConceptDAO(), observationDAO);
        }


        @Override
        public void reject(final UserAccount userAccount, final History history) {
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
                catch (Exception e) {
                    log.error("Problem occurred when looking up '" + rejectedName + "'", e);
                    okToProceed = false;
                }
            }

            if (okToProceed) {
                okToProceed = deleteConceptTask.delete(rejectedConcept);
            }

            /*
             * Mark the history as processed
             */
            if (okToProceed) {
                super.reject(userAccount, history);
            }
        }

        private Concept findRejectedConcept(final History history) {
            final String rejectedName = history.getNewValue();
            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
            Concept rejectedConcept = conceptDAO.findByName(rejectedName);
            if (rejectedConcept == null) {
                dropHistory(history, "Unable to find a concept with the name '" +
                        rejectedName + "' in the knowledgebase. I'll drop the history reference");
            }
            if (!parentConcept.getChildConcepts().contains(rejectedConcept)) {
                dropHistory(history, "The concept with the name '" + rejectedName + 
                        "' is no longer a child of '" + parentConcept.getPrimaryConceptName().getName() +
                        "'. Unable to process the history reference.");
                rejectedConcept = null;
            }

            return rejectedConcept;
        }
    }

    private class AddLinkTemplateTask extends GenericRejectTask {

        private final KnowledgebaseFactory knowledgebaseFactory;
        private final DeleteLinkTemplateTask deleteLinkTemplateTask;

        public AddLinkTemplateTask(KnowledgebaseFactory knowledgebaseFactory, KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.knowledgebaseFactory = knowledgebaseFactory;
            this.deleteLinkTemplateTask = new DeleteLinkTemplateTask(knowledgebaseDAOFactory.newLinkTemplateDAO());
        }

        @Override
        public void reject(UserAccount userAccount, History history) {

            if (canDo(userAccount, history)) {
                // Convienet means to parse the string stored in the history
                final LinkBean linkBean = new LinkBean(history.getNewValue());
                final LinkTemplate exampleTemplate = knowledgebaseFactory.newLinkTemplate();
                exampleTemplate.setLinkName(linkBean.getLinkName());
                exampleTemplate.setToConcept(linkBean.getToConcept());
                exampleTemplate.setLinkValue(linkBean.getLinkValue());

                final ConceptMetadata conceptMetadata = history.getConceptMetadata();

                /*
                 * Find the matching linkTemplate
                 */
                Set<LinkTemplate> linkTemplates = conceptMetadata.getLinkTemplates();
                LinkTemplate linkTemplate = null;
                for (Iterator i = linkTemplates.iterator(); i.hasNext();) {
                    LinkTemplate t = (LinkTemplate) i.next();
                    if (t.getLinkName().equals(exampleTemplate.getLinkName()) && t.getToConcept().equals(exampleTemplate.getToConcept()) && t.getLinkValue().equals(exampleTemplate.getLinkValue())) {

                        linkTemplate = t;
                        break;
                    }
                }

                if (linkTemplate == null) {
                    dropHistory(history, "Unable to locate '" + history.getNewValue() +
                            "'. It may have been moved. I'll remove the History reference.");
                }
                else {
                    deleteLinkTemplateTask.delete(linkTemplate);
                    super.reject(userAccount, history);
                }
            }
        }
    }

    private class AddLinkRealizationTask extends GenericRejectTask {

        private final DeleteLinkRealizationTask deleteLinkRealizationTask;

        public AddLinkRealizationTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.deleteLinkRealizationTask = new DeleteLinkRealizationTask(knowledgebaseDAOFactory.newLinkRealizationDAO());
        }

        @Override
        public void reject(UserAccount userAccount, History history) {

            if (canDo(userAccount, history)) {
                // Convienet means to parse the string stored in the history
                LinkBean exampleTemplate = new LinkBean(history.getNewValue());
                
                // Work on copy collection to avoid synchronization issues
                final Set<LinkRealization> linkRealizations = new HashSet<LinkRealization>(history.getConceptMetadata().getLinkRealizations());

                /*
                 * Find the matching linkRealization
                 */
                LinkRealization linkRealization = null;
                LinkComparator linkComparator = new LinkComparator();
                for (LinkRealization t : linkRealizations) {
                    if (linkComparator.compare(exampleTemplate, linkRealization) == 0) {
                        linkRealization = t;
                        break;
                    }
                }

                if (linkRealization == null) {
                    dropHistory(history, "Unable to locate \'" + history.getNewValue() +
                            "\'. It may have been moved. I\'ll remove the History reference.");
                }
                else {
                    deleteLinkRealizationTask.delete(linkRealization);
                    super.reject(userAccount, history);
                }
            }
        }
    }

    private class AddMediaTask extends GenericRejectTask {


        public AddMediaTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void reject(UserAccount userAccount, final History history) {
            if (canDo(userAccount, history)) {
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();
                
                // Iterate on copy to avoid threading issues
                final Set<Media> mediaSet = new HashSet<Media>(conceptMetadata.getMedias());

                final Collection<Media> matches = Collections2.filter(mediaSet, new Predicate<Media>() {
                    @Override
                    public boolean apply(Media input) {
                        return input.getUrl().equals(history.getNewValue());
                    }
                });

                MediaDAO mediaDAO = getKnowledgebaseDAOFactory().newMediaDAO();
                for (Media media : matches) {
                    conceptMetadata.removeMedia(media);
                    mediaDAO.makeTransient(media);
                }

                try {
                    super.reject(userAccount, history);
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                }
                
            }
        }
    }

    private class ReplaceParentConceptTask extends GenericRejectTask {

        public ReplaceParentConceptTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void reject(UserAccount userAccount, final History history) {
            if (canDo(userAccount, history)) {
                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();

                final Concept concept = history.getConceptMetadata().getConcept();
                final Concept currentParent = concept.getParentConcept();

                /*
                 * Need to do a little database lookup.
                 */
                Concept newParent = null;
                Concept oldParent = null;
                try {
                    newParent = conceptDAO.findByName(history.getNewValue());
                    oldParent = conceptDAO.findByName(history.getOldValue());
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                }

                if (currentParent.equals(newParent)) {
                    if (oldParent != null) {

                        Collection<Concept> descendents = conceptDAO.findDescendents(oldParent);
                        Collection<Concept> matches = Collections2.filter(descendents, new Predicate<Concept>(){
                            @Override
                            public boolean apply(Concept input) {
                                return input.getPrimaryConceptName().getName().equals(concept.getPrimaryConceptName().getName());
                            }

                        });

                        if (matches.size() > 0) {
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "\'" + oldParent.getPrimaryConceptName().getName() +
                                    "\' already has a child named \'" + concept.getPrimaryConceptName().getName() +
                                    "\'. Unable to process your request.");
                            return;
                        }

                        /*
                         * Update the database.
                         */
                        currentParent.removeChildConcept(concept);
                        super.reject(userAccount, history);
                        try {
                            conceptDAO.update(currentParent);
                            oldParent.addChildConcept(concept);
                            conceptDAO.update(oldParent);
                        }
                        catch (Exception e) {
                            currentParent.addChildConcept(concept);
                            oldParent.removeChildConcept(concept);
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "An error occured while processing updating the database." +
                                    " Your change was not successful");
                        }
                    }
                    else {
                        /*
                         * We can't reject the change if we can't find the original parent
                         */
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Unable to find the original parent, \'" +
                                oldParent.getPrimaryConceptName().getName() + "\'. Unable to move \'" +
                                concept.getPrimaryConceptName().getName() + "\' back to it\'s previous state.");
                    }
                }
                else {
                    /*
                     * If the expected new parent does not match the current parent we can't reject it. Ultimatly,
                     * the user will have to accept the change since there is no way to roll back.
                     */
                    String message = null;
                    if (newParent != null) {
                        message = "The concept, \'" + concept.getPrimaryConceptName().getName() + "\' was a child of \'" +
                                newParent.getPrimaryConceptName().getName() + "\' when this history was created. However, it\'s now a child of \'" +
                                currentParent.getPrimaryConceptName().getName() + "\'. Unable to move it back to it\'s previous state.";
                    }
                    else {
                        message = "The concept, \'" + history.getNewValue() + "\' was not found in the knowledgebase. It may " + 
                                "have been deleted or renamed. Unable to move \'" + concept.getPrimaryConceptName().getName() +
                                "\' back to it\'s previous state.";
                    }
                    EventBus.publish(Lookup.TOPIC_WARNING, message);
                }
            }
        }
    }

    private class ReplaceNodcCodeTask extends GenericRejectTask {

        public ReplaceNodcCodeTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }


        @Override
        public void reject(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getNodcCode();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setNodcCode(history.getOldValue());
                    try {
                        conceptDAO.update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    }
                    
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,"Unable to reject this history. The NODC Code has been modified" +
                            " since this history was created.");
                }
            }
        }
    }

    private class ReplaceRankNameTask extends GenericRejectTask {

        public ReplaceRankNameTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void reject(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getRankName();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setRankName(history.getOldValue());
                    try {
                        conceptDAO.update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    }
                    
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING, "Unable to reject this history. The Rank Name has been modified" +
                            " since this history was created.");
                }
            }
        }
    }

    private class ReplaceRankLevelTask extends GenericRejectTask {

        public ReplaceRankLevelTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void reject(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getRankLevel();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setRankLevel(history.getOldValue());
                    try {
                        conceptDAO.update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    }
                    
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING, "Unable to reject this history. The Rank Level has been modified" +
                            " since this history was created.");
                }
            }
        }
    }

    private class ReplaceReferenceTask extends GenericRejectTask {

        public ReplaceReferenceTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void reject(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getReference();
                final String newValue = history.getNewValue();
                if ((currentValue != null && currentValue.equals(newValue)) || (newValue != null && newValue.equals(currentValue))) {
                    concept.setReference(history.getOldValue());
                    try {
                        conceptDAO.update((Concept) concept);
                        super.reject(userAccount, history);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    }
                    
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING, "Unable to reject this history. The Reference has been modified" +
                            " since this history was created.");
                }
            }
        }
    }

    private class ReplaceConceptNameTask extends GenericRejectTask {
        private final KnowledgebaseDAO knowledgebaseDAO;

        public ReplaceConceptNameTask(KnowledgebaseDAO knowledgebaseDAO, KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.knowledgebaseDAO = knowledgebaseDAO;
        }

        @Override
        public void reject(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {

                final ConceptDAO conceptDAO = getKnowledgebaseDAOFactory().newConceptDAO();
                final Concept concept = history.getConceptMetadata().getConcept();
                final ConceptName conceptName = concept.getConceptName(history.getNewValue());
                
                if (conceptName == null) {
                    EventBus.publish(Lookup.TOPIC_WARNING, "Unable to find a concept named '" +
                            history.getNewValue() + "'" + "associated with '" + concept.getPrimaryConceptName().getName() +
                            "'. Unable to reject this history.");
                }
                else {
                    
                    // Verify that the old name is not being used by another concept in the database. If it is return
                    Concept duplicate = null;
                    try {
                        duplicate = conceptDAO.findByName(history.getOldValue());
                    }
                    catch (Exception e1) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                        log.error("A database error occured while looking up '" + conceptName.getName() + "'", e1);
                    }
                    
                    if (duplicate == null) {
                            conceptName.setName(history.getOldValue());
                        try {
                            knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept);
                            conceptDAO.update(concept);
                            super.reject(userAccount, history);
                        }
                        catch (Exception e) {
                            String s = "The attempt to change " + history.getNewValue() + 
                                    " to " + history.getOldValue() + " failed.";
                            conceptName.setName(history.getNewValue());
                            Exception ne = new VARSException(s, e);
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, ne);
                            log.error(s, e);
                        }
                        
                    }
                    else {
                        EventBus.publish(Lookup.TOPIC_WARNING, "Unable to reject this history. The name '" +
                                history.getOldValue() + "' exists in the knowledgebase.");
                    }
                }
            }
        }
    }
}
