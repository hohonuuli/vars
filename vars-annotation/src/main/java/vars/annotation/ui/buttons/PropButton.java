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
package vars.annotation.ui.buttons;

import vars.UserAccount;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.shared.ui.FancyButton;


/**
 * <p>This button toggles its enabled state based on if Person is logged in
 * and if the OBservationTable has selected rows. Person  != null and
 * ObservationTable has rows selected = enabled; otherwise not enabled.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PropButton extends FancyButton {

    /**
     * I hate to use a static reference BUT, I need no arg constructors for
     * the prop buttons and they need a toolBelt to work with.
     */
    private static ToolBelt toolBelt;



    /**
     * Constructs ...
     *
     */
    public PropButton() {
        super();

        // Enable the button if a user is logged in and one or more rows are
        // selected in the table.
        StateLookup.selectedObservationsProperty().addListener((obs, oldVal, observations) -> {
            boolean enable = false;

            if (observations.size() > 0) {
                UserAccount userAccount = StateLookup.getUserAccount();
                enable = userAccount != null;
            }

            setEnabled(enable);
        });

        
        setEnabled(false);
        
    }

    public static ToolBelt getToolBelt() {
        return toolBelt;
    }

    public static void setToolBelt(ToolBelt toolBelt) {
        PropButton.toolBelt = toolBelt;
    }

    
}
