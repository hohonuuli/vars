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

import vars.annotation.ui.FrameGrabPanel;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.jdesktop.swingx.JXTree;
import org.mbari.swing.SearchableTreePanel;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.shared.ui.UIDecorator;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;
import vars.shared.ui.tree.ConceptTreePanel;
import vars.shared.ui.tree.JTreeDragAndDropDecorator;

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

            /*
             * Listen to messages to select a concept in the tree
             */
            EventBus.subscribe(Lookup.TOPIC_SELECT_CONCEPT, new EventTopicSubscriber<String>() {

                public void onEvent(String topic, String data) {
                    treePanel.goToMatchingNode(data, false);
                }

            });

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
        tabbedPane.add("Frame-grab", new FrameGrabPanel());
        tabbedPane.add("Observation", new PObservationPanel(toolbelt));
        tabbedPane.add("Video Archive", new PVideoArchivePanel(toolbelt));
        tabbedPane.add("Physical Data", new PPhysicalDataPanel());
        tabbedPane.add("Camera Data", new PCameraDataPanel(toolbelt.getPersistenceController()));
        tabbedPane.add("Knowledge Base", getTreePanel());
    }

    private void resizeHandler(final java.awt.event.ComponentEvent evt) {

        tabbedPane.setSize(getSize());
        final Component[] cs = tabbedPane.getComponents();
        for (int i = 0; i < cs.length; i++) {
            cs[i].setSize(getSize());
        }
    }
}
