/*
 * @(#)ApproveHistoryTask.java   2009.12.01 at 03:16:25 PST
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
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
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
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.History;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;

/**
 * <!-- Class Description -->
 *
 *
 * @version $Id: ApproveHistoryTask.java 257 2006-06-13 22:49:09Z hohonuuli $
 * @author MBARI
 */
public class ApproveHistoryTask extends AbstractHistoryTask {

    /*
     * Map<String, AbstractHistoryTask>
     */
    private final Map<String, Map<String, GenericApproveTask>> actionMap = new HashMap<String,
        Map<String, GenericApproveTask>>();
    private final GenericApproveTask DEFAULT_TASK;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    @Inject
    public ApproveHistoryTask(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;


        DEFAULT_TASK = new GenericApproveTask();

        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_*
         * for acceptable values. The Map holds that process the approval
         */
        final Map<String, GenericApproveTask> addMap = new HashMap<String, GenericApproveTask>();

        actionMap.put(History.ACTION_ADD, addMap);
        addMap.put(History.FIELD_CONCEPT_CHILD, DEFAULT_TASK);
        addMap.put(History.FIELD_CONCEPTNAME, DEFAULT_TASK);
        addMap.put(History.FIELD_LINKREALIZATION, DEFAULT_TASK);
        addMap.put(History.FIELD_LINKTEMPLATE, DEFAULT_TASK);
        addMap.put(History.FIELD_MEDIA, DEFAULT_TASK);

        /*
         * This map holds actions that process the approval of remove actions
         * deleteMap<String, IAction> String defines which field was Added
         */
        final Map<String, GenericApproveTask> deleteMap = new HashMap<String, GenericApproveTask>();

        actionMap.put(History.ACTION_DELETE, deleteMap);

        /*
         * A concept is never deleted directly. It's deleted from the parent
         * concept. This allows us to track History better.
         * deleteMap.put(History.FIELD_CONCEPT, new RemoveConceptAction());
         */
        deleteMap.put(History.FIELD_CONCEPT_CHILD, new ADeleteChildConceptTask());
        deleteMap.put(History.FIELD_CONCEPTNAME, new ADeleteConceptNameAction());
        deleteMap.put(History.FIELD_LINKREALIZATION, new ADeleteLinkRealizationTask());
        deleteMap.put(History.FIELD_LINKTEMPLATE, new ADeleteLinkTemplateTask());
        deleteMap.put(History.FIELD_MEDIA, new ADeleteMediaTask());

        final Map<String, GenericApproveTask> replaceMap = new HashMap<String, GenericApproveTask>();

        actionMap.put(History.ACTION_REPLACE, replaceMap);
        replaceMap.put(History.FIELD_CONCEPT_PARENT, DEFAULT_TASK);
    }

    /**
     *
     * @param userAccount
     * @param history
     * @param dao
     */
    public void approve(final UserAccount userAccount, final History history, DAO dao) {
        if ((history != null) && !history.isProcessed()) {
            if (log.isDebugEnabled()) {
                log.debug("Approving " + history);
            }

            final Map<String, GenericApproveTask> approveActions = actionMap.get(history.getAction());
            GenericApproveTask processer = approveActions.get(history.getField());

            if (processer == null) {
                processer = DEFAULT_TASK;
            }

            processer.approve(userAccount, history, dao);
        }
    }

    /**
     *
     * @param userAccount
     * @param history
     */
    public void doTask(final UserAccount userAccount, History history) {
        DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();

        try {
            dao.startTransaction();
            // Bring History into transaction
            try {
            	history = dao.merge(history);
            }
            catch (Exception e) {
            	history = dao.find(history);
            }
            approve(userAccount, history, dao);
        }
        catch (Exception e) {
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
        }
        finally {
            dao.endTransaction();
            dao.close();
        }
    }

    /**
     * Approve multiple histories. Most will get done in a single transaction, but
     * some, such as deleting a branch of concepts, requires multiple transactions
     * @param userAccount
     * @param histories
     */
    public void doTask(final UserAccount userAccount, Collection<? extends History> histories) {
        DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
        for (History history : histories) {

            // Skip histories that have already been accepted or rejected
            if (history.isProcessed()) {
                continue;
            }

            try {
                dao.startTransaction();
                // Bring History into transaction
                history = dao.find(history);
                approve(userAccount, history, dao);
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            }
        }
        dao.endTransaction();
        dao.close();
    }

    private class ADeleteChildConceptTask extends GenericApproveTask {


        /**
         *
         * @param userAccount
         * @param history Should already be part of the dao's transaction
         * @param dao
         */
        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {

            /*
             * Find the child concept to be deleted.
             */
            String nameToDelete = history.getOldValue();

            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final Collection<Concept> children = new ArrayList<Concept>(parentConcept.getChildConcepts());
            Concept concept = null;

            for (Concept child : children) {
                final ConceptName conceptName = child.getConceptName(nameToDelete);

                if (conceptName != null) {
                    concept = child;

                    break;
                }
            }

            if (concept == null) {
                dropHistory(history,
                            "No child Concept containg the name '" + history.getOldValue() +
                            "' was found. I'll remove the obsolete history information", dao);

                return;
            }

            /*
             * Get a count of all the concepts we're about to disappear forever
             */
            // DAOTX
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(dao.getEntityManager());

            concept = conceptDAO.find(concept);

            Collection<Concept> conceptsToBeDeleted = conceptDAO.findDescendents(concept);

            /*
             * Let the user know just how much damage they're about to do to the database
             */
            Frame frame = StateLookup.getApplicationFrame();
            int option = JOptionPane.showConfirmDialog(frame,
                "You are about to delete " + conceptsToBeDeleted.size() +
                " concept(s) from the \nknowledgebase. Are you sure you want to continue?", "VARS - Delete Concepts",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            /*
             * Get all concept-names that will be deleted. Use those to find all the Observations that
             * will be affected. ObservationDAO and conceptDAO may use different EntityManagerFactories
             * so we don't share a transaction
             */
            ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
            observationDAO.startTransaction();
            Collection<Observation> observations = observationDAO.findAllByConcept(concept, true, conceptDAO);

            if (observations.size() > 0) {
                final String newName = parentConcept.getPrimaryConceptName().getName();
                final String msg = observations.size() + " Observations were found using '" + nameToDelete +
                                   "' or one of it's \nchildren. Do you want to update the names to '" + newName +
                                   "' or \nignore them and leave them as is?";

                /*
                 * Report the usages to the user. Allow them to replace with parent concept or leave as is.
                 */
                final Object[] options = { "Update", "Ignore", "Cancel" };

                option = JOptionPane.showOptionDialog(frame, msg, "VARS - Removing '" + nameToDelete + "'",
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
            }
            observationDAO.endTransaction();

            // Delete the concept and it's children
            super.approve(userAccount, history, dao);
            conceptDAO.endTransaction();
            conceptDAO.cascadeRemove(concept);    // This handles starting and stopping the transaction internally
            conceptDAO.close();
        }
    }


    private class ADeleteConceptNameAction extends GenericApproveTask {


        /**
         *
         * @param userAccount
         * @param history Should already be part of the dao's transaction
         * @param dao
         */
        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {

            final String conceptNameToDelete = history.getOldValue();

            if (canDo(userAccount, history)) {
                final Frame frame = StateLookup.getApplicationFrame();
                final int option = JOptionPane.showConfirmDialog(
                        frame, "Are you sure you want to delete '" + conceptNameToDelete +
                        "' ? Be aware that this will change existing annotations that use it.", "VARS - Delete ConceptName", JOptionPane
                            .YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }


            }

            /*
	         * DAOTX Bring objects into persistence transaction
	         */
            final Concept concept = history.getConceptMetadata().getConcept();

            /*
             * Make sure that no annotations are using the concept-name that is
             * being deleted.
             */
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    try {
                        toolBelt.getKnowledgebasePersistenceService().updateConceptNameUsedByLinkTemplates(concept);
                        toolBelt.getAnnotationPersistenceService().updateConceptNameUsedByAnnotations(concept);
                    }
                    catch (Exception e) {
                        EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                    }

                }
            }, "Update thread for annotations named " + conceptNameToDelete);

            thread.setDaemon(false);
            thread.start();

            /*
             * Drop the conceptname from the database
             */
            ConceptNameDAO conceptNameDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptNameDAO(
                dao.getEntityManager());
            ConceptName conceptName = conceptNameDAO.findByName(conceptNameToDelete);

            if (conceptName != null) {

                if (conceptName.getNameType().equalsIgnoreCase(ConceptNameTypes.PRIMARY.getName())) {
                    // findByName is case-insensitive. Check that we got the right name
                    ConceptName otherName = conceptName.getConcept().getConceptName(conceptNameToDelete);
                    if (conceptNameDAO.equalInDatastore(conceptName, otherName)) {
                        EventBus.publish(StateLookup.TOPIC_WARNING,
                                         "You are attempting to delete a primary concept-name." + " This is NOT allowed.");
                        return;
                    }
                    else {
                        // same name different case. Use the non-primary name
                        conceptName = otherName;
                    }
                }

                conceptName.getConcept().removeConceptName(conceptName);
                dao.remove(conceptName);
                super.approve(userAccount, history, conceptNameDAO);

            }
            else {
                dropHistory(history,
                            "Unable to locate '" + conceptNameToDelete + "'. I'll remove the History reference.", dao);
            }
            conceptNameDAO.close();
        }
    }


    private class ADeleteLinkRealizationTask extends GenericApproveTask {

        /**
         *
         * @param userAccount
         * @param history Should already be part of the dao's transaction
         * @param dao
         */
        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {

                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = StateLookup.getApplicationFrame();
                final int option = JOptionPane.showConfirmDialog(
                        frame, "Are you sure you want to delete '" + history.getOldValue() +
                        "' ? Be aware that this will not effect existing annotations that use it.", "VARS - Delete LinkRealization", JOptionPane
                            .YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // Convenient means to parse the string stored in the history
                final LinkBean link = new LinkBean(history.getOldValue());
                final LinkRealization exampleRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();

                exampleRealization.setLinkName(link.getLinkName());
                exampleRealization.setToConcept(link.getToConcept());
                exampleRealization.setLinkValue(link.getLinkValue());

                // DAOTX
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();

                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkRealizations = new ArrayList<ILink>(conceptMetadata.getLinkRealizations());
                Collection<ILink> matchingLinkRealizations = LinkUtilities.findMatchingLinksIn(linkRealizations,
                    exampleRealization);
                LinkRealization linkRealization = null;

                if (matchingLinkRealizations.size() > 0) {
                    linkRealization = (LinkRealization) matchingLinkRealizations.iterator().next();
                    conceptMetadata.removeLinkRealization(linkRealization);
                    dao.remove(linkRealization);
                    super.approve(userAccount, history, dao);

                }
                else {
                    dropHistory(history,
                                "Unable to locate '" + history.getOldValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }
            }
        }
    }


    private class ADeleteLinkTemplateTask extends GenericApproveTask {


        /**
         *
         * @param userAccount
         * @param history Should already be part of the dao's transaction
         * @param dao
         */
        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {

                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = StateLookup.getApplicationFrame();
                final int option = JOptionPane.showConfirmDialog(
                        frame, "Are you sure you want to delete '" + history.getOldValue() +
                        "' ? Be aware that this will not effect existing annotations that use it.", "VARS - Delete LinkTemplate", JOptionPane
                            .YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // Convenient means to parse the string stored in the history
                final LinkBean link = new LinkBean(history.getOldValue());
                final LinkTemplate exampleTemplate = toolBelt.getKnowledgebaseFactory().newLinkTemplate();

                exampleTemplate.setLinkName(link.getLinkName());
                exampleTemplate.setToConcept(link.getToConcept());
                exampleTemplate.setLinkValue(link.getLinkValue());

                // DAOTX
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();

                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkTemplates = new ArrayList<ILink>(conceptMetadata.getLinkTemplates());
                Collection<ILink> matchingLinkTemplates = LinkUtilities.findMatchingLinksIn(linkTemplates,
                    exampleTemplate);
                LinkTemplate linkTemplate = null;

                if (matchingLinkTemplates.size() > 0) {
                    linkTemplate = (LinkTemplate) matchingLinkTemplates.iterator().next();
                    conceptMetadata.removeLinkTemplate(linkTemplate);
                    dao.remove(linkTemplate);
                    super.approve(userAccount, history, dao);
                }
                else {
                    dropHistory(history,
                                "Unable to locate '" + history.getOldValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }
            }
        }
    }


    private class ADeleteMediaTask extends GenericApproveTask {

        /**
         *
         * @param userAccount
         * @param history Should already be part of the dao's transaction
         * @param dao
         */
        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {

                final String imageReference = history.getOldValue();

                final Frame frame = StateLookup.getApplicationFrame();
                final int option = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete '" + imageReference + "' ? ", "VARS - Delete Media",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // DAOTX
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();

                /*
                 * Find the matching Media
                 */
                Collection<Media> medias = new ArrayList<Media>(conceptMetadata.getMedias());
                Collection<Media> matchingMedia = Collections2.filter(medias, new Predicate<Media>() {

                    public boolean apply(Media input) {
                        return input.getUrl().equals(imageReference);
                    }

                });

                Media media = null;

                if (matchingMedia.size() > 0) {
                    media = matchingMedia.iterator().next();
                    conceptMetadata.removeMedia(media);
                    dao.remove(media);
                    super.approve(userAccount, history, dao);
                }
                else {
                    dropHistory(history,
                                "Unable to locate '" + imageReference +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }
            }

        }
    }


    /**
     * For those History's who only need the history approved and no other action
     * done.
     */
    private class GenericApproveTask extends AbstractHistoryTask implements IApproveHistoryTask {

        /**
         *
         * @param userAccount
         * @param history
         * @param dao
         */
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {
                history.setProcessedDate(new Date());
                history.setProcessorName(userAccount.getUserName());
                history.setApproved(Boolean.TRUE);
            }
            else {
                final String msg = "Unable to approve the History [" + history + "]";

                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, msg);
            }
        }

        /**
         *
         * @param userAccount
         * @param history
         */
        public void doTask(final UserAccount userAccount, History history) {
            throw new UnsupportedOperationException("Don't call doTask(), call approve() instead!");
        }
    }
}
