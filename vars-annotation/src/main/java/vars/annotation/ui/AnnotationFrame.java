/*
 * @(#)AnnotationFrame.java   2009.12.16 at 02:51:33 PST
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
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.util.Dispatcher;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.cbpanel.ConceptButtonPanel;
import vars.annotation.ui.roweditor.RowEditorPanel;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.video.VideoControlPanel;

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
    private JPanel miscTabsPanel;
    private JSplitPane outerSplitPane;
    private QuickControlsPanel quickControlsPanel;
    private RowEditorPanel rowEditorPanel;
    private JXObservationTable table;
    private JScrollPane tableScrollPane;
    private JToolBar toolBar;
    private final ToolBelt toolBelt;
    private VideoControlPanel videoControlPanel;
    private JSplitPane controlsPanelSplitPane;
    private JSplitPane allControlsSplitPane;

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
            actionPanel.setMinimumSize(new Dimension(350, 100));
        }

        return actionPanel;
    }

    private JSplitPane getAllControlsSplitPane() {
        if (allControlsSplitPane == null) {
            allControlsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            allControlsSplitPane.setLeftComponent(getControlsPanelSplitPane());
            allControlsSplitPane.setRightComponent(getConceptButtonPanel());
        }
        return allControlsSplitPane;
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
            controlsPanel.add(getActionPanel());
            controlsPanel.add(getVideoControlPanel());
        }

        return controlsPanel;
    }

    private JSplitPane getControlsPanelSplitPane() {
        if (controlsPanelSplitPane == null) {
            controlsPanelSplitPane = new JSplitPane();
            controlsPanelSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            controlsPanelSplitPane.setLeftComponent(getRowEditorPanel());
            controlsPanelSplitPane.setRightComponent(getControlsPanel());
            Dimension size = controlsPanelSplitPane.getPreferredSize();
            controlsPanelSplitPane.setPreferredSize(new Dimension(size.width, 200));
        }
        return controlsPanelSplitPane;
    }

    private JSplitPane getInnerSplitPane() {
        if (innerSplitPane == null) {
            innerSplitPane = new JSplitPane();
            innerSplitPane.setLeftComponent(getTableScrollPane());
            innerSplitPane.setRightComponent(getMiscTabsPanel());
            innerSplitPane.setOneTouchExpandable(true);
        }

        return innerSplitPane;
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
            outerSplitPane.setRightComponent(getAllControlsSplitPane());
        }

        return outerSplitPane;
    }

    private QuickControlsPanel getQuickControlPanel() {
        if (quickControlsPanel == null) {
            quickControlsPanel = new QuickControlsPanel(toolBelt);
        }

        return quickControlsPanel;
    }

    public RowEditorPanel getRowEditorPanel() {
        if (rowEditorPanel == null) {
            rowEditorPanel = new RowEditorPanel(toolBelt);
            rowEditorPanel.setPreferredSize(new Dimension(600, 250));
        }

        return rowEditorPanel;
    }

    private JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();
            table.setFocusable(false);    // The row editor panel should get focus NOT the table

            // Map Mask+UP-ARROW Key Stroke
            String upTable = "up-table";
            Action upAction = new AbstractAction() {

                public void actionPerformed(final ActionEvent e) {
                    final int numRows = table.getRowCount();
                    final int currentRow = table.getSelectionModel().getLeadSelectionIndex();
                    final int nextRow = (currentRow - 1 < 0) ? numRows - 1 : currentRow - 1;
                    table.getSelectionModel().setSelectionInterval(nextRow, nextRow);
                    table.scrollToVisible(nextRow, 0);
                }
            };

            table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), upTable);
            table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), upTable);
            table.getActionMap().put(upTable, upAction);


            // Map Mask+DOWN-ARROW Key Stroke
            String downTable = "down-table";
            Action downAction = new AbstractAction() {

                public void actionPerformed(final ActionEvent e) {
                    final int numRows = table.getRowCount();
                    final int currentRow = table.getSelectionModel().getLeadSelectionIndex();
                    final int nextRow = (currentRow + 1 >= numRows) ? 0 : currentRow + 1;
                    table.getSelectionModel().setSelectionInterval(nextRow, nextRow);
                    table.scrollToVisible(nextRow, 0);
                }

            };
            table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), downTable);
            table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), downTable);
            table.getActionMap().put(downTable, downAction);

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
            toolBar.add(new StatusLabelForVideoArchive(toolBelt));


        }

        return toolBar;
    }

    private VideoControlPanel getVideoControlPanel() {
        if (videoControlPanel == null) {
            videoControlPanel = new VideoControlPanel();
            Lookup.getVideoControlServiceDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    videoControlPanel.setVcr((IVCR) evt.getNewValue());
                }
            });
        }
        return videoControlPanel;
    }

    private void initialize() {
        getContentPane().add(getOuterSplitPane(), BorderLayout.CENTER);
        getContentPane().add(getQuickControlPanel(), BorderLayout.SOUTH);
        getContentPane().add(getToolBar(), BorderLayout.NORTH);




    }
}
