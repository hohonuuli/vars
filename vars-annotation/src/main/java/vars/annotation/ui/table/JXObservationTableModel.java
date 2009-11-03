package org.mbari.vars.annotation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.mbari.vars.annotation.ui.table.IObservationTableModel;
import org.mbari.vars.annotation.ui.table.ValueColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.IAssociation;
import vars.annotation.IObservation;

public class JXObservationTableModel extends AbstractTableModel implements IObservationTableModel {
    
    private final List<IObservation> observations = Collections.synchronizedList(new ArrayList<IObservation>());
    private final Logger log = LoggerFactory.getLogger(JXObservationTableModel.class);
    private final TableColumnModel tableColumnModel;

    public JXObservationTableModel(TableColumnModel tableColumnModel) {
        this.tableColumnModel = tableColumnModel;
    }


    public void addObservation(final IObservation obs) {
        if ((obs != null) && (obs.getVideoFrame() != null)) {

            /**
             * Don't allow duplicate rows of the same observation in the table!!!!!
             */
            if (observations.contains(obs)) {
                return;
            }

            observations.add(obs);

            /*
             * This property change listener redraws the row of the table when
             * an association is added or removed from an observation.
             */
            obs.addPropertyChangeListener(IAssociation.PROP_ASSOCIATIONS, new AssociationListListener(obs));

            /*
             * This property change listener redraws the row of the table if the
             * conceptName of the observation changes.
             */
            obs.addPropertyChangeListener(IObservation.PROP_CONCEPTNAME, new ConceptNameListener(obs));

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

    public int getColumnCount() {
        return tableColumnModel.getColumnCount();
    }

    public int getNumberOfObservations() {
        return observations.size();
    }

    public IObservation getObservationAt(int rowIndex) {
        IObservation out = null;
        if (rowIndex < observations.size()) {
            out = (IObservation) observations.get(rowIndex);
        }
        return out;
    }

    public int getObservationRow(IObservation observation) {
        int row = -1;
        if (observation != null) {
            row = observations.indexOf(observation);
        }

        if (log.isDebugEnabled()) {
            log.debug("Found observation, " + observation + ", at row = " + row);
        }

        return row;
    }

    public int getRowCount() {
        return observations.size();
    }

    public void redrawAll() {
        fireTableDataChanged();
        
    }

    public void redrawRow(int row) {
        fireTableRowsUpdated(row, row);
        
    }

    public void removeObservation(IObservation obs) {

        final int index = observations.indexOf(obs);
        if (index >= 0) {
            observations.remove(index);

            // TODO remove property change listeners
            //IObservation observation = observations.get(index);
            //obser

            fireTableRowsDeleted(index, index);


        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object out = null;
        if (rowIndex < observations.size()) {
            final IObservation obs = (IObservation) observations.get(rowIndex);

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

    public void clear() {
        observations.clear();
        redrawAll();
    }

    /**
     * Listens for changes to the AssociationList and redraws the table if needed
     */
    private class AssociationListListener implements PropertyChangeListener {

        private final IObservation observation;

        AssociationListListener(final IObservation observation) {
            this.observation = observation;
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getNewValue() == null) {

                        /*
                 * Do nothing if the new value is null, if the table tries to update
                 * when the new value is null a null pointer exception is thrown.
                 * I am assuming, since new value is null, something else is probably
                 * going to happen soon (like the observation being removed)
                 */
                return;
            }

            /*
             * The try-catch below is to address JIRA issue VARS-163
             */
            try {
                final int row = JXObservationTableModel.this.getObservationRow(observation);
                if (row == -1) {
                    log.warn("Attempted to redraw an Observation that can no longer be found in the UI VIew");
                    // the observation is no longer in the table, move along
                }
                else {
                    redrawRow(row);
                }
            }
            catch (ArrayIndexOutOfBoundsException e1) {
                log.warn("Unable to find index of " + observation + " in table, redrawing entire view");
                redrawAll();
            }
        }

    }

    /**
     * Listens for changes to the ConceptName and redraws the table if needed
     */
    private class ConceptNameListener implements PropertyChangeListener {
        
        private final IObservation observation;

        ConceptNameListener(final IObservation observation) {
            this.observation = observation;
        }

        public void propertyChange(final PropertyChangeEvent e) {
            final String oldName = (String) e.getOldValue();
            final String newName = (String) e.getNewValue();
            if ((oldName == null) || (newName == null) || !oldName.equals(newName)) {
                final int row = JXObservationTableModel.this.getObservationRow(observation);
                if (row != -1) {
                    redrawRow(row);
                }
            }
        }
        
    }

}
