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

import vars.annotation.ui.ToolBelt;

/**
 * <p>Adds 'sampled-by | physical-object | nil' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public final class AddSamplePropAction extends AddPropertyAction {



    /**
     *
     */
    public AddSamplePropAction(ToolBelt toolBelt) {
    	// TODO this is hard coded
        super(toolBelt, "sampled-by", "physical object", "nil");
    }
}
