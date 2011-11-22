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
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.UserAccount;
import vars.jpa.VarsJpaModule;
import vars.shared.ui.UserAccountPreferencesPanel;

/**
 *
 *
 * @version        Enter version here..., 2010.03.15 at 10:11:58 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class UserAccountDialog extends StandardDialog {

    final Logger log = LoggerFactory.getLogger(getClass());
    private DialogController controller;
    private JTextField messageLabel;
    private UserAccountPreferencesPanel panel;
    private UserAccount userAccount;


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


    /**
     * @return
     */
    public DialogController getDialogController() {
        return controller;
    }



    JTextField getMessageLabel() {
        if (messageLabel == null) {
            messageLabel = new JTextField("Create a new user. (* required field)");
            messageLabel.setEditable(false);
        }

        return messageLabel;
    }

    public UserAccountPreferencesPanel getPanel() {
    	if (panel == null) {
    		panel = new UserAccountPreferencesPanel();
    	}
    	return panel;
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
        UserAccount admin = CreateUserAccountDialog.showDialog(null, true, "VARS - Create Administrator Account",
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

}
