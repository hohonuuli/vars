/*
 * @(#)UserAccountDialog.java   2010.05.20 at 08:53:35 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.dialogs;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.UserAccountRoles;
import vars.jpa.VarsJpaModule;
import javax.swing.JComboBox;

/**
 *
 *
 * @version        Enter version here..., 2010.03.15 at 10:11:58 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class UserAccountDialog extends StandardDialog {

    final Logger log = LoggerFactory.getLogger(getClass());
    private JTextField affiliationTextField;

    //private final Controller controller = new Controller();
    private DialogController controller;
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
    private JTextField messageLabel;
    private JPanel panel;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private UserAccount userAccount;
    private JLabel lblPermissions;
    private JComboBox roleComboBox;

    /**
     * Constructs ...
     */
    public UserAccountDialog() {
        super();
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param dialogController
     */
    public UserAccountDialog(DialogController dialogController) {
        super();
        this.controller = dialogController;
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param parent
     * @param modal
     */
    public UserAccountDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     *
     * @param dialogController
     * @param parent
     * @param modal
     */
    public UserAccountDialog(DialogController dialogController, Frame parent, boolean modal) {
        super(parent, modal);
        this.controller = dialogController;
        initialize();
    }

    JTextField getAffiliationTextField() {
        if (affiliationTextField == null) {
            affiliationTextField = new JTextField();
            affiliationTextField.setColumns(10);
        }

        return affiliationTextField;
    }

    /**
     * @return
     */
    public DialogController getDialogController() {
        return controller;
    }

    JTextField getEmailTextField() {
        if (emailTextField == null) {
            emailTextField = new JTextField();
            emailTextField.setColumns(10);
        }

        return emailTextField;
    }

    JTextField getFirstNameTextField() {
        if (firstNameTextField == null) {
            firstNameTextField = new JTextField();
            firstNameTextField.setColumns(10);
        }

        return firstNameTextField;
    }

    JTextField getLastNameTextField() {
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

    JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();
            loginTextField.setColumns(10);
        }

        return loginTextField;
    }

    JTextField getMessageLabel() {
        if (messageLabel == null) {
            messageLabel = new JTextField("Create a new user. (* required field)");
            messageLabel.setEditable(false);
        }

        return messageLabel;
    }

    private JPanel getPanel() {
    	if (panel == null) {
    		panel = new JPanel();
    		GroupLayout gl_panel = new GroupLayout(panel);
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
    		panel.setLayout(gl_panel);
    	}
    	return panel;
    }

    JPasswordField getPasswordField1() {
        if (passwordField1 == null) {
            passwordField1 = new JPasswordField();
        }

        return passwordField1;
    }

    JPasswordField getPasswordField2() {
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
    }

    private void initialize() {
        getContentPane().add(getMessageLabel(), BorderLayout.NORTH);
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DialogController c = getDialogController();
                if (c != null) {
                    c.doOkay();
                }
            }

        });
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DialogController c = getDialogController();
                if (c != null) {
                    c.doCancel();
                }
            }

        });
        pack();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new VarsJpaModule("vars-jpa-annotation", "vars-jpa-knowledgebase",
            "vars-jpa-misc"));
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);
        UserAccount admin = CreateNewUserDialog.showDialog(null, true, "VARS - Create Administrator Account",
            toolBelt.getMiscDAOFactory(), toolBelt.getMiscFactory());

    }

    /**
     *
     * @param dialogController
     */
    public void setDialogController(DialogController dialogController) {
        this.controller = dialogController;
    }

    /**
     *
     * @param userAccount
     */
    public void setReturnValue(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
	private JLabel getLblPermissions() {
		if (lblPermissions == null) {
			lblPermissions = new JLabel("Permissions:");
		}
		return lblPermissions;
	}
	
	JComboBox getRoleComboBox() {
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
