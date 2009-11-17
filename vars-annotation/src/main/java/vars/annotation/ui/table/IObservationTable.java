package vars.annotation.ui.table;

import javax.swing.JTable;

import vars.annotation.Observation;
import vars.annotation.VideoArchive;

public interface IObservationTable {

    /**
     * Populates the table with the observations available in videoArchive. I'm
     * using this a s a work around for a bug that I can't nail down when you
     * delete certain observations.
     * 
     * @param videoArchive
     */
    void populateWithObservations(final VideoArchive videoArchive);

    /**
     * Delegate method that passes the call on to the ObservationTableModel
     * 
     * @param observation
     */
    void addObservation(final Observation observation);

    /**
     * Delegate method that passes the call on to the ObservationTableModel
     * 
     * @param observation
     */
    void removeObservation(final Observation observation);

    /**
     * @param obs
     */
    void setSelectedObservation(final Observation obs);

    /**
     *  Delegate method that passes the call on to the ObservationTableModel
     *
     * @param  row Description of the Parameter
     * @return  The observationAt value
     */
    Observation getObservationAt(final int row);

    /**
     * Delegate method that forces the model to redraw the table.
     */
    void redrawAll();

    /**
     * Delegate method to force the model to redraw the specified row
     *
     * @param  row The row to redraw
     */
    void redrawRow(final int row);

    /**
     * Gets the preferredRowHeight attribute of the ObservationTable object
     * 
     * @param rowIndex
     *            Description of the Parameter
     * @param margin
     *            Description of the Parameter
     * @return The preferredRowHeight value
     */
    int getPreferredRowHeight(final int rowIndex, final int margin);

    void scrollToVisible(final int rowIndex, final int vColIndex);
    
    JTable getJTable();

}