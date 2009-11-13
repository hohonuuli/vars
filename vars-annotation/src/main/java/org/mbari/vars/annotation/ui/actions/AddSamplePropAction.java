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

/**
 * <p>Adds 'sampled-by | physical-object | nil' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: AddSamplePropAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public final class AddSamplePropAction extends AddPropertyAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public AddSamplePropAction() {
        super("sampled-by", "physical object", "nil");
    }
}
