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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.PersistenceCache;
import vars.jpa.HibernateCacheProvider;
import vars.jpa.EntityManagerFactoryAspect;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.shared.ui.kbtree.SearchableConceptTreePanel;

/**
 *
 * @author brian
 */
class KnowledgebaseFrameController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EventTopicSubscriber refreshTreeSubscriber = new RefreshTreeAndOpenNodeSubscriber();
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
        final EntityManagerFactoryAspect jpaAspect = (EntityManagerFactoryAspect) toolBelt.getKnowledgebaseDAOFactory();
        this.persistenceCache = new PersistenceCache(new HibernateCacheProvider(jpaAspect.getEntityManagerFactory()));

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
        try {
            persistenceCache.clear();
        }
        catch (Exception e) {
            log.error("Failed to clear cache", e);
            EventBus.publish(Lookup.TOPIC_FATAL_ERROR,
                             "Failed to clear" + " knowledgebase cache. Please close this " + "application");
        }

        final SearchableConceptTreePanel treePanel = knowledgebaseFrame.getTreePanel();
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(name);
        conceptDAO.endTransaction();
        treePanel.refreshTreeAndOpenNode(concept);

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
