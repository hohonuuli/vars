/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import com.google.inject.Inject;
import java.util.Collection;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptMetadata;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.History;
import vars.knowledgebase.HistoryDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkRealizationDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.Media;
import vars.knowledgebase.MediaDAO;

/**
 *
 * @author brian
 */
public class KnowledgebaseAppController {

    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    @Inject
    protected KnowledgebaseAppController(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        EventBus.subscribe(Lookup.TOPIC_DELETE_HISTORY, new DeleteHistorySubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT, new DeleteConceptSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT_NAME, new DeleteConceptNameSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_REALIZATION, new DeleteLinkRealizationSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_TEMPLATE, new DeleteLinkTemplateSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_MEDIA, new DeleteMediaSubscriber());
    }


    /**
     * This subscriber deletes lists of Histories from the database.
     */
    private class DeleteHistorySubscriber implements EventTopicSubscriber<Collection<History>> {

        public void onEvent(String topic, Collection<History> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                for (History history : data) {

                    HistoryDAO historyDAO = knowledgebaseDAOFactory.newHistoryDAO();
                    ConceptMetadata conceptMetadata = history.getConceptMetadata();
                    conceptMetadata.removeHistory(history);
                    historyDAO.makeTransient(history);

                }
            }
        }
    }

    /**
     * This subscriber deletes lists of LinkTemplates from the database.
     */
    private class DeleteLinkTemplateSubscriber implements EventTopicSubscriber<Collection<LinkTemplate>> {

        public void onEvent(String topic, Collection<LinkTemplate> data) {
            if (Lookup.TOPIC_DELETE_LINK_TEMPLATE.equals(topic)) {
                for (LinkTemplate linkTemplate : data) {

                    LinkTemplateDAO linkTemplateDAO = knowledgebaseDAOFactory.newLinkTemplateDAO();
                    ConceptMetadata conceptMetadata = linkTemplate.getConceptMetadata();
                    conceptMetadata.removeLinkTemplate(linkTemplate);
                    linkTemplateDAO.makeTransient(linkTemplate);

                }
            }
        }
    }

    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteLinkRealizationSubscriber implements EventTopicSubscriber<Collection<LinkRealization>> {

        public void onEvent(String topic, Collection<LinkRealization> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                for (LinkRealization linkRealization : data) {

                    LinkRealizationDAO linkRealizationDAO = knowledgebaseDAOFactory.newLinkRealizationDAO();
                    ConceptMetadata conceptMetadata = linkRealization.getConceptMetadata();
                    conceptMetadata.removeLinkTemplate(linkRealization);
                    linkRealizationDAO.makeTransient(linkRealization);

                }
            }
        }
    }

    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteMediaSubscriber implements EventTopicSubscriber<Collection<Media>> {

        public void onEvent(String topic, Collection<Media> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                for (Media media : data) {

                    MediaDAO mediaDAO = knowledgebaseDAOFactory.newMediaDAO();
                    ConceptMetadata conceptMetadata = media.getConceptMetadata();
                    conceptMetadata.removeMedia(media);
                    mediaDAO.makeTransient(media);

                }
            }
        }
    }

    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteConceptSubscriber implements EventTopicSubscriber<Collection<Concept>> {

        public void onEvent(String topic, Collection<Concept> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                for (Concept concept : data) {
                    ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                    concept.getParentConcept().removeChildConcept(concept);
                    conceptDAO.makeTransient(concept);

                }
            }
        }
    }

    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteConceptNameSubscriber implements EventTopicSubscriber<Collection<ConceptName>> {

        public void onEvent(String topic, Collection<ConceptName> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                for (ConceptName conceptName : data) {
                    ConceptNameDAO conceptNameDAO = knowledgebaseDAOFactory.newConceptNameDAO();
                    conceptName.getConcept().removeConceptName(conceptName);
                    conceptNameDAO.makeTransient(conceptName);
                }
            }
        }
    }

    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class UpdateConceptSubscriber implements EventTopicSubscriber<Collection<Concept>> {

        public void onEvent(String topic, Collection<Concept> data) {
            if (Lookup.TOPIC_DELETE_HISTORY.equals(topic)) {
                ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
                for (Concept concept : data) {
                    //concept.getParentConcept().removeChildConcept(concept);
                    conceptDAO.update(concept);
                }
            }
        }
    }


}
