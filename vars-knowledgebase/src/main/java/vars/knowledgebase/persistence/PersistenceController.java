/*
 * @(#)PersistenceController.java   2009.10.29 at 09:17:12 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.persistence;

import vars.knowledgebase.ui.*;
import com.google.inject.Inject;
import java.util.Collection;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.LinkRealization;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Media;
import vars.knowledgebase.ui.actions.ApproveHistoryTask;

/**
 *
 * @author brian
 */
public class PersistenceController {

    private final ApproveHistoryTask approveHistoryTask;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    @Inject
    protected PersistenceController(ToolBelt toolBelt) {
        knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        approveHistoryTask = toolBelt.getApproveHistoryTask();

        // Delete subscriptions
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT, new DeleteConceptSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT_NAME, new DeleteConceptNameSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_HISTORY, new DeleteHistorySubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_REALIZATION, new DeleteLinkRealizationSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_TEMPLATE, new DeleteLinkTemplateSubscriber());
        EventBus.subscribe(Lookup.TOPIC_DELETE_MEDIA, new DeleteMediaSubscriber());

        // Update subscriptions
        EventBus.subscribe(Lookup.TOPIC_UPDATE_CONCEPT, new UpdateConceptSubscriber());
        EventBus.subscribe(Lookup.TOPIC_UPDATE_CONCEPT_NAME, new UpdateConceptNameSubscriber());
        EventBus.subscribe(Lookup.TOPIC_UPDATE_HISTORY, new UpdateHistorySubscriber());
        EventBus.subscribe(Lookup.TOPIC_UPDATE_LINK_REALIZATION, new UpdateLinkRealizationSubscriber());
        EventBus.subscribe(Lookup.TOPIC_UPDATE_LINK_TEMPLATE, new UpdateLinkTemplateSubscriber());
        EventBus.subscribe(Lookup.TOPIC_UPDATE_MEDIA, new UpdateMediaSubscriber());

        // Insert subscriptions
        EventBus.subscribe(Lookup.TOPIC_INSERT_CONCEPT, new InsertConceptSubscriber());
        EventBus.subscribe(Lookup.TOPIC_INSERT_CONCEPT_NAME, new InsertConceptNameSubscriber());
        EventBus.subscribe(Lookup.TOPIC_INSERT_HISTORY, new InsertHistorySubscriber());
        EventBus.subscribe(Lookup.TOPIC_INSERT_LINK_REALIZATION, new InsertLinkRealizationSubscriber());
        EventBus.subscribe(Lookup.TOPIC_INSERT_LINK_TEMPLATE, new InsertLinkTemplateSubscriber());
        EventBus.subscribe(Lookup.TOPIC_INSERT_MEDIA, new InsertMediaSubscriber());

        // Approval subscription
        EventBus.subscribe(Lookup.TOPIC_APPROVE_HISTORY, new ApproveHistorySubscriber());

    }

    private abstract class ADeleteSubscriber<T> extends PersistenceSubscriber<T> {

        /**
         * Constructs ...
         *
         * @param deleteTopic
         * @param dao
         */
        public ADeleteSubscriber(String deleteTopic, DAO dao) {
            super(deleteTopic, dao);
        }

        @Override
        T before(T obj) {
            return dao.findInDatastore(obj);
        }

        @Override
        T doPersistenceThing(T obj) {
            return dao.makeTransient(obj);
        }
    }


    private abstract class AInsertSubscriber<T> extends PersistenceSubscriber<T> {

        /**
         * Constructs ...
         *
         * @param deleteTopic
         * @param dao
         */
        public AInsertSubscriber(String deleteTopic, DAO dao) {
            super(deleteTopic, dao);
        }

        @Override
        T doPersistenceThing(T obj) {
            return dao.makePersistent(obj);
        }

        T prepareForTransaction(T obj) {
            return obj;    // noop
        }
    }


    private abstract class AUpdateSubscriber<T> extends PersistenceSubscriber<T> {

        /**
         * Constructs ...
         *
         * @param deleteTopic
         * @param dao
         */
        public AUpdateSubscriber(String deleteTopic, DAO dao) {
            super(deleteTopic, dao);
        }

        @Override
        T doPersistenceThing(T obj) {
            return dao.update(obj);
        }

        T prepareForTransaction(T obj) {
            return obj;    // noop
        }
    }


    private class ApproveHistorySubscriber implements EventTopicSubscriber<Collection<History>> {

        public void onEvent(String topic, Collection<History> data) {
            if (Lookup.TOPIC_INSERT_CONCEPT_NAME.equals(topic)) {

                final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                if ((userAccount != null) && userAccount.isAdministrator()) {
                    for (History history : data) {
                        try {
                            approveHistoryTask.approve(userAccount, history);
                        }
                        catch (Exception e) {
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                            EventBus.publish(
                                Lookup.TOPIC_REFRESH_KNOWLEGEBASE,
                                history.getConceptMetadata().getConcept().getPrimaryConceptName().getName());
                        }
                    }
                }
            }
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteConceptNameSubscriber extends ADeleteSubscriber<ConceptName> {

        /**
         * Constructs ...
         */
        public DeleteConceptNameSubscriber() {
            super(Lookup.TOPIC_DELETE_CONCEPT_NAME, knowledgebaseDAOFactory.newConceptNameDAO());
        }

        @Override
        String getLookupName(ConceptName obj) {
            return obj.getConcept().getPrimaryConceptName().getName();
        }

        @Override
        ConceptName prepareForTransaction(ConceptName conceptName) {
            conceptName.getConcept().removeConceptName(conceptName);

            return conceptName;
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteConceptSubscriber extends ADeleteSubscriber<Concept> {

        /**
         * Constructs ...
         */
        public DeleteConceptSubscriber() {
            super(Lookup.TOPIC_DELETE_CONCEPT, knowledgebaseDAOFactory.newConceptDAO());
        }

        @Override
        String getLookupName(Concept obj) {
            return obj.getPrimaryConceptName().getName();
        }

        @Override
        Concept prepareForTransaction(Concept obj) {
            Concept concept = (Concept) obj;
            concept.getParentConcept().removeChildConcept(concept);

            return obj;
        }
    }


    /**
     * This subscriber deletes lists of Histories from the database.
     */
    private class DeleteHistorySubscriber extends ADeleteSubscriber<History> {

        /**
         * Constructs ...
         */
        public DeleteHistorySubscriber() {
            super(Lookup.TOPIC_DELETE_HISTORY, knowledgebaseDAOFactory.newHistoryDAO());
        }

        @Override
        String getLookupName(History obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }

        @Override
        History prepareForTransaction(History history) {
            history.getConceptMetadata().removeHistory(history);

            return history;
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteLinkRealizationSubscriber extends ADeleteSubscriber<LinkRealization> {

        /**
         * Constructs ...
         */
        public DeleteLinkRealizationSubscriber() {
            super(Lookup.TOPIC_DELETE_LINK_REALIZATION, knowledgebaseDAOFactory.newLinkRealizationDAO());
        }

        @Override
        String getLookupName(LinkRealization obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }

        @Override
        LinkRealization prepareForTransaction(LinkRealization linkRealization) {
            linkRealization.getConceptMetadata().removeLinkRealization(linkRealization);

            return linkRealization;
        }
    }


    /**
     * This subscriber deletes lists of LinkTemplates from the database.
     */
    private class DeleteLinkTemplateSubscriber extends ADeleteSubscriber<LinkTemplate> {

        /**
         * Constructs ...
         */
        public DeleteLinkTemplateSubscriber() {
            super(Lookup.TOPIC_DELETE_LINK_TEMPLATE, knowledgebaseDAOFactory.newLinkTemplateDAO());
        }

        @Override
        String getLookupName(LinkTemplate obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }

        @Override
        LinkTemplate prepareForTransaction(LinkTemplate linkTemplate) {
            linkTemplate.getConceptMetadata().removeLinkTemplate(linkTemplate);

            return linkTemplate;
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class DeleteMediaSubscriber extends ADeleteSubscriber<Media> {

        /**
         * Constructs ...
         */
        public DeleteMediaSubscriber() {
            super(Lookup.TOPIC_DELETE_MEDIA, knowledgebaseDAOFactory.newMediaDAO());
        }

        @Override
        String getLookupName(Media obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }

        @Override
        Media prepareForTransaction(Media media) {
            media.getConceptMetadata().removeMedia(media);

            return media;
        }
    }


    /**
     * This subscriber inserts lists of observations from the database.
     */
    private class InsertConceptNameSubscriber extends AInsertSubscriber<ConceptName> {

        /**
         * Constructs ...
         */
        public InsertConceptNameSubscriber() {
            super(Lookup.TOPIC_INSERT_CONCEPT_NAME, knowledgebaseDAOFactory.newConceptNameDAO());
        }

        @Override
        String getLookupName(ConceptName obj) {
            return obj.getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber inserts lists of observations from the database.
     */
    private class InsertConceptSubscriber extends AInsertSubscriber<Concept> {

        /**
         * Constructs ...
         */
        public InsertConceptSubscriber() {
            super(Lookup.TOPIC_INSERT_CONCEPT, knowledgebaseDAOFactory.newConceptDAO());
        }

        @Override
        String getLookupName(Concept obj) {
            return obj.getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber inserts lists of Histories from the database.
     */
    private class InsertHistorySubscriber extends AInsertSubscriber<History> {

        /**
         * Constructs ...
         */
        public InsertHistorySubscriber() {
            super(Lookup.TOPIC_INSERT_HISTORY, knowledgebaseDAOFactory.newHistoryDAO());
        }

        @Override
        History after(History obj) {

            // After putting a new history in the database we'll see if we can approve it.
            EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, obj);

            return obj;
        }

        @Override
        String getLookupName(History obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber inserts lists of observations from the database.
     */
    private class InsertLinkRealizationSubscriber extends AInsertSubscriber<LinkRealization> {

        /**
         * Constructs ...
         */
        public InsertLinkRealizationSubscriber() {
            super(Lookup.TOPIC_INSERT_LINK_REALIZATION, knowledgebaseDAOFactory.newLinkRealizationDAO());
        }

        @Override
        String getLookupName(LinkRealization obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber inserts lists of LinkTemplates from the database.
     */
    private class InsertLinkTemplateSubscriber extends AInsertSubscriber<LinkTemplate> {

        /**
         * Constructs ...
         */
        public InsertLinkTemplateSubscriber() {
            super(Lookup.TOPIC_INSERT_LINK_TEMPLATE, knowledgebaseDAOFactory.newLinkTemplateDAO());
        }

        @Override
        String getLookupName(LinkTemplate obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber inserts lists of observations from the database.
     */
    private class InsertMediaSubscriber extends AInsertSubscriber<Media> {

        /**
         * Constructs ...
         */
        public InsertMediaSubscriber() {
            super(Lookup.TOPIC_INSERT_MEDIA, knowledgebaseDAOFactory.newMediaDAO());
        }

        @Override
        String getLookupName(Media obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    private abstract class PersistenceSubscriber<T> implements EventTopicSubscriber<Collection<T>> {

        final DAO dao;
        final String myTopic;

        /**
         * Constructs ...
         *
         * @param myTopic
         * @param dao
         */
        public PersistenceSubscriber(String myTopic, DAO dao) {
            this.myTopic = myTopic;
            this.dao = dao;
        }

        T after(T obj) {
            return obj;

            /* Do nothing. Subclass can override */
        }

        T before(T obj) {
            return obj;

            /* Do nothing. Subclass can override */
        }

        abstract T doPersistenceThing(T obj);

        abstract String getLookupName(T obj);

        public void onEvent(String topic, Collection<T> data) {
            if (myTopic.equals(topic)) {
                String lookupName = null;
                for (T deleteObj : data) {
                    lookupName = getLookupName(deleteObj);

                    try {
                        deleteObj = before(deleteObj);
                        deleteObj = prepareForTransaction(deleteObj);
                        doPersistenceThing(deleteObj);
                        deleteObj = after(deleteObj);
                    }
                    catch (Exception e) {
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                        EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, lookupName);

                        break;
                    }
                }

                EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, lookupName);
            }
        }

        abstract T prepareForTransaction(T obj);

    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class UpdateConceptNameSubscriber extends AUpdateSubscriber<ConceptName> {

        /**
         * Constructs ...
         */
        public UpdateConceptNameSubscriber() {
            super(Lookup.TOPIC_UPDATE_CONCEPT, knowledgebaseDAOFactory.newConceptDAO());
        }

        @Override
        String getLookupName(ConceptName obj) {
            return obj.getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class UpdateConceptSubscriber extends AUpdateSubscriber<Concept> {

        /**
         * Constructs ...
         */
        public UpdateConceptSubscriber() {
            super(Lookup.TOPIC_UPDATE_CONCEPT, knowledgebaseDAOFactory.newConceptDAO());
        }

        @Override
        String getLookupName(Concept obj) {
            return obj.getPrimaryConceptName().getName();
        }
    }


    /**
 * This subscriber deletes lists of Histories from the database.
 */
    private class UpdateHistorySubscriber extends AUpdateSubscriber<History> {

        /**
         * Constructs ...
         */
        public UpdateHistorySubscriber() {
            super(Lookup.TOPIC_UPDATE_HISTORY, knowledgebaseDAOFactory.newHistoryDAO());
        }

        @Override
        String getLookupName(History obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class UpdateLinkRealizationSubscriber extends AUpdateSubscriber<LinkRealization> {

        /**
         * Constructs ...
         */
        public UpdateLinkRealizationSubscriber() {
            super(Lookup.TOPIC_UPDATE_LINK_REALIZATION, knowledgebaseDAOFactory.newLinkRealizationDAO());
        }

        @Override
        String getLookupName(LinkRealization obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber deletes lists of LinkTemplates from the database.
     */
    private class UpdateLinkTemplateSubscriber extends AUpdateSubscriber<LinkTemplate> {

        /**
         * Constructs ...
         */
        public UpdateLinkTemplateSubscriber() {
            super(Lookup.TOPIC_UPDATE_LINK_TEMPLATE, knowledgebaseDAOFactory.newLinkTemplateDAO());
        }

        @Override
        String getLookupName(LinkTemplate obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }


    /**
     * This subscriber deletes lists of observations from the database.
     */
    private class UpdateMediaSubscriber extends AUpdateSubscriber<Media> {

        /**
         * Constructs ...
         */
        public UpdateMediaSubscriber() {
            super(Lookup.TOPIC_UPDATE_MEDIA, knowledgebaseDAOFactory.newMediaDAO());
        }

        @Override
        String getLookupName(Media obj) {
            return obj.getConceptMetadata().getConcept().getPrimaryConceptName().getName();
        }
    }
}
