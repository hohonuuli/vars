/*
 * @(#)CreateNewUserDialog.java   2010.03.15 at 10:11:58 PDT
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
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.UserAccountRoles;
import vars.VARSException;
import vars.jpa.VarsJpaModule;

/**
 *
 *
 * @version        Enter version here..., 2010.03.15 at 10:11:58 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class CreateNewUserDialog extends StandardDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Controller controller = new Controller();
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
    private JTextField messageLabel;
    private MiscDAOFactory miscDAOFactory;
    private MiscFactory miscFactory;
    private JPanel panel;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private UserAccount returnValue;

    /**
     * Constructs ...
     */
    public CreateNewUserDialog() {
        super();
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param parent
     * @param modal
     * @param miscDAOFactory
     * @param miscFactory
     */
    public CreateNewUserDialog(Frame parent, boolean modal, MiscDAOFactory miscDAOFactory, MiscFactory miscFactory) {
        super(parent, modal);
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        initialize();
    }

    private JTextField getAffiliationTextField() {
        if (affiliationTextField == null) {
            affiliationTextField = new JTextField();
            affiliationTextField.setColumns(10);
        }

        return affiliationTextField;
    }

    private JTextField getEmailTextField() {
        if (emailTextField == null) {
            emailTextField = new JTextField();
            emailTextField.setColumns(10);
        }

        return emailTextField;
    }

    private JTextField getFirstNameTextField() {
        if (firstNameTextField == null) {
            firstNameTextField = new JTextField();
            firstNameTextField.setColumns(10);
        }

        return firstNameTextField;
    }

    private JTextField getLastNameTextField() {
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

    private JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();
            loginTextField.setColumns(10);
        }

        return loginTextField;
    }

    private JTextField getMessageLabel() {
        if (messageLabel == null) {
            messageLabel = new JTextField("Create a new user. (* required field)");
            messageLabel.setEditable(false);
        }

        return messageLabel;
    }

    private JPanel getPanel() {
    	if (panel == null) {
    		panel = new JPanel();
    		GroupLayout groupLayout = new GroupLayout(panel);
    		groupLayout.setHorizontalGroup(
    			groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addContainerGap()
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addComponent(getLblAffiliationorganization())
    						.addComponent(getLblLastName())
    						.addComponent(getLblEmail())
    						.addComponent(getLblFirstName())
    						.addComponent(getLblPasswordagain())
    						.addComponent(getLblPassword())
    						.addComponent(getLblLogin()))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    						.addComponent(getLoginTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getPasswordField1(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getPasswordField2(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getFirstNameTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getLastNameTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getEmailTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
    						.addComponent(getAffiliationTextField(), GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
    					.addContainerGap())
    		);
    		groupLayout.setVerticalGroup(
    			groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addContainerGap()
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblLogin())
    						.addComponent(getLoginTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblPassword())
    						.addComponent(getPasswordField1(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblPasswordagain())
    						.addComponent(getPasswordField2(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblFirstName())
    						.addComponent(getFirstNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblLastName())
    						.addComponent(getLastNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblEmail())
    						.addComponent(getEmailTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    						.addComponent(getLblAffiliationorganization())
    						.addComponent(getAffiliationTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addContainerGap(16, Short.MAX_VALUE))
    		);
    		panel.setLayout(groupLayout);
    	}
    	return panel;
    }

    private JPasswordField getPasswordField1() {
        if (passwordField1 == null) {
            passwordField1 = new JPasswordField();
        }

        return passwordField1;
    }

    private JPasswordField getPasswordField2() {
        if (passwordField2 == null) {
            passwordField2 = new JPasswordField();
        }

        return passwordField2;
    }

    /**
     * @return
     */
    public UserAccount getReturnValue() {
        return returnValue;
    }

    private void initialize() {
        add(getMessageLabel(), BorderLayout.NORTH);
        add(getPanel(), BorderLayout.CENTER);
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.doOkay();
            }

        });
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.doCancel();
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
     * @param userAccount
     */
    public void setReturnValue(UserAccount userAccount) {
        this.returnValue = userAccount;
    }

    /**
     *
     * @param parent
     * @param modal
     * @param title
     * @param miscDAOFactory
     * @param miscFactory
     * @return
     */
    public static UserAccount showDialog(Frame parent, boolean modal, String title, MiscDAOFactory miscDAOFactory,
            MiscFactory miscFactory) {
        CreateNewUserDialog dialog = new CreateNewUserDialog(parent, modal, miscDAOFactory, miscFactory);
        dialog.setTitle(title);
        dialog.setVisible(true);
        return dialog.getReturnValue();
    }

    private class Controller {

        void doCancel() {
            returnValue = null;
            CreateNewUserDialog.this.setVisible(false);
            CreateNewUserDialog.this.dispose();
        }

        void doOkay() {
            UserAccount userAccount = null;
            String userName = getLoginTextField().getText();
            String pwd1 = new String(getPasswordField1().getPassword());
            String pwd2 = new String(getPasswordField2().getPassword());
            exit:
            {

                if (pwd1.equals(pwd2)) {
                    UserAccountDAO userAccountDAO = miscDAOFactory.newUserAccountDAO();
                    userAccountDAO.startTransaction();

                    try {
                        userAccount = userAccountDAO.findByUserName(userName);
                    }
                    catch (Exception ex) {
                        getMessageLabel().setText("An error occured while connecting to the database");
                        log.warn("An error occured while looking up " + userName, ex);
                        return;
                    }

                    if (userAccount != null) {
                        getMessageLabel().setText("The login, ' " + userName + "', already exists in the database");
                        break exit;
                    }
                    else {
                        userAccount = miscFactory.newUserAccount();
                        userAccount.setUserName(userName);
                        userAccount.setPassword(pwd1);
                        userAccount.setRole(UserAccountRoles.MAINTENANCE.toString());
                        userAccount.setEmail(getEmailTextField().getText());
                        userAccount.setAffiliation(getAffiliationTextField().getText());
                        userAccount.setFirstName(getFirstNameTextField().getText());
                        userAccount.setLastName(getLastNameTextField().getText());

                        try {
                            userAccountDAO.persist(userAccount);
                            setVisible(false);
                        }
                        catch (Exception ex) {
                            getMessageLabel().setText(
                                "A database error occurred. Unable to insert a new user into the database");
                            log.warn("An error occured while inserting " + userName + " into the database", ex);

                            break exit;
                        }
                    }

                    userAccountDAO.endTransaction();
                    returnValue = userAccount;
                }
                else {
                    getMessageLabel().setText("The passwords do not match");
                }

            }
        }
    }
}
