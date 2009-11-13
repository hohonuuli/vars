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
import org.mbari.vars.annotation.ui.actions.AddDensePropAction;

/**
 * <p>
 * Adds a 'dense' annotation to the currently selected observations.
 * </p>
 *
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: DensePropButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class DensePropButton extends PropButton {

    /**
     *
     */
    private static final long serialVersionUID = 1911458013400590503L;

    /**
     *      Constructor
     */
    public DensePropButton() {
        super();
        setAction(new AddDensePropAction());
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/dbutton.png")));
        setToolTipText("dense population");
        setEnabled(false);
    }
}
