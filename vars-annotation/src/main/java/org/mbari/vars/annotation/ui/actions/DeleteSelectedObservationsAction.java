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


package org.mbari.vars.annotation.ui.actions;

import java.util.Arrays;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.table.IObservationTableModel;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import vars.annotation.IObservation;

/**
 * <p>Deletes all the Observations selected in the table from the database.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: DeleteSelectedObservationsAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class DeleteSelectedObservationsAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final DeleteObservationAction action = new DeleteObservationAction();

    /*
     *  (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.actions.IAction#doAction()
     */

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {

        /*
         *  Set the Observation to null in the ObservationDispatcher. Failure to
         * do this will result in a DAOException getting thrown. This excepetion
         * is caused by 1st deleting the current observation, then making a
         * call to ObservationDispatcher.getInstance.setObservation(obs). The
         * setObservation method tries to update the currently held object in the
         * database before changing the reference to the new observation. i.e
         * it's trying to update an observation that's already been deleted from
         * the database.
         */
        ObservationDispatcher.getInstance().setObservation(null);
        final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
        final IObservationTableModel model = (IObservationTableModel) table.getModel();
        final int[] rows = table.getSelectedRows();
        final int n = rows.length;
        if (n > 0) {

            // First grab the selected observations
            final IObservation[] observations = new IObservation[n];
            for (int i = 0; i < n; i++) {
                observations[i] = table.getObservationAt(rows[i]);
            }

            // Delete them from the database then remove them from the table
            for (int i = 0; i < observations.length; i++) {
                action.setObservation(observations[i]);
                action.doAction();
                model.removeObservation(observations[i]);
            }
        }

        Arrays.sort(rows);
        final int rowCount = table.getRowCount();
        int activeRow = rows[n - 1] - n + 1;
        if (activeRow > rowCount - 1) {
            activeRow = rowCount - 1;
        }

        if (rowCount > 0) {
            table.setSelectedObservation(table.getObservationAt(activeRow));
            table.scrollToVisible(activeRow, 0);
        }
    }
}
