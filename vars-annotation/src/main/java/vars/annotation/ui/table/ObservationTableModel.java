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


/*
Created on Oct 22, 2003
 */
package vars.annotation.ui.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;

/**
 * <p><!--Insert summary here--></p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ObservationTableModel.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @stereotype  thing
 */
public class ObservationTableModel extends AbstractTableModel implements IObservationTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 7137432907510247220L;
    private static final Logger log = LoggerFactory.getLogger(ObservationTableModel.class);

    /**
     *     A list of the observations to be rendered in the table
     *     @uml.property  name="observations"
     *     @uml.associationEnd  multiplicity="(0 -1)" elementType="org.mbari.vars.annotation.model.Observation"
     */
    private final List observations = new ArrayList();

    /**
     *     @uml.property  name="tableColumnModel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private TableColumnModel tableColumnModel;

    /**
     * Constructor
     */
    public ObservationTableModel() {
        super();
        this.tableColumnModel = ObservationColumnModel.getInstance();
    }
    
    public ObservationTableModel(TableColumnModel tableColumnModel) {
        super();
        this.tableColumnModel = tableColumnModel;
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#addObservation(vars.annotation.IObservation)
     */
    public void addObservation(final Observation obs) {
        if ((obs != null) && (obs.getVideoFrame() != null)) {
            observations.add(obs);

            /*
             * This property change listener redraws the row of the table when
             * an association is added or removed from an observation.
             */
            obs.addPropertyChangeListener(Association.PROP_ASSOCIATIONS, new PropertyChangeListener() {

                public void propertyChange(final PropertyChangeEvent e) {
                    if (e.getNewValue() == null) {

                        // do nothing if the new value is null, if the table tries to update
                        // when the new value is null a null pointer exception is thrown.
                        // I am assuming, since new value is null, something else is probably
                        // going to happen soon (like the observation being removed)
                        return;
                    }

                    /*
                     * The try-catch below is to address JIRA issue VARS-163
                     */ 
                    try {
                        final int row = ObservationTableModel.this.getObservationRow(obs);
                        if (row == -1) {
                            log.warn("Attempted to redraw an Observation that can no longer be found in the UI VIew");
                            // the observation is no longer in the table, move along
                            //return;
                        }
                        else {
                            ObservationTableModel.this.redrawRow(row);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e1) {
                        log.warn("Unable to find index of " + obs + " in table, redrawing entire view", e1);
                        ObservationTableModel.this.redrawAll();
                    }
                    
                    
                }

            });

            /*
             * This property change listener redraws the row of the table if the
             * conceptName of the observation changes.
             */
            obs.addPropertyChangeListener("conceptName", new PropertyChangeListener() {

                public void propertyChange(final PropertyChangeEvent e) {
                    final String oldName = (String) e.getOldValue();
                    final String newName = (String) e.getNewValue();
                    if ((oldName == null) || (newName == null) ||!oldName.equals(newName)) {
                        final int row = ObservationTableModel.this.getObservationRow(obs);
                        if (row != -1) {
                            ObservationTableModel.this.redrawRow(row);
                        }
                    }
                }

            });
            final int index = observations.indexOf(obs);
            fireTableRowsInserted(index, index);
        }
        else {
            if (log.isWarnEnabled()) {
                log.warn("Attempted to add an Observation without a parent " +
                         "VideoFrame to the data model. This is not allowed.");
            }
        }
    }

    /**
     * remove all data from the table
     */
    public void clear() {
        observations.clear();
        redrawAll();
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#findColumn(java.lang.String)
     */
    @Override
    public int findColumn(final String id) {
        return ((ObservationColumnModel) getTableColumnModel()).findColumn(id);
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getColumnClass(int)
     */
    @Override
    public Class getColumnClass(final int columnIndex) {
        return ((ValueColumn) tableColumnModel.getColumn(columnIndex)).getColumnClass();
    }

    /*
     *  (non-Javadoc)
     *  @see javax.swing.table.TableModel#getColumnCount()
     */

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getColumnCount()
     */
    public int getColumnCount() {
        return tableColumnModel.getColumnCount();
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getNumberOfObservations()
     */
    public int getNumberOfObservations() {
        if (observations == null) {
            return 0;
        }

        return observations.size();
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getObservationAt(int)
     */
    public Observation getObservationAt(final int rowIndex) {
        Observation out = null;
        if (rowIndex < observations.size()) {
            out = (Observation) observations.get(rowIndex);
        }

        return out;
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getObservationRow(vars.annotation.IObservation)
     */
    public int getObservationRow(final Observation observation) {
        int row = -1;
        if (observation != null) {
            row = observations.indexOf(observation);
        }

        if (log.isDebugEnabled()) {
            log.debug("Found observation, " + observation + ", at row = " + row);
        }

        return row;
    }

    /*
     *  (non-Javadoc)
     *  @see javax.swing.table.TableModel#getRowCount()
     */

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#getRowCount()
     */
    public int getRowCount() {
        return getNumberOfObservations();
    }

    /**
     *     Gets the tableColumnModel attribute of the ObservationTableModel object
     *     @return   The tableColumnModel value
     *     @uml.property  name="tableColumnModel"
     */
    public TableColumnModel getTableColumnModel() {
        return tableColumnModel;
    }

    /*
     *  (non-Javadoc)
     *  @see javax.swing.table.TableModel#getValueAt(int, int)
     */

    /**
     *  Gets the valueAt attribute of the ObservationTableModel object
     *
     * @param  rowIndex Description of the Parameter
     * @param  columnIndex Description of the Parameter
     * @return  The valueAt value
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Object out = null;
        if (rowIndex < observations.size()) {
            final Observation obs = (Observation) observations.get(rowIndex);

            /*
             *  Since the columns can be reordered we can't rely on the columnIndex
             *  to retrieve the correctcolumn. So we use the name of the column
             *  instead.
             */
            final TableColumn tc = tableColumnModel.getColumn(columnIndex);

            // log.info("Column = " + columnIndex + ": TableColumn = " + tc.getClass().getName());
            if (tc instanceof ValueColumn) {
                out = ((ValueColumn) tc).getValue(obs);
            }
        }
        else {
            if (log.isWarnEnabled()) {
                log.warn("Attempted to access an empty row in the TableModel.");
            }
        }

        return out;
    }

    /**
     *  Gets the cellEditable attribute of the ObservationTableModel object
     *
     * @param  rowIndex Description of the Parameter
     * @param  columnIndex Description of the Parameter
     * @return  The cellEditable value
     */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        final TableColumn tc = tableColumnModel.getColumn(columnIndex);
        boolean out = false;
        if (tc instanceof ValueColumn) {
            out = ((ValueColumn) tc).isCellEditable();
        }

        return out;
    }

    /**
     *  Gets the sortable attribute of the ObservationTableModel object
     *
     * @param  col Description of the Parameter
     * @return  The sortable value
     */
    public boolean isSortable(final int col) {
        final TableColumn tc = tableColumnModel.getColumn(col);
        boolean out = false;
        if (tc instanceof ValueColumn) {
            out = ((ValueColumn) tc).isSortable();
        }

        return out;
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#redrawAll()
     */
    public void redrawAll() {
        fireTableDataChanged();
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#redrawRow(int)
     */
    public void redrawRow(final int row) {
        fireTableRowsUpdated(row, row);

        // fireTableChanged(new TableModelEvent(this));
    }

    /* (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.table.IObservationTableModel#removeObservation(vars.annotation.IObservation)
     */
    public void removeObservation(final Observation obs) {
        if (obs.getVideoFrame() != null) {
            if (log.isWarnEnabled()) {
                log.warn("You have requested to remove an observation from " +
                         "the view that still has a videoFrame. This operation " +
                         "is not allowed.\nWill search for a matching observation" +
                         " that does not have a parent VideoFrame and remove that instead.");
            }
        }

        final int index = observations.indexOf(obs);
        if (index >= 0) {

            /*
             *  HACK ALERT!! Sometimes the wrong observation gets removed from
             *  the view, something to do with the equals method of Observation.
             *  As a workaround, if we can't unamigously determine which row
             *  to redraw we reset the entire videoarchive.
             */
            final Observation deadObs = (Observation) observations.get(index);
            if (deadObs.getVideoFrame() != null) {
                if (log.isWarnEnabled()) {
                    log.warn("Oops, we've got trouble locating the correct " + "observation to remove." +
                             ". Just to be safe I'll redraw the entire table.");
                }

                final ObservationTable table = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
                final VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
                table.setVideoArchive(va);
            }
            else {
                observations.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }
    }

    /**
     *  Sets the valueAt attribute of the ObservationTableModel object
     *
     * @param  aValue The new valueAt value
     * @param  rowIndex The new valueAt value
     * @param  columnIndex The new valueAt value
     */
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        if (rowIndex < observations.size()) {
            final Observation obs = (Observation) observations.get(rowIndex);
            final TableColumn tc = tableColumnModel.getColumn(columnIndex);
            if (tc instanceof ValueColumn) {
                ((ValueColumn) tc).setValue(obs, aValue);
            }
        }

        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
