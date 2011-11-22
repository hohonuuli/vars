package vars.shared.ui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.UserAccountRoles;
import javax.swing.JComboBox;

public class UserAccountPreferencesPanel extends JPanel {

        final Logger log = LoggerFactory.getLogger(getClass());
        private JTextField affiliationTextField;

        private JTextField emailTextField;
        private JTextField firstNameTextField;
        private JTextField lastNameTextField;
        private JLabel lblAffiliationorganization;
        private JLabel lblEmail;
        private JLabel lblFirstName;
        private JLabel lblLastName;
        private JLabel lblLogin;
        private JLabel lblPassword;
        private JLabel lblPasswordagain;
        private JTextField loginTextField;
        private JPasswordField passwordField1;
        private JPasswordField passwordField2;
        private UserAccount userAccount;
        private JLabel lblPermissions;
        private JComboBox roleComboBox;

        /**
         * Constructs ...
         */
        public UserAccountPreferencesPanel() {
            super();
            initialize();
        }



        public JTextField getAffiliationTextField() {
            if (affiliationTextField == null) {
                affiliationTextField = new JTextField();
                affiliationTextField.setColumns(10);
            }

            return affiliationTextField;
        }


        public JTextField getEmailTextField() {
            if (emailTextField == null) {
                emailTextField = new JTextField();
                emailTextField.setColumns(10);
            }

            return emailTextField;
        }

        public JTextField getFirstNameTextField() {
            if (firstNameTextField == null) {
                firstNameTextField = new JTextField();
                firstNameTextField.setColumns(10);
            }

            return firstNameTextField;
        }

        public JTextField getLastNameTextField() {
            if (lastNameTextField == null) {
                lastNameTextField = new JTextField();
                lastNameTextField.setColumns(10);
            }

            return lastNameTextField;
        }

        private JLabel getLblAffiliationorganization() {
            if (lblAffiliationorganization == null) {
                lblAffiliationorganization = new JLabel("Affiliation/Organization:");
            }

            return lblAffiliationorganization;
        }

        private JLabel getLblEmail() {
            if (lblEmail == null) {
                lblEmail = new JLabel("Email:");
            }

            return lblEmail;
        }

        private JLabel getLblFirstName() {
            if (lblFirstName == null) {
                lblFirstName = new JLabel("First Name:");
            }

            return lblFirstName;
        }

        private JLabel getLblLastName() {
            if (lblLastName == null) {
                lblLastName = new JLabel("Last Name:");
            }

            return lblLastName;
        }

        private JLabel getLblLogin() {
            if (lblLogin == null) {
                lblLogin = new JLabel("* Login:");
            }

            return lblLogin;
        }

        private JLabel getLblPassword() {
            if (lblPassword == null) {
                lblPassword = new JLabel("* Password:");
            }

            return lblPassword;
        }

        private JLabel getLblPasswordagain() {
            if (lblPasswordagain == null) {
                lblPasswordagain = new JLabel("* Password (again):");
            }

            return lblPasswordagain;
        }

        public JTextField getLoginTextField() {
            if (loginTextField == null) {
                loginTextField = new JTextField();
                loginTextField.setColumns(10);
            }

            return loginTextField;
        }

    /**
     * Restes all the fields to empty values
     */
        public void reset() {
            getFirstNameTextField().setText("");
            getLoginTextField().setText("");
            getLastNameTextField().setText("");
            getPasswordField1().setText("");
            getPasswordField2().setText("");
            getEmailTextField().setText("");
            getAffiliationTextField().setText("");
        }

        private void initialize() {
            GroupLayout gl_panel = new GroupLayout(this);
            gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                            .addComponent(getLblAffiliationorganization())
                            .addComponent(getLblLastName())
                            .addComponent(getLblEmail())
                            .addComponent(getLblFirstName())
                            .addComponent(getLblPasswordagain())
                            .addComponent(getLblPassword())
                            .addComponent(getLblLogin())
                            .addComponent(getLblPermissions()))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                            .addComponent(getLoginTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getPasswordField1(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getPasswordField2(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getFirstNameTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getLastNameTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getEmailTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getAffiliationTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(getRoleComboBox(), 0, 277, Short.MAX_VALUE))
                        .addContainerGap())
            );
            gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblLogin())
                            .addComponent(getLoginTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblPassword())
                            .addComponent(getPasswordField1(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblPasswordagain())
                            .addComponent(getPasswordField2(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblFirstName())
                            .addComponent(getFirstNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblLastName())
                            .addComponent(getLastNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblEmail())
                            .addComponent(getEmailTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblAffiliationorganization())
                            .addComponent(getAffiliationTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(getLblPermissions())
                            .addComponent(getRoleComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
            );
            this.setLayout(gl_panel);

        }

        public JPasswordField getPasswordField1() {
            if (passwordField1 == null) {
                passwordField1 = new JPasswordField();
            }

            return passwordField1;
        }

        public JPasswordField getPasswordField2() {
            if (passwordField2 == null) {
                passwordField2 = new JPasswordField();
            }

            return passwordField2;
        }

        /**
         * @return
         */
        public UserAccount getUserAccount() {
            return userAccount;
        }

        public void setUserAccount(UserAccount userAccount) {
            this.userAccount = userAccount;
            if (userAccount == null) {
                reset();
            }
            else {
                getLoginTextField().setText(userAccount.getUserName());
                getEmailTextField().setText(userAccount.getEmail());
                getAffiliationTextField().setText(userAccount.getAffiliation());
                getFirstNameTextField().setText(userAccount.getFirstName());
                getLastNameTextField().setText(userAccount.getLastName());
                getRoleComboBox().setSelectedItem(userAccount.getRole());
                getPasswordField1().setText("");
                getPasswordField2().setText("");
            }
        }


        private JLabel getLblPermissions() {
            if (lblPermissions == null) {
                lblPermissions = new JLabel("Permissions:");
            }
            return lblPermissions;
        }
        
        public JComboBox getRoleComboBox() {
            if (roleComboBox == null) {
                roleComboBox = new JComboBox();
                UserAccountRoles[] roles = UserAccountRoles.values();
                for (int i = 0; i < roles.length; i++) {
                    roleComboBox.addItem(roles[i].toString());
                }
                roleComboBox.setSelectedItem(UserAccountRoles.READONLY.toString());
            }
            return roleComboBox;
        }
    }
