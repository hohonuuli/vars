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

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.Association;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IObservation;
import vars.annotation.IAssociation;

/**
 * <p>
 * Base class for the various AddXXXPropActions.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 * @version $Id: AddPropertyAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class AddPropertyAction extends ActionAdapter {

    /** <!-- Field description --> */
    public static final String NIL = "nil";

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AddPropertyAction.class);

    /**
     *     @uml.property  name="linkName"
     */
    private String linkName;

    /**
     *     @uml.property  name="linkValue"
     */
    private String linkValue;

    /**
     *     @uml.property  name="toConcept"
     */
    private String toConcept;

    /**
     * Constructs ...
     *
     */
    public AddPropertyAction() {
        this(NIL, NIL, NIL);
    }

    /**
     * Constructs ...
     *
     *
     * @param linkName
     * @param toConcept
     * @param linkValue
     */
    public AddPropertyAction(final String linkName, final String toConcept, final String linkValue) {
        setLinkName(linkName);
        setToConcept(toConcept);
        setLinkValue(linkValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mbari.vars.annotation.ui.actions.IAction#doAction()
     */

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
        if (table != null) {
            final int[] selectedRows = table.getSelectedRows();

            // Loop through each selected observation and add the association
            for (int i = 0; i < selectedRows.length; i++) {
                final int row = selectedRows[i];
                if (row > -1) {
                    final IObservation obs = table.getObservationAt(row);
                    if (obs != null) {
                        IAssociation a = new Association(linkName, toConcept, linkValue);
                        a = obs.addAssociation(a);

                        /*
                         * This changes the observer to the person who adds the
                         * association. It's commented out at the request of the
                         * video-lab.
                         */

                        // final String person =
                        // PersonDispatcher.getInstance().getPerson();
                        // obs.setObserver(person);
                        // if a is already in the database don't store it
                        if (a.getId() == null || a.getId() == 0) {

                            // Insert the association into the database
                            DAOEventQueue.insert((Association) a, new InsertExceptionHandler(a));
                        }
                    }
                    else {
                        if (log.isWarnEnabled()) {
                            log.warn("Unable to add an Association. No Observation was selected");
                        }
                    }
                }
            }

            // Reset the table so that the rows that the user selected remain
            // selected.
            for (int i = 0; i < selectedRows.length; i++) {
                table.addRowSelectionInterval(selectedRows[i], selectedRows[i]);
            }

            if (selectedRows.length == 1) {
                table.scrollToVisible(selectedRows[0], 0);
            }
        }
    }

    /**
     *     @return  Returns the linkName.
     *     @uml.property  name="linkName"
     */
    public String getLinkName() {
        return linkName;
    }

    /**
     *     @return  Returns the linkValue.
     *     @uml.property  name="linkValue"
     */
    public String getLinkValue() {
        return linkValue;
    }

    /**
     *     @return  Returns the toConcept.
     *     @uml.property  name="toConcept"
     */
    public String getToConcept() {
        return toConcept;
    }

    /**
     *     @param linkName  The linkName to set.
     *     @uml.property  name="linkName"
     */
    public void setLinkName(final String linkName) {
        this.linkName = linkName;
    }

    /**
     *     @param linkValue  The linkValue to set.
     *     @uml.property  name="linkValue"
     */
    public void setLinkValue(final String linkValue) {
        this.linkValue = linkValue;
    }

    /**
     *     @param toConcept  The toConcept to set.
     *     @uml.property  name="toConcept"
     */
    public void setToConcept(final String toConcept) {
        this.toConcept = toConcept;
    }

    /**
     *     Rolls back the data if a problem occurs in the insert transaction. This is needed to keep the UI in a state consistent with what's in the  database.
     */
    private class InsertExceptionHandler extends DAOExceptionHandler {

        private final IAssociation association;

        InsertExceptionHandler(final IAssociation association) {
            this.association = association;
        }

        protected void doAction(final Exception e) {

            /*
             * Remove the reference to the associaiton to keep the in memory
             * model in synch with the database. There's no need to redraw the
             * UI since it listens to changes in the observation.
             */
            final IObservation observation = association.getObservation();
            observation.removeAssociation(association);
            AppFrameDispatcher.showErrorDialog("Failed to insert the association, '" + association +
                                               "', into the database. The error was '" + e.getMessage() +
                                               "'. You may need to restart VARS");
        }
    }
}
