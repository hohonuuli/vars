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
Created on Apr 30, 2004
 *
TODO To change the template for this generated file go to Window -
Preferences - Java - Code Generation - Code and Comments
 */
package org.mbari.vars.annotation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.mbari.swing.JFancyButton;

import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;


/**
 * <p>This button toggles its enabled state based on if Person is logged in
 * and if the OBservationTable has selected rows. Person  != null and
 * ObservationTable has rows selected = enabled; otherwise not enabled.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PropButton extends JFancyButton {



    /**
     * Constructs ...
     *
     */
    public PropButton() {
        super();

        // Enable the button if a user is logged in and one ro more rows are
        // selected in the table.
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                boolean enable = false;
                
                Collection<Observation> observations = (Collection<Observation>) evt.getNewValue();
                if (observations.size() > 0) {
                    UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                    enable = userAccount != null;
                }
                
                setEnabled(enable);
            }
        });
        
    }
}
