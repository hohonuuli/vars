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
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.PersistenceCache;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.shared.ui.tree.ConceptTreePanel;

/**
 *
 * @author brian
 */
class KnowledgebaseFrameController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EventTopicSubscriber<String> refreshTreeSubscriber = new RefreshTreeAndOpenNodeSubscriber();
    private final KnowledgebaseFrame knowledgebaseFrame;
    private final ToolBelt toolBelt;
    private final PersistenceCache persistenceCache;

    /**
     * Constructs ...
     *
     * @param knowledgebaseFrame
     * @param toolBelt
     */
    public KnowledgebaseFrameController(KnowledgebaseFrame knowledgebaseFrame, ToolBelt toolBelt) {
        this.knowledgebaseFrame = knowledgebaseFrame;
        this.toolBelt = toolBelt;
        this.persistenceCache = toolBelt.getPersistenceCache();

        // When a Refresh event is published refresh the knowledgebase
        EventBus.subscribe(StateLookup.TOPIC_REFRESH_KNOWLEGEBASE, refreshTreeSubscriber);
    }

    /**
    * This call clears the Knowledgebase cache, refreshes the Concept tree
    * and opens the tree to the given node.
    * @param name Representing the node that we want to open to. If it's <b>null</b> then
    *  the root node will be opened
    */
    public void refreshTreeAndOpenNode(String name) {

        WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(knowledgebaseFrame.getTreePanel(), "Refreshing");

        /**
         * Refresh node
         */
        try {
            persistenceCache.clear();
        }
        catch (Exception e) {
            log.error("Failed to clear cache", e);
            EventBus.publish(StateLookup.TOPIC_FATAL_ERROR,
                             "Failed to clear" + " knowledgebase cache. Please close this " + "application");
        }

        final ConceptTreePanel treePanel = knowledgebaseFrame.getTreePanel();
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = null;
        if (name == null) {
            concept = conceptDAO.findRoot();
        }
        else {
            concept = conceptDAO.findByName(name);
        }
        
        if (concept == null) {
            concept = conceptDAO.findRoot();
        }
        conceptDAO.endTransaction();
        conceptDAO.close();
        treePanel.refreshAndOpenNode(concept);
        waitIndicator.dispose();
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
