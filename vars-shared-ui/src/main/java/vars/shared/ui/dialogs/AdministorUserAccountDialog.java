/*
 * @(#)AdministorUserAccountDialog.java   2011.11.16 at 05:17:33 PST
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

import org.bushe.swing.event.EventBus;
import vars.MiscDAOFactory;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.shared.ui.GlobalStateLookup;
import vars.shared.ui.UserAccountComboBox;
import vars.shared.ui.UserAccountPreferencesPanel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Brian Schlining
 * @since 2011-11-16
 */
public class AdministorUserAccountDialog extends StandardDialog {

    private UserAccountComboBox comboBox;
    private final MiscDAOFactory miscDAOFactory;
    private UserAccountPreferencesPanel panel;

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     * @param miscDAOFactory
     */
    public AdministorUserAccountDialog(Frame owner, String title, boolean modal, MiscDAOFactory miscDAOFactory) {
        super(owner, title, modal);
        this.miscDAOFactory = miscDAOFactory;
        initialize();
        pack();
    }

    /**
     */
    public void doOkay() {
        String pwd1 = new String(getPanel().getPasswordField1().getPassword());
        String pwd2 = new String(getPanel().getPasswordField2().getPassword());
        pwd1 = (pwd1.equals("")) ? null : pwd1;
        pwd2 = (pwd2.equals("")) ? null : pwd2;
        boolean ok = ((pwd1 == null) && (pwd2 == null)) || pwd1.equals(pwd2);

        if (!ok) {
            return;
        }

        try {
            UserAccount userAccount = getComboBox().getSelectedUserAccount();
            if (userAccount != null) {
                UserAccountDAO dao = miscDAOFactory.newUserAccountDAO();
                dao.startTransaction();
                userAccount = dao.find(userAccount);
                if (userAccount == null) {
                    EventBus.publish(GlobalStateLookup.TOPIC_WARNING, "Unable to find a the selected user");
                    dispose();
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
            }
        }
        catch (Exception e) {
            EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
        }

        dispose();
        getComboBox().update();

    }

    public void setUserAccount(UserAccount userAccount) {
        if (userAccount != null) {
            getComboBox().setSelectedUserName(userAccount.getUserName());
        }
    }

    private UserAccountComboBox getComboBox() {
        if (comboBox == null) {
            comboBox = new UserAccountComboBox(miscDAOFactory);
            comboBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    UserAccount userAccount = comboBox.getSelectedUserAccount();
                    getPanel().setUserAccount(userAccount);
                }
            });
            comboBox.update();
        }

        return comboBox;
    }

    private UserAccountPreferencesPanel getPanel() {
        if (panel == null) {
            panel = new UserAccountPreferencesPanel();
        }

        return panel;
    }

    /**
     * @return
     */
    public UserAccount getSelectedUser() {
        return comboBox.getSelectedUserAccount();
    }

    private void initialize() {
        getContentPane().add(getComboBox(), BorderLayout.NORTH);
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        getOkayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doOkay();
            }
        });
        getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
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
