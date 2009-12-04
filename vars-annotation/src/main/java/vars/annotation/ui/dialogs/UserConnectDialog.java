/*
 * @(#)UserConnectDialog.java   2009.11.19 at 08:49:58 PST
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



package vars.annotation.ui.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * <p>A dialog box for connecting a user. Basically a wrapper for UserConnectPanel,
 * but with "okay" and "cancel" buttons added in.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class UserConnectDialog extends OkayCancelDialog {

    private UserConnectPanel userConnectPanel = null;

    /**
     * This is the default constructor
     *
     * @param toolBelt
     */
    public UserConnectDialog(ToolBelt toolBelt) {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), "VARS - User Login", true);
        userConnectPanel = new UserConnectPanel(toolBelt);
        initialize();
    }

    /**
     * Bind the okay button to the text fields that should contain data before
     * the okay button can be pressed
     */
    private void bindOkayButton() {
        getUserConnectPanel().getEditPassword().addKeyListener(new KeyAdapter() {

            public void keyPressed(final KeyEvent ke) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        updateOkayButton();
                    }
                });
            }

        });
    }

    private UserConnectPanel getUserConnectPanel() {

        return userConnectPanel;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        initializeOkayButton();
        initializeContentPane();
        this.pack();
    }

    private void initializeContentPane() {
        getContentPane().add(getUserConnectPanel(), java.awt.BorderLayout.CENTER);
    }

    private void initializeOkayButton() {
        setCloseDialogOnOkay(false);
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                final boolean success = getUserConnectPanel().attemptLogin();

                // login complete, close dialog.
                if (success) {
                    UserConnectDialog.this.dispose();
                }
            }

        });

        // bind the okay button to the user name and password fields.
        // it should not be enabled when these fields are not filled in.
        bindOkayButton();

        // update the button to account for the new bindings
        updateOkayButton();
    }

    /**
     * Check the text fields which the okay button is bound to and
     * enable/disable the button accordingly.
     *
     */
    private void updateOkayButton() {
        final Object o = getUserConnectPanel().getUserComboBox().getSelectedItem();
        if ((getUserConnectPanel().getEditPassword().getPassword().length > 0) && (o != null) &&
                (o.toString().length() > 0)) {
            getOkayButton().setEnabled(true);
        }
        else {
            getOkayButton().setEnabled(false);
        }
    }
}
