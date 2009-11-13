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

/**
 * <p>Creates a new Observation using the specified concept-name.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: NewObservationUsingConceptNameAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class NewObservationUsingConceptNameAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="action1"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    NewVideoFrameAction action1;

    /**
     *     @uml.property  name="conceptName"
     */
    private String conceptName;

    /**
     * @param  conceptName Observations created by this action will have this
     * name
     */
    public NewObservationUsingConceptNameAction(final String conceptName) {
        super();
        this.conceptName = conceptName;
        action1 = new NewVideoFrameAction();

        // action2 = new ChangeObservationConceptNameAction(conceptName);
    }

    /**
     * Initiates the action.
     *
     * @see  org.mbari.awt.event.IAction#doAction()
     */
    public void doAction() {
        action1.doAction(conceptName);
    }

    /**
     *     Gets the conceptName attribute of the NewObservationUsingConceptNameAction object
     *     @return   The conceptName value
     *     @uml.property  name="conceptName"
     */
    public String getConceptName() {
        return conceptName;
    }
}
