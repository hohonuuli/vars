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
import org.mbari.vars.annotation.ui.actions.AddCloseUpPropAction;

import vars.annotation.ui.ToolBelt;

/**
 * <p>Adds a close up annotation to the selected observations.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class CloseUpPropButton extends PropButton {

    /**
     * Constructor.
     */
    public CloseUpPropButton(ToolBelt toolBelt) {
        super();
        setAction(new AddCloseUpPropAction(toolBelt));
        setToolTipText("close-up");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/ccbutton.png")));
        setEnabled(false);
    }
}
