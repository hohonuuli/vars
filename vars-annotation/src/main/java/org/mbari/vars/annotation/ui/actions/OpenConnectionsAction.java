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

import javax.swing.JDialog;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.dialogs.ConnectionDialog;

import vars.annotation.ui.ToolBelt;

/**
 * <p>Brings up a dialog to sign in as a user and
 * to connect to the VCR</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class OpenConnectionsAction extends ActionAdapter {

    private final JDialog dialog;

    /**
     * Constructor for the OpenConnectionsAction object
     */
    public OpenConnectionsAction(ToolBelt toolBelt) {
        super("Connect");
        dialog = new ConnectionDialog(toolBelt);
    }

    /**
     * Initiates the action.
     * @see  org.mbari.vars.annotation.ui.actions.IAction#doAction()
     */
    public void doAction() {

        // Bring up a dialog box to log into VARS
        dialog.setVisible(true);
    }
}
