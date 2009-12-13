/*
 * @(#)AnnotationFrame.java   2009.12.12 at 09:42:18 PST
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

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.cbpanel.ConceptButtonPanel;
import vars.annotation.ui.roweditor.RowEditorPanel;
import vars.annotation.ui.table.JXObservationTable;

/**
 *
 * @author brian
 */
public class AnnotationFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JPanel actionPanel;
    private JPanel conceptButtonPanel;
    private final AnnotationFrameController controller;
    private JPanel controlsPanel;
    private JSplitPane innerSplitPane;
    private JPanel lowerPanel;
    private JPanel miscTabsPanel;
    private JSplitPane outerSplitPane;
    private QuickControlsPanel quickControlsPanel;
    private JXObservationTable table;
    private JScrollPane tableScrollPane;
    private JToolBar toolBar;
    private final ToolBelt toolBelt;
    private JPanel rowEditorPanel;

    /**
     * Constructs ...
     *
     * @param toolBelt
     *
     * @throws HeadlessException
     */
    public AnnotationFrame(ToolBelt toolBelt) throws HeadlessException {
        this.toolBelt = toolBelt;
        this.controller = new AnnotationFrameController(this, toolBelt);
        initialize();
    }

    private JPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new ActionPanel(toolBelt);
        }

        return actionPanel;
    }

    private JPanel getConceptButtonPanel() {
        if (conceptButtonPanel == null) {
            conceptButtonPanel = new ConceptButtonPanel(toolBelt);
        }

        return conceptButtonPanel;
    }

    private JPanel getControlsPanel() {
        if (controlsPanel == null) {
            controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
            controlsPanel.add(getRowEditorPanel());
            controlsPanel.add(getActionPanel());
        }

        return controlsPanel;
    }

    private JSplitPane getInnerSplitPane() {
        if (innerSplitPane == null) {
            innerSplitPane = new JSplitPane();
            innerSplitPane.setLeftComponent(getTableScrollPane());
            innerSplitPane.setRightComponent(getMiscTabsPanel());
        }

        return innerSplitPane;
    }

    private JPanel getLowerPanel() {
        if (lowerPanel == null) {
            lowerPanel = new JPanel();
            lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
            lowerPanel.add(getControlsPanel());
            lowerPanel.add(getConceptButtonPanel());
        }

        return lowerPanel;
    }

    private JPanel getMiscTabsPanel() {
        if (miscTabsPanel == null) {
            miscTabsPanel = new MiscTabsPanel(toolBelt);
        }

        return miscTabsPanel;
    }

    private JSplitPane getOuterSplitPane() {
        if (outerSplitPane == null) {
            outerSplitPane = new JSplitPane();
            outerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            outerSplitPane.setLeftComponent(getInnerSplitPane());
            outerSplitPane.setRightComponent(getLowerPanel());
        }

        return outerSplitPane;
    }

    private QuickControlsPanel getQuickControlPanel() {
        if (quickControlsPanel == null) {
            quickControlsPanel = new QuickControlsPanel(toolBelt);
        }

        return quickControlsPanel;
    }

    private JPanel getRowEditorPanel() {
        if (rowEditorPanel == null) {
            rowEditorPanel = new RowEditorPanel(toolBelt);
        }
        return rowEditorPanel;
    }

    private JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();
            table.setFocusable(false);    // The row editor panel should get focus NOT the table

            /*
             * Watch the selected rows and notify the world when the selected rows
             * are changed
             */
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int[] rows = table.getSelectedRows();
                        final Dispatcher dispatcher = Lookup.getSelectedObservationsDispatcher();
                        final List<Observation> selectedObservations = new Vector<Observation>(rows.length);

                        for (int i = 0; i < rows.length; i++) {
                            selectedObservations.add(table.getObservationAt(rows[i]));
                        }

                        dispatcher.setValueObject(selectedObservations);
                    }
                }
            });

            /*
             * Watch for opening of a new videoarchive. When that happens we
             * have to re-populate the Table
             */
            Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    VideoArchive videoArchive = (VideoArchive) evt.getNewValue();
                    toolBelt.getPersistenceController().updateUI(videoArchive);
                }

            });

            Lookup.getObservationTableDispatcher().setValueObject(table);

        }

        return table;
    }

    private JScrollPane getTableScrollPane() {
        if (tableScrollPane == null) {
            tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScrollPane.setViewportView(getTable());
        }

        return tableScrollPane;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(new RefreshButton(toolBelt));
            toolBar.add(new StatusLabelForPerson(toolBelt));
            toolBar.add(new StatusLabelForVcr());
            toolBar.add(new StatusLabelForVideoArchive(toolBelt.getPersistenceController()));


        }

        return toolBar;
    }

    private void initialize() {
        getContentPane().add(getOuterSplitPane(), BorderLayout.CENTER);
        getContentPane().add(getQuickControlPanel(), BorderLayout.SOUTH);
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
    }
}
