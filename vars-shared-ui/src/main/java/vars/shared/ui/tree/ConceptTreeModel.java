/*
 * @(#)ConceptTreeModel.java   2009.11.05 at 10:35:32 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptPrimaryNameComparator;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 *
 * @author brian
 */
public class ConceptTreeModel extends DefaultTreeModel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BlockingQueue<ConceptTreeNode> queue = new ArrayBlockingQueue<ConceptTreeNode>(100);
    private final Thread conceptLoaderThread;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    /**
     * Constructs ...
     *
     * @param knowledgebaseDAOFactory
     */
    public ConceptTreeModel(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super(new ConceptTreeNode(new ConceptTreeConcept("Loading ...")));
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        conceptLoaderThread = new Thread(new ConceptLoaderRunnable(),
                                         "Concept Loader Thread for " + getClass().getName());
        conceptLoaderThread.setDaemon(true);
        conceptLoaderThread.start();
        refresh();
    }

 
    @Override
    public int getChildCount(Object parent) {
        ConceptTreeNode parentNode = (ConceptTreeNode) parent;
        if (!parentNode.isLoaded()) {
            // Add the concept to the DAO queue for loading children
            queue.add(parentNode);
        }

        return super.getChildCount(parent);
    }

    public void refresh() {
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept rootConcept = conceptDAO.findRoot();
        List<Concept> childConcepts = new ArrayList<Concept>(rootConcept.getChildConcepts());
        final ConceptTreeNode rootNode = new ConceptTreeNode(rootConcept);
        for (Concept child : childConcepts) {
            ConceptTreeNode childNode = new ConceptTreeNode(child);
            childNode.setLoaded(false);
            if (child.hasChildConcepts()) {
                ConceptTreeNode fakeNode = new ConceptTreeNode(new ConceptTreeConcept("Loading ..."));
                fakeNode.setLoaded(true);
                childNode.add(fakeNode);
            }
            rootNode.add(childNode);
        }
        conceptDAO.endTransaction();
        rootNode.setLoaded(true);
        super.setRoot(rootNode);
        super.reload();
    }

    @Override
    public void setRoot(TreeNode root) {
        if (!(root instanceof ConceptTreeNode)) {
            throw new IllegalArgumentException("The root must be an instance of ConceptTreeNode");
        }
        super.setRoot(root);
    }

  

    private class ConceptLoaderRunnable implements Runnable {

        /**
         * Caches a concept from the database locally. Also caches all parents upto
         * the root as well as it's immediate children.
         * @param concept The concept to cache
         * @param dao The DAO object (with it's transaction begun)
         */
        private void loadChildConcepts(final ConceptTreeNode node) {


            if (!node.isLoaded()) {

                node.removeAllChildren();
                Concept concept = (Concept) node.getUserObject();

                // ---- Load and cache
                ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
                dao.startTransaction();
                concept = dao.merge(concept);
                List<Concept> childConcepts = new ArrayList<Concept>(concept.getChildConcepts());
               
                for (Concept child : childConcepts) {
                    ConceptTreeNode childNode = new ConceptTreeNode(child);
                    childNode.setLoaded(false);
                    if (child.hasChildConcepts()) {
                        ConceptTreeNode fakeNode = new ConceptTreeNode(new ConceptTreeConcept("Loading ..."));
                        fakeNode.setLoaded(true);
                        childNode.add(fakeNode);
                    }
                    node.add(childNode);
                }
                dao.endTransaction();
                node.setLoaded(true);

                // Refresh node in UI
                final Concept c = concept;
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        reload(node);
                    }

                });

                
            }
        }

        public void run() {
            ConceptTreeNode node;
            try {
                while (true) {
                    node = queue.take();
                    loadChildConcepts(node);
                }
            }
            catch (InterruptedException e) {
                log.error("DAO queue died", e);
            }
        }
    }
}
