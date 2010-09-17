/*
 * @(#)UpdateExistingUserDialog.java   2010.05.20 at 09:30:16 PDT
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
import org.bushe.swing.event.EventBus;
import org.mbari.util.BeanUtilities;
import vars.MiscDAOFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.jpa.VarsJpaModule;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.UserAccountPreferencesPanel;

/**
 *
 * @author brian
 */
public class UpdateExistingUserDialog extends UserAccountDialog {

    private final MiscDAOFactory miscDAOFactory;

    /**
 * Constructs ...
     *
     * @param miscDAOFactory
     * @wbp.parser.constructor
     */
    public UpdateExistingUserDialog(MiscDAOFactory miscDAOFactory) {
        super();
        this.miscDAOFactory = miscDAOFactory;
        setDialogController(new Controller(this));
    }

    /**
     * Constructs ...
     *
     * @param parent
     * @param modal
     * @param miscDAOFactory
     */
    public UpdateExistingUserDialog(Frame parent, boolean modal, MiscDAOFactory miscDAOFactory) {
        super(parent, modal);
        this.miscDAOFactory = miscDAOFactory;
        setDialogController(new Controller(this));
    }


    private void initialize() {
        getPanel().getLoginTextField().setEnabled(false);
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
     * @param parent
     * @param modal
     * @param title
     * @param miscDAOFactory
     * @return
     */
    public static UserAccount showDialog(Frame parent, boolean modal, String title, MiscDAOFactory miscDAOFactory) {
        UpdateExistingUserDialog dialog = new UpdateExistingUserDialog(parent, modal, miscDAOFactory);

        dialog.setTitle(title);
        dialog.setVisible(true);

        return dialog.getUserAccount();
    }

    @Override
    public void setUserAccount(UserAccount userAccount) {
        super.setUserAccount(userAccount);
        getOkayButton().setEnabled(userAccount != null);
        final UserAccountPreferencesPanel panel = getPanel();
        String name = BeanUtilities.getProperty(userAccount, "userName", "");
        panel.getLoginTextField().setText(name);
        String email = BeanUtilities.getProperty(userAccount, "email", "");
        panel.getEmailTextField().setText(email);
        // TODO finish implementing setting UI fields when the useraccount is set
        String affiliation = BeanUtilities.getProperty(userAccount, "affiliation", "");
        panel.getAffiliationTextField().setText(affiliation);
        String firstName = BeanUtilities.getProperty(userAccount, "firstName", "");
        panel.getFirstNameTextField().setText(firstName);
        String lastName = BeanUtilities.getProperty(userAccount, "lastName", "");
        panel.getLastNameTextField().setText(lastName);
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
            boolean ok = (pwd1 == null && pwd2 == null) || pwd1.equals(pwd2);

            if (!ok) {
                getMessageLabel().setText("The passwords do not match");
                return;
            }

            try {
	            UserAccountDAO dao = miscDAOFactory.newUserAccountDAO();
	            dao.startTransaction();
	            UserAccount userAccount = dao.findByUserName(getUserAccount().getUserName());
	            if (userAccount == null) {
	                EventBus.publish(GlobalLookup.TOPIC_WARNING, "Unable to find a user with the name '" + getUserAccount().getUserName());
	                dialog.dispose();
	            }
	            userAccount.setPassword(pwd1);
	            userAccount.setEmail(valueOf(getPanel().getEmailTextField().getText()));
	            userAccount.setAffiliation(valueOf(getPanel().getAffiliationTextField().getText()));
	            userAccount.setFirstName(valueOf(getPanel().getFirstNameTextField().getText()));
	            userAccount.setLastName(valueOf(getPanel().getLastNameTextField().getText()));
	            dao.endTransaction();
	            setReturnValue(userAccount);
            }
            catch (Exception e) {
            	setReturnValue(null);
            	log.error("Failed to update user in database", e);
            	EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR, e);
            }

            dialog.dispose();
            
        }

        private String valueOf(String v0) {
            String v1 = null;
            if (v0 == null || v0.trim().isEmpty()) {
                // Do nothing
            }
            else {
                v1 = v0;
            }
            return v1;
        }
    }
}
