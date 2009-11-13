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
The Monterey Bay Aquarium Research Institute (MBARI) provides this
documentation and code 'as is', with no warranty, express or
implied, of its quality or consistency. It is provided without support and
without obligation on the part of MBARI to assist in its use, correction,
modification, or enhancement. This information should not be published or
distributed to third parties without specific written permission from MBARI
 */
package org.mbari.vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;

/**
 * <p>
 * Adds a New Observation to the selected VideoFrame. The selected
 * VideoFrame is retrieved by getting the current observation, then
 * getting it's videoFrame.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: NewObservationAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public final class NewObservationAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(NewObservationAction.class);

    /**
     * Constructs ...
     *
     */
    public NewObservationAction() {
        putValue(Action.NAME, "New observation");
        putValue(Action.ACTION_COMMAND_KEY, "new observation");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     */
    public void doAction() {

        // Get the current observation
        final IObservation observation = ObservationDispatcher.getInstance().getObservation();
        if (observation != null) {

            // The new observation will use the same videoframe as the
            // current observation
            final IVideoFrame vf = observation.getVideoFrame();
            if (vf != null) {

                // Create the new observation and add it to the VideoFrame
                final Observation newObs = new Observation();
                final String conceptName = observation.getConceptName();
                newObs.setConceptName(conceptName);
                String person = PersonDispatcher.getInstance().getPerson();
                if (person == null) {
                    person = PersonDispatcher.DEFAULT_USER;
                }

                newObs.setObserver(person);
                newObs.setObservationDate(new Date());
                vf.addObservation(newObs);

                // Insert the new Observation into the database.
                DAOEventQueue.insert(newObs, new InsertErrorHandler(newObs));

                // Add the observation to the table model and set the
                // selected row to it.
                final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
                table.addObservation(newObs);
                table.setSelectedObservation(newObs);
            }
            else {
                log.warn("Unable to add a new Observation to an existing " +
                         "VideoFrame; no VideoFrame has been specified.");
            }
        }
        else {
            log.warn("Unable to add new Observation, unable to retrieve an " + "existing observation");
        }
    }

    /**
     *     @author  brian
     */
    private class InsertErrorHandler extends DAOExceptionHandler {

        private final Observation observation;

        InsertErrorHandler(final Observation observation) {
            this.observation = observation;
        }

        protected void doAction(final Exception e) {

            /*
             * Remove the observation from the data model to keep it in synch
             * with the database.
             */
            final IVideoFrame videoFrame = observation.getVideoFrame();
            videoFrame.removeObservation(observation);

            /*
             * Redraw the UI
             */

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
                    table.removeObservation(observation);
                }

            });

        }
    }
}
