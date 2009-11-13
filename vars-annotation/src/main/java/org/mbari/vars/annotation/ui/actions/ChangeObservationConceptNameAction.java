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
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import vars.annotation.IObservation;

/**
 * <p>Changes the conceptName of the currently selected Observation</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: ChangeObservationConceptNameAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ChangeObservationConceptNameAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="conceptName"
     */
    private final String conceptName;

    /**
     * Constructs ...
     *
     *
     * @param conceptName
     */
    public ChangeObservationConceptNameAction(final String conceptName) {
        this.conceptName = conceptName;
    }

    /**
     * Changes the conceptName of the <code>Observation</code> retrieved
     * from the ObservationDispatcher.
     *
     */
    public void doAction() {

        // Get the current observation
        final IObservation observation = ObservationDispatcher.getInstance().getObservation();
        if (observation != null) {
            observation.setConceptName(conceptName);
        }

        /*
         * Note we do not need to notify the ObservationTable to redraw because
         * the table listens to each observation directly using
         * PropertyChangeListeners
         */
    }

    /**
     *     @return  The conceptName used by this action
     *     @uml.property  name="conceptName"
     */
    public String getConceptName() {
        return conceptName;
    }
}
