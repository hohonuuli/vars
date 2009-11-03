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
package org.mbari.vars.annotation.ui.table;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.ObservationTable;
import org.mbari.vars.annotation.ui.NewVideoFrameButton;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PreferencesDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;

/**
 * <p>
 * A Table which can be sorted by clicking on the heaer of any row. Also
 * supports independently sizeable rows. This class also has hooks to store
 * preferences in the database. The various CellRenderers and CellEditors are
 * set in this class
 * </p>
 *
 * <h2><u>UML </u></h2>
 *
 * <pre>
 *  [ObservationTableDispatcher]
 *     |
 *     |
 *   1 V                1
 *  [ObservationTable]-->[ObservationTableModel]
 *     |1
 *     |
 *     |1
 *  [VideoArchiveDispatcher]
 *
 * </pre>
 *
 * @author  <a href="http://www.mbari.org">MBARI </a>
 * @version  $Id: ObservationTable.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @stereotype  thing
 */
public class ObservationTable extends JTable implements IObserver, IObservationTable {

    /**
     *
     */
    private static final long serialVersionUID = 1004644909970584936L;
    private static final Logger log = LoggerFactory.getLogger(ObservationTable.class);
    private static final String PREFS_COLUMN_WIDTH = " column width";

    /**
     *     @uml.property  name="policy"
     */
    FocusTraversalPolicy policy;

    /**
     * @param  model A TableSorter
     * @param  columnModel The column model to use.
     */
    public ObservationTable(final TableSorter model, final ObservationColumnModel columnModel) {
        super(model, columnModel);

        // policy = new TableFocusTraversalPolicy(this);
        // setFocusTraversalPolicy(policy);
        // Create the table
        setAutoCreateColumnsFromModel(false);
        setRowHeight(23);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);

        /*
         *  TODO 20040518 brian: We want the default view to sort by timecode
         *  this works unless the column order was changed and then reloaded
         *  from the database preferences. How do we find the timecode
         *  column then?
         */
        if (model != null) {
            model.setSortingStatus(0, TableSorter.ASCENDING);
        }

        // Register to listen to changes in the VideoArchive
        VideoArchiveDispatcher.getInstance().addObserver(this);

        // Listen for changes in the Preferences
        PreferencesDispatcher.getInstance().addObserver(new IObserver() {

                    public void update(final Object theObervered, final Object changeCode) {
                        loadPreferences();
                    }
                });
        this.addComponentListener(new PreferencesAdapter());
        setEnterBehavior();
        setTabToNextCellBehavior();

    // addMouseListener(new PopupListener());
    }

    /**
     * Delegate method that passes the call on to the ObservationTableModel
     *
     * @param  observation
     */
    public void addObservation(final IObservation observation) {
        ((IObservationTableModel) getModel()).addObservation(observation);
        ((TableSorter) getModel()).clearSortingState();

        if (log.isDebugEnabled()) {
            log.debug("Adding " + observation + " to the table model");
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void enableObservationDispatcherNotification() {

        // Add a Listener that updates the CurrentObservatonDispatcher
        getSelectionModel().addListSelectionListener(new ObservationListener());
    }

    /**
     *  Delegate method that passes the call on to the ObservationTableModel
     *
     * @param  row Description of the Parameter
     * @return  The observationAt value
     */
    public IObservation getObservationAt(final int row) {
        final IObservationTableModel model = (IObservationTableModel) getModel();

        return model.getObservationAt(row);
    }

    /**
     *  Gets the preferredRowHeight attribute of the ObservationTable object
     *
     * @param  rowIndex Description of the Parameter
     * @param  margin Description of the Parameter
     * @return  The preferredRowHeight value
     */
    public int getPreferredRowHeight(final int rowIndex, final int margin) {

        // Get the current default height for all rows
        int height = getRowHeight();

        // Determine highest cell in the row
        for (int c = 0; c < getColumnCount(); c++) {
            final TableCellRenderer renderer = getCellRenderer(rowIndex, c);
            final Component comp = prepareRenderer(renderer, rowIndex, c);
            final int h = comp.getPreferredSize().height + 2 * margin;
            height = Math.max(height, h);
        }

        return height;
    }

    @Override
    public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
        TableCellRenderer renderer = super.getDefaultRenderer(columnClass);
        if (renderer == null) {
            renderer = super.getDefaultRenderer(Object.class);
        }
        return renderer;
    }



    /**
     * Delegate method that forces the model to redraw the table.
     */
    public void redrawAll() {
        ((IObservationTableModel) getModel()).redrawAll();
    }

    /**
     * Delegate method to force the model to redraw the specified row
     *
     * @param  row The row to redraw
     */
    public void redrawRow(final int row) {
        ((IObservationTableModel) getModel()).redrawRow(row);
    }

    /**
     * Delegate method that passes the call on to the ObservationTableModel
     *
     * @param  observation
     */
    public void removeObservation(final IObservation observation) {
        ((IObservationTableModel) getModel()).removeObservation(observation);
    }

    /**
     *  Saves the preferences to the database.
     */
    public void savePreferences() {
        String nodeName = this.dataModel.toString();
        nodeName = nodeName.substring(0, nodeName.indexOf('@'));
        nodeName = nodeName.replaceAll("\\.", "/");
        Preferences preferences = PreferencesDispatcher.getInstance().getPreferences();
        if (preferences != null) {
            preferences = preferences.node(nodeName);

            if (preferences != null) {
                final Enumeration colEnum = super.getColumnModel().getColumns();
                int index = 0;
                while (colEnum.hasMoreElements()) {
                    final TableColumn currTableColumn = (TableColumn) colEnum.nextElement();
                    preferences.putInt(currTableColumn.getIdentifier() + PREFS_COLUMN_WIDTH, currTableColumn.getWidth());
                    index++;
                }
            }
        }
    }

    /**
     *  Scrolls to the currently selected rows
     *
     * @param  rowIndex Description of the Parameter
     * @param  vColIndex Description of the Parameter
     */
    public void scrollToVisible(final int rowIndex, final int vColIndex) {
        if (!(getParent() instanceof JViewport)) {
            return;
        }

        final JViewport viewport = (JViewport) getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        final Rectangle rect = getCellRect(rowIndex, vColIndex, true);

        // The location of the viewport relative to the table
        final Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);

        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

    /**
     * Set the behavior of the "enter" key when the user is working in the
     * table
     */
    private void setEnterBehavior() {
        this.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        this.getActionMap().put("enter", new NewVideoFrameButton().getAction());
    }

    /**
     *  Loads the preferences attribute of the ObservationTable object
     */
    public void loadPreferences() {

        /*
         * A Preference is reference via a 'nodename' and a 'prefkey'. The 
         * nodename here will be /[username]/path/of/datamodel/class/name for
         * example '/brian/org/mbari/vars/annotation/ui/table/TableSorter'. Here
         * the we've already gotten all nodes starting with brian so the node
         * will be org/mbari/vars/annotation/ui/table/TableSorter
         * 
         * 
         * Basically all we're doing right now is preserving the users 
         * column widths
         */
        String nodeName = this.dataModel.toString();
        nodeName = nodeName.substring(0, nodeName.indexOf('@'));
        nodeName = nodeName.replaceAll("\\.", "/");

        // Preference that only apply to the node named %org/mbari/vars/annotation/ui/table/TableSorter
        final Preferences preferences = PreferencesDispatcher.getInstance().getPreferences().node(nodeName);

        for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {

            // First check to see if the current model matches preferences
            final TableColumn tableColumn = this.getColumnModel().getColumn(i);
            final Object identifier = tableColumn.getIdentifier();
            int columnWidth = preferences.getInt(identifier + PREFS_COLUMN_WIDTH, 0);
            if (columnWidth == 0) {
                final String headerValue = tableColumn.getHeaderValue().toString();
                final int headerLength = headerValue.length();
                if (headerLength < 5) {
                    columnWidth = 50;
                }
                else {
                    columnWidth = headerLength * 10;
                }

            }
            tableColumn.setPreferredWidth(columnWidth);
        }
    }

    /**
     * @param  obs
     */
    public void setSelectedObservation(final IObservation obs) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the observation selected in the table to " + obs);
        }

        ((TableSorter) getModel()).clearSortingState();
        final int row = ((TableSorter) getModel()).getObservationRow(obs);
        getSelectionModel().setSelectionInterval(row, row);
        scrollToVisible(row, 0);
    }

    /**
     * On tab to the next cell, set the focus to the component within the cell.
     */
    private void setTabToNextCellBehavior() {
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_TAB) {
                    ObservationTable.this.editCellAt(ObservationTable.this.getSelectedRow(),
                            ObservationTable.this.getSelectedColumn());
                }
            }
        });
    }

    /**
     * Populates the table with the observations available in videoArchive.
     * I'm using this a s a work around for a bug that I can't nail down when
     * you delete certain observations.
     *
     * @param  videoArchive
     * @deprecated Use populateWithObservations instead
     */
    public void setVideoArchive(final IVideoArchive videoArchive) {

        populateWithObservations(videoArchive);
    }
    
    public void populateWithObservations(IVideoArchive videoArchive) {
//      Get the TableModel
        final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
        final ObservationTableModel model = (ObservationTableModel) table.getModel();

        // Remove the current contents
        model.clear();

        // Repopulate it with the contents of the new VideoArchive
        if (videoArchive != null) {
            final IVideoArchive va = videoArchive;

            /*
             * This collection copy to a HashSet is a workaround to an obscure
             * bug that manifested at CSIRO. It may be needed any more but
             * leaving it in doesn't break anything.
             */
            final Collection vfs = new HashSet(va.getVideoFrames());

            synchronized (vfs) {
                for (final Iterator i = vfs.iterator(); i.hasNext();) {
                    final IVideoFrame vf = (IVideoFrame) i.next();
                    final Collection obs = vf.getObservations();
                    synchronized (obs) {
                        for (final Iterator j = obs.iterator(); j.hasNext();) {
                            final IObservation ob = (IObservation) j.next();
                            model.addObservation(ob);
                        }
                    }
                }
            }
        }
        
    }

    /**
     * This is not called be the developer. THis shouldbe called by the
     * VideoArchiveDispatcher only.
     *
     * @param  videoArchive Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see  org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object videoArchive, final Object changeCode) {
        setVideoArchive((IVideoArchive) videoArchive);
    }

    /**
     *     <p> Listens for changes in which rows are selected in the table. If 0 or many rows are selected it sets the current Observation to null. If 1 observation is selected its set in the ObservationDispatcher </p>
     */
    private class ObservationListener implements ListSelectionListener {

        //private final ObservationDispatcher cod = ObservationDispatcher.getInstance();

        private final Dispatcher dispatcher = PredefinedDispatcher.OBSERVATION.getDispatcher();

        /**
         *  Description of the Method
         *
         * @param  e Description of the Parameter
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(final ListSelectionEvent e) {
            final int[] selectedRows = getSelectedRows();
            if (selectedRows.length == 1) {

                //cod.setObservation(getObservationAt(selectedRows[0]));
                dispatcher.setValueObject(getObservationAt(selectedRows[0]));
            }
            else {

                //cod.setObservation(null);
                dispatcher.setValueObject(null);
            }
        }
    }

    /**
     * Saves changes to the table to the database via the preferences API.
     * Preferences are not immediately saved. There is a delay (RESPONSE_DELAY)
     * where it waits for changes to the table to stop for 10 seconds before
     * commiting to the database.
     *
     * The reason for such a long delay is that the only changes that are
     * persisted to the database are the width of the columns and the column
     * order. However, adding a row sets off a bunch of preference changes which
     * we normally don't care about.
     *
     * @author brian
     *
     */
    private class PreferencesAdapter extends ComponentAdapter {

        final int RESPONSE_DELAY = 10000;
        final Timer delayTimer;

        /**
         * Constructs ...
         *
         */
        PreferencesAdapter() {

            /*
             * We're adding a slight delay here so that db lookups don't try
             * happen too often.
             */

            // This action occurs when the timer fires.
            final ActionListener changeItemAction = new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            savePreferences();
                        }
                    };

            // The timer with a delay and bound to above action.
            delayTimer = new Timer(RESPONSE_DELAY, changeItemAction);
            delayTimer.setRepeats(false);
        }

        /**
         * Method description
         *
         *
         * @param e
         */
        public void componentResized(final ComponentEvent e) {
            delayTimer.restart();
        }
    }

    
}
