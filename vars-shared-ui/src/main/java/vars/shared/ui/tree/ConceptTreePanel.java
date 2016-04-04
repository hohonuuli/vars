/*
 * @(#)ConceptTreePanel.java   2009.11.05 at 02:05:54 PST
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

import com.google.inject.Inject;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.SearchableTreePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.PersistenceCache;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.shared.ui.GlobalStateLookup;

/**
 *
 * @author brian
 */
public class ConceptTreePanel extends SearchableTreePanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MouseListener popupListener = new PopupListener();
    private final KeyListener enterListener = new EnterKeyListener();

    /**
         * Store previous searches so that we don't try to do database lookup on them again.
         */
    private final Collection<String> cachedGlobSearches;

    /**
     * Store previous searches so that we don't try to do database lookup on them again.
     */
    private final Collection<String> cachedWordSearches;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private JPopupMenu popupMenu;

    /**
     * Constructor
     *
     *
     * @param knowledgebaseDAOFactory
     */
    @Inject
    public ConceptTreePanel(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        cachedWordSearches = new HashSet<String>();
        cachedGlobSearches = new HashSet<String>();
        
    }

    /**
     * @param node
     *
     * @return
     */
    @Override
    public String getNodeTextToSearch(final DefaultMutableTreeNode node) {
        final Object userObject = node.getUserObject();
        final StringBuffer textToSearch = new StringBuffer();

        // Objects whos children have not been loaded yet will return a Boolean
        // as a user object. We should ignore these as much as we can.
        if (userObject instanceof Concept) {
            final Concept concept = (Concept) node.getUserObject();

            /*
             * The text is actually a composite of all names,
             * including primary, secondary, and common
             */
            final Collection<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
            for (ConceptName conceptName1 : conceptNames) {
                textToSearch.append(conceptName1.getName());
                textToSearch.append(" ");
            }

        }

        return textToSearch.toString();
    }

    /**
     * Returns the popupmenu that's been associated with this panel
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * This overridden method does a database lookup for searches. This is a
     * woorkaround needed for lazy loading. This method will load the branches
     * of all matches from the database.
     *
     * @param text
     * @param useGlobSearch
     *
     * @return
     */
    @Override
    public boolean goToMatchingNode(final String text, final boolean useGlobSearch) {

        /*
         * Disable so that folks can't start multiple searches.
         */
        getSearchBtn().setEnabled(false);
        getSearchTextField().setEnabled(false);
        LabeledSpinningDialWaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(this,
            "Searching for '" + text + "'");
        loadNodes(text, useGlobSearch, waitIndicator);
        boolean ok = super.goToMatchingNode(text, useGlobSearch);
        waitIndicator.dispose();
        getSearchBtn().setEnabled(true);
        getSearchTextField().setEnabled(true);
        getSearchTextField().requestFocus();

        return ok;
    }

    /**
     * Perform the database lookup of all {@link Concept} that match the criteria
     *
     * @param text
     * @param useGlobSearch
     */
    private void loadNodes(final String text, final boolean useGlobSearch,
                           final LabeledSpinningDialWaitIndicator waitIndicator) {
        Worker.post(new Job() {

            public Object run() {
                Collection<ConceptName> matches = null;

                /*
                 *  Do a fast lookup of the matching concepts
                 */
                try {
                    if (useGlobSearch) {
                        if (!cachedGlobSearches.contains(text)) {
                            ConceptNameDAO dao = knowledgebaseDAOFactory.newConceptNameDAO();
                            dao.startTransaction();
                            matches = dao.findByNameContaining(text);
                            dao.endTransaction();
                            dao.close();
                            cachedGlobSearches.add(text);
                            cachedWordSearches.add(text);
                        }
                    }
                    else {
                        if (!cachedWordSearches.contains(text)) {
                            ConceptNameDAO dao = knowledgebaseDAOFactory.newConceptNameDAO();
                            dao.startTransaction();
                            matches = dao.findByNameStartingWith(text);
                            dao.endTransaction();
                            dao.close();
                            cachedWordSearches.add(text);
                        }
                    }
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Database lookup of " + text + " failed", e);
                        EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
                    }
                }

                /*
                 * Open each node
                 */
                try {
                    if (matches != null) {


                        for (final ConceptName conceptName : matches) {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    waitIndicator.setLabel("Loading '" + conceptName.getName() + "'");
                                }

                            });
                            openNode(conceptName.getConcept());
                        }



                    }
                }
                catch (Exception e) {
                    log.error("A problem occurred with the database while loading concepts matching " + text, e);
                    EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
                }

                return matches;

            }

        });


    }

    /**
     * Loads the branch of a particular concept. This method does the following
     * <ol>
     *      <li>Walks from the concept up the tree to the root concept, storing
     *      the concepts in a list. (This is very fast)</li>
     *  <li>Starts walking from the root down (using lazyExpand), searching each
     *      childnode for a matching primary name (which was stored in the first
     *      step</li>
     *  <li>If a matching primary name is found this stops otherwise
     *              it opens the next level and searches for the next mathc in the list.</li>
     *  <li></li>
     * </ol>
     * @param concept
     */
    public synchronized void openNode(final Concept concept) {

        if (concept == null) {
            return;
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Opening node containing '" + concept.getPrimaryConceptName().getName() + "', " + concept);
        }

        final TreePath treePath = buildTreePathForNode(concept);
//        TreePath treePath = (TreePath) Worker.post(new Job() {
//
//            @Override
//            public Object run() {
//                TreePath path = buildTreePathForNode(concept);
//                return path;
//            }
//        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJTree().setSelectionPath(treePath);
                getJTree().scrollPathToVisible(treePath);
            }
        });



    }

    /**
     * Internal method for opening multiple nodes in a single transaction
     * @param concept
     */
    private TreePath buildTreePathForNode(final Concept concept) {
        if (log.isDebugEnabled()) {
            log.debug("Opening node containing '" + concept.getPrimaryConceptName().getName() + "', " + concept);
        }

        if (concept == null) {
            return null;
        }

        final JTree tree = getJTree();
        final ConceptTreeModel model = (ConceptTreeModel) tree.getModel();
        final TreeNode node = model.loadNode(concept.getPrimaryConceptName().getName());

        final TreePath path = new TreePath(model.getPathToRoot(node));
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                model.reload(node);
                tree.scrollPathToVisible(path);
            }

        });
        validate();
        return path;
    }

    /**
     * Refreshes the tree from data in the database (beawre you made need to
     * purge the {@link PersistenceCache} first. This methods will open the root
     * node.
     */
    public void refresh() {
        cachedGlobSearches.clear();
        cachedWordSearches.clear();
        ConceptTreeModel model = (ConceptTreeModel) getJTree().getModel();
        model.refresh();
    }

    /**
     * Refreshes the tree from data in the database (beawre you made need to
     * purge the {@link PersistenceCache} first. This methods will open the node
     * containing the specified {@link Concept}.
     * 
     * @param concept The concept to open in the tree
     */
    public void refreshAndOpenNode(final Concept concept) {
        if (log.isDebugEnabled()) {
            String name = concept == null ? "the root node" : concept.getPrimaryConceptName().getName();
            log.debug("Refreshing ConceptTree and opening it to '" + name +
                      "' -> " + concept);
        }
        refresh();
        Worker.post(new Job() {
            @Override
            public Object run() {
                openNode(concept);
                return null;
            }
        });

    }

    /**
     * This overriden method adds a few needed listeners to the concept tree
     * 
     * @param tree
     */
    @Override
    public void setJTree(JTree tree) {
        JTree oldTree = getJTree();
        if (oldTree != null) {
            oldTree.removeKeyListener(enterListener);
            oldTree.removeMouseListener(popupListener);
        }

        if (tree != null) {
            tree.addKeyListener(enterListener);
            tree.addMouseListener(popupListener);
        }
        super.setJTree(tree);
    }

    /**
     * Sets a popupmenu to use with the JTree
     */
    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    /**
     * Listens for ENTER and toggles the selected node open or closed i
     */
    private class EnterKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                JTree tree = getJTree();
                if (tree != null) {
                    int row = tree.getSelectionRows()[0];

                    if (tree.isCollapsed(row)) {
                        tree.expandRow(row);
                    }
                    else {
                        tree.collapseRow(row);
                    }
                }
            }
        }
    }


    /**
     * Listens for mouse events that can popup the Popupmenu
     */
    private class PopupListener extends MouseAdapter {

        private void evalutePopup(MouseEvent e) {

            // Display popup menu next to selected item
            if (e.isPopupTrigger() && (getJTree().getSelectionCount() != 0)) {
                JPopupMenu p = getPopupMenu();
                if (p != null) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent event) {
            evalutePopup(event);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            evalutePopup(event);
        }
    }
}
