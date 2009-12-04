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


package vars.annotation.ui.actions;

import org.mbari.awt.event.ActionAdapter;

import vars.annotation.ui.ToolBelt;

/**
 * <p>Creates a new Observation using the specified concept-name.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class NewObservationUsingConceptNameAction extends ActionAdapter {


    NewVideoFrameAction action1;


    private String conceptName;

    /**
     * @param  conceptName Observations created by this action will have this
     * name
     */
    public NewObservationUsingConceptNameAction(ToolBelt toolBelt, final String conceptName) {
        super();
        this.conceptName = conceptName;
        action1 = new NewVideoFrameAction(toolBelt);

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
     */
    public String getConceptName() {
        return conceptName;
    }
}
