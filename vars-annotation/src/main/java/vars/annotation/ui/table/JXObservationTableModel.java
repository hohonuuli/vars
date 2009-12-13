/*
 * @(#)JXObservationTableModel.java   2009.12.12 at 10:11:31 PST
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



package vars.annotation.ui.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;

/**
 *
 *
 * @version        Enter version here..., 2009.12.12 at 10:11:31 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class JXObservationTableModel extends AbstractTableModel implements ObservationTableModel {

    /** Map using database PrimaryKey values as the key */
    private final List<Object> primaryKeys = new Vector<Object>();
    private final List<Observation> observations = new Vector<Observation>();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TableColumnModel tableColumnModel;

    /**
     * Constructs ...
     *
     * @param tableColumnModel
     */
    public JXObservationTableModel(TableColumnModel tableColumnModel) {
        this.tableColumnModel = tableColumnModel;
    }

    /**
     *
     * @param obs
     */
    public void addObservation(final Observation obs) {
        if ((obs != null) && (obs.getVideoFrame() != null)) {

            /**
             * Don't allow duplicate rows of the same observation in the table!!!!!
             */

            if ((obs.getPrimaryKey() == null) || primaryKeys.contains(obs.getPrimaryKey())) {
                log.debug("Observation does not have a primary key or is already in the TableModel");
                return;
            }

            primaryKeys.add(obs.getPrimaryKey());
            observations.add(obs);

            /*
             * This property change listener redraws the row of the table when
             * an association is added or removed from an observation.
             */
            obs.addPropertyChangeListener(Observation.PROP_ASSOCIATIONS, new AssociationListListener(obs));

            /*
             * This property change listener redraws the row of the table if the
             * conceptName of the observation changes.
             */
            obs.addPropertyChangeListener(Observation.PROP_CONCEPT_NAME, new ConceptNameListener(obs));

            final int index = primaryKeys.indexOf(obs.getPrimaryKey());
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
     */
    public void clear() {
        observations.clear();
        primaryKeys.clear();
        redrawAll();
    }

    /**
     * @return
     */
    public int getColumnCount() {
        return tableColumnModel.getColumnCount();
    }

    /**
     * @return
     */
    public int getNumberOfObservations() {
        return observations.size();
    }

    /**
     *
     * @param rowIndex
     * @return
     */
    public Observation getObservationAt(int rowIndex) {
        Observation out = null;
        if (rowIndex < observations.size()) {
            out = (Observation) observations.get(rowIndex);
        }

        return out;
    }

    /**
     *
     * @param observation
     * @return
     */
    public int getObservationRow(Observation observation) {
        int row = -1;
        if (observation != null) {
            row = primaryKeys.indexOf(observation.getPrimaryKey());
        }

        if (log.isDebugEnabled()) {
            log.debug("Found observation, " + observation + ", at row = " + row);
        }

        return row;
    }

    /**
     * @return
     */
    public int getRowCount() {
        return observations.size();
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object out = null;
        if (rowIndex < observations.size()) {
            final Observation obs = (Observation) observations.get(rowIndex);

            /*
             *  Since the columns can be reordered we can't rely on the columnIndex
             *  to retrieve the correct column. So we use the name of the column
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
     */
    public void redrawAll() {
        fireTableDataChanged();

    }

    /**
     *
     * @param row
     */
    public void redrawRow(int row) {
        fireTableRowsUpdated(row, row);

    }

    /**
     *
     * @param obs
     */
    public void removeObservation(Observation obs) {

        final int index = primaryKeys.indexOf(obs.getPrimaryKey());
        if (index >= 0) {

            // Remove propertychangelisteners
            Observation observation = observations.get(index);
            PropertyChangeListener[] pcl = observation.getPropertyChangeListeners();
            for (PropertyChangeListener listener : pcl) {
                if (listener instanceof AssociationListListener || listener instanceof ConceptNameListener) {
                    observation.removePropertyChangeListener(listener);
                }
            }
            observations.remove(index);
            primaryKeys.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }

    /**
     *
     * @param observation
     */
    public void updateObservation(Observation observation) {
        if ((observation != null) && (observation.getVideoFrame() != null)) {

            /**
             * Don't allow duplicate rows of the same observation in the table!!!!!
             */

            if ((observation.getPrimaryKey() == null) || !primaryKeys.contains(observation.getPrimaryKey())) {
                log.debug("Observation does not have a primary key or is not already in the TableModel");
                return;
            }

            int idx = primaryKeys.indexOf(observation.getPrimaryKey());
            observations.remove(idx);
            observations.add(idx, observation);

            /*
             * This property change listener redraws the row of the table when
             * an association is added or removed from an observation.
             */
            observation.addPropertyChangeListener(Observation.PROP_ASSOCIATIONS,
                    new AssociationListListener(observation));

            /*
             * This property change listener redraws the row of the table if the
             * conceptName of the observation changes.
             */
            observation.addPropertyChangeListener(Observation.PROP_CONCEPT_NAME, new ConceptNameListener(observation));

            fireTableRowsUpdated(idx, idx);
        }

    }

    /**
     * Listens for changes to the AssociationList and redraws the table if needed
     */
    private class AssociationListListener implements PropertyChangeListener {

        private final Observation observation;

        AssociationListListener(final Observation observation) {
            this.observation = observation;
        }

        /**
         *
         * @param e
         */
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

        private final Observation observation;

        ConceptNameListener(final Observation observation) {
            this.observation = observation;
        }

        /**
         *
         * @param e
         */
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
