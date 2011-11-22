/*
 * @(#)ConceptTreeModel.java   2009.11.05 at 03:03:42 PST
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.PersistenceCache;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 * Class that represents a threaded database aware TreeModel. Data is loaded
 * from the database in background threads.
 *
 * @author brian
 */
public class ConceptTreeModel extends DefaultTreeModel {

    private final Logger log = LoggerFactory.getLogger(getClass());
//    private final BlockingQueue<ConceptTreeNode> queue = new ArrayBlockingQueue<ConceptTreeNode>(1000);
//    private Thread conceptLoaderThread;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    /**
     * Constructs ...
     *
     * @param knowledgebaseDAOFactory
     */
    public ConceptTreeModel(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super(new ConceptTreeNode(new ConceptTreeConcept("Loading ...")));
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        refresh();
    }

    /**
    * Gets the list of <code>Concept</code> objects from the root down to the
    * <code>Concept</code> for the specified concept name.
    *
    * @param  name           The name of the concept for the tree.
    * @return  The list of concepts from the root to the parameter concept.
    */
    private List<Concept> findFamilyTree(final String name) {

        final LinkedList<Concept> conceptList = new LinkedList<Concept>();
        ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
        dao.startTransaction();
        Concept concept = dao.findByName(name);
        conceptList.add(concept);

        while (concept.hasParent()) {
            concept = concept.getParentConcept();
            conceptList.addFirst(concept);
        }
        dao.endTransaction();
        dao.close();

        return conceptList;

    }

    @Override
    public int getChildCount(Object parent) {
        ConceptTreeNode parentNode = (ConceptTreeNode) parent;
        if (!parentNode.isLoaded()) {
            loadChildConcepts(parentNode);
        }

        return super.getChildCount(parent);
    }

    /**
     * Caches a concept from the database locally. Also caches all parents upto
     * the root as well as it's immediate children. This calls
     * the database so you should make calls to this method off of the EDT.
     *
     * @param node The concept to cache
     */
    private void loadChildConcepts(final ConceptTreeNode node) {


        if (!node.isLoaded()) {

            node.removeAllChildren();
            ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
            dao.startTransaction();
            Concept concept = (Concept) node.getUserObject();

            concept = dao.find(concept);


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

            node.setLoaded(true);
            dao.endTransaction();
            dao.close();

            // Refresh node in UI.
            // I commented this out since it was causing the expand item
            // in the ConceptTreePopupMenu to only expand the immediate children
            // of a node (even though they all were loaded from the database.
            // When I commented out invokeLater everything expanded fine.
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    reload(node);
                }

            });

        }
    }


    /**
     * Expands all tree nodes from the root down to the specified node name.
     * This method is used to open multiple nodes in a single database transaction
     * You MUST call dao.startTransaction() in the same thread as the call to loadNode
     * before using this method. (DAO's are NOT thread safe so you can't pass
     * one between threads)
     *
     * @param name
     * @return
     */
    ConceptTreeNode loadNode(final String name) {

        /*
         * Get a list of the family tree for the parameter concept. This list is
         * used to travel down the tree to the desired concept node.
         */
        List<Concept> list = findFamilyTree(name);
        Iterator<Concept> familyTree = list.iterator();

        // Pop the root Concept off the stack since it is the degenerative case.
        familyTree.next();

        // Then walk down the family tree, starting at the root node.
        ConceptTreeNode treeNode = (ConceptTreeNode) getRoot();

        while (familyTree.hasNext()) {
            String nextConceptName = (familyTree.next()).getPrimaryConceptName().getName();

            // Need to ensure the tree node for the current family name is expanded.
            loadChildConcepts(treeNode);

            // Find the child node for the next family member.
            boolean found = false;
            Enumeration<ConceptTreeNode> childrenNodes = treeNode.children();
            while (!found && childrenNodes.hasMoreElements()) {
                treeNode = childrenNodes.nextElement();
                Concept concept = (Concept) treeNode.getUserObject();

                if (nextConceptName.equals(concept.getPrimaryConceptName().getName())) {
                    found = true;
                }
            }
        }

        return treeNode;
    }

    /**
     * Removes all nodes from the tree and refreshes the information from the
     * database. Beaware you may need to clear the {@link PersistenceCache}
     * for changes in the database to be picked up.
     */
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
        conceptDAO.close();

        rootNode.setLoaded(true);
        super.setRoot(rootNode);
        super.reload();
    }


    /**
     * Sets the root node of the TreeModel
     * @param root Must be an instance of ConceptTreeNode or an {@link IllegalArgumentException}
     *      will be thrown
     */
    @Override
    public void setRoot(TreeNode root) {
        if (!(root instanceof ConceptTreeNode)) {
            throw new IllegalArgumentException("The root must be an instance of ConceptTreeNode");
        }

        super.setRoot(root);
    }

}
