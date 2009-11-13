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
Created on Dec 4, 2003 @author achase
 */
package org.mbari.vars.annotation.ui.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * <p>A dialog box for connecting a user. Basically a wrapper for UserConnectPanel,
 * but with "okay" and "cancel" buttons added in.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: UserConnectDialog.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class UserConnectDialog extends OkayCancelDialog {

    /**
     *
     */
    private static final long serialVersionUID = 6888182274269467835L;

    /**
     *     @uml.property  name="userConnectPanel"
     *     @uml.associationEnd
     */
    private UserConnectPanel userConnectPanel = null;

    /**
     * This is the default constructor
     */
    public UserConnectDialog() {
        super(AppFrameDispatcher.getFrame(), "VARS - User Login", true);
        initialize();
    }

    /**
     * Bind the okay button to the text fields that should contain data before
     * the okay button can be pressed
     */
    private void bindOkayButton() {
        getUserConnectPanel().getEditPassword().addKeyListener(new KeyAdapter() {

            public void keyPressed(final KeyEvent ke) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        updateOkayButton();
                    }
                });
            }

        });
    }

    /**
     *     @return
     *     @uml.property  name="userConnectPanel"
     */
    private UserConnectPanel getUserConnectPanel() {
        if (userConnectPanel == null) {
            userConnectPanel = new UserConnectPanel();
        }

        return userConnectPanel;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        initializeOkayButton();
        initializeContentPane();
        this.pack();
    }

    private void initializeContentPane() {
        getContentPane().add(getUserConnectPanel(), java.awt.BorderLayout.CENTER);
    }

    private void initializeOkayButton() {
        setCloseDialogOnOkay(false);
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                final boolean success = getUserConnectPanel().attemptLogin();

                // login complete, close dialog.
                if (success) {
                    UserConnectDialog.this.dispose();
                }
            }

        });

        // bind the okay button to the user name and password fields.
        // it should not be enabled when these fields are not filled in.
        bindOkayButton();

        // update the button to account for the new bindings
        updateOkayButton();
    }

    /**
     * Check the text fields which the okay button is bound to and
     * enable/disable the button accordingly.
     *
     */
    private void updateOkayButton() {
        final Object o = getUserConnectPanel().getUserComboBox().getSelectedItem();
        if ((getUserConnectPanel().getEditPassword().getPassword().length > 0) && (o != null) &&
                (o.toString().length() > 0)) {
            getOkayButton().setEnabled(true);
        }
        else {
            getOkayButton().setEnabled(false);
        }
    }
}
