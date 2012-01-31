/*
 * @(#)CreateNewUserDialog.java   2010.05.20 at 09:01:54 PDT
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
import java.awt.Frame;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.UserAccountRoles;
import vars.jpa.VarsJpaModule;
import vars.shared.ui.UserAccountPreferencesPanel;

/**
 *
 *
 * @version        Enter version here..., 2010.03.15 at 10:11:58 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class CreateUserAccountDialog extends UserAccountDialog {

    private final MiscDAOFactory miscDAOFactory;
    private final MiscFactory miscFactory;

    /**
 * Constructs ...
     *
     * @param miscDAOFactory
     * @param miscFactory
 */
    public CreateUserAccountDialog(MiscDAOFactory miscDAOFactory, MiscFactory miscFactory) {
        super();
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        setDialogController(new Controller(this));
        getPanel().getRoleComboBox().setSelectedItem(UserAccountRoles.READONLY.toString());
        getPanel().getRoleComboBox().setEnabled(false);
    }

    /**
     * Constructs ...
     *
     * @param parent
     * @param modal
     * @param miscDAOFactory
     * @param miscFactory
     */
    public CreateUserAccountDialog(Frame parent, boolean modal, MiscDAOFactory miscDAOFactory, MiscFactory miscFactory) {
        super(parent, modal);
        this.miscDAOFactory = miscDAOFactory;
        this.miscFactory = miscFactory;
        setDialogController(new Controller(this));
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new VarsJpaModule("vars-jpa-annotation", "vars-jpa-knowledgebase",
            "vars-jpa-misc"));
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);
        UserAccount admin = CreateUserAccountDialog.showDialog(null, true, "VARS - Create Administrator Account",
                toolBelt.getMiscDAOFactory(), toolBelt.getMiscFactory());

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
        CreateUserAccountDialog dialog = new CreateUserAccountDialog(parent, modal, miscDAOFactory, miscFactory);

        dialog.setTitle(title);
        dialog.setVisible(true);

        return dialog.getUserAccount();
    }

    @Override
    public void setUserAccount(UserAccount userAccount) {
        throw new UnsupportedOperationException("You can't call this method on the 'CreateUserAccountDialog'!");
    }

    private class Controller implements DialogController {

        private final UserAccountDialog dialog;

        /**
         * Constructs ...
         *
         * @param dialog
         */
        public Controller(UserAccountDialog dialog) {
            this.dialog = dialog;
        }

        /**
         */
        public void doCancel() {
            dialog.setReturnValue(null);
            dialog.setVisible(false);
            dialog.getPanel().reset(); // removes text from the textfields
            dialog.dispose();
        }

        /**
         */
        public void doOkay() {
            UserAccount userAccount = null;
            final UserAccountPreferencesPanel panel = dialog.getPanel();
            String userName = panel.getLoginTextField().getText();
            String pwd1 = new String(panel.getPasswordField1().getPassword());
            String pwd2 = new String(panel.getPasswordField2().getPassword());

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
                        // TODO Check if this is causing reset ROLE issue
                        userAccount.setRole(UserAccountRoles.READONLY.toString());
                        userAccount.setEmail(panel.getEmailTextField().getText());
                        userAccount.setAffiliation(panel.getAffiliationTextField().getText());
                        userAccount.setFirstName(panel.getFirstNameTextField().getText());
                        userAccount.setLastName(panel.getLastNameTextField().getText());

                        try {
                            userAccountDAO.persist(userAccount);
                            setVisible(false);
                            dialog.getPanel().reset(); 
                        }
                        catch (Exception ex) {
                            getMessageLabel().setText(
                                "A database error occurred. Unable to insert a new user into the database");
                            log.warn("An error occured while inserting " + userName + " into the database", ex);

                            break exit;
                        }
                    }

                    userAccountDAO.endTransaction();
                    userAccountDAO.close();
                    setReturnValue(userAccount);
                }
                else {
                    getMessageLabel().setText("The passwords do not match");
                }

            }
        }
    }
}
