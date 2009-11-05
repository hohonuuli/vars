/*
 * @(#)ModifyUserDialog.java   2009.10.27 at 09:57:31 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.dialogs;

import com.sun.media.jai.opimage.LookupCRIF;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import org.bushe.swing.event.EventBus;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.MiscDAOFactory;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.UserAccountRoles;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.ILockableEditor;
import vars.shared.ui.OkCancelButtonPanel;
import vars.shared.ui.UserAccountComboBox;

/**
 * Class description
 *
 *
 * @version        $date$, 2009.10.27 at 09:56:54 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class ModifyUserDialog extends JDialog implements ILockableEditor {

    private static final long serialVersionUID = 1L;
    private OkCancelButtonPanel buttonPanel = null;
    private JPanel centerPanel = null;
    private JPanel jContentPane = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private boolean locked = false;
    private UserAccountComboBox nameComboBox = null;
    private JLabel nameLabel = null;
    private JPasswordField newPwd1Field = null;
    private JPasswordField newPwd2Field = null;
    private JLabel pwd1Label = null;
    private JLabel pwd2Label = null;
    private JLabel pwd3Label = null;
    private JPasswordField pwdField = null;
    private JComboBox roleComboBox = null;
    private JLabel userLabel = null;
    private final MiscDAOFactory miscDAOFactory;

    /**
     * @param owner
     * @param userAccountDAO
     */
    public ModifyUserDialog(Frame owner, MiscDAOFactory miscDAOFactory) {
        super(owner);
        this.miscDAOFactory = miscDAOFactory;
        initialize();
    }

    private void doCancel() {
        setVisible(false);
    }

    private void doOk() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(UserAccount.class);
        final UserAccount currentUserAccount = (UserAccount) dispatcher.getValueObject();
        final UserAccount selectedUserAccount = getNameComboBox().getSelectedUserAccount();

        boolean visible = false;

        if (currentUserAccount != null) {

            /*
             * Administrators can change users roles. Do that here.
             */
            if (currentUserAccount.isAdministrator()) {
                String roleName = (String) getRoleComboBox().getSelectedItem();
                selectedUserAccount.setRole(roleName);
            }

            /*
             * A user can change their own password. Do that here.
             */
            if (currentUserAccount.equals(selectedUserAccount)) {
                String password = new String(getPwdField().getPassword());
                if (password.equals(selectedUserAccount.getPassword())) {
                    String pwd1 = new String(getNewPwd1Field().getPassword());
                    String pwd2 = new String(getNewPwd2Field().getPassword());
                    if ((pwd1 != null) && pwd1.equals(pwd2)) {
                        selectedUserAccount.setPassword(pwd1);
                    }
                    else {

                        // Notify user that their new passwords did not match
                        EventBus.publish(GlobalLookup.TOPIC_WARNING, "The new passwords do not match. Try again.");
                        getNewPwd1Field().setText(null);
                        getNewPwd2Field().setText(null);
                        getNewPwd1Field().requestFocus();
                        visible = true;
                    }
                }
                else {

                    // Notify user that the password is incorrect
                    EventBus.publish(GlobalLookup.TOPIC_WARNING,
                                     "The password you entered for '" + selectedUserAccount.getUserName() +
                                     "' is incorrect.");
                    getPwdField().setText(null);
                    getPwdField().requestFocus();
                    visible = true;
                }
            }
        }

        setVisible(visible);

        if (visible) {
            toFront();
        }


        if ((selectedUserAccount != null) && !visible) {
            try {
                UserAccountDAO userAccountDAO = miscDAOFactory.newUserAccountDAO();
                userAccountDAO.startTransaction();
                userAccountDAO.merge(selectedUserAccount);
                userAccountDAO.endTransaction();
            }
            catch (Exception e) {
                EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR,
                                 "Failed to update the user account '" + selectedUserAccount.getUserName() + "'");
            }
        }

    }

    private OkCancelButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new OkCancelButtonPanel();
            buttonPanel.getOkButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doOk();
                }

            });

            buttonPanel.getCancelButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doCancel();
                }

            });
        }

        return buttonPanel;
    }

    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 4;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = GridBagConstraints.CENTER;
            gridBagConstraints6.insets = new Insets(0, 0, 4, 20);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridy = 3;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.insets = new Insets(0, 0, 4, 20);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(0, 0, 4, 20);
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints31.gridy = 4;
            pwd3Label = new JLabel();
            pwd3Label.setText("New password:");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridy = 3;
            pwd2Label = new JLabel();
            pwd2Label.setText("New password:");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints12.gridy = 2;
            pwd1Label = new JLabel();
            pwd1Label.setText("Password:");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(0, 0, 4, 20);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(0, 20, 0, 10);
            gridBagConstraints11.gridy = 1;
            nameLabel = new JLabel();
            nameLabel.setText("Role:");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(0, 0, 4, 20);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(4, 20, 4, 10);
            gridBagConstraints.gridy = 0;
            userLabel = new JLabel();
            userLabel.setText("User:");
            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            centerPanel.add(userLabel, gridBagConstraints);
            centerPanel.add(getNameComboBox(), gridBagConstraints1);
            centerPanel.add(nameLabel, gridBagConstraints11);
            centerPanel.add(getRoleComboBox(), gridBagConstraints3);
            centerPanel.add(pwd1Label, gridBagConstraints12);
            centerPanel.add(pwd2Label, gridBagConstraints2);
            centerPanel.add(pwd3Label, gridBagConstraints31);
            centerPanel.add(getPwdField(), gridBagConstraints4);
            centerPanel.add(getNewPwd1Field(), gridBagConstraints5);
            centerPanel.add(getNewPwd2Field(), gridBagConstraints6);
        }

        return centerPanel;
    }

    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
            jContentPane.add(getCenterPanel(), BorderLayout.CENTER);
        }

        return jContentPane;
    }

    private UserAccountComboBox getNameComboBox() {
        if (nameComboBox == null) {
            nameComboBox = new UserAccountComboBox(miscDAOFactory);
            nameComboBox.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    updateUserAccount(getNameComboBox().getSelectedUserAccount());
                }

            });
        }

        return nameComboBox;
    }

    private JPasswordField getNewPwd1Field() {
        if (newPwd1Field == null) {
            newPwd1Field = new JPasswordField();
        }

        return newPwd1Field;
    }

    private JPasswordField getNewPwd2Field() {
        if (newPwd2Field == null) {
            newPwd2Field = new JPasswordField();
        }

        return newPwd2Field;
    }

    private JPasswordField getPwdField() {
        if (pwdField == null) {
            pwdField = new JPasswordField();
        }

        return pwdField;
    }

    private JComboBox getRoleComboBox() {
        if (roleComboBox == null) {
            roleComboBox = new JComboBox();
            UserAccountRoles[] roles = UserAccountRoles.values();
            for (int i = 0; i < roles.length; i++) {
                roleComboBox.addItem(roles[i].toString());
            }
        }

        return roleComboBox;
    }

    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;

    }

    public void setUserAccount(UserAccount userAccount) {
        try {
            getNameComboBox().update();
        }
        catch (Exception e) {
            log.error("Failed to retrieve user accounts from the database", e);
            EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR, "Failed to retrieve user accounts from the database");
        }

        final String userName = (userAccount == null) ? null : userAccount.getUserName();
        getNameComboBox().setSelectedUserName(userName);
    }

    @Override
    public void setVisible(boolean b) {
        if (!b) {
            getPwdField().setText(null);
            getNewPwd1Field().setText(null);
            getNewPwd2Field().setText(null);
        }

        super.setVisible(b);
    }

    /**
     * Updates the ui based on the account selected in the nameComboBox
     * @param userAccount
     */
    private void updateUserAccount(final UserAccount userAccount) {

        final Dispatcher dispatcher = Dispatcher.getDispatcher(UserAccount.class);
        final UserAccount currentUserAccount = (UserAccount) dispatcher.getValueObject();

        // If the name matches the current user account then enable change of password
        final boolean canEditPwd = (currentUserAccount != null) && currentUserAccount.equals(userAccount);
        getPwdField().setEnabled(canEditPwd);
        getNewPwd1Field().setEnabled(canEditPwd);
        getNewPwd2Field().setEnabled(canEditPwd);

        // If current user is admin then enable role cb
        getRoleComboBox().setEnabled((currentUserAccount != null) && currentUserAccount.isAdministrator());

        if (userAccount != null) {
            getRoleComboBox().setSelectedItem(userAccount.getRole());
        }
    }
}
