/*
 * @(#)ConceptTreeModel.java   2009.11.04 at 02:30:15 PST
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
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
public class ConceptTreeModel implements TreeModel {

    private final Comparator<Concept> comparator = new ConceptPrimaryNameComparator();
    private List<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Load concepts from database and cache them here
     * Parent -> Child concept map */
    private final Map<Concept, List<Concept>> cachedConcepts = Collections.synchronizedMap(new HashMap<Concept, List<Concept>>());
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private Concept rootConcept;
    private final Concept TEMP_CONCEPT = new ConceptTreeConcept(1);
    private final BlockingQueue<Concept> queue = new ArrayBlockingQueue<Concept>(8);
    private final Thread daoThread;

    /**
     * Constructs ...
     *
     * @param knowledgebaseDAOFactory
     */
    public ConceptTreeModel(final KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        daoThread = new Thread(new Runnable() {

            public void run() {
                Concept concept;
                try {
                    while (true) {
                        concept = queue.take();
                        cacheConcept(concept);
                    }
                } catch (InterruptedException e) {
                    log.error("DAO queue died", e);
                }
            }
            
            /**
             * Caches a concept from the database locally. Also caches all parents upto
             * the root as well as it's immediate children.
             * @param concept The concept to cache
             * @param dao The DAO object (with it's transaction begun)
             */
            private void cacheConcept(Concept concept) {
                if ((concept != null) && (cachedConcepts.get(concept) == null)) {

                    // Load and cache
                    ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
                    dao.startTransaction();
                    concept = dao.merge(concept);
                    List<Concept> children = new Vector<Concept>(concept.getChildConcepts());
                    Collections.sort(children, comparator);
                    cachedConcepts.put(concept, children);
                    dao.endTransaction();

                    // Refresh node
                    final Concept c = concept;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            nodeChanged(c);
                        }
                    });

                }
            }


        }, getClass().getSimpleName() + " - ConceptTreeModel Lazy Loader");
        daoThread.setDaemon(true);
        daoThread.start();
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    /**
    * Notifies all listeners that have registered interest for notification
    * on this event type. The event instance is lazily created using the parameters
    * passed into the fire method.
    *
    * @param source the node being changed
    * @param path the path to the root node
    * @param childIndices the indices of the changed elements
    * @param children the changed elements
    */
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        for (TreeModelListener treeModelListener : getTreeModelListeners()) {
            treeModelListener.treeNodesChanged(event);
        }
    }

    /**
     * fireTreeNodesInserted
     *
     * @param source the node where new nodes got inserted
     * @param path the path to the root node
     * @param childIndices the indices of the new elements
     * @param children the new elements
     */
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        for (TreeModelListener treeModelListener : getTreeModelListeners()) {
            treeModelListener.treeNodesInserted(event);
        }
    }

    /**
     * fireTreeNodesRemoved
     *
     * @param source the node where nodes got removed-
     * @param path the path to the root node
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        for (TreeModelListener treeModelListener : getTreeModelListeners()) {
            treeModelListener.treeNodesRemoved(event);
        }
    }

    /**
     * fireTreeStructureChanged
     *
     * @param source the node where the model has changed
     * @param path the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     */
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        for (TreeModelListener treeModelListener : getTreeModelListeners()) {
            treeModelListener.treeStructureChanged(event);
        }
    }

    public Object getChild(Object parent, int index) {
        Concept parentConcept = (Concept) parent;
        if (cachedConcepts.containsKey(parentConcept)) {
            return cachedConcepts.get(parentConcept).get(index);
        }
        else {
            loadConcept(parentConcept);
            return TEMP_CONCEPT;
        }
    }

    public int getChildCount(Object parent) {
        Concept parentConcept = (Concept) parent;
        if (cachedConcepts.containsKey(parentConcept)) {
            return cachedConcepts.get(parentConcept).size();
        }
        else {
            loadConcept(parentConcept);
            return TEMP_CONCEPT.getChildConcepts().size();
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        Concept parentConcept = (Concept) parent;
        loadConcept((Concept) parent);
        return cachedConcepts.get(parentConcept).indexOf(child);
    }

    /**
     * Get the root of the concept tree
     * @return
     */
    public Object getRoot() {
        if (rootConcept == null) {
            ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
            dao.startTransaction();
            rootConcept = dao.findRoot();
            List<Concept> children = new Vector<Concept>(rootConcept.getChildConcepts());
            Collections.sort(children, comparator);
            cachedConcepts.put(rootConcept, children);
            dao.endTransaction();
        }

        return rootConcept;
    }

    /**
    * Returns all registered <code>TreeModelListener</code> listeners.
    *
    */
    public List<TreeModelListener> getTreeModelListeners() {
        return new ArrayList<TreeModelListener>(treeModelListeners);
    }

    /**
     * Builds the parents of node up to and including the root node, where
     * the original node is the last element in the returned array. The
     * length of the returned array gives the node's depth in the tree.
     *
     * @param node - the TreeNode to get the path for
     * @return TreeNode[] - the path from node to the root
     */
    public TreePath getTreePathToConcept(Concept node) {
        List<Concept> concepts = new ArrayList<Concept>();
        concepts.add(node);
        node = node.getParentConcept();

        while (node != null) {
            concepts.add(node);
            node = node.getParentConcept();
        }

        Collections.reverse(concepts);

        return new TreePath(concepts.toArray());

    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    /**
     * Caches a concept from the database locally. Also caches all parents upto
     * the root as well as it's immediate children.
     * @param concept The concept to cache
     * @param dao The DAO object (with it's transaction begun)
     */
    private void loadConcept(Concept concept) {
        if ((concept != null) && (cachedConcepts.get(concept) == null)) {
            queue.offer(concept);
        }
    }



    public void nodeChanged(Concept concept) {

        // ---- Build treepath and fire an event
        TreePath treePath = getTreePathToConcept(concept);
        fireTreeNodesChanged(this, treePath.getPath(),
                             new int[] { getIndexOfChild(concept.getParentConcept(), concept) },
                             new Object[] { concept });
    }


    /**
     * Clears the entire internal cache for the TreeModel and reloads the root
     * concept
     */
    public void refresh() {
        rootConcept = null;
        cachedConcepts.clear();
        cachedConcepts.put(TEMP_CONCEPT, new ArrayList<Concept>(TEMP_CONCEPT.getChildConcepts()));
        reloadConcept((Concept) getRoot());
    }

    /**
     * Reloads a concept from the database. Note, only this concept and it's children
     * are reloaded; none of the parent path is reloaded
     * @param concept
     */
    public void reloadConcept(Concept concept) {

        // ---- Clear cached concept so that loadConcepts method will reload it
        removeConceptFromCache(concept);

        // ---- Reload concept from databse
        loadConcept(concept);

        // ---- Fire a notification
        nodeChanged(concept);

    }

    /**
     * Removes a concept and it's children from the cache so that it can be reloaded
     * @param concept
     */
    private void removeConceptFromCache(Concept concept) {
        cachedConcepts.remove(concept);

        for (Concept childConcept : new ArrayList<Concept>(concept.getChildConcepts())) {
            removeConceptFromCache(childConcept);
        }
    }

    /**
     * Removes a listener previously added with addTreeModelListener().
     */
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        reloadConcept((Concept) path.getLastPathComponent());
    }
}
