/*
 * @(#)AnnotationFrame.java   2010.03.12 at 09:28:23 PST
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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.collect.Sets;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.movie.Timecode;
import org.mbari.util.Dispatcher;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.buttons.RedoButton;
import vars.annotation.ui.buttons.UndoButton;
import vars.annotation.ui.cbpanel.ConceptButtonPanel;
import vars.annotation.ui.commandqueue.RedoEvent;
import vars.annotation.ui.commandqueue.UndoEvent;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.annotation.ui.eventbus.UIEventSubscriber;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;
import vars.annotation.ui.eventbus.VideoArchiveSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.annotation.ui.preferences.PreferenceFrameButton;
import vars.annotation.ui.roweditor.RowEditorPanel;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.table.JXObservationTableColumnModel;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;
import vars.annotation.ui.video.VideoControlPanel;
import vars.annotation.ui.videoset.VideoArchiveSetEditorButton;
import vars.shared.ui.video.VideoControlService;

/**
 *
 * @author brian
 */
public class AnnotationFrame extends JFrame implements UIEventSubscriber {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JPanel actionPanel;
    private JSplitPane allControlsSplitPane;
    private JPanel conceptButtonPanel;
    private final AnnotationFrameController controller;
    private JPanel controlsPanel;
    private JSplitPane controlsPanelSplitPane;
    private JSplitPane innerSplitPane;
    private JPanel miscTabsPanel;
    private JSplitPane outerSplitPane;
    private QuickControlsPanel quickControlsPanel;
    private RowEditorPanel rowEditorPanel;
    private JXObservationTable table;
    private JPopupMenu tablePopupMenu;
    private JScrollPane tableScrollPane;
    private JToolBar toolBar;
    private final ToolBelt toolBelt;
    private VideoControlPanel videoControlPanel;
    private VideoArchive videoArchive;

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
        AnnotationProcessor.process(this); // Create EventBus Proxy
        initialize();
    }

    private JPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new ActionPanel(toolBelt);
            actionPanel.setMinimumSize(new Dimension(350, 100));
        }

        return actionPanel;
    }

    protected JSplitPane getAllControlsSplitPane() {
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

    protected JSplitPane getControlsPanelSplitPane() {
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

    protected JSplitPane getInnerSplitPane() {
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

    protected JSplitPane getOuterSplitPane() {
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

    /**
     * @return
     */
    public RowEditorPanel getRowEditorPanel() {
        if (rowEditorPanel == null) {
            rowEditorPanel = new RowEditorPanel(toolBelt);
            rowEditorPanel.setPreferredSize(new Dimension(600, 250));
        }

        return rowEditorPanel;
    }

    protected JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();
            table.setFocusable(false);    // The row editor panel should get focus NOT the table
            ((JXObservationTableColumnModel) table.getColumnModel()).setMiniView(true);

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
                        final List<Observation> selectedObservations = new Vector<Observation>(rows.length);
                        for (int i = 0; i < rows.length; i++) {
                            selectedObservations.add(table.getObservationAt(rows[i]));
                        }
                        // TODO add check to see if the selected observations are different thant
                        // the previously selected observations BEFORE sending this
                        Collection<Observation> oldObservations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                        Set<Observation> oldSelectedObservations = new HashSet<Observation>(oldObservations);
                        if (!Sets.symmetricDifference(new HashSet<Observation>(selectedObservations), oldSelectedObservations).isEmpty()) {
                            EventBus.publish(new ObservationsSelectedEvent(table, selectedObservations));
                        }
                    }
                }
            });

            /*
             * Right-click popup menu
             */
            table.setComponentPopupMenu(getTablePopupMenu());

            Lookup.getObservationTableDispatcher().setValueObject(table);

        }

        return table;
    }

    protected JPopupMenu getTablePopupMenu() {
        if (tablePopupMenu == null) {
            tablePopupMenu = new JPopupMenu();
            JMenuItem seekItem = new JMenuItem("Seek to this timecode");
            tablePopupMenu.add(seekItem);
            Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener((evt) -> {
                Collection<Observation> observations = (Collection<Observation>) evt.getNewValue();
                seekItem.setEnabled(observations.size() == 1);
            });

            seekItem.addActionListener((e) -> {
                VideoControlService vcr = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
                if (vcr != null) {
                    // Get selected annotation
                    Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                    if (observations.size() == 1) {
                        Observation obs = observations.iterator().next();
                        vcr.seek(obs.getVideoFrame().getTimecode());
                    }
                }
            });
        }
        return tablePopupMenu;
    }

    protected JScrollPane getTableScrollPane() {
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
            toolBar.add(new UndoButton());
            toolBar.add(new RedoButton());
            toolBar.add(new RefreshButton(toolBelt));
            // toolBar.add(new VideoArchiveSetEditorButton(toolBelt));
            toolBar.add(new PreferenceFrameButton());
            toolBar.add(new StatusLabelForPerson(toolBelt));
            // toolBar.add(new StatusLabelForVcr());
            //toolBar.add(new StatusLabelForVideoArchive(toolBelt));
            toolBar.add(new StatusContainerForVideoPlayer(toolBelt));

            // Map in undo and redo keys
            InputMap inputMap = toolBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            KeyStroke undoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            inputMap.put(undoStroke, "undo");
            KeyStroke redoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
            inputMap.put(redoStroke, "redo");

            ActionMap actionMap = toolBar.getActionMap();
            actionMap.put("undo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new UndoEvent());
                }
            });
            actionMap.put("redo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new RedoEvent());
                }
            });

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

    public AnnotationFrameController getController() {
        return controller;
    }


    @EventSubscriber(eventClass = ObservationsAddedEvent.class)
    @Override
    public void respondTo(ObservationsAddedEvent event) {
        respondTo(new ObservationsChangedEvent(this, event.get()));
        //EventBus.publish(new ObservationsSelectedEvent(null, event.get()));
    }

    @EventSubscriber(eventClass = ObservationsChangedEvent.class)
    @Override
    public void respondTo(ObservationsChangedEvent event) {
        final ObservationTable observationTable = getTable();
        if (event.getEventSource() != observationTable) {
            final JTable table = observationTable.getJTable();
            final ObservationTableModel model = (ObservationTableModel) table.getModel();
            for (Observation observation : event.get()) {
                int row = model.getObservationRow(observation);
                if ((row > -1) && (row < model.getRowCount())) {
                    observationTable.updateObservation(observation);
                }
                else {
                    observationTable.addObservation(observation);
                    // Scroll to a new observation
                    row = model.getObservationRow(observation);

                    if ((row > -1) && (row < model.getRowCount())) {
                        Rectangle cellRect = table.getCellRect(row, 0, true);
                        Rectangle visibleRect = table.getVisibleRect();
                        if (cellRect.intersects(visibleRect)) {
                            table.scrollRectToVisible(cellRect);
                            //observationTable.scrollToVisible(row, 0);
                        }
                    }
                }
            }
        }
    }

    @EventSubscriber(eventClass = ObservationsRemovedEvent.class)
    @Override
    public void respondTo(ObservationsRemovedEvent event) {
        final ObservationTable observationTable = getTable();
        if (event.getEventSource() != observationTable) {
            final JTable table = observationTable.getJTable();
            final ObservationTableModel model = (ObservationTableModel) table.getModel();
            for (Observation observation : event.get()) {
                observationTable.removeObservation(observation);
            }
        }
    }

    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    @Override
    public void respondTo(ObservationsSelectedEvent event) {
        final ObservationTable observationTable = getTable();
        if(event.getSelectionSource() != observationTable) {
            observationTable.setSelectedObservations(event.get());
        }
    }

    @EventSubscriber(eventClass = VideoArchiveChangedEvent.class)
    @Override
    public void respondTo(VideoArchiveChangedEvent event) {
        // --- hang on to videoArchive reference
        VideoArchive oldVideoArchive = videoArchive;
        VideoArchive newVideoArchive = event.get();
        videoArchive = newVideoArchive;

        // --- Clear table
        final ObservationTable observationTable = getTable();
        final JTable table = observationTable.getJTable();
        table.getSelectionModel().clearSelection();
        ((ObservationTableModel) table.getModel()).clear();

        // --- Repopulate table with observations
        // DAOTX - Needed to deal with lazy loading
        if (newVideoArchive != null) {
            Collection<Observation> observations = new ArrayList<Observation>();
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            VideoArchive videoArchive = dao.find(event.get());
            final Collection<VideoFrame> videoFrames = videoArchive.getVideoFrames();
            for (VideoFrame videoFrame : videoFrames) {
                observations.addAll(videoFrame.getObservations());
            }
            dao.endTransaction();
            dao.close();
            final Rectangle rect = table.getVisibleRect();
            respondTo(new ObservationsChangedEvent(null, observations));

            // --- Scroll view if needed
            if (newVideoArchive.equals(oldVideoArchive)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // When observations are deleted the table would jump to the last row UNLESS
                        // we make this call which mostly preserves the current view. Doing this still
                        // makes a little visible 'jump' but it takes the user back to about the same
                        // position in the table
                        table.scrollRectToVisible(rect);
                    }
                });
            }
        }
    }

    @EventSubscriber(eventClass = VideoArchiveSelectedEvent.class)
    @Override
    public void respondTo(VideoArchiveSelectedEvent event) {
//        VideoArchive oldVideoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
//        VideoArchive newVideoArchive = event.get();
//        if (oldVideoArchive == null || newVideoArchive == null || !oldVideoArchive.equals(newVideoArchive)) {
//            respondTo(new VideoArchiveChangedEvent(null, newVideoArchive));
//        }
        respondTo(new VideoArchiveChangedEvent(null, event.get()));
    }

    @EventSubscriber(eventClass = VideoFramesChangedEvent.class)
    @Override
    public void respondTo(VideoFramesChangedEvent event) {
        Collection<Observation> observations = new HashSet<Observation>();
        Collection<VideoFrame> videoFrames = event.get();
        for (VideoFrame videoFrame : videoFrames) {
            observations.addAll(videoFrame.getObservations());
        }
        respondTo(new ObservationsChangedEvent(null, observations));
    }
}
