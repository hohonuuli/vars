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
import vars.DAO;
import vars.ILink;
import vars.LinkBean;
import vars.LinkUtilities;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
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
    private final Map<String, Map<String, AbstractHistoryTask>> actionMap = new HashMap<String,
        Map<String, AbstractHistoryTask>>();
    private final GenericApproveTask DEFAULT_TASK;
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
        super(toolBelt.getKnowledgebaseDAOFactory());
        this.toolBelt = toolBelt;


        DEFAULT_TASK = new GenericApproveTask(knowledgebaseDAOFactory);

        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_*
         * for acceptabe values. The Map holds that process the approval
         */
        final Map<String, AbstractHistoryTask> addMap = new HashMap<String, AbstractHistoryTask>();
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
        final Map<String, AbstractHistoryTask> deleteMap = new HashMap<String, AbstractHistoryTask>();
        actionMap.put(History.ACTION_DELETE, deleteMap);

        /*
         * A concept is never deleted directly. It's deleted from the parent
         * concept. This allows us to track History better.
         * deleteMap.put(History.FIELD_CONCEPT, new RemoveConceptAction());
         */
        deleteMap.put(History.FIELD_CONCEPT_CHILD, new ADeleteChildConceptTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_CONCEPTNAME, new ADeleteConceptNameAction(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_LINKREALIZATION, new ADeleteLinkRealizationTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_LINKTEMPLATE, new ADeleteLinkTemplateTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_MEDIA, new ADeleteMediaTask(knowledgebaseDAOFactory));

        final Map<String, AbstractHistoryTask> replaceMap = new HashMap<String, AbstractHistoryTask>();
        actionMap.put(History.ACTION_REPLACE, replaceMap);
        replaceMap.put(History.FIELD_CONCEPT_PARENT, DEFAULT_TASK);
    }

    public void approve(final UserAccount userAccount, final History history) {
        if ((history != null) && !history.isProcessed()) {
            if (log.isDebugEnabled()) {
                log.debug("Approving " + history);
            }

            final Map approveActions = actionMap.get(history.getAction());
            GenericApproveTask processer = (GenericApproveTask) approveActions.get(history.getField());
            if (processer == null) {
                processer = DEFAULT_TASK;
            }

            DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
            processer.approve(userAccount, history);
        }
    }

    public void doTask(final UserAccount userAccount, final History history) {
        approve(userAccount, history);
    }

    private class ADeleteChildConceptTask extends GenericApproveTask {

        ADeleteChildConceptTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        public void approve(final UserAccount userAccount, History history, DAO dao) {

            /*
             * Find the child concept to be deleted.
             */
            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final Collection<Concept> children = new ArrayList<Concept>(parentConcept.getChildConcepts());
            Concept concept = null;
            for (Concept child : children) {
                final ConceptName conceptName = child.getConceptName(history.getOldValue());
                if (conceptName != null) {
                    concept = child;

                    break;
                }
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

        ADeleteConceptNameAction(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {
            DAO dao = knowledgebaseDAOFactory.newDAO();
            dao.startTransaction();
            history = dao.merge(history);
            Concept concept = history.getConceptMetadata().getConcept();
            final ConceptName conceptName = concept.getConceptName(history.getOldValue());
            dao.endTransaction();
            if (conceptName != null) {

                // Make sure that the name you are deleting isn't used by any observations anymore
                toolBelt.getKnowledgebaseDAO().updateConceptNameUsedByAnnotations(concept);

                dao.startTransaction();
                concept = dao.merge(concept);
                concept.removeConceptName(conceptName);
                dao.remove(conceptName);
                dao.endTransaction();
            }
            else {
                dropHistory(history, "'" + history.getOldValue() + "' was not found. I'll remove the obsolete history");
            }

            super.approve(userAccount, history);
        }
    }


    private class ADeleteLinkRealizationTask extends GenericApproveTask {

        private final vars.knowledgebase.ui.actions.DeleteLinkRealizationTask deleteLinkRealizationTask;

        @Inject
        ADeleteLinkRealizationTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.deleteLinkRealizationTask = new DeleteLinkRealizationTask(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {
            if (canDo(userAccount, history)) {

                // Parse the string stored in the history
                final ILink link = new LinkBean(history.getOldValue());
                final LinkRealization exampleRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();
                exampleRealization.setLinkName(link.getLinkName());
                exampleRealization.setToConcept(link.getToConcept());
                exampleRealization.setLinkValue(link.getLinkValue());

                final Concept concept = history.getConceptMetadata().getConcept();

                /*
                 * Find the matching linkTemplate
                 */
                LinkRealization linkRealization = null;
                Set<LinkRealization> linkRealizations = new HashSet<LinkRealization>(
                    concept.getConceptMetadata().getLinkRealizations());
                for (LinkRealization t : linkRealizations) {
                    if (t.getLinkName().equals(exampleRealization.getLinkName()) &&
                            t.getToConcept().equals(exampleRealization.getToConcept()) &&
                            t.getLinkValue().equals(exampleRealization.getLinkValue())) {

                        linkRealization = t;

                        break;
                    }
                }

                if (linkRealization != null) {
                    if (deleteLinkRealizationTask.delete(linkRealization)) {
                        super.approve(userAccount, history);
                    }
                    else {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                         "Failed to delete the linkTemplate, " + linkRealization.stringValue() +
                                         " from the knowledgebase");
                    }
                }
                else {
                    dropHistory(history,
                                "Unable to locate '" + history.getNewValue() +
                                "'. It may have been moved. I'll remove the History reference.");
                }

            }
        }
    }


    private class ADeleteLinkTemplateTask extends GenericApproveTask {

        private final DeleteLinkTemplateTask deleteLinkTemplateTask;

        ADeleteLinkTemplateTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.deleteLinkTemplateTask = new DeleteLinkTemplateTask(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {

            if (canDo(userAccount, history)) {

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


                DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
                dao.startTransaction();
                history = dao.findInDatastore(history);
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();
                final Concept concept = conceptMetadata.getConcept();
                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkTemplates = new ArrayList<ILink>(conceptMetadata.getLinkTemplates());
                Collection<ILink> matchingLinkTemplates = LinkUtilities.findMatchesIn(exampleTemplate, linkTemplates);
                LinkTemplate linkTemplate = null;
                if (matchingLinkTemplates.size() > 0) {
                    linkTemplate = (LinkTemplate) matchingLinkTemplates.iterator().next();
                }
                else {
                    dropHistory(history, "Unable to locate '" + history.getOldValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }


                if (linkTemplate != null) {
                    if (deleteLinkTemplateTask.delete(linkTemplate)) {
                        super.approve(userAccount, history);
                    }
                    else {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                         "Failed to delete the linkTemplate, " + linkTemplate.stringValue() +
                                         " from the knowledgebase");
                    }

                }
                else {
                    dropHistory(history,
                                "Unable to locate '" + history.getNewValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.");
                }
            }
        }
    }


    private class ADeleteMediaTask extends GenericApproveTask {

        private final DeleteMediaTask deleteMediaTask;

        ADeleteMediaTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
            this.deleteMediaTask = new DeleteMediaTask(knowledgebaseDAOFactory);
        }

        public void approve(UserAccount userAccount, History history, DAO dao) {

            /*
             * Find the correct media object to delete
             */
            final Collection<Media> mediaSet = history.getConceptMetadata().getMedias();
            Media media = null;
            for (Iterator i = mediaSet.iterator(); i.hasNext(); ) {
                final Media m = (Media) i.next();
                if (m.getUrl().equalsIgnoreCase(history.getOldValue())) {
                    media = m;

                    break;
                }
            }

            if (media != null) {
                if (deleteMediaTask.delete(media)) {
                    super.approve(userAccount, history);
                }
                else {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR,
                                     "Failed to delete the media, " + media.stringValue() + " from the knowledgebase");
                }
            }
            else {
                dropHistory(history, "No matching media was found; removing the History reference");
            }

        }
    }


    /**
     * For those History's who only need the history approbed and no othe action
     * done.
     */
    private class GenericApproveTask extends AbstractHistoryTask implements IApproveHistoryTask {

        GenericApproveTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        public void approve(UserAccount userAccount, History history, DAO dao) {
            doTask(userAccount, history);
        }

        /**
         * @throws RuntimeException
         *             This exception should be caught, logged and a dialog
         *             should be shown to the user.
         */
        public void doTask(final UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                
                ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
                try {
                    dao.startTransaction();
                    history = dao.merge(history);
                    history.setProcessedDate(new Date());
                    history.setProcessorName(userAccount.getUserName());
                    history.setApproved(Boolean.TRUE);
                    dao.endTransaction();
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                }
            }
            else {
                final String msg = "Unable to approve the History [" + history + "]";
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
            }

        }
    }
}
