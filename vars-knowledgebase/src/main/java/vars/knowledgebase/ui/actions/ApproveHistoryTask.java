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

import java.util.*;

import vars.knowledgebase.*;
import vars.knowledgebase.jpa.GLinkRealization;
import vars.knowledgebase.jpa.GLinkTemplate;
import vars.knowledgebase.ui.Lookup;
import vars.UserAccount;
import vars.LinkBean;
import vars.ILink;
import com.google.inject.Inject;
import org.bushe.swing.event.EventBus;


// ~--- classes ----------------------------------------------------------------

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
    private final Map<String, Map<String, AbstractHistoryTask>> actionMap = new HashMap<String, Map<String, AbstractHistoryTask>>();
    
    private final GenericApproveTask DEFAULT_TASK;

    private final KnowledgebaseDAO knowledgebaseDAO;
    private final KnowledgebaseFactory knowledgebaseFactory;
    
    @Inject
    public ApproveHistoryTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory, KnowledgebaseDAO knowledgebaseDAO,
                              KnowledgebaseFactory knowledgebaseFactory) {
        super(knowledgebaseDAOFactory);
        this.knowledgebaseDAO = knowledgebaseDAO;
        this.knowledgebaseFactory = knowledgebaseFactory;

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
        deleteMap.put(History.FIELD_CONCEPT_CHILD, new DeleteChildConceptTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_CONCEPTNAME, new DeleteConceptNameAction(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_LINKREALIZATION, new DeleteLinkRealizationTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_LINKTEMPLATE, new DeleteLinkTemplateTask(knowledgebaseDAOFactory));
        deleteMap.put(History.FIELD_MEDIA, new DeleteMediaTask(knowledgebaseDAOFactory));

        final Map<String, AbstractHistoryTask> replaceMap = new HashMap<String, AbstractHistoryTask>();
        actionMap.put(History.ACTION_REPLACE, replaceMap);
        replaceMap.put(History.FIELD_CONCEPT_PARENT, DEFAULT_TASK);
    }
    
    public void approve(final UserAccount userAccount, final History history) {
        if ((history != null) && !history.isApproved() && !history.isRejected()) {
            if (log.isDebugEnabled()) {
                log.debug("Approving " + history);
            }
            final Map approveActions = actionMap.get(history.getAction());
            GenericApproveTask processer = (GenericApproveTask) approveActions.get(history.getField());
            if (processer == null) {
                processer = DEFAULT_TASK;
            }
            processer.approve(userAccount, history);
        }
    }
    
    
    /**
     * For those History's who only need the history approbed and no othe action
     * done.
     */
    private class GenericApproveTask extends AbstractHistoryTask implements IApproveHistoryTask {


        @Inject
        GenericApproveTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        /**
         * @throws RuntimeException
         *             This exception should be caught, logged and a dialog
         *             should be shown to the user.
         */
        public void doTask(final UserAccount userAccount, final History history) {
            if (canDo(userAccount, history)) {
                history.setApprovalDate(new Date());
                history.setApproverName(userAccount.getUserName());
                ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
                dao.update(history.getConceptMetadata().getConcept());
            } else {
                final String msg = "Unable to approve the History [" + history + "]";
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, msg);
            }
            
        }
        
        public void approve(UserAccount userAccount, History history) {
            doTask(userAccount, history);
        }
    }
    
    private class DeleteConceptNameAction extends GenericApproveTask {

        DeleteConceptNameAction(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(final UserAccount userAccount, final History history) {
            final Concept concept = history.getConceptMetadata().getConcept();
            final ConceptName conceptName = concept.getConceptName(history.getOldValue());
            if (conceptName != null) {
                // Make sure that the name you are deleting isn't used by any observations anymore
                knowledgebaseDAO.updateConceptNameUsedByAnnotations(concept);

                ConceptNameDAO conceptNameDAO = knowledgebaseDAOFactory.newConceptNameDAO();
                concept.removeConceptName(conceptName);
                conceptNameDAO.makeTransient(conceptName);
            } else {
                dropHistory(history, "'" + history.getOldValue() + "' was not found. I'll remove the obsolete history");
            }
            super.approve(userAccount, history);
        }
    }
    
    private class DeleteChildConceptTask extends GenericApproveTask {

        DeleteChildConceptTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(final UserAccount userAccount, final History history) {
            
            /*
             * Find the child concept to be deleted.
             */
            final Concept parentConcept = history.getConceptMetadata().getConcept();
            final Collection<Concept> children = new ArrayList<Concept>(parentConcept.getChildConcepts());
            Concept concept = null;
            for(Concept child: children) {
                final ConceptName conceptName = child.getConceptName(history.getOldValue());
                if (conceptName != null) {
                    concept = child;
                    break;
                }
            }
            
            if (concept != null) {
                if (vars.knowledgebase.ui.actions.DeleteConceptTask.delete(concept)) {
                    super.approve(userAccount, history);
                } else {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to delete the concept, " +
                            concept.getPrimaryConceptName().getName() +
                            " from the knowledgebase");
                }
            } else {
                dropHistory(history, "No child Concept containg the name '" + history.getOldValue()
                        + "' was found. I'll remove the obsolete history information");
            }
        }
    }
    
    private class DeleteLinkRealizationTask extends GenericApproveTask {

        @Inject
        DeleteLinkRealizationTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }
        
        @Override
        public void approve(UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                // Parse the string stored in the history
                final ILink link = new LinkBean(history.getOldValue());
                final LinkRealization exampleRealization = knowledgebaseFactory.newLinkRealization();
                exampleRealization.setLinkName(link.getLinkName());
                exampleRealization.setToConcept(link.getToConcept());
                exampleRealization.setLinkValue(link.getLinkValue());

                final Concept concept = history.getConceptMetadata().getConcept();
                
                /*
                 * Find the matching linkTemplate
                 */
                LinkRealization linkRealization = null;
                Set<LinkRealization> linkRealizations = new HashSet<LinkRealization>(concept.getConceptMetadata().getLinkRealizations());
                for (LinkRealization t : linkRealizations) {
                    if(t.getLinkName().equals(exampleRealization.getLinkName()) &&
                            t.getToConcept().equals(exampleRealization.getToConcept()) &&
                            t.getLinkValue().equals(exampleRealization.getLinkValue())) {

                        linkRealization = t;
                        break;
                    }
                }

                if (linkRealization != null) {
                    if (vars.knowledgebase.ui.actions.DeleteLinkRealizationTask.delete(linkRealization)) {
                        super.approve(userAccount, history);
                    } else {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to delete the linkTemplate, " +
                                linkRealization.stringValue() + " from the knowledgebase");
                    }
                } else {
                    dropHistory(history, "Unable to locate '" + history.getNewValue() +
                            "'. It may have been moved. I'll remove the History reference.");
                }
                
            }
        }
    }
    
    private class DeleteLinkTemplateTask extends GenericApproveTask {

        DeleteLinkTemplateTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }
        
        @Override
        public void approve(UserAccount userAccount, History history) {
            
            if (canDo(userAccount, history)) {
                // Convienet means to parse the string stored in the history
                final LinkBean link = new LinkBean(history.getOldValue());
                final LinkTemplate exampleTemplate = knowledgebaseFactory.newLinkTemplate();
                exampleTemplate.setLinkName(link.getLinkName());
                exampleTemplate.setToConcept(link.getToConcept());
                exampleTemplate.setLinkValue(link.getLinkValue());

                final Concept concept = history.getConceptMetadata().getConcept();
                
                /*
                 * Find the matching linkTemplate
                 */
                Set linkTemplates = concept.getConceptMetadata().getLinkTemplates();
                LinkTemplate linkTemplate = null;
                for (Iterator i = linkTemplates.iterator(); i.hasNext();) {
                    LinkTemplate t = (LinkTemplate) i.next();
                    if(t.getLinkName().equals(exampleTemplate.getLinkName()) &&
                            t.getToConcept().equals(exampleTemplate.getToConcept()) &&
                            t.getLinkValue().equals(exampleTemplate.getLinkValue())) {
                        
                        linkTemplate = t;
                        break;
                    }
                }
                
                if (linkTemplate != null) {
                    if (vars.knowledgebase.ui.actions.DeleteLinkTemplateTask.delete(linkTemplate)) {
                        super.approve(userAccount, history);
                    } else {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to delete the linkTemplate, " +
                                linkTemplate.stringValue() + " from the knowledgebase");
                    }
                    
                } else {
                    dropHistory(history, "Unable to locate '" + history.getNewValue() +
                            "'. It may have \nbeen moved. I'll remove the History reference.");
                }
            }
        }
        
    }
    
    private class DeleteMediaTask extends GenericApproveTask {

        DeleteMediaTask(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
            super(knowledgebaseDAOFactory);
        }

        @Override
        public void approve(UserAccount userAccount, History history) {
            
            /*
             * Find the correct media object to delete
             */
            final Set<Media> mediaSet = history.getConceptMetadata().getMedias();
            Media media = null;
            for (Iterator i = mediaSet.iterator(); i.hasNext();) {
                final Media m = (Media) i.next();
                if (m.getUrl().equalsIgnoreCase(history.getOldValue())) {
                    media = m;
                    break;
                }
            }
            
            if (media != null) {
                if (vars.knowledgebase.ui.actions.DeleteMediaTask.delete(media)) {
                    super.approve(userAccount, history);
                } else {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to delete the media, " +
                            media.stringValue() + " from the knowledgebase");
                }
            } else {
                dropHistory(history, "No matching media was found; removing the History reference");
            }
            
        }
    }
    
    public void doTask(final UserAccount userAccount, final History history) {
        approve(userAccount, history);
    }
    
    
}
