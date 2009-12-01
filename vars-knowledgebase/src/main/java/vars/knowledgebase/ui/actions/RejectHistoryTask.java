/*
 * @(#)RejectHistoryTask.java   2009.11.30 at 04:29:39 PST
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



package vars.knowledgebase.ui.actions;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import vars.DAO;
import vars.ILink;
import vars.LinkBean;
import vars.LinkUtilities;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.ToolBelt;

/**
 * <p>Handles the steps need to reject a History. NOTE: For some operations it's important to
 * clear the knowledgebase after rejecting a History (Such as rejecting the addition of a concept name or concept</p>
 *
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class RejectHistoryTask extends AbstractHistoryTask {

    /*
     * Map<String, IAction>
     */
    private final Map<String, Map<String, GenericRejectTask>> actionMap = new HashMap<String,
        Map<String, GenericRejectTask>>();
    private final GenericRejectTask DEFAULT_TASK;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    @Inject
    public RejectHistoryTask(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;


        DEFAULT_TASK = new GenericRejectTask();

        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_*
         * for acceptable values. The Map holds that process the approval
         */
        final Map<String, GenericRejectTask> addMap = new HashMap<String, GenericRejectTask>();

        actionMap.put(History.ACTION_ADD, addMap);
        addMap.put(History.FIELD_CONCEPT, DEFAULT_TASK);
        addMap.put(History.FIELD_CONCEPT_CHILD, new AddConceptTask());
        addMap.put(History.FIELD_CONCEPTNAME, new AddConceptNameTask());
        addMap.put(History.FIELD_LINKREALIZATION, new AddLinkRealizationTask());
        addMap.put(History.FIELD_LINKTEMPLATE, new AddLinkTemplateTask());
        addMap.put(History.FIELD_MEDIA, new AddMediaTask());
        addMap.put(History.FIELD_SECTIONINFO, DEFAULT_TASK);

        /*
         * This map holds actions that process the approval of remove actions
         * deleteMap<String, IAction> String defines which field was Added
         */
        final Map<String, GenericRejectTask> deleteMap = new HashMap<String, GenericRejectTask>();

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

        final Map<String, GenericRejectTask> replaceMap = new HashMap<String, GenericRejectTask>();

        actionMap.put(History.ACTION_REPLACE, replaceMap);
        replaceMap.put(History.FIELD_CONCEPT_PARENT, new ReplaceParentConceptTask());
        replaceMap.put(History.FIELD_CONCEPT_NODCCODE, new ReplaceNodcCodeTask());
        replaceMap.put(History.FIELD_CONCEPT_RANKNAME, new ReplaceRankNameTask());
        replaceMap.put(History.FIELD_CONCEPT_RANKLEVEL, new ReplaceRankLevelTask());
        replaceMap.put(History.FIELD_CONCEPT_REFERENCE, new ReplaceReferenceTask());
        replaceMap.put(History.FIELD_CONCEPTNAME, new ReplaceConceptNameTask());

    }

    /**
     *
     * @param userAccount
     * @param history
     */
    public void doTask(final UserAccount userAccount, final History history) {
        DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();

        try {
            dao.startTransaction();
            reject(userAccount, history, dao);
        }
        catch (Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }
        finally {
            dao.endTransaction();
        }
    }

    private LabeledSpinningDialWaitIndicator newWaitIndicator() {
        return (LabeledSpinningDialWaitIndicator) Worker.post(new Job() {

            @Override
            public Object run() {
                JComponent frame = (JComponent) Lookup.getApplicationFrameDispatcher().getValueObject();

                return new LabeledSpinningDialWaitIndicator(frame);
            }

        });
    }

    /**
     *
     * @param userAccount
     * @param history
     * @param dao
     */
    public void reject(final UserAccount userAccount, final History history, DAO dao) {
        if ((history != null) && !history.isProcessed()) {
            if (log.isDebugEnabled()) {
                log.debug("Rejecting " + history);
            }

            final Map<String, GenericRejectTask> rejectActions = actionMap.get(history.getAction());
            GenericRejectTask processer = rejectActions.get(history.getField());

            if (processer == null) {
                processer = DEFAULT_TASK;
            }

            processer.reject(userAccount, history, dao);
        }
    }

    private void updateWaitIndicator(final LabeledSpinningDialWaitIndicator waitIndicator, final String msg) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                waitIndicator.setLabel(msg);
            }

        });
    }

    private class AddConceptNameTask extends GenericRejectTask {


        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(final UserAccount userAccount, History history, DAO dao) {

            if (!canDo(userAccount, history)) {
                return;
            }

            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(dao.getEntityManager());

            /*
             * Verify that the concept name still exists in the knowledgebase
             */
            history = dao.findInDatastore(history);

            final String name = history.getNewValue();

            Concept thatConcept = conceptDAO.findByName(name);

            if (thatConcept == null) {
                dropHistory(history,
                            "The concept-name, '" + name +
                            "' no longer exists in the knowledgebase. I'll remove this history from the database", dao);

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
                if (thisConcept.getPrimaryConceptName().getName().equals(name)) {
                    dropHistory(history,
                                "Sorry, can't remove the primary concept name. We'll drop the history instead", dao);
                }
                else {

                    /*
                     * Make sure that no annotations are using the concept-name that is
                     * being deleted.
                     */
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            try {
                                toolBelt.getKnowledgebasePersistenceService().updateConceptNameUsedByAnnotations(
                                    thisConcept);
                            }
                            catch (Exception e) {
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                            }

                        }
                    }, "Update thread for annotations named " + thisConcept.getPrimaryConceptName().getName());

                    thread.setDaemon(false);
                    thread.start();

                    ConceptName conceptName = thisConcept.getConceptName(name);

                    thisConcept.removeConceptName(conceptName);
                    super.reject(userAccount, history, dao);

                }


            }
            else {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "The history appears to be attached to the wrong concept. " +
                                 "Moving it to the correct one.");
                thisConcept.getConceptMetadata().removeHistory(history);
                thatConcept.getConceptMetadata().addHistory(history);
            }

        }
    }


    /**
     * Handles the Rejection of a Concept that was added to the knowledgebase
     */
    private class AddConceptTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(final UserAccount userAccount, History history, DAO dao) {
            if (!canDo(userAccount, history)) {
                return;
            }

            history = dao.findInDatastore(history);

            final String rejectedName = history.getNewValue();

            /*
             * Look up the concept that we're deleting. Make sure it exists and it's not the root concept
             */
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(dao.getEntityManager());
            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final Concept rejectedConcept = conceptDAO.findByName(rejectedName);

            if (rejectedConcept == null) {
                dropHistory(history,
                            "Unable to find a concept with the name '" + rejectedName +
                            "' in the knowledgebase. I'll drop the history reference", dao);

                return;
            }
            else if (!parentConcept.getChildConcepts().contains(rejectedConcept)) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "The concept with the name '" + rejectedName + "' is no longer a child of '" +
                                 parentConcept.getPrimaryConceptName().getName() +
                                 "'. Moving the history to the correct concept.");

                Concept newParentConcept = rejectedConcept.getParentConcept();

                parentConcept.getConceptMetadata().removeHistory(history);
                newParentConcept.getConceptMetadata().addHistory(history);

                return;
            }

            /*
             * Get a count of all the concepts we're about to disappear forever
             */
            Collection<Concept> conceptsToBeDeleted = conceptDAO.findDescendents(rejectedConcept);

            /*
             * Let the user know just how much damage they're about to do to the database
             */
            Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
            int option = JOptionPane.showConfirmDialog(frame,
                "You are about to delete " + conceptsToBeDeleted.size() +
                " concept(s) from the \nknowledgebase. Are you sure you want to continue?", "VARS - Delete Concepts",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            /*
             * Get all concept-names that will be deleted. Use those to find all the Observations that
             * will be affected.
             */
            ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO(
                dao.getEntityManager());
            Collection<Observation> observations = observationDAO.findAllByConcept(rejectedConcept, true);
            final String newName = parentConcept.getPrimaryConceptName().getName();
            final String msg = observations.size() + " Observations were found using '" + rejectedName +
                               "' or one of it's \nchildren. Do you want to update the names to '" + newName +
                               "' or \nignore them and leave them as is?";

            /*
             * Report the usages to the user. Allow them to replace with parent concept or leave as is.
             */
            final Object[] options = { "Update", "Ignore", "Cancel" };

            option = JOptionPane.showOptionDialog(frame, msg, "VARS - Removing '" + rejectedName + "'",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

            switch (option) {
            case JOptionPane.YES_OPTION:

                // Updated
                for (Observation observation : observations) {
                    observation.setConceptName(newName);
                }

                break;

            case JOptionPane.NO_OPTION:

                // Ignore
                break;

            default:

                // Cancel
                return;
            }

            // Delete the concept and it's children
            conceptDAO.endTransaction();
            conceptDAO.cascadeRemove(rejectedConcept);    // This handles starting and stopping the transaction internally

            dao.startTransaction();
            super.reject(userAccount, history, dao);
        }
    }


    private class AddLinkRealizationTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {

            if (!canDo(userAccount, history)) {
                return;
            }

            // DAOTX
            history = dao.findInDatastore(history);

            // Convenient means to parse the string stored in the history
            final LinkBean linkBean = new LinkBean(history.getNewValue());
            final LinkRealization exampleRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();

            exampleRealization.setLinkName(linkBean.getLinkName());
            exampleRealization.setToConcept(linkBean.getToConcept());
            exampleRealization.setLinkValue(linkBean.getLinkValue());

            final ConceptMetadata conceptMetadata = history.getConceptMetadata();

            /*
             * Find the matching linkTemplate
             */
            Collection<ILink> linkRealizations = new ArrayList<ILink>(conceptMetadata.getLinkRealizations());
            Collection<ILink> matchingLinks = LinkUtilities.findMatchingLinksIn(linkRealizations, exampleRealization);
            LinkRealization linkRealization = (LinkRealization) ((matchingLinks.size() > 0)
                ? matchingLinks.iterator().next() : null);

            if (linkRealization != null) {

                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane
                    .showConfirmDialog(
                        frame, "Are you sure you want to delete '" + history.getNewValue() +
                        "' ? Be aware that this will not effect existing annotations that use it.", "VARS - Delete LinkTemplate", JOptionPane
                            .YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                conceptMetadata.removeLinkRealization(linkRealization);
                dao.remove(linkRealization);

            }

            super.reject(userAccount, history, dao);
        }
    }


    private class AddLinkTemplateTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            if (!canDo(userAccount, history)) {
                return;
            }

            // DAOTX
            history = dao.findInDatastore(history);

            // Convenient means to parse the string stored in the history
            final LinkBean linkBean = new LinkBean(history.getNewValue());
            final LinkTemplate exampleTemplate = toolBelt.getKnowledgebaseFactory().newLinkTemplate();

            exampleTemplate.setLinkName(linkBean.getLinkName());
            exampleTemplate.setToConcept(linkBean.getToConcept());
            exampleTemplate.setLinkValue(linkBean.getLinkValue());

            final ConceptMetadata conceptMetadata = history.getConceptMetadata();

            /*
             * Find the matching linkTemplate
             */
            Collection<ILink> linkTemplates = new ArrayList<ILink>(conceptMetadata.getLinkTemplates());
            Collection<ILink> matchingLinks = LinkUtilities.findMatchingLinksIn(linkTemplates, exampleTemplate);
            LinkTemplate linkTemplate = (LinkTemplate) ((matchingLinks.size() > 0)
                ? matchingLinks.iterator().next() : null);

            if (linkTemplate != null) {

                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane
                    .showConfirmDialog(
                        frame, "Are you sure you want to delete '" + history.getNewValue() +
                        "' ? Be aware that this will not effect existing annotations that use it.", "VARS - Delete LinkTemplate", JOptionPane
                            .YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                conceptMetadata.removeLinkTemplate(linkTemplate);
                dao.remove(linkTemplate);

            }

            super.reject(userAccount, history, dao);

        }
    }


    private class AddMediaTask extends GenericRejectTask {


        /**
         *
         * @param userAccount
         * @param aHistory
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History aHistory, DAO dao) {
            if (canDo(userAccount, aHistory)) {

                MediaDAO mediaDAO = toolBelt.getKnowledgebaseDAOFactory().newMediaDAO();

                mediaDAO.startTransaction();

                final History history = mediaDAO.merge(aHistory);
                ConceptMetadata conceptMetadata = history.getConceptMetadata();

                // Iterate on copy to avoid threading issues
                final Set<Media> mediaSet = new HashSet<Media>(conceptMetadata.getMedias());

                final Collection<Media> matches = Collections2.filter(mediaSet, new Predicate<Media>() {

                    public boolean apply(Media input) {
                        return input.getUrl().equals(history.getNewValue());
                    }

                });

                for (Media media : matches) {
                    conceptMetadata.removeMedia(media);
                    mediaDAO.remove(media);
                }

                mediaDAO.endTransaction();

                super.reject(userAccount, history, dao);

            }
        }
    }


    private class GenericRejectTask extends AbstractHistoryTask {

        /**
         *
         * @param userAccount
         * @param history
         */
        public void doTask(final UserAccount userAccount, History history) {
            throw new UnsupportedOperationException("Don't call doTask(), call reject() instead.");
        }

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        public void reject(final UserAccount userAccount, History history, DAO dao) {
            if (canDo(userAccount, history)) {
                history = dao.findInDatastore(history);
                history.setProcessedDate(new Date());
                history.setApproved(Boolean.FALSE);
                history.setProcessorName(userAccount.getUserName());
            }
        }
    }


    private class ReplaceConceptNameTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            history = dao.findInDatastore(history);

            if (canDo(userAccount, history)) {

                final Concept concept = history.getConceptMetadata().getConcept();
                final ConceptName conceptName = concept.getConceptName(history.getNewValue());

                if (conceptName == null) {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "Unable to find a concept named '" + history.getNewValue() + "'" +
                                     "associated with '" + concept.getPrimaryConceptName().getName() +
                                     "'. Unable to reject this history.");

                    // TODO what to do now?
                }
                else {
                    ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(dao.getEntityManager());

                    // Verify that the old name is not being used by another concept in the database. If it is return
                    Concept duplicate = conceptDAO.findByName(history.getOldValue());

                    if (duplicate == null) {
                        conceptName.setName(history.getOldValue());

                        /*
                         * Make sure that no annotations are using the concept-name that is
                         * being deleted.
                         */
                        Thread thread = new Thread(new Runnable() {

                            public void run() {
                                try {
                                    toolBelt.getKnowledgebasePersistenceService().updateConceptNameUsedByAnnotations(
                                        concept);
                                }
                                catch (Exception e) {
                                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                                }

                            }
                        }, "Update thread for annotations named " + history.getOldValue());

                        thread.setDaemon(false);
                        thread.start();
                        super.reject(userAccount, history, dao);


                    }
                    else {
                        dropHistory(history,
                                    "Unable to reject this history. The name '" + history.getOldValue() +
                                    "' exists in the knowledgebase.", dao);
                    }
                }
            }
        }
    }


    private class ReplaceNodcCodeTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            if (canDo(userAccount, history)) {
                history = dao.findInDatastore(history);

                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getNodcCode();
                final String newValue = history.getNewValue();

                if (((currentValue != null) && currentValue.equals(newValue)) ||
                        ((newValue != null) && newValue.equals(currentValue))) {
                    concept.setNodcCode(history.getOldValue());
                    super.reject(userAccount, history, dao);
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "Unable to reject this history. The NODC Code has been modified" +
                                     " since this history was created.");

                    // TODO what to do now?
                }
            }
        }
    }


    private class ReplaceParentConceptTask extends GenericRejectTask {


        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            if (canDo(userAccount, history)) {
                history = dao.findInDatastore(history);

                final ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(
                    dao.getEntityManager());

                final Concept concept = history.getConceptMetadata().getConcept();
                Concept currentParent = concept.getParentConcept();

                /*
                 * Need to do a little database lookup.
                 */
                Concept newParent = null;
                Concept oldParent = null;

                conceptDAO.startTransaction();

                try {
                    history = conceptDAO.merge(history);
                    newParent = conceptDAO.findByName(history.getNewValue());
                    oldParent = conceptDAO.findByName(history.getOldValue());
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                }

                conceptDAO.endTransaction();

                if (currentParent.equals(newParent)) {
                    if (oldParent != null) {

                        conceptDAO.startTransaction();
                        oldParent = conceptDAO.merge(oldParent);

                        Collection<Concept> descendents = conceptDAO.findDescendents(oldParent);

                        conceptDAO.endTransaction();

                        Collection<Concept> matches = Collections2.filter(descendents, new Predicate<Concept>() {

                            public boolean apply(Concept input) {
                                return input.getPrimaryConceptName().getName().equals(
                                    concept.getPrimaryConceptName().getName());
                            }


                        });

                        if (matches.size() > 0) {
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                             "\'" + oldParent.getPrimaryConceptName().getName() +
                                             "\' already has a child named \'" +
                                             concept.getPrimaryConceptName().getName() +
                                             "\'. Unable to process your request.");

                            return;
                        }

                        /*
                         * Update the database.
                         */

                        currentParent.removeChildConcept(concept);
                        oldParent.addChildConcept(concept);
                        super.reject(userAccount, history, dao);

                    }
                    else {

                        /*
                         * We can't reject the change if we can't find the original parent
                         */
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                         "Unable to find the original parent. Unable to move \'" +
                                         concept.getPrimaryConceptName().getName() +
                                         "\' back to it\'s previous state.");
                    }
                }
                else {

                    /*
                     * If the expected new parent does not match the current parent we can't reject it. Ultimately,
                     * the user will have to accept the change since there is no way to roll back.
                     */
                    String message = null;

                    if (newParent != null) {
                        message = "The concept, \'" + concept.getPrimaryConceptName().getName() +
                                  "\' was a child of \'" + newParent.getPrimaryConceptName().getName() +
                                  "\' when this history was created. However, it\'s now a child of \'" +
                                  currentParent.getPrimaryConceptName().getName() +
                                  "\'. Unable to move it back to it\'s previous state.";
                    }
                    else {
                        message = "The concept, \'" + history.getNewValue() +
                                  "\' was not found in the knowledgebase. It may " +
                                  "have been deleted or renamed. Unable to move \'" +
                                  concept.getPrimaryConceptName().getName() + "\' back to it\'s previous state.";
                    }

                    EventBus.publish(Lookup.TOPIC_WARNING, message);
                }
            }
        }
    }


    private class ReplaceRankLevelTask extends GenericRejectTask {


        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            history = dao.findInDatastore(history);

            if (canDo(userAccount, history)) {
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getRankLevel();
                final String newValue = history.getNewValue();

                if (((currentValue != null) && currentValue.equals(newValue)) ||
                        ((newValue != null) && newValue.equals(currentValue))) {
                    concept.setRankLevel(history.getOldValue());
                    super.reject(userAccount, history, dao);

                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "Unable to reject this history. The Rank Level has been modified" +
                                     " since this history was created.");

                    // TODO what to do now?
                }
            }
        }
    }


    private class ReplaceRankNameTask extends GenericRejectTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            history = dao.findInDatastore(history);

            if (canDo(userAccount, history)) {
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getRankName();
                final String newValue = history.getNewValue();

                if (((currentValue != null) && currentValue.equals(newValue)) ||
                        ((newValue != null) && newValue.equals(currentValue))) {
                    concept.setRankName(history.getOldValue());
                    super.reject(userAccount, history, dao);
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "Unable to reject this history. The Rank Name has been modified" +
                                     " since this history was created.");

                    // TODO what to do now?
                }
            }
        }
    }


    private class ReplaceReferenceTask extends GenericRejectTask {


        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        @Override
        public void reject(UserAccount userAccount, History history, DAO dao) {
            history = dao.findInDatastore(history);

            if (canDo(userAccount, history)) {
                final Concept concept = history.getConceptMetadata().getConcept();
                String currentValue = concept.getReference();
                final String newValue = history.getNewValue();

                if (((currentValue != null) && currentValue.equals(newValue)) ||
                        ((newValue != null) && newValue.equals(currentValue))) {
                    concept.setReference(history.getOldValue());
                    super.reject(userAccount, history, dao);
                }
                else {
                    EventBus.publish(Lookup.TOPIC_WARNING,
                                     "Unable to reject this history. The Reference has been modified" +
                                     " since this history was created.");
                }
            }
        }
    }
}
