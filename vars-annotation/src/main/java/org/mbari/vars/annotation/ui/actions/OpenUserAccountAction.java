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
import org.mbari.vars.annotation.ui.dialogs.UserConnectDialog;

import vars.annotation.ui.ToolBelt;

/**
 * <p>Brings up a dialog allowing the user to open a account in the database.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class OpenUserAccountAction extends ActionAdapter {

    private JDialog dialog;
    private final ToolBelt toolBelt;

    /**
     * Constructor for the OpenUserAccountAction object
     */
    public OpenUserAccountAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        getDialog().setVisible(true);
        getDialog().repaint();
    }

    /**
     *     Gets the dialog used to open a user account.
     *     @return   A <code>UserConnectDialog</code>
     */
    public JDialog getDialog() {
        if (dialog == null) {
            dialog = new UserConnectDialog(toolBelt);
        }

        return dialog;
    }
}
