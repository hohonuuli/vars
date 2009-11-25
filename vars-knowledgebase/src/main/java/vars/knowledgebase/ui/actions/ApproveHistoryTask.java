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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.ConceptNameTypes;
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
        this.toolBelt = toolBelt;


        DEFAULT_TASK = new GenericApproveTask();

        /*
         * This Map holds actions that process approval of add action. addMap<String,
         * IAction> String defines which field was added. See History.FIELD_*
         * for acceptabe values. The Map holds that process the approval
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

    public void doTask(final UserAccount userAccount, final History history) {
    	DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
    	try {
    		dao.startTransaction();
        	approve(userAccount, history, dao);
    	}
    	catch (Exception e) {
    		EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
    	}
    	finally {
    		dao.endTransaction();
    	}
    }

    private class ADeleteChildConceptTask extends GenericApproveTask {


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


        @Override
        public void approve(final UserAccount userAccount, History history, DAO dao) {
        	
        	final String conceptNameToDelete = history.getOldValue();
        	
        	if (canDo(userAccount, history)) {

                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to delete '" + conceptNameToDelete +
                            "' ? Be aware that this will change existing annotations that use it.",
                        "VARS - Delete ConceptName", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
                
                
        	}
        	
        	/*
             * Bring objects into persistence transaction 
             */
            history = dao.findInDatastore(history);
            final Concept concept = history.getConceptMetadata().getConcept();
            
            /*
             * Make sure that no annotations are using the concept-name that is
             * being deleted.
             */
            Thread thread = new Thread(new Runnable() {
				
				public void run() {
					try {
						toolBelt.getKnowledgebaseDAO().updateConceptNameUsedByAnnotations(concept);
					}
					catch (Exception e) {
						EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
					}
					
				}
			}, "Update thread for annotations named " + conceptNameToDelete);
            thread.setDaemon(false);
            thread.start();
            
            /*
             * Drop the conceptname from the database
             */
            ConceptNameDAO conceptNameDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptNameDAO(dao.getEntityManager());
            ConceptName conceptName = conceptNameDAO.findByName(conceptNameToDelete);
            if (conceptName != null) {
	            if (conceptName.getNameType().equals(ConceptNameTypes.PRIMARY.getName())) {
	            	EventBus.publish(Lookup.TOPIC_WARNING, "You are attempting to delete a primary concept-name." +
	            			" This is NOT allowed.");
	            	return;
	            }
	            
	            conceptName.getConcept().removeConceptName(conceptName);
	            dao.remove(conceptName);
	            super.approve(userAccount, history, conceptNameDAO);
	            
            }
            else {
            	dropHistory(history, "Unable to locate '" + conceptNameToDelete +
                        "'. I'll remove the History reference.", dao);
            }
        }
    }


    private class ADeleteLinkRealizationTask extends GenericApproveTask {
 
        @Override
        public void approve(UserAccount userAccount, History history, DAO dao) {
        	
        	if (canDo(userAccount, history)) {

                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to delete '" + history.getOldValue() +
                            "' ? Be aware that this will not effect existing annotations that use it.",
                        "VARS - Delete LinkRealization", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // Convenient means to parse the string stored in the history
                final LinkBean link = new LinkBean(history.getOldValue());
                final LinkRealization exampleRealization = toolBelt.getKnowledgebaseFactory().newLinkRealization();
                exampleRealization.setLinkName(link.getLinkName());
                exampleRealization.setToConcept(link.getToConcept());
                exampleRealization.setLinkValue(link.getLinkValue());

                /*
                 * Bring objects into persistence transaction 
                 */
                history = dao.findInDatastore(history);
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();
                
                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkRealizations = new ArrayList<ILink>(conceptMetadata.getLinkRealizations());
                Collection<ILink> matchingLinkRealizations = LinkUtilities.findMatchesIn(exampleRealization, linkRealizations);
                LinkRealization linkRealization = null;
                if (matchingLinkRealizations.size() > 0) {
                    linkRealization = (LinkRealization) matchingLinkRealizations.iterator().next();
                    conceptMetadata.removeLinkRealization(linkRealization);
                    dao.remove(linkRealization);
                    super.approve(userAccount, history, dao);
                }
                else {
                    dropHistory(history, "Unable to locate '" + history.getOldValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }
            }
        }
        	
    }


    private class ADeleteLinkTemplateTask extends GenericApproveTask {


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

                /*
                 * Bring objects into persistence transaction 
                 */
                history = dao.findInDatastore(history);
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();
                
                /*
                 * Find the matching linkTemplate
                 */
                Collection<ILink> linkTemplates = new ArrayList<ILink>(conceptMetadata.getLinkTemplates());
                Collection<ILink> matchingLinkTemplates = LinkUtilities.findMatchesIn(exampleTemplate, linkTemplates);
                LinkTemplate linkTemplate = null;
                if (matchingLinkTemplates.size() > 0) {
                    linkTemplate = (LinkTemplate) matchingLinkTemplates.iterator().next();
                    conceptMetadata.removeLinkTemplate(linkTemplate);
                    dao.remove(linkTemplate);
                    super.approve(userAccount, history, dao);
                }
                else {
                    dropHistory(history, "Unable to locate '" + history.getOldValue() +
                                "'. It may have \nbeen moved. I'll remove the History reference.", dao);
                }
            }
        }
    }


    private class ADeleteMediaTask extends GenericApproveTask {


        public void approve(UserAccount userAccount, History history, DAO dao) {
        	
        	if (canDo(userAccount, history)) {
        		
        		final String imageReference = history.getOldValue();

                final Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                final int option = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to delete '" + imageReference + "' ? ",
                        "VARS - Delete Media", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                /*
                 * Bring objects into persistence transaction 
                 */
                history = dao.findInDatastore(history);
                final ConceptMetadata conceptMetadata = history.getConceptMetadata();
                
                /*
                 * Find the matching Media
                 */
                Collection<Media> medias = new ArrayList<Media>(conceptMetadata.getMedias());
                Collection<Media> matchingMedia = Collections2.filter(medias, new Predicate<Media>(){
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
                    dropHistory(history, "Unable to locate '" + imageReference +
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
        	history = dao.findInDatastore(history);
            doTask(userAccount, history);
        }

        /**
         */
        public void doTask(final UserAccount userAccount, History history) {
            if (canDo(userAccount, history)) {
                history.setProcessedDate(new Date());
                history.setProcessorName(userAccount.getUserName());
                history.setApproved(Boolean.TRUE);
            }

        }
    }
}
