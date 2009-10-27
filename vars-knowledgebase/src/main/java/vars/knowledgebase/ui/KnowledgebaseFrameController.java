/*
 * @(#)KnowledgebaseFrameController.java   2009.10.27 at 08:42:22 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.swing.SearchableTreePanel;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;

/**
 *
 * @author brian
 */
class KnowledgebaseFrameController {

    private static final int MAX_SEARCH_LOOP_COUNT = 1000;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EventTopicSubscriber refreshTreeSubscriber = new RefreshTreeAndOpenNodeSubscriber();
    private final KnowledgebaseFrame knowledgebaseFrame;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param knowledgebaseFrame
     * @param toolBelt
     */
    public KnowledgebaseFrameController(KnowledgebaseFrame knowledgebaseFrame, ToolBelt toolBelt) {
        this.knowledgebaseFrame = knowledgebaseFrame;
        this.toolBelt = toolBelt;

        // When a Refresh event is published refresh the knowledgbase
        EventBus.subscribe(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, refreshTreeSubscriber);
    }

    /**
    * This call clears the Knowledgebase cache, refreshes the Concept tree
    * and opens the tree to the given node.
    * @param name Representing the node that we want to open to.
    */
    public void refreshTreeAndOpenNode(String name) {

        /**
         * Refresh node
         */
        Concept concept = null;
        try {
            toolBelt.getPersistenceCache().clear();
            ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
            concept = conceptDAO.findByName(name);
        }
        catch (Exception e) {
            log.error("Failed to clear cache", e);
            EventBus.publish(Lookup.TOPIC_FATAL_ERROR,
                             "Failed to clear" + " knowledgebase cache. Please close this " + "application");
        }


        final SearchableTreePanel tp = knowledgebaseFrame.getTreePanel();
        if (tp != null) {
            final Dispatcher dispatcher = Lookup.getSelectedConceptDispatcher();
            int count = 0;

            /*
             * We check in this loop that we are indeed at the node we wanted.
             */
            while (count < MAX_SEARCH_LOOP_COUNT) {
                tp.goToMatchingNode(name, false);

                final Concept selectedConcept = (Concept) dispatcher.getValueObject();

                if ((selectedConcept != null) &&
                        (selectedConcept.getPrimaryConceptName().getName().equals(
                            concept.getPrimaryConceptName().getName()))) {
                    break;
                }

                count++;
            }

            if (count >= MAX_SEARCH_LOOP_COUNT) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, "Failed to reopen '" + name + "'");
            }
        }
    }

    /**
     * Listens for refresh messages.
     */
    private class RefreshTreeAndOpenNodeSubscriber implements EventTopicSubscriber<String> {

        public void onEvent(String topic, String data) {
            refreshTreeAndOpenNode(data);
        }
    }
}
