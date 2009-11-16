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


package org.mbari.vars.annotation.ui;

import javax.swing.ImageIcon;
import org.mbari.vars.annotation.ui.actions.AddOldRefNumPropWithDialogAction;

/**
 * <p>
 * A button that calls the <code>AddOldRefNumPropWithDialogAction</code> object.
 * </p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class OldRefNumPropButton extends PropButton {

    /**
     *
     */
    private static final long serialVersionUID = 2674794812728840741L;

    /**
     * Constructor for the OldRefNumPropButton object
     */
    public OldRefNumPropButton() {
        super();
        setAction(new AddOldRefNumPropWithDialogAction());
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/oldnum.png")));
        setToolTipText("choose from existing reference numbers");
        setEnabled(false);
    }
}
