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
Created on October 21, 2003, 2:43 PM
 */
package org.mbari.vars.annotation.ui.table;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.JTableHeader;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;

/**
 * <p>A JPanel that contains the ObservationTable.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ObservationTablePanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @stereotype  thing
 */
public class ObservationTablePanel extends javax.swing.JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 3060110736037091257L;

    /**
     *     @uml.property  name="scrollPane"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private javax.swing.JScrollPane scrollPane;

    /**
     *     @uml.property  name="table"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final ObservationTable table;

    /**
     * Creates new form ObservationTablePanel
     */
    public ObservationTablePanel() {

        /*
         *  ACC -- I moved this table initialization out of the init
         *  so that I could make the table variable final, which simplifies
         *  the getObservationTable method by assuring that the table variable
         *  will be non-null (unless the initialization of the variable fails.
         */

//      ObservationTableModel observationModel = new ObservationTableModel();
        final TableSorter sorter = new TableSorter();
        table = new ObservationTable(sorter, (ObservationColumnModel) ObservationColumnModel.getInstance());
        table.enableObservationDispatcherNotification();
        ObservationTableDispatcher.getInstance().setObservationTable(table);
        table.setTableHeader(new JTableHeader(ObservationColumnModel.getInstance()));

        // Set up tool tips for column headers.
        sorter.setTableHeader(table.getTableHeader());
        table.getTableHeader().setToolTipText("Click to specify sorting; Control-Click to specify secondary sorting");
        initComponents();
    }

    /**
     *  Adds a feature to the Observation attribute of the ObservationTablePanel object
     *
     * @param  observation The feature to be added to the Observation attribute
     */
    public void addObservation(final Observation observation) {
        table.addObservation(observation);
    }

    /**
     *  Gets the observationTable attribute of the ObservationTablePanel object
     *
     * @return  The observationTable value
     */
    public ObservationTable getObservationTable() {
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
        scrollPane.setViewportView(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     *  Description of the Method
     *
     * @param  observation Description of the Parameter
     */
    public void removeObservation(final Observation observation) {
        table.removeObservation(observation);
    }

    /*
     *  # ObservationTable lnkObservationTable;
     */
}
