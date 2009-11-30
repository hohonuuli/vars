/*
 * @(#)ApproveHistoryTask.java   2009.10.02 at 03:37:20 PDT
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

import com.google.inject.Inject;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;

import vars.DAO;
import vars.ILink;
import vars.LinkBean;
import vars.LinkUtilities;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.ui.Lookup;
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
    private final Map<String, Map<String, GenericApproveTask>> actionMap = new HashMap<String, Map<String, GenericApproveTask>>();
    private final GenericApproveTask DEFAULT_TASK = new GenericApproveTask();
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param annotationDAOFactory
     * @param knowledgebaseDAO
     * @param knowledgebaseDAOFactory
     * @param knowledgebaseFactory
     */
    @Inject
    public ApproveHistoryTask(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;



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

    public void approve(final UserAccount userAccount, final History history) {
        if ((history != null) && !history.isProcessed()) {
            if (log.isDebugEnabled()) {
                log.debug("Approving " + history);
            }

            final Map<String, GenericApproveTask> approveActions = actionMap.get(history.getAction());
            GenericApproveTask processer = approveActions.get(history.getField());
            if (processer == null) {
                processer = DEFAULT_TASK;
            }

            DAO dao = null;
            try {
                dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
                dao.startTransaction();
                processer.approve(userAccount, history, dao);
            }
            catch (Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            }
            finally {
                if (dao != null) {
                    dao.endTransaction();
                }
            }
        }
    }

    public void doTask(final UserAccount userAccount, final History history) {
        approve(userAccount, history);
    }

    private class ADeleteChildConceptTask extends GenericApproveTask {


        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {

            /*
             * Find the child concept to be deleted.
             */
            history = dao.findInDatastore(history);
            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final Collection<Concept> children = new ArrayList<Concept>(parentConcept.getChildConcepts());
            Concept concept = null; // <-- Concept node we are dropping. This Cascades!!!!
            for (Concept child : children) {
                final ConceptName conceptName = child.getConceptName(history.getOldValue());
                if (conceptName != null) {
                    concept = child;
                    break;
                }
            }
            
            /*
             * Get a count of all the concepts we're about to disappear forever
             */
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO(dao.getEntityManager());
            concept = conceptDAO.findInDatastore(concept);
            Collection<Concept> conceptsToBeDeleted = conceptDAO.findDescendents(concept);
            
            
            Dispatcher dispatcher = Lookup.getApplicationFrameDispatcher();
            Frame frame = (Frame) dispatcher.getValueObject();
            final int option = JOptionPane .showConfirmDialog( frame,
                    "You are about to delete " + conceptsToBeDeleted.size() +
                    " concept(s) from the \nknowledgebase. Are you sure you want to continue?",
                    "VARS - Delete Concepts",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            
            

            
            if (concept != null) {
                DeleteConceptTask dct = new DeleteConceptTask(toolBelt.getAnnotationDAOFactory(), knowledgebaseDAOFactory);
                if (dct.delete(concept)) {
                    DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
                    history = dao.findInDatastore(history);
                    super.approve(userAccount, history);
                }
                else {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                     "Failed to delete the concept, " + concept.getPrimaryConceptName().getName() +
                                     " from the knowledgebase");
                }
            }
            else {
                dropHistory(history,
                            "No child Concept containg the name '" + history.getOldValue() +
                            "' was found. I'll remove the obsolete history information");
            }
        }
    }


    private class ADeleteConceptNameAction extends GenericApproveTask {


        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {
            /*
             * Confirm that we really want to do this
             */
            final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
            final int option = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete '" + history.getOldValue() + 
                    "' ? Be aware that this will update existing annotations that use it.", 
                    "VARS - Delete ConceptName", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            
            history = dao.findInDatastore(history);
            final Concept concept = history.getConceptMetadata().getConcept();
            final ConceptName conceptName = concept.getConceptName(history.getOldValue());
            if (conceptName != null) {

                /*
                 *  Make sure that the name you are deleting isn't used by any observations anymore.
                 *  We'll do this in the backgrounds since the knowledgebase UI doesn't depend on 
                 *  any annotation information
                 */
                Thread thread = new Thread(new Runnable() {
                    
                    public void run() {
                        log.info("Updating conceptnames that used '{}'", conceptName.getName());
                        toolBelt.getKnowledgebaseDAO().updateConceptNameUsedByAnnotations(concept);
                    }
                }, "Thread for updating annotations named " + conceptName.getName());
                thread.setDaemon(false);
                thread.start();
                concept.removeConceptName(conceptName);
                dao.remove(conceptName);
            }
            else {
                dropHistory(history, "'" + history.getOldValue() + "' was not found. I'll remove the obsolete history", dao);
            }

            super.approve(userAccount, history, dao);
        }
    }


    private class ADeleteLinkRealizationTask extends GenericApproveTask {
        
        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {
                
                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete '" + history.getOldValue() + 
                        "' ? Be aware that this will not effect existing annotations that use it.", 
                        "VARS - Delete LinkRealization", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // Convenient means to parse the string stored in the history
                final ILink link = new LinkBean(history.getOldValue());
                final LinkRealization exampleRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();
                exampleRealization.setLinkName(link.getLinkName());
                exampleRealization.setToConcept(link.getToConcept());
                exampleRealization.setLinkValue(link.getLinkValue());

                // DAOTX
                history = dao.findInDatastore(history);
                final Concept concept = history.getConceptMetadata().getConcept();

                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkRealizations = new ArrayList<ILink>(concept.getConceptMetadata().getLinkRealizations());
                Collection<ILink> matchingLinks = LinkUtilities.findMatchingLinksIn(linkRealizations, exampleRealization);
                LinkRealization linkRealization = null; // <-- This is what we are deleting
                if (matchingLinks.size() > 0) {
                    linkRealization = (LinkRealization) matchingLinks.iterator().next();
                }

                if (linkRealization != null) {
                    
                    linkRealization.getConceptMetadata().removeLinkTemplate(linkRealization);
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

        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {
                
                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete '" + history.getOldValue() + 
                        "' ? Be aware that this will not effect existing annotations that use it.", 
                        "VARS - Delete LinkTemplate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
                history = dao.findInDatastore(history);
                final Concept concept = history.getConceptMetadata().getConcept();

                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkTemplates = new ArrayList<ILink>(concept.getConceptMetadata().getLinkTemplates());
                Collection<ILink> matchingLinks = LinkUtilities.findMatchingLinksIn(linkTemplates, exampleTemplate);
                LinkTemplate linkTemplate = null; // <-- This is what we are deleting
                if (matchingLinks.size() > 0) {
                    linkTemplate = (LinkTemplate) matchingLinks.iterator().next();
                }

                if (linkTemplate != null) {
                    linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);
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

        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {
                
                /*
                 * Confirm that we really want to do this
                 */
                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to delete '" + history.getOldValue() +  "' ? ", 
                        "VARS - Delete Media", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // DAOTX
                history = dao.findInDatastore(history);
                final Concept concept = history.getConceptMetadata().getConcept();

                /*
                 * Find the matching Media
                 */
                Collection<Media> medias = new ArrayList<Media>(concept.getConceptMetadata().getMedias());
                Media media = null;
                for (Media m : medias) {
                    if (m.getUrl().equalsIgnoreCase(history.getOldValue())) {
                        media = m;
                        break;
                    }
                }

                if (media != null) {
                    media.getConceptMetadata().removeMedia(media);
                    dao.remove(media);
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


    /**
     * For those History's who only need the history approved and no other action
     * done.
     */
    private class GenericApproveTask extends AbstractHistoryTask implements IApproveHistoryTask {


        public void approve(UserAccount userAccount, History history, DAO dao) {
            doTask(userAccount, history, dao);
        }

        /**
         * @throws RuntimeException
         *             This exception should be caught, logged and a dialog
         *             should be shown to the user.
         */
        public void doTask(final UserAccount userAccount, History history, DAO dao) {
            if (canDo(userAccount, history)) {
                history = dao.findInDatastore(history);
                history.setProcessedDate(new Date());
                history.setProcessorName(userAccount.getUserName());
                history.setApproved(Boolean.TRUE);
            }
            else {
                final String msg = "Unable to approve the History [" + history + "]";
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
            }

        }

    }

    @Override
    public void doTask(UserAccount userAccount, History history, DAO dao) {
        log.info("{} is not being used", dao);
        doTask(userAccount, history);
    }
}
