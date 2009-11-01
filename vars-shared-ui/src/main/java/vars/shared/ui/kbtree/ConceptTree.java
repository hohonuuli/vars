/*
 * @(#)ConceptTree.java   2009.10.26 at 02:15:05 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.kbtree;

import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.shared.ui.ConceptChangeListener;
import vars.shared.ui.GlobalLookup;

/**
 * A JTree implementation for displaying <code>KnowledgeBase</code>
 * <code>Concept</code> objects in the <code>MaintGui</code>.
 *
 *
 * @author  brian
 * @created  November 11, 2004
 * @version  $Id: ConceptTree.java 277 2006-06-23 23:47:55Z hohonuuli $
 */
public class ConceptTree extends JTree implements ConceptChangeListener {

    public final static String COLLAPSE_ALL = "Collapse All";
    public final static String COLLAPSE_CHILDREN = "Hide Children";
    public final static String EXPAND_ALL = "Show All";
    public final static String EXPAND_DESCENDENTS = "Show Children";
    public final static String REFRESH = "Refresh";
    public final static Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    public final static Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected ConceptDAO conceptDAO;

    /**
         * @uml.property  name="popupMenu"
         * @uml.associationEnd  inverse="this$0:org.mbari.vars.ui.ConceptTree$ConceptPopupMenu"
         */
    protected ConceptTreePopupMenu popupMenu;

    /**
     * Constructs ...
     *
     * @param conceptDAO
     */
    public ConceptTree(ConceptDAO conceptDAO) {
        this(conceptDAO.findRoot(), conceptDAO);
    }

    /**
     * Required for subclasses used by annotation ui
     *
     * @param  rootConcept
     * @param conceptDAO
     */
    public ConceptTree(Concept rootConcept, ConceptDAO conceptDAO) {
        this.conceptDAO = conceptDAO;
        loadModel(rootConcept);
        initialize();
    }


    protected ConceptTree() {
        // Hack to get EditableConceptTree working correctly. Don't delete this.
    }

    /**
     * Refresh the tree from the database
     */
    public void refresh() {
        // Reload from the root
        loadModel(conceptDAO.findRoot());
    }

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void addedConcept(Concept concept) {
        // no-op
        // Tree node added through tree model
    }

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void addedConceptName(ConceptName conceptName) {
        updateTreeNode(conceptDAO.findByName(conceptName.getName()));
    }

    /**
     * Collapses all tree nodes.
     */
    public void collapseAll() {
        setCursor(WAIT_CURSOR);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
        makeChildrenInvisible(rootNode);
        setCursor(DEFAULT_CURSOR);
    }

    /**
     * Collapses all children nodes under the currently selected node.
     */
    public void collapseChildren() {
        setCursor(WAIT_CURSOR);
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
        makeChildrenInvisible(selectedNode);
        setCursor(DEFAULT_CURSOR);
    }

    /**
     * Expands all tree nodes.
     */
    public void expandAll() {
        setCursor(WAIT_CURSOR);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getModel().getRoot();
        makeChildrenVisible(rootNode);
        setCursor(DEFAULT_CURSOR);
    }

    /**
     * Expands all descendent nodes under the currently selected node.
     */
    public void expandDescendents() {
        setCursor(WAIT_CURSOR);
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
        makeChildrenVisible(selectedNode);
        setCursor(DEFAULT_CURSOR);
    }

    /**
     * Expands all tree nodes from the root down to the specified node name. Does
     * not expand the final node as that node may not have any children.
     *
     * @param  name   The name of the final node.
     * @return    The final node, which itself has not been expanded.
     */
    DefaultMutableTreeNode expandDownToNode(String name) {

        /*
         * Get a list of the family tree for the parameter concept. This list is
         * used to travel down the tree to the desired concept node.
         */
        List list = null;

        try {
            list = findConceptFamilyTree(name);
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Call to knowledgebase cache failed", e);
            }

            EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR,
                             "There was a problem talking" + " to the database. Unable to open '" + name +
                             "' in the tree.");
        }

        Iterator familyTree = list.iterator();

        // Pop the root Concept off the stack since it is the degenerative case.
        familyTree.next();

        // Then walk down the family tree, starting at the root node.
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) getModel().getRoot();

        while (familyTree.hasNext()) {
            String nextConceptName = ((Concept) familyTree.next()).getPrimaryConceptName().getName();

            // Need to ensure the tree node for the current family name is expanded.
            TreeConcept treeConcept = (TreeConcept) treeNode.getUserObject();
            treeConcept.lazyExpand(treeNode);

            // Find the child node for the next family member.
            boolean found = false;
            Enumeration childrenNodes = treeNode.children();

            while (!found && childrenNodes.hasMoreElements()) {
                treeNode = (DefaultMutableTreeNode) childrenNodes.nextElement();
                treeConcept = (TreeConcept) treeNode.getUserObject();

                if (nextConceptName.equals(treeConcept.getName())) {
                    found = true;
                }
            }
        }

        /*
         * RxNOTE The final value of treeNode drops out of the above while loop
         * without a call to lazyExpand. This is purposeful as the final node may
         * or may not have children. We are only expanding down to the node, not
         * expanding the node itself.
         */
        return treeNode;
    }

    /**
     * Gets the list of <code>Concept</code> objects from the root down to the
     * <code>Concept</code> for the specified concept name.
     *
     * @param  name           The name of the concept for the tree.
     * @return  The list of concepts from the root to the parameter concept.
     */
    List findConceptFamilyTree(final String name) {
        final LinkedList conceptList = new LinkedList();
        Concept concept = conceptDAO.findByName(name);
        conceptList.add(concept);

        while (concept.hasParent()) {
            concept = (Concept) concept.getParentConcept();
            conceptList.addFirst(concept);
        }

        return conceptList;
    }

    /**
         * Method description
         * @return
         */
    public ConceptTreePopupMenu getPopupMenu() {
        if (popupMenu == null) {
            log.debug(getClass().getName() + "getPopupMenu called");
            // Add Concept popup menu to this tree
            popupMenu = new ConceptTreePopupMenu(this);
        }

        return popupMenu;
    }

    /**
     * This method returns the currently selected concept in the concept tree
     *
     * @return  the <code>Concept</code> that is currently selected in the tree
     */
    public Concept getSelectedConcept() {
        Concept concept = null;
        DefaultMutableTreeNode node = getSelectedNode();
        if (node != null) {
            TreeConcept treeConcept = (TreeConcept) node.getUserObject();
            concept = treeConcept.getConcept();
        }

        return concept;
    }

    /**
     * Gets the name of the <code>Concept</code> represented by the currently
     * selected node.
     *
     * @return    The name of the <code>Concept</code> represented by the currently
     *  selected node.
     */
    public String getSelectedName() {
        String name = null;
        DefaultMutableTreeNode node = getSelectedNode();

        if (node != null) {
            name = ((TreeConcept) node.getUserObject()).getName();
        }

        return name;
    }

    /**
     * Gets the currently selected tree node.
     *
     * @return    The currently selected tree node.
     */
    protected DefaultMutableTreeNode getSelectedNode() {
        DefaultMutableTreeNode node = null;
        TreePath path = getSelectionPath();

        if (path != null) {
            node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }

        return node;
    }

    protected void initialize() {

        /*
         * Initialize the properties of this conceptTree
         */
        putClientProperty("JTree.lineStyle", "Angled");
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // don't allow edits to the Concept names directly in this tree
        setEditable(false);
        setCellRenderer(new ConceptTreeCellRenderer());
        setRootVisible(true);

        setupListeners();
        addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent event) {
                    evalutePopup(event);
                }
                @Override
                public void mouseReleased(MouseEvent event) {
                    evalutePopup(event);
                }
                private void evalutePopup(MouseEvent e) {

                    // Display popup menu next to selected item
                    if (e.isPopupTrigger() && (getSelectedNode() != null)) {
                        getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                    }
                }

            });
        //add(getPopupMenu());

    }

    /**
     * Loads the JTree model using the specified <code>Concept</code> as the root
     * node.
     *
     * @param  rootConcept Description of the Parameter
     */
    public void loadModel(Concept rootConcept) {
        if (rootConcept == null) {
            setModel(null);
        }
        else {
            TreeConcept treeConcept = new TreeConcept(rootConcept);
            DefaultMutableTreeNode rootNode = new SortedTreeNode(treeConcept);

            /*
             * Adding a boolean value as a child to this concept indicates that
             * the node has children that will be loaded dynamically, start off by
             * assuming this node has children, the rest will be taken care of in
             * the ConceptTreeLazyLoader
             */
            rootNode.add(new DefaultMutableTreeNode(Boolean.TRUE));
            treeConcept.lazyExpand(rootNode);

            DefaultTreeModel model = new DefaultTreeModel(rootNode);

            setModel(model);
            addTreeExpansionListener(new ConceptTreeLazyLoader(model));
        }
    }

    /**
     * Makes the children nodes under the specified node invisible.
     *
     * @param  node   The node on which to act.
     */
    private void makeChildrenInvisible(DefaultMutableTreeNode node) {
        Enumeration children = node.children();

        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();

            if (!childNode.isLeaf()) {
                makeChildrenInvisible(childNode);

                TreeNode[] nodesFromRoot = childNode.getPath();
                TreePath pathFromRoot = new TreePath(nodesFromRoot);

                collapsePath(pathFromRoot);
            }
        }
    }

    /**
     * Makes the children nodes under the specified node visible.
     *
     * @param  node   The node on which to act.
     */
    private void makeChildrenVisible(DefaultMutableTreeNode node) {

        // RxTBD wcpr The Java API interaction of using TreeNodes and TreePaths
        // doesn't seem to make sense. There should be a cleaner way to implement
        // this method.
        if (node.isLeaf()) {
            return;
        }

        // Expand the node
        TreeConcept treeConcept = (TreeConcept) node.getUserObject();

        treeConcept.lazyExpand(node);

        boolean allChildrenAreLeaves = true;
        Enumeration children = node.children();

        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();

            if (!childNode.isLeaf()) {
                makeChildrenVisible(childNode);
                allChildrenAreLeaves = false;
            }
        }

        if (allChildrenAreLeaves) {
            DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) node.getLastChild();
            TreeNode[] nodesFromRoot = node.getPath();
            TreePath pathFromRoot = new TreePath(nodesFromRoot).pathByAddingChild(lastNode);

            makeVisible(pathFromRoot);
        }
    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void removedConcept(Concept concept) {

        // RxNOTE It is assumed the removed Concept is the currently selected node.
        DefaultMutableTreeNode node = getSelectedNode();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        int removedNodeIndex = parentNode.getIndex(node);

        node.removeFromParent();

        DefaultTreeModel model = (DefaultTreeModel) getModel();

        model.nodeStructureChanged(parentNode);


        int numChildren = parentNode.getChildCount();
        DefaultMutableTreeNode nodeToSelect;
        if (0 < numChildren) {
            int displayNodeIndex = removedNodeIndex;

            if (removedNodeIndex == numChildren) {
                displayNodeIndex = removedNodeIndex - 1;
            }

            nodeToSelect = (DefaultMutableTreeNode) parentNode.getChildAt(displayNodeIndex);
        }
        else {
            nodeToSelect = parentNode;
        }

        setSelectionPath(new TreePath(nodeToSelect.getPath()));
    }

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void removedConceptName(ConceptName conceptName) {
        updateTreeNode(getSelectedConcept());
    }

    /**
     * Sets the selected tree node to the node representing the specified
     * <code>Concept</code> name.
     *
     * @param  name The new selectedConcept value
     */
    public void setSelectedConcept(String name) {
        if (name == null) {
            return;
        }

        // RxNOTE Strategy: The tree node for the Concept being selected may not
        // yet be expanded, so expand down to the desired node.
        DefaultMutableTreeNode treeNode = expandDownToNode(name);

        // Now select the node and scroll to it.
        TreePath path = new TreePath(treeNode.getPath());

        setSelectionPath(path);
        scrollPathToVisible(path);

    }

    /**
     * Sets up the various listeners needed for GUI interaction with this
     * <code>ConceptTree</code>.
     */
    protected void setupListeners() {

        // Add context popup menu and right mouse button selection
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {

                // Selected item before showing popup menu
                if (event.getModifiers() == MouseEvent.BUTTON3_MASK) {
                    int row = getRowForLocation(event.getX(), event.getY());

                    setSelectionRow(row);
                }
            }

        });

        // Toggle expand/collapse on ENTER
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = getSelectionRows()[0];

                    if (isCollapsed(row)) {
                        expandRow(row);
                    }
                    else {
                        collapseRow(row);
                    }
                }
            }

        });
    }

    /**
     *  Description of the Method
     */
    protected void updateTreeNode(Concept concept) {
        DefaultMutableTreeNode selectedNode = getSelectedNode();

        selectedNode.setUserObject(new TreeConcept(concept));

        /*
         * Announcing the node structure change triggers the necessary repaint to
         * display the effects of changes.
         */
        DefaultTreeModel model = (DefaultTreeModel) getModel();

        model.nodeStructureChanged(selectedNode);
    }

    /**
     * Sets the parent node of the currently selected node to be the node
     * representing the <code>Concept</code> of the specified name.
     *
     * @param  newParentName   The name of the <code>Concept</code> for which the currently selected
     *  node is to become a child.
     */
    public void updateTreeNodeParent(String newParentName) {

        // Get the node being moved
        DefaultMutableTreeNode conceptNode = (DefaultMutableTreeNode) getSelectionPath().getLastPathComponent();
        String conceptNodeName = ((TreeConcept) conceptNode.getUserObject()).getName();
        DefaultTreeModel model = (DefaultTreeModel) getModel();

        // Remove node from current parent node and update structure
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) conceptNode.getParent();

        parentNode.remove(conceptNode);
        model.nodeStructureChanged(parentNode);

        // Get the new parent node
        DefaultMutableTreeNode newParentNode = expandDownToNode(newParentName);
        TreeConcept treeConcept = (TreeConcept) newParentNode.getUserObject();
        boolean parentNeededExpanding = treeConcept.lazyExpand(newParentNode);

        // Branch on whether parent needed expanding:
        // - The parent node needed to be expanded. The call to lazyExpand()
        // updates the parent node's children so we don't need to explicitly add
        // the new child node. Find and select the new child node.
        // - The parent node is already expanded, so insert the new child node in
        // the appropriate slot and select the new child node.
        if (parentNeededExpanding) {
            Enumeration children = newParentNode.children();

            while (children.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                String nodeName = ((TreeConcept) node.getUserObject()).getName();

                if (nodeName.equals(conceptNodeName)) {
                    setSelectionPath(new TreePath(node.getPath()));

                    break;
                }
            }
        }
        else {

            // Insert the node at the appropriate point in the new parent node.
            int insertPosition = 0;
            Enumeration children = newParentNode.children();

            while (children.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                String nodeName = ((TreeConcept) node.getUserObject()).getName();

                if (0 < nodeName.compareTo(conceptNodeName)) {
                    break;
                }
                else {
                    insertPosition++;
                }
            }

            model.insertNodeInto(conceptNode, newParentNode, insertPosition);
            setSelectionPath(new TreePath(conceptNode.getPath()));
        }
    }
}
