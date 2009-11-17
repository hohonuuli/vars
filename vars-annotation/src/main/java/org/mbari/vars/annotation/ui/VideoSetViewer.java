/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui;

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
import javax.swing.table.JTableHeader;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.model.CameraData;
import org.mbari.vars.annotation.model.dao.VideoArchiveDAO;
import org.mbari.vars.annotation.ui.actions.MoveVideoFrameWithDialogAction;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationColumnModel;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.annotation.ui.table.ObservationTableModel;
import org.mbari.vars.annotation.ui.table.SearchAndReplaceWidget;
import org.mbari.vars.annotation.ui.table.TableSorter;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ICameraData;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoArchiveSet;

/**
 * <p>
 * Frame that is used for editing annotations for an entire VideoArchiveSet. It
 * provides search and replace capabilites, but not the fine editing control
 * that one gets from the AnnotationApp
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 * @version $Id: VideoSetViewer.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class VideoSetViewer extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -4001020589826092183L;
    private static final Logger log = LoggerFactory.getLogger(VideoSetViewer.class);

    /**
     *     @uml.property  name="changes"
     */
    private final PropertyChangeSupport changes;

    /**
     *     @uml.property  name="moveVideoFramesButton"
     *     @uml.associationEnd
     */
    private JButton moveVideoFramesButton;

    /**
     *     @uml.property  name="myMenuBar"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private JMenuBar myMenuBar;

    /**
     *     @uml.property  name="searchAndReplaceWidget"
     *     @uml.associationEnd  inverse="videoSetViewer:org.mbari.vars.annotation.ui.table.SearchAndReplaceWidget"
     */
    private SearchAndReplaceWidget searchAndReplaceWidget = null;

    /**
     *     @uml.property  name="table"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private ObservationTable table;

    /**
     *     @uml.property  name="toolPanel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private JPanel toolPanel;

    /**
     *     @uml.property  name="videoArchiveSet"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private IVideoArchiveSet videoArchiveSet;

    /**
     * Constructor for the VideoSetViewer object
     */
    public VideoSetViewer() {
        changes = new PropertyChangeSupport(this);
        final VideoArchiveDispatcher vad = VideoArchiveDispatcher.getInstance();
        vad.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                setVideoArchive((IVideoArchive) evt.getNewValue());
            }
        });

        // Need to grab the dispatcher when constructing a new instance
        setVideoArchive(VideoArchiveDispatcher.getInstance().getVideoArchive());
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
     *
     * @param l
     *            The listener which will receive <tt>PropertyChangeEvent</tt>
     *            s
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
     * @param propertyName
     *            The name of the property to receive change events on.
     * @param l
     *            The listener which will receive <tt>PropertyChangeEvent</tt>
     *            s
     */
    @Override
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */

//  private JButton getMergeCameraLogButton() {
//      if (mergeCameraLogButton != null) {
//          return mergeCameraLogButton;
//      }
//
//      mergeCameraLogButton = new JButton(new MergeCameraLogAction());
//      return mergeCameraLogButton;
//  }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="moveVideoFramesButton"
     */
    private JButton getMoveVideoFramesButton() {
        if (moveVideoFramesButton == null) {
            moveVideoFramesButton = new JButton(new MoveAction());
        }

        return moveVideoFramesButton;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="myMenuBar"
     */
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

    /**
     *     @return
     *     @uml.property  name="searchAndReplaceWidget"
     */
    private SearchAndReplaceWidget getSearchAndReplaceWidget() {
        if (searchAndReplaceWidget == null) {
            searchAndReplaceWidget = new SearchAndReplaceWidget(this, getTable());
        }

        return searchAndReplaceWidget;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="table"
     */
    public ObservationTable getTable() {
        if (table == null) {
            final TableSorter sorter = new TableSorter();
            table = new ObservationTable(sorter, (ObservationColumnModel) ObservationColumnModel.getInstance());
            table.setTableHeader(new JTableHeader(ObservationColumnModel.getInstance()));

            // Set up tool tips for column headers.
            sorter.setTableHeader(table.getTableHeader());
            table.getTableHeader().setToolTipText(
                "Click to specify sorting; Control-Click to specify secondary sorting");
        }

        return table;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    private JPanel getToolBar() {
        if (toolPanel == null) {
            toolPanel = new JPanel(new BorderLayout());
            final JButton refreshButton = new JButton("Refresh Table");
            refreshButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent ae) {
                    populateTable();

                    /*
                     * SwingUtilities.invokeLater( new Runnable() {
                     *
                     * public void run() { populateTable(); } });
                     */
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

    /**
     * <p><!-- Method description --></p>
     *
     */
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
        final ObservationTable obsTable = getTable();
        ((ObservationTableModel) obsTable.getModel()).clear();

        if (videoArchiveSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving all video frames for " + videoArchiveSet);
            }

            final Collection videoFrames = videoArchiveSet.getVideoFrames();
            for (final Iterator i = videoFrames.iterator(); i.hasNext(); ) {
                final IVideoFrame videoFrame = (IVideoFrame) i.next();
                final Collection observations = videoFrame.getObservations();
                synchronized (observations) {
                    for (final Iterator j = observations.iterator(); j.hasNext(); ) {
                        final IObservation observation = (IObservation) j.next();
                        obsTable.addObservation(observation);
                    }
                }
            }
        }

        getSearchAndReplaceWidget().updateContentsOfSearchConceptNameComboBox(null);
    }

    /**
     * Remove a listener from property change notification.
     *
     *
     * @param l
     *            The listener to be removed.
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param va
     */
    private void setVideoArchive(final IVideoArchive va) {
        if (va == null) {

            // TODO what to do?
            return;
        }

        videoArchiveSet = va.getVideoArchiveSet();

        // TODO what is the identifier for a videoArchiveSet -- I'm using
        // tracking number
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
         *
         */
        private static final long serialVersionUID = -8381419005332235806L;

        /**
         */
        public ChangeCameraDirectionAction() {
            super("Change Camera Direction");
        }

        /**
         * Description of the Method
         */
        public void doAction() {
            final String[] directions = ICameraData.DIRECTIONS;
            final String selectedValue = (String) JOptionPane.showInputDialog(VideoSetViewer.this,
                                             "Select a camera direction.", "VARS - Change Camera Direction",
                                             JOptionPane.QUESTION_MESSAGE, null, directions, directions[0]);
            if (selectedValue != null) {
                final ObservationTable obsTable = getTable();
                final int[] rows = obsTable.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    final IObservation obs = obsTable.getObservationAt(rows[i]);
                    final IVideoFrame vf = obs.getVideoFrame();
                    if (vf != null) {
                        ICameraData cd = vf.getCameraData();
                        if (cd == null) {
                            cd = new CameraData();
                            vf.setCameraData(cd);
                        }

                        final String orginalDirection = cd.getDirection();
                        cd.setDirection(selectedValue);

                        try {
                            DAOEventQueue.updateVideoArchiveSet((IDataObject) vf);
                        }
                        catch (final Exception e1) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update a videoFrame", e1);
                            }

                            cd.setDirection(orginalDirection);
                        }
                    }

                    searchAndReplaceWidget.redrawSelectedRows();
                }
            }
        }
    }

    /**
     * Action that moves videoframe from their respective parent VideoArchives
     * to a new one. MOst of the work is done with MoveVidoeFrameWithDialogAction
     * This class just wraps that action with a method to pass in the selected video
     * frames.
     */
    private class MoveAction extends MoveVideoFrameWithDialogAction {

        /**
         *
         */
        private static final long serialVersionUID = 5100323856709680084L;

        /**
         * Constructs ...
         *
         */
        MoveAction() {
            super(VideoSetViewer.this);
        }

        /**
         * <p><!-- Method description --></p>
         *
         */
        @Override
        public void doAction() {

            /*
             * Get the selected VideoFrames and set them.
             */
            final ObservationTable obsTable = getTable();
            final Collection frames = new ArrayList();
            final int[] rows = obsTable.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                final IObservation obs = obsTable.getObservationAt(rows[i]);
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
             * need to reopen the VideoArchive so that anychanges to it are
             * propagated to the table model. (i.e so the view matches the data)
             */
            final IObservation observation = ObservationDispatcher.getInstance().getObservation();
            if (observation != null) {
                try {
                    DAOEventQueue.updateVideoArchiveSet((IDataObject) observation);
                }
                catch (final Exception e) {
                    final String msg = "Failed to update the last modified observation:\n '" + observation + "'";
                    AppFrameDispatcher.showErrorDialog(msg);
                }
            }

            /*
             * Setting the VideoArchive in the VideoARchiveDispatcher will cause both
             * the VideoSetViewer and annotaiton app to redraw thier tables
             */
            final IVideoArchive videoArchive = VideoArchiveDispatcher.getInstance().getVideoArchive();
            IVideoArchive va = null;
            try {
                va = VideoArchiveDAO.getInstance().findByVideoArchiveName(videoArchive.getVideoArchiveName());
                VideoArchiveDispatcher.getInstance().setVideoArchive(va);
            }
            catch (final DAOException e) {
                final String msg = "Problem with database. Reopen the VideoArchive!";
                AppFrameDispatcher.showErrorDialog(msg);
            }
        }
    }
}
