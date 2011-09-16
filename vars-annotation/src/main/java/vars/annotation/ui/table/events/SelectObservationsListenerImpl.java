package vars.annotation.ui.table.events;

import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.table.ObservationTable;
import vars.annotation.ui.table.ObservationTableModel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class SelectObservationsListenerImpl implements SelectObservationsListener {

    @Override
    public void doSelect(SelectObservationsEvent event) {
        Collection<Observation> obs = new ArrayList<Observation>(event.getObservations());
        ObservationTable table = (ObservationTable) Lookup.getObservationTableDispatcher().getValueObject();
        ObservationTableModel model = (ObservationTableModel) ((JTable) table).getModel();
        ListSelectionModel selectionModel = ((JTable) table).getSelectionModel();
        selectionModel.clearSelection();

        for (Observation observation : obs) {
            int row = ((JTable) table).convertRowIndexToView(model.getObservationRow(observation));
            selectionModel.addSelectionInterval(row, row);
        }
    }
}
