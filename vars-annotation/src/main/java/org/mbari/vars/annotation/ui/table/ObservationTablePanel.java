/*
 * @(#)ObservationTablePanel.java   2009.11.18 at 04:22:45 PST
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



package org.mbari.vars.annotation.ui.table;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.util.Dispatcher;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.table.IObservationTable;
import vars.annotation.ui.table.JXObservationTable;

/**
 * <p>A JPanel that contains the ObservationTable.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class ObservationTablePanel extends javax.swing.JPanel {

    private javax.swing.JScrollPane scrollPane;
    private final IObservationTable table;

    /**
     * Creates new form ObservationTablePanel
     */
    public ObservationTablePanel() {

        table = new JXObservationTable();
        final JTable jTable = table.getJTable();
        table.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            private final Dispatcher dispatcher = Lookup.getSelectedObservationsDispatcher();

            public void valueChanged(ListSelectionEvent e) {
                final int[] selectedRows = jTable.getSelectedRows();
                Collection<Observation> observations = new ArrayList<Observation>(selectedRows.length);
                for (int i = 0; i < selectedRows.length; i++) {
                    observations.add(table.getObservationAt(i));
                }

                dispatcher.setValueObject(observations);

            }
        });

        Dispatcher dispatcher = Lookup.getObservationTableDispatcher();
        dispatcher.setValueObject(table);
        initComponents();
    }

    /**
     *  Adds a feature to the Observation attribute of the ObservationTablePanel object
     *
     * @param  observation The observation to be added to the tables view
     */
    public void addObservation(final Observation observation) {
        table.addObservation(observation);
    }

    /**
     *  Gets the observationTable attribute of the ObservationTablePanel object
     *
     * @return  The observationTable used for representing the data
     */
    public IObservationTable getObservationTable() {
        return table;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initComponents() {
        scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(new BorderLayout());
        scrollPane.setViewportView(table.getJTable());
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     *  Description of the Method
     *
     * @param  observation remove an observation from the table's view
     */
    public void removeObservation(final Observation observation) {
        table.removeObservation(observation);
    }
}
