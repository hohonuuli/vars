/*
 * @(#)UpdateExistingUserDialog.java   2011.11.16 at 03:52:56 PST
 *
 * Copyright 2011 MBARI
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
import org.bushe.swing.event.EventBus;
import vars.MiscDAOFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.jpa.VarsJpaModule;
import vars.shared.ui.GlobalStateLookup;

import java.awt.Frame;

/**
 *
 * @author brian
 */
public class ModifyUserAccountDialog extends UserAccountDialog {

    private final MiscDAOFactory miscDAOFactory;

    /**
 * Constructs ...
     *
     * @param miscDAOFactory
     * @wbp.parser.constructor
     */
    public ModifyUserAccountDialog(MiscDAOFactory miscDAOFactory) {
        super();
        this.miscDAOFactory = miscDAOFactory;
        initialize();
        setDialogController(new Controller(this));
    }

    /**
     * Constructs ...
     *
     * @param parent
     * @param modal
     * @param miscDAOFactory
     */
    public ModifyUserAccountDialog(Frame parent, boolean modal, MiscDAOFactory miscDAOFactory) {
        super(parent, modal);
        this.miscDAOFactory = miscDAOFactory;
        initialize();
        setDialogController(new Controller(this));
    }

    private void initialize() {
        getPanel().getLoginTextField().setEditable(false);
        getPanel().getRoleComboBox().setEditable(false);
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
     * @param userAccount
     */
    @Override
    public void setUserAccount(UserAccount userAccount) {
        super.setUserAccount(userAccount);
        getOkayButton().setEnabled(userAccount != null);
        getPanel().setUserAccount(userAccount);
    }

    /**
     *
     * @param parent
     * @param modal
     * @param title
     * @param miscDAOFactory
     * @return
     */
    public static UserAccount showDialog(Frame parent, boolean modal, String title, MiscDAOFactory miscDAOFactory) {
        ModifyUserAccountDialog dialog = new ModifyUserAccountDialog(parent, modal, miscDAOFactory);

        dialog.setTitle(title);
        dialog.setVisible(true);

        return dialog.getUserAccount();
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
            dialog.dispose();
        }

        /**
         */
        public void doOkay() {
            String pwd1 = new String(dialog.getPanel().getPasswordField1().getPassword());
            String pwd2 = new String(dialog.getPanel().getPasswordField2().getPassword());
            pwd1 = (pwd1.equals("")) ? null : pwd1;
            pwd2 = (pwd2.equals("")) ? null : pwd2;
            boolean ok = ((pwd1 == null) && (pwd2 == null)) || pwd1.equals(pwd2);

            if (!ok) {
                getMessageLabel().setText("The passwords do not match");

                return;
            }

            try {
                UserAccountDAO dao = miscDAOFactory.newUserAccountDAO();
                dao.startTransaction();
                UserAccount userAccount = dao.findByUserName(getUserAccount().getUserName());
                if (userAccount == null) {
                    EventBus.publish(GlobalStateLookup.TOPIC_WARNING,
                            "Unable to find a user with the name '" + getUserAccount().getUserName());
                    dialog.dispose();
                }
                else {
                    if (pwd1 != null) {
                        userAccount.setPassword(pwd1);
                    }
                    userAccount.setEmail(valueOf(getPanel().getEmailTextField().getText()));
                    userAccount.setAffiliation(valueOf(getPanel().getAffiliationTextField().getText()));
                    userAccount.setFirstName(valueOf(getPanel().getFirstNameTextField().getText()));
                    userAccount.setLastName(valueOf(getPanel().getLastNameTextField().getText()));
                }

                dao.endTransaction();
                setReturnValue(userAccount);
            }
            catch (Exception e) {
                setReturnValue(null);
                log.error("Failed to update user in database", e);
                EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
            }

            dialog.dispose();

        }

        private String valueOf(String v0) {
            String v1 = null;
            if ((v0 == null) || v0.trim().isEmpty()) {

                // Do nothing
            }
            else {
                v1 = v0;
            }

            return v1;
        }
    }
}
