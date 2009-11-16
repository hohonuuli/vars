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
Created on Dec 4, 2003
 */
package org.mbari.vars.annotation.ui.dialogs;

import com.jgoodies.forms.factories.Borders;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mbari.swing.JFancyButton;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.dao.DAOException;
import vars.Role;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.model.dao.UserAccountDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Panel that provides input for opeining a user account.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: UserConnectPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class UserConnectPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 4495664437481073744L;
    private static final Logger log = LoggerFactory.getLogger(UserConnectPanel.class);

    /**
     *     @uml.property  name="activeUser"
     *     @uml.associationEnd
     */
    private UserAccount activeUser = null;

    /**
     *     @uml.property  name="createUserButton"
     *     @uml.associationEnd
     */
    private javax.swing.JButton createUserButton = null;

    /**
     *     @uml.property  name="editPassword"
     *     @uml.associationEnd
     */
    private javax.swing.JPasswordField editPassword = null;

    /**
     *     @uml.property  name="messageLabel"
     *     @uml.associationEnd
     */
    private JLabel messageLabel = null;

    /**
     *     @uml.property  name="passwordLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel passwordLabel = null;

    /**
     *     @uml.property  name="userComboBox"
     *     @uml.associationEnd  multiplicity="(0 -1)" elementType="org.mbari.vars.model.UserAccount"
     */
    private javax.swing.JComboBox userComboBox = null;

    /**
     *     @uml.property  name="userNameAndPasswordPanel"
     *     @uml.associationEnd
     */
    private JPanel userNameAndPasswordPanel = null;

    /**
     *     @uml.property  name="userNameLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel userNameLabel = null;

    /**
     * This is the default constructor
     */
    public UserConnectPanel() {
        super();
        initialize();
    }

    /**
     * attemptLogin -- method which performs the login action
     *
     *
     * @return  true if login worked
     */
    boolean attemptLogin() {
        boolean success = false;
        final UserAccount selectedUser = (UserAccount) userComboBox.getSelectedItem();

        if ((selectedUser.getPassword() != null) && (selectedUser.getPassword().length() > 0)) {
            if (editPassword.isEnabled()) {
                final char[] passAttempt = getEditPassword().getPassword();
                final String password = new String(passAttempt);
                if (password.equalsIgnoreCase(UserAccount.PASSWORD_DEFAULT)) {
                    log.info("Logging user in using default password. Setting role to read-only.");
                    selectedUser.setRole(Role.DEFAULT);
                    activeUser = selectedUser;
                    success = true;
                }
                else {
                    boolean same = true;
                    if (password.equals(selectedUser.getPassword())) {
                        same = true;
                    }
                    else {
                        same = false;
                    }

                    if (same) {
                        setMessageLabelText("User and password are valid");
                        log.info("User and password were valid");
                        activeUser = selectedUser;
                        success = true;
                    }
                    else {
                        setMessageLabelText("Unable to log in. Check name and password.");
                        log.info("Incorrect password was entered");
                    }
                }
            }
            else {
                setMessageLabelText("");
                log.warn("selected user has a password, but password box is diabled");
            }
        }
        else {
            log.info("selected user does not have a password");
            activeUser = selectedUser;
            success = true;
        }

        if (success) {
            final PersonDispatcher pd = PersonDispatcher.getInstance();
            pd.setPerson(activeUser.getUserName());
        }

        return success;
    }

    /**
     * Description of the Method
     *
     *
     * @param  e             Description of the Parameter
     */
    private void createNewUser(final ActionEvent e) {
        final NewUserDialog jDia = new NewUserDialog();
        jDia.setPreferredSize(new Dimension(350, 300));
        final UserAccount user = jDia.doNewUser();

        // if the create was not cancelled
        if (user != null) {
            userComboBox.addItem(user);
            userComboBox.setSelectedItem(user);
        }
    }

    /**
     *     Get the current active user
     *     @return
     *     @uml.property  name="activeUser"
     */
    public UserAccount getActiveUser() {
        if (activeUser == null) {
            attemptLogin();
        }

        return activeUser;
    }

    /**
     *     This method initializes createUserButton
     *     @return   javax.swing.JButton
     *     @uml.property  name="createUserButton"
     */
    private javax.swing.JButton getCreateUserButton() {
        if (createUserButton == null) {
            createUserButton = new JFancyButton();
            createUserButton.setText("New");
            createUserButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/user1_add.png")));
            createUserButton.setPreferredSize(new java.awt.Dimension(70, 26));
            createUserButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    createNewUser(e);
                }
            });
        }

        return createUserButton;
    }

    /**
     *     This method initializes editPassword. The method is package accessible so that listeners can keep track of when the password has been updated.
     *     @return   javax.swing.JPasswordField
     *     @uml.property  name="editPassword"
     */

    // TODO If it is not okay to leave this password method non-private, then
    // a variable should be setup that can be listened to by outside classes so
    // that listeners can observe when the password is being typed.
    javax.swing.JPasswordField getEditPassword() {
        if (editPassword == null) {
            editPassword = new javax.swing.JPasswordField();
            editPassword.setPreferredSize(new java.awt.Dimension(150, 20));
            editPassword.setMaximumSize(new java.awt.Dimension(250, 20));
        }

        return editPassword;
    }

    /**
     *     This message label is updated by the component, but it is not placed whithin <tt>this</tt>. If you want to see the information on the label, you'll need to place it somewhere using this method to get it.
     *     @return
     *     @uml.property  name="messageLabel"
     */
    private JLabel getMessageLabel() {
        if (messageLabel == null) {
            messageLabel = new JLabel();
            messageLabel.setForeground(Color.red);
        }

        return messageLabel;
    }

    /**
     *     This method initializes passwordLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="passwordLabel"
     */
    private javax.swing.JLabel getPasswordLabel() {
        if (passwordLabel == null) {
            passwordLabel = new javax.swing.JLabel();
            passwordLabel.setText("Password:");
            passwordLabel.setToolTipText("Enter your password here");
            passwordLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        }

        return passwordLabel;
    }

    /**
     *     This method initializes userComboBox. Method is package accessible to allow action listeners from other classes to listen to changes on this combo box.
     *     @return   javax.swing.JComboBox
     *     @uml.property  name="userComboBox"
     */
    javax.swing.JComboBox getUserComboBox() {
        if (userComboBox == null) {
            userComboBox = new JComboBox();
            userComboBox.setPreferredSize(new java.awt.Dimension(130, 25));

            // Get potential user naems and add them to the comboBox
            final UserAccount[] accounts = retrieveUserAccounts();
            Arrays.sort(accounts, new IgnoreCaseToStringComparator());

            for (int i = 0; i < accounts.length; i++) {
                userComboBox.addItem(accounts[i]);
            }

            /*
             * if the System.getProperty("user.name") is one of the items in
             * the list of users, select it.
             */
            final String userName = System.getProperty("user.name");
            for (int i = 0; i < userComboBox.getItemCount(); i++) {
                final UserAccount ua = (UserAccount) userComboBox.getItemAt(i);
                if (userName.compareToIgnoreCase(ua.getUserName()) == 0) {
                    userComboBox.setSelectedIndex(i);
                }
            }
        }

        return userComboBox;
    }

    /**
     *     @return  the userNameAndPasswordPanel
     *     @uml.property  name="userNameAndPasswordPanel"
     */
    private JPanel getUserNameAndPasswordPanel() {
        if (userNameAndPasswordPanel == null) {

            final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridheight = -1;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.gridwidth = -1;
            final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(4, 4, 4, 4);
            final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridx = 0;
            final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new Insets(4, 4, 4, 10);
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.gridx = 2;
            final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(4, 4, 4, 10);
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridx = 0;
            userNameAndPasswordPanel = new JPanel();
            userNameAndPasswordPanel.setLayout(new GridBagLayout());

            // across the whole screen, to achieve that effect with this layout
            // change cc.xy(4,4) to cc.xywh(3,3,3,1)
            userNameAndPasswordPanel.setBorder(Borders.DIALOG_BORDER);
            userNameAndPasswordPanel.add(getUserNameLabel(), gridBagConstraints);
            userNameAndPasswordPanel.add(getUserComboBox(), gridBagConstraints1);
            userNameAndPasswordPanel.add(getCreateUserButton(), gridBagConstraints2);
            userNameAndPasswordPanel.add(getPasswordLabel(), gridBagConstraints3);
            userNameAndPasswordPanel.add(getEditPassword(), gridBagConstraints4);
            userNameAndPasswordPanel.add(getMessageLabel(), gridBagConstraints6);
        }

        return userNameAndPasswordPanel;
    }

    /**
     *     This method initializes userNameLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="userNameLabel"
     */
    private javax.swing.JLabel getUserNameLabel() {
        if (userNameLabel == null) {
            userNameLabel = new javax.swing.JLabel();
            userNameLabel.setText("User Name:");
        }

        return userNameLabel;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        this.add(getUserNameAndPasswordPanel(), null);

        // Generated
    }

    /**
     * Retrieve the user accounts available.
     *
     *
     * @return  Description of the Return Value
     */
    private UserAccount[] retrieveUserAccounts() {
        Collection userAccounts = null;
        UserAccount userAccount = null;
        try {
            userAccounts = UserAccountDAO.getInstance().findAll();
        }
        catch (final DAOException e) {
            log.error("An exception was throws while attempting to retrieve the " +
                      "UserAccounts from the VARS database. ", e);

            // make sure this is null so that it will be inititialized to a
            // default
            userAccounts = null;

            // TODO 20031206 achase: I added this error message, do we want this
            // here or not?
            setMessageLabelText("Error retrieving user accounts");
        }

        // Something failed, so supply a default user
        if ((userAccounts == null) || (userAccounts.size() == 0)) {
            log.warn("No user accounts are available. Using default.");
            userAccount = new UserAccount("default");
            userAccount.setPassword("");
            userAccounts = new ArrayList(1);
            userAccounts.add(userAccount);
        }

        final UserAccount[] dummy = new UserAccount[0];

        return (UserAccount[]) userAccounts.toArray(dummy);
    }

    /**
     * @param  text The new messageLabelText value
     */
    public void setMessageLabelText(final String text) {
        getMessageLabel().setText(text);
    }
}