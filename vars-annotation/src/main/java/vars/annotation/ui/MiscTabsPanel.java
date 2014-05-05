/*
 * @(#)MiscTabsPanel.java   2009.11.16 at 02:30:29 PST
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



package vars.annotation.ui;

import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXTree;
import org.mbari.swing.SearchableTreePanel;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;

import vars.annotation.ui.ppanel.VideoArchiveEditorPanel;
import vars.shared.ui.UIDecorator;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;
import vars.shared.ui.tree.ConceptTreePanel;
import vars.shared.ui.tree.JTreeDragAndDropDecorator;
import vars.annotation.ui.ppanel.FrameGrabPanel;
import vars.annotation.ui.ppanel.PCameraDataPanel;
import vars.annotation.ui.ppanel.PObservationPanel;
import vars.annotation.ui.ppanel.PPhysicalDataPanel;
import vars.annotation.ui.ppanel.PVideoArchivePanel;
import vars.knowledgebase.Concept;
import vars.shared.ui.tree.ConceptTreeNode;
import vars.shared.ui.tree.ConceptTreePopupMenu;

/**
 * <p>JPanel that contains various tabbed panes used in the annotation
 * application. These tabbed panesl include the frame-grab and
 * knowledgebase tabs.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class MiscTabsPanel extends javax.swing.JPanel {

    private javax.swing.JTabbedPane tabbedPane;
    private final ToolBelt toolbelt;

    /** We don't read this but we hang on to a reference to prevent garbage collection */
    @SuppressWarnings("unused")
    private UIDecorator treeDecorator;
    private SearchableTreePanel treePanel;
    private EventTopicSubscriber<String> lookupConceptSubscriber;

    /**
     * Creates new form MiscTabsPanel
     *
     * @param toolbelt
     */
    public MiscTabsPanel(ToolBelt toolbelt) {
        this.toolbelt = toolbelt;
        initComponents();
        initTabs();
    }

    /**
     *     @return  Returns the treePanel.
     */
    public SearchableTreePanel getTreePanel() {
        if (treePanel == null) {
            treePanel = new ConceptTreePanel(toolbelt.getKnowledgebaseDAOFactory());
            final TreeModel treeModel = new ConceptTreeModel(toolbelt.getKnowledgebaseDAOFactory());
            final JTree tree = new JXTree(treeModel);
            tree.setCellRenderer(new ConceptTreeCellRenderer());
            treeDecorator = new JTreeDragAndDropDecorator(tree);
            treePanel.setJTree(tree);
            ((ConceptTreePanel) treePanel).setPopupMenu(new ConceptTreePopupMenu(tree, toolbelt.getKnowledgebaseDAOFactory()));

            lookupConceptSubscriber = new EventTopicSubscriber<String>() {

                public void onEvent(String topic, String data) {
                    treePanel.goToMatchingNode(data, false);
                }

            };

            // Refresh the tree if the cache is cleared
            toolbelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

                public void afterClear(CacheClearedEvent evt) {
                    TreePath treePath = tree.getSelectionPath();
                    if (treePath != null) {
                        ConceptTreeNode node = (ConceptTreeNode) treePath.getLastPathComponent();
                        Concept concept = (Concept) node.getUserObject();
                        ((ConceptTreePanel) treePanel).refreshAndOpenNode(concept);
                    }
                    else {
                        ((ConceptTreePanel) treePanel).refresh();
                    }
                }

                public void beforeClear(CacheClearedEvent evt) {
                    // Do Nothing
                }
            });

            /*
             * Listen to messages to select a concept in the tree
             */
            EventBus.subscribe(Lookup.TOPIC_SELECT_CONCEPT, lookupConceptSubscriber);

        }

        return treePanel;
    }

    private void initComponents() {

        tabbedPane = new JTabbedPane();
        setLayout(new java.awt.BorderLayout());
        addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(final java.awt.event.ComponentEvent evt) {
                resizeHandler(evt);
            }
        });
        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }

    private void initTabs() {
        tabbedPane.add("Frame-grab", new FrameGrabPanel(toolbelt));
        tabbedPane.add("Bulk Editor", new VideoArchiveEditorPanel(toolbelt));
        tabbedPane.add("Knowledge Base", getTreePanel());
        tabbedPane.add("Observation", new PObservationPanel(toolbelt));
        tabbedPane.add("Video Archive", new PVideoArchivePanel(toolbelt));
        tabbedPane.add("Physical Data", new PPhysicalDataPanel());
        tabbedPane.add("Camera Data", new PCameraDataPanel(toolbelt));
    }

    private void resizeHandler(final java.awt.event.ComponentEvent evt) {

        tabbedPane.setSize(getSize());
        final Component[] cs = tabbedPane.getComponents();
        for (int i = 0; i < cs.length; i++) {
            cs[i].setSize(getSize());
        }
    }
}
