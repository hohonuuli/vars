/*
 * @(#)VideoSetViewer.java   2009.11.17 at 01:20:08 PST
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.CameraData;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.actions.MoveVideoFrameWithDialogAction;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;

/**
 * <p>
 * Frame that is used for editing annotations for an entire VideoArchiveSet. It
 * provides search and replace capabilites, but not the fine editing control
 * that one gets from the AnnotationApp
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 */
public class VideoSetViewer extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SearchAndReplaceWidget searchAndReplaceWidget = null;
    private final PropertyChangeSupport changes;
    private JButton moveVideoFramesButton;
    private JMenuBar myMenuBar;
    private JXObservationTable table;
    private final ToolBelt toolBelt;
    private JPanel toolPanel;
    private VideoArchiveSet videoArchiveSet;

    /**
     * Constructor for the VideoSetViewer object
     *
     * @param toolBelt
     */
    public VideoSetViewer(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        changes = new PropertyChangeSupport(this);
        final Dispatcher dispatcher = Lookup.getVideoArchiveDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                setVideoArchive((VideoArchive) evt.getNewValue());
            }
        });

        // Need to grab the dispatcher when constructing a new instance
        VideoArchive videoArchive = (VideoArchive) dispatcher.getValueObject();
        setVideoArchive(videoArchive);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        SwingUtils.smartSetBounds(this);
        getContentPane().setLayout(new BorderLayout());
        final JScrollPane scrollPane = new JScrollPane(getTable());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
        setJMenuBar(getMyMenuBar());
    }

    /**
     * Add a listener to receive notification of changes to all the bound
     * properties in this class.
     *
     * @param l The listener which will receive <tt>PropertyChangeEvent</tt>
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    /**
     * Add a listener to receive notification of changes to the bound property
     * whose name matches the string provided.
     *
     *
     * @param propertyName The name of the property to receive change events on.
     * @param l The listener which will receive <tt>PropertyChangeEvent</tt>
     */
    @Override
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    private JButton getMoveVideoFramesButton() {
        if (moveVideoFramesButton == null) {
            moveVideoFramesButton = new JButton(new MoveAction());
        }

        return moveVideoFramesButton;
    }

    private JMenuBar getMyMenuBar() {
        if (myMenuBar == null) {
            myMenuBar = new JMenuBar();
            final JMenu fileMenu = new JMenu("File");
            myMenuBar.add(fileMenu);
            final JMenuItem cdItem = new JMenuItem(new ChangeCameraDirectionAction());
            fileMenu.add(cdItem);
        }

        return myMenuBar;
    }

    private SearchAndReplaceWidget getSearchAndReplaceWidget() {
        if (searchAndReplaceWidget == null) {
            searchAndReplaceWidget = new SearchAndReplaceWidget(this, (ObservationTable) getTable(), toolBelt);
        }

        return searchAndReplaceWidget;
    }

    /**
     * @return
     */
    public JXObservationTable getTable() {
        if (table == null) {
            table = new JXObservationTable();
        }

        return table;
    }

    private JPanel getToolBar() {
        if (toolPanel == null) {
            toolPanel = new JPanel(new BorderLayout());
            final JButton refreshButton = new JButton("Refresh Table");
            refreshButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    populateTable();
                }

            });
            toolPanel.add(getSearchAndReplaceWidget(), BorderLayout.CENTER);
            final JPanel bottomPanel = new JPanel(new FlowLayout());
            bottomPanel.add(refreshButton);

            //bottomPanel.add(getMergeCameraLogButton());
            bottomPanel.add(getMoveVideoFramesButton());
            toolPanel.add(bottomPanel, BorderLayout.SOUTH);
        }

        return toolPanel;
    }

    private void populateTable() {

        /*
         * all observations that get added to the table, or that are currently
         * in the table get added to this set and removed from the
         * observationsInTable set. Then, observations left in the
         * observationsInTable set are removed from the table and the
         * observationsInTable variable is set to reference the new
         * observationsStillInTable set. (this is the most efficient thing I
         * could think of).
         */
        JXObservationTable myTable = getTable();
        ((ObservationTableModel) myTable.getModel()).clear();

        if (videoArchiveSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving all video frames for " + videoArchiveSet);
            }

            final Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>(videoArchiveSet.getVideoFrames());
            for (final Iterator<VideoFrame> i = videoFrames.iterator(); i.hasNext(); ) {
                final VideoFrame videoFrame = (VideoFrame) i.next();
                final Collection<Observation> observations = videoFrame.getObservations();
                for (final Iterator<Observation> j = observations.iterator(); j.hasNext(); ) {
                    final Observation observation = (Observation) j.next();
                    myTable.addObservation(observation);
                }

            }
        }

        getSearchAndReplaceWidget().updateContentsOfSearchConceptNameComboBox(null);
    }

    /**
     * Remove a listener from property change notification.
     *
     * @param l The listener to be removed.
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    private void setVideoArchive(final VideoArchive va) {
        if (va == null) {
            return;
        }

        videoArchiveSet = va.getVideoArchiveSet();

        if (videoArchiveSet != null) {
            setTitle("Video Archive Set: " + videoArchiveSet.getStartDate() + " -- " + videoArchiveSet.getEndDate());
        }
        else {
            setTitle("No Video Archive Set is Available");
        }

        populateTable();

        // the two arguments are dummies, used to make sure the
        // propertyChangeEvent always gets propagated.
        changes.firePropertyChange(new PropertyChangeEvent(this, "tableChange", Integer.valueOf(1),
                Integer.valueOf(2)));
    }

    class ChangeCameraDirectionAction extends ActionAdapter {

        /**
         * Constructs ...
         */
        public ChangeCameraDirectionAction() {
            super("Change Camera Direction");
        }

        /**
         * Description of the Method
         */
        public void doAction() {
            final CameraDirections[] directions = CameraDirections.values();
            final String selectedValue = (String) JOptionPane.showInputDialog(VideoSetViewer.this,
                "Select a camera direction.", "VARS - Change Camera Direction", JOptionPane.QUESTION_MESSAGE, null,
                directions, directions[0]);
            if (selectedValue != null) {
                final JXObservationTable obsTable = getTable();
                final int[] rows = obsTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    final Observation obs = obsTable.getObservationAt(rows[i]);
                    final VideoFrame vf = obs.getVideoFrame();
                    if (vf != null) {
                        CameraData cd = vf.getCameraData();
                        final String orginalDirection = cd.getDirection();
                        cd.setDirection(selectedValue);

                        try {
                            toolBelt.getPersistenceController().updateVideoFrames(new ArrayList<VideoFrame>() {

                                {
                                    add(vf);
                                }

                            });
                        }
                        catch (final Exception e1) {
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                        }
                    }

                    searchAndReplaceWidget.redrawSelectedRows();
                }
            }
        }
    }


    /**
     * Action that moves {@link VideoFrame} from their respective parent {@link VideoArchive}
     * to a new one. MOst of the work is done with MoveVidoeFrameWithDialogAction
     * This class just wraps that action with a method to pass in the selected video
     * frames.
     */
    private class MoveAction extends MoveVideoFrameWithDialogAction {

        MoveAction() {
            super(VideoSetViewer.this, toolBelt);
        }

        /**
         */
        @Override
        public void doAction() {

            /*
             * Get the selected VideoFrames and set them.
             */
            final JXObservationTable obsTable = getTable();
            final Collection<VideoFrame> frames = new ArrayList<VideoFrame>();
            final int[] rows = obsTable.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                final Observation obs = obsTable.getObservationAt(rows[i]);
                if (obs != null) {
                    frames.add(obs.getVideoFrame());
                }
            }

            setVideoFrames(frames);

            /*
             * This call moves the frames and persists the changes.
             */
            super.doAction();

            /*
             * Update the display in the annotation editor. First we need to save the
             * last observation to make sure all changes to it are persisted. Then we
             * need to reopen the VideoArchive so that any changes to it are
             * propagated to the table model. (i.e so the view matches the data)
             */
            Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
            try {
                toolBelt.getPersistenceController().updateObservations(observations);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            }

            /*
             * Setting the VideoArchive in the VideoArchiveDispatcher will cause both
             * the VideoSetViewer and annotation app to redraw their tables
             */
            Dispatcher dispatcher = Lookup.getVideoArchiveDispatcher();
            final VideoArchive videoArchive = (VideoArchive) dispatcher.getValueObject();
            dispatcher.setValueObject(null);
            dispatcher.setValueObject(videoArchive);

        }
    }
}
