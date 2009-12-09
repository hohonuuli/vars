/*
 * @(#)AnnotationFrame.java   2009.12.07 at 04:29:40 PST
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.table.ObservationTableModel;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.cbpanel.ConceptButtonPanel;

/**
 *
 * @author brian
 */
public class AnnotationFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * When observations are deleted we need to remove them from the view.
     * The AppFrameController contains a subscriber that deletes them from the
     * data store
     */
    private final EventTopicSubscriber<Collection<Observation>> deleteObservationsSubscriber = new EventTopicSubscriber<Collection<Observation>>() {

        public void onEvent(String topic, Collection<Observation> data) {
            if (log.isDebugEnabled()) {
                log.debug("Removing observations from table\nDATA: " + data);
            }

            final ObservationTableModel model = (ObservationTableModel) getTable().getModel();

            for (Observation observation : data) {
                model.removeObservation(observation);
            }
        }
    };

    /**
     * When observations are persisted we need to add them to the view.
     * The AppFrameController contains a subscriber that persists them into the
     * data store
     */
    private final EventTopicSubscriber<Collection<Observation>> persistObservationsSubscriber = new EventTopicSubscriber<Collection<Observation>>() {

        public void onEvent(String topic, Collection<Observation> data) {
            if (log.isDebugEnabled()) {
                log.debug("Adding observations to table\nDATA: " + data);
            }

            final ObservationTableModel model = (ObservationTableModel) getTable().getModel();

            for (Observation observation : data) {
                model.addObservation(observation);
            }

            /*
             * If we just added one select it in the table
             */
            if (data.size() == 1) {
                final Observation observation = data.iterator().next();
                getTable().setSelectedObservation(observation);
            }

        }
    };

    /**
     * When observations are updated we redraw the table
     */
    private final EventTopicSubscriber<Collection<Observation>> mergeObservationsSubscriber = new EventTopicSubscriber<Collection<Observation>>() {

        public void onEvent(String topic, Collection<Observation> data) {
            if (log.isDebugEnabled()) {
                log.debug("Removing observations from table\nDATA: " + data);
            }

            getTable().redrawAll();
        }
    };
    private JPanel conceptButtonPanel;
    private final AnnotationFrameController controller;
    private JSplitPane innerSplitPane;
    private JPanel miscTabsPanel;
    private JSplitPane outerSplitPane;
    private QuickControlsPanel quickControlsPanel;
    private JXObservationTable table;
    private JToolBar toolBar;
    private final ToolBelt toolBelt;
    private JScrollPane tableScrollPane;

    /**
     * Constructs ...
     *
     * @param toolBelt
     *
     * @throws HeadlessException
     */
    public AnnotationFrame(ToolBelt toolBelt) throws HeadlessException {
        this.toolBelt = toolBelt;
        this.controller = new AnnotationFrameController(this, toolBelt.getPersistenceController());
        initialize();
    }

    private JPanel getConceptButtonPanel() {
        if (conceptButtonPanel == null) {
            conceptButtonPanel = new ConceptButtonPanel(toolBelt);
        }

        return conceptButtonPanel;
    }

    private JSplitPane getInnerSplitPane() {
        if (innerSplitPane == null) {
            innerSplitPane = new JSplitPane();
            innerSplitPane.setLeftComponent(getTableScrollPane());
            innerSplitPane.setRightComponent(getMiscTabsPanel());
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
            outerSplitPane.setRightComponent(getConceptButtonPanel());
        }

        return outerSplitPane;
    }

    private QuickControlsPanel getQuickControlPanel() {
        if (quickControlsPanel == null) {
            quickControlsPanel = new QuickControlsPanel(toolBelt.getPersistenceController());
        }

        return quickControlsPanel;
    }

    private JScrollPane getTableScrollPane() {
        if (tableScrollPane == null) {
            tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScrollPane.setViewportView(getTable());
        }
        return tableScrollPane;
    }

    private JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();

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


            /*
             * When observations are deleted we remove them from the view.
             */
            EventBus.subscribe(Lookup.TOPIC_DELETE_OBSERVATIONS, deleteObservationsSubscriber);

            /*
             * When observations are persisted we add them to the view.
             */
            EventBus.subscribe(Lookup.TOPIC_PERSIST_OBSERVATIONS, persistObservationsSubscriber);

            /*
             * When observations are updated we redraw them in the view.
             */
            EventBus.subscribe(Lookup.TOPIC_MERGE_OBSERVATIONS, mergeObservationsSubscriber);

        }

        return table;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(new StatusLabelForPerson(toolBelt));
            toolBar.add(new StatusLabelForVcr());
            toolBar.add(new StatusLabelForVideoArchive(toolBelt.getAnnotationDAOFactory()));
        }

        return toolBar;
    }

    private void initialize() {
        getContentPane().add(getOuterSplitPane(), BorderLayout.CENTER);
        getContentPane().add(getQuickControlPanel(), BorderLayout.SOUTH);
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
    }
}
