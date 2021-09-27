/*
 * @(#)ConceptTreePopupMenu.java   2010.05.28 at 02:10:03 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import mbarix4j.swing.JImageFrame;
import mbarix4j.swing.JImageUrlFrame;
import org.bushe.swing.event.EventBus;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.knowledgebase.Media;
import vars.shared.ui.GlobalStateLookup;

/**
 * @author brian
 */
public class ConceptTreePopupMenu extends JPopupMenu {

    JMenuItem expandMenuItem;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    JMenuItem showMediaItem;
    private final JTree tree;

    /**
     * Constructs ...
     *
     * @param tree
     * @param knowledgebaseDAOFactory
     */
    public ConceptTreePopupMenu(JTree tree, KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.tree = tree;
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;

        if (!(tree.getModel() instanceof ConceptTreeModel)) {
            throw new IllegalArgumentException("The JTree you provided does not use a ConceptTreeModel");
        }

        showMediaItem = new JMenuItem("Show Image", 'I');
        showMediaItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showPrimaryImage();
            }

        });
        add(showMediaItem);

        expandMenuItem = new JMenuItem("Expand", 'E');
        expandMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                expandNode();
            }

        });
        add(expandMenuItem);

        JMenuItem collapseMenuItem = new JMenuItem("Collapse", 'C');

        collapseMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                collapseNode();
            }

        });
        add(collapseMenuItem);


        // Listen for nodes selection. If the concept has a primary image
        // then enable the show image menu item
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                TreePath selectionPath = ConceptTreePopupMenu.this.tree.getSelectionPath();

                if (selectionPath != null) {
                    ConceptTreeNode node = (ConceptTreeNode) selectionPath.getLastPathComponent();
                    Concept concept = (Concept) node.getUserObject();
                    boolean enable = (concept != null) && (concept.getConceptMetadata().getPrimaryImage() != null);

                    showMediaItem.setEnabled(enable);
                }
            }
        });


    }

    /**
     * Collapses all the child nodes from the selected node on down
     */
    private void collapseNode() {

        // We really want to expand the path nearest to where the mouse is
        // currently in the tree. However, getClosestPathForLocation returns
        // a full path that includes he nearest node AND all it's children
        // It's trying to collapse just the end nodes, which can't be collapsed
        togglePath(tree.getSelectionPath(), false);
    }

    /**
     * Makes the children nodes under the specified node visible.
     *
     */
    private void expandNode() {

        TreePath selectionPath = tree.getSelectionPath();

        ConceptTreeNode node = (ConceptTreeNode) selectionPath.getLastPathComponent();

        if ((node == null) || node.isLeaf()) {
            return;
        }

        expandMenuItem.setEnabled(false);
        expandMenuItem.setText("Expanding " + ((Concept) node.getUserObject()).getPrimaryConceptName().getName() +
                               " ...");

        // ---- Step 1: Load the Concepts from the database
        final ConceptTreeModel model = (ConceptTreeModel) tree.getModel();
        final Concept concept = (Concept) node.getUserObject();
        final TreePath fSelectionPath = selectionPath;
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();

                Concept aConcept = dao.find(concept);
                Collection<Concept> descendants = dao.findDescendents(aConcept);

                for (Concept c : descendants) {
                    model.loadNode(c.getPrimaryConceptName().getName());
                }

                dao.close();

                return null;

            }

            @Override
            protected void done() {
                togglePath(fSelectionPath, true);
                expandMenuItem.setText("Expand");
                expandMenuItem.setEnabled(true);
            }


        };

        worker.execute();


    }

    /**
     * Show the primary image of the selected node in a seperate frame
     */
    private void showPrimaryImage() {
        TreePath selectionPath = tree.getSelectionPath();
        ConceptTreeNode node = (ConceptTreeNode) selectionPath.getLastPathComponent();
        Concept concept = (Concept) node.getUserObject();
        Media media = concept.getConceptMetadata().getPrimaryImage();

        try {
            URL url = new URL(media.getUrl());
            JImageFrame imageFrame = new JImageUrlFrame(url);

            imageFrame.setTitle(concept.getPrimaryConceptName().getName());
            imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            imageFrame.setLocationRelativeTo(GlobalStateLookup.getSelectedFrame());
            imageFrame.setVisible(true);
        }
        catch (MalformedURLException ex) {
            EventBus.publish(GlobalStateLookup.TOPIC_WARNING, "Unable to load " + media.getUrl());
        }

    }

    /**
     * Expands or collapses the path you provide. THis ahs to be done from the bottom
     * up.
     * @param parent The tree ndoe to expand/collapse
     * @param expand if true all the paths under the provided path will be expanded
     *  if false the paths will be collapsed.
     */
    private void togglePath(TreePath parent, boolean expand) {

        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                ConceptTreeNode n = (ConceptTreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);

                togglePath(path, expand);
            }
        }

        if (expand) {
            tree.expandPath(parent);
        }
        else {
            tree.collapsePath(parent);
        }

    }
}
