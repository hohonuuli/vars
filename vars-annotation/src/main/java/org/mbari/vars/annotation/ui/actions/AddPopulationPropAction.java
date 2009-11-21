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

import vars.annotation.ui.ToolBelt;

/**
 * <p>Adds 'population-quantity | self | 999' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: AddPopulationPropAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public final class AddPopulationPropAction extends AddPropertyAction {


    public AddPopulationPropAction(ToolBelt toolBelt) {
        super(toolBelt, "population-quantity", "self", "999");
    }
}
