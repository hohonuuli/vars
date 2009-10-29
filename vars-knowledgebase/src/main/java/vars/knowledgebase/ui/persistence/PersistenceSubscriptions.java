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



package vars.knowledgebase.ui.persistence;

import com.google.inject.Inject;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class PersistenceSubscriptions {

    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final EventTopicSubscriber dc;
    private final EventTopicSubscriber dcn;
    private final EventTopicSubscriber dh;
    private final EventTopicSubscriber dlr;
    private final EventTopicSubscriber dlt;
    private final EventTopicSubscriber dm;

    private final EventTopicSubscriber uc;
    private final EventTopicSubscriber ucn;
    private final EventTopicSubscriber uh;
    private final EventTopicSubscriber ulr;
    private final EventTopicSubscriber ult;
    private final EventTopicSubscriber um;

    private final EventTopicSubscriber ic;
    private final EventTopicSubscriber icn;
    private final EventTopicSubscriber ih;
    private final EventTopicSubscriber ilr;
    private final EventTopicSubscriber ilt;
    private final EventTopicSubscriber im;

    @Inject
    public PersistenceSubscriptions(ToolBelt toolBelt) {

        knowledgebaseDAOFactory = toolBelt.getKnowledgebaseDAOFactory();
        dc = new DeleteConceptSubscriber(knowledgebaseDAOFactory.newConceptDAO());
        dcn = new DeleteConceptNameSubscriber(knowledgebaseDAOFactory.newConceptNameDAO());
        dh = new DeleteHistorySubscriber(knowledgebaseDAOFactory.newHistoryDAO());
        dlr = new DeleteLinkRealizationSubscriber(knowledgebaseDAOFactory.newLinkRealizationDAO());
        dlt = new DeleteLinkTemplateSubscriber(knowledgebaseDAOFactory.newLinkTemplateDAO());
        dm = new DeleteMediaSubscriber(knowledgebaseDAOFactory.newMediaDAO());

        uc = new UpdateConceptSubscriber(knowledgebaseDAOFactory.newConceptDAO());
        ucn = new UpdateConceptNameSubscriber(knowledgebaseDAOFactory.newConceptNameDAO());
        uh = new UpdateHistorySubscriber(knowledgebaseDAOFactory.newHistoryDAO());
        ulr = new UpdateLinkRealizationSubscriber(knowledgebaseDAOFactory.newLinkRealizationDAO());
        ult = new UpdateLinkTemplateSubscriber(knowledgebaseDAOFactory.newLinkTemplateDAO());
        um =  new UpdateMediaSubscriber(knowledgebaseDAOFactory.newMediaDAO());

        ic = new InsertConceptSubscriber(knowledgebaseDAOFactory.newConceptDAO());
        icn = new InsertConceptNameSubscriber(knowledgebaseDAOFactory.newConceptNameDAO());
        ih = new InsertHistorySubscriber(knowledgebaseDAOFactory.newHistoryDAO());
        ilr = new InsertLinkRealizationSubscriber(knowledgebaseDAOFactory.newLinkRealizationDAO());
        ilt = new InsertLinkTemplateSubscriber(knowledgebaseDAOFactory.newLinkTemplateDAO());
        im = new InsertMediaSubscriber(knowledgebaseDAOFactory.newMediaDAO());


        // Delete subscriptions
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT, dc);
        EventBus.subscribe(Lookup.TOPIC_DELETE_CONCEPT_NAME, dcn);
        EventBus.subscribe(Lookup.TOPIC_DELETE_HISTORY, dh);
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_REALIZATION, dlr);
        EventBus.subscribe(Lookup.TOPIC_DELETE_LINK_TEMPLATE, dlt);
        EventBus.subscribe(Lookup.TOPIC_DELETE_MEDIA, dm);

        // Update subscriptions
        EventBus.subscribe(Lookup.TOPIC_UPDATE_CONCEPT, uc);
        EventBus.subscribe(Lookup.TOPIC_UPDATE_CONCEPT_NAME, ucn);
        EventBus.subscribe(Lookup.TOPIC_UPDATE_HISTORY, uh);
        EventBus.subscribe(Lookup.TOPIC_UPDATE_LINK_REALIZATION, ulr);
        EventBus.subscribe(Lookup.TOPIC_UPDATE_LINK_TEMPLATE, ult);
        EventBus.subscribe(Lookup.TOPIC_UPDATE_MEDIA, um);

        // Insert subscriptions
        EventBus.subscribe(Lookup.TOPIC_INSERT_CONCEPT, ic);
        EventBus.subscribe(Lookup.TOPIC_INSERT_CONCEPT_NAME, icn);
        EventBus.subscribe(Lookup.TOPIC_INSERT_HISTORY, ih);
        EventBus.subscribe(Lookup.TOPIC_INSERT_LINK_REALIZATION, ilr);
        EventBus.subscribe(Lookup.TOPIC_INSERT_LINK_TEMPLATE, ilt);
        EventBus.subscribe(Lookup.TOPIC_INSERT_MEDIA, im);

        // Approval subscription
        EventBus.subscribe(Lookup.TOPIC_APPROVE_HISTORY, new ApproveHistorySubscriber(toolBelt.getApproveHistoryTask()));

    }

}
