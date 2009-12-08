/*
 * @(#)AnnotationPanel.java   2009.11.16 at 09:00:31 PST
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



package vars.old.annotation.ui;

import vars.annotation.ui.ToolBelt;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import vars.annotation.ui.MiscTabsPanel;
import vars.annotation.ui.cbpanel.ConceptButtonPanel;



/**
 * <p>This panel contains all the components of the annotation appliation.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class AnnotationPanel extends JPanel {

    private javax.swing.JSplitPane bottomSplitPane = null;
    private JSplitPane buttonEditorSplitPane = null;
    private javax.swing.JPanel buttonPanel = null;
    private javax.swing.JPanel conceptButtonPanel = null;
    private javax.swing.JPanel editorPanel = null;
    private javax.swing.JPanel miscTabsPanel = null;
    private javax.swing.JSplitPane outerSplitPane = null;
    private javax.swing.JSplitPane topSplitPane = null;
    private final ToolBelt toolBelt;

    /**
     * This is the default constructor
     *
     * @param toolbelt
     */
    public AnnotationPanel(ToolBelt toolbelt) {
        super();
        this.toolBelt = toolbelt;
        initialize();
        PreferenceSetter.getInstance().setResizePreference(this, "MainPanel");
    }

    private javax.swing.JSplitPane getBottomSplitPane() {
        if (bottomSplitPane == null) {
            bottomSplitPane = new javax.swing.JSplitPane();
            bottomSplitPane.setTopComponent(getButtonAndEditorPanel());
            bottomSplitPane.setBottomComponent(getConceptButtonPanel());
            bottomSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        }

        return bottomSplitPane;
    }

    private JComponent getButtonAndEditorPanel() {
        if (buttonEditorSplitPane == null) {
            buttonEditorSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            buttonEditorSplitPane.setRightComponent(getButtonPanel());
            buttonEditorSplitPane.setLeftComponent(new RowEditorPanel(toolBelt));
        }

        return buttonEditorSplitPane;
    }

    private javax.swing.JComponent getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.add(new VCRPanel(), BorderLayout.EAST);    // TODO comment this line for OPENHOUSE
            final ActionPanel ap = new ActionPanel(toolBelt);
            buttonPanel.add(ap, BorderLayout.CENTER);
            ap.registerHotKeys();
        }

        return buttonPanel;
    }

    private javax.swing.JPanel getConceptButtonPanel() {
        if (conceptButtonPanel == null) {
            conceptButtonPanel = new ConceptButtonPanel(toolBelt);
        }

        return conceptButtonPanel;
    }

    private javax.swing.JPanel getEditorPanel() {
        if (editorPanel == null) {
            editorPanel = new EditorPanel();
            editorPanel.setMinimumSize(new Dimension(450, 100));
        }

        return editorPanel;
    }

    private javax.swing.JPanel getMiscTabsPanel() {
        if (miscTabsPanel == null) {
            miscTabsPanel = new MiscTabsPanel(toolBelt);
        }

        return miscTabsPanel;
    }

    private javax.swing.JSplitPane getOuterSplitPane() {
        if (outerSplitPane == null) {
            outerSplitPane = new javax.swing.JSplitPane();
            outerSplitPane.setTopComponent(getTopSplitPane());

            //outerSplitPane.setBottomComponent(getButtonAndEditorPanel());  // TODO uncommented this line for OPENHOUSE
            outerSplitPane.setBottomComponent(getBottomSplitPane());    // TODO commented this line for OPENHOUSE
            outerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            outerSplitPane.setOneTouchExpandable(true);
            outerSplitPane.setResizeWeight(1);
        }

        return outerSplitPane;
    }

    private javax.swing.JSplitPane getTopSplitPane() {
        if (topSplitPane == null) {
            topSplitPane = new javax.swing.JSplitPane();
            topSplitPane.setLeftComponent(getEditorPanel());
            topSplitPane.setRightComponent(getMiscTabsPanel());
            topSplitPane.setResizeWeight(0.8);
            topSplitPane.setOneTouchExpandable(true);
        }

        return topSplitPane;
    }

    private void initialize() {
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
        this.add(getOuterSplitPane(), BorderLayout.CENTER);
        this.setSize(300, 200);
    }
}
