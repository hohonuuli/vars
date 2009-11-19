/*
 * @(#)NewUserDialog.java   2009.11.19 at 08:46:03 PST
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

package org.mbari.vars.annotation.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.JFancyButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Dialog for creating a new user for editing annotations.</p>
 */
public class NewUserDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    UserAccount newUser = null;
    private JButton cancelButton;
    private JPanel connDialogPanel;
    private JPanel dbConnPanel;
    private JPasswordField editPassword;
    private ActionListener okActionListener;
    private JButton okButton;
    private JPanel okCancelPanel;
    private final ToolBelt toolBelt;
    private JTextField userNameTextField;

    /**
     * dialog constructor -- USE THIS ONE
     *
     * @param toolBelt
     */
    public NewUserDialog(ToolBelt toolBelt) {
        this(null, "VARS - Create New VARS User Account", toolBelt);
    }

    /**
     *  dialog constructor -- show the login dialog
     *
     * @param  frame parent frame for this window
     * @param  title title for this window
     * @param toolBelt
     */
    public NewUserDialog(final Frame frame, final String title, final ToolBelt toolBelt) {
        super(frame, title, true);
        this.toolBelt = toolBelt;

        try {
            initialize();
            pack();
        }
        catch (final Exception e) {
            log.error("Failed to intialize '" + getClass().getName() + "'", e);
        }
    }

    /**
     *  Description of the Method
     *
     * @return
     */
    public UserAccount createNewUser() {
        newUser = toolBelt.getMiscFactory().newUserAccount();
        newUser.setUserName(getUserNameTextField().getText());
        newUser.setPassword(new String(getEditPassword().getPassword()));

        try {
            DAO dao = toolBelt.getMiscDAOFactory().newUserAccountDAO();
            dao.persist(newUser);
        }
        catch (final Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }

        // close the popup window
        dispose();

        return newUser;
    }

    /**
     *  shortcut method for thie class....  use this!
     *
     * @return  created vcr interface for type of login selected
     */

    public UserAccount doNewUser() {
        this.setSize(new Dimension(400, 300));

        // Center the popup window
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        setVisible(true);

        return newUser;
    }

    /**
     *     Method description
     *     @return
     */
    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton();
            cancelButton.setText("Cancel");
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
            cancelButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    dispose();
                }
            });
        }

        return cancelButton;
    }

    /**
     *     Method description
     *     @return
     */
    public JPanel getConnDialogPanel() {
        if (connDialogPanel == null) {
            connDialogPanel = new JPanel();
            final JLabel msgLabel = new JLabel();
            msgLabel.setText(
                "<html><p><font color=\"CC0000\">MBARI employees should use their email prefix " +
                "as their \'User Name\' (i.e. brian if your email is brian@mbari.org) . External " +
                "users should use their full name (i.e Brian Schlining).</font> IMPORTANT: " +
                "The password use select will be visible in the database to the database administrators.</p></html>");
            msgLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            connDialogPanel.setLayout(new BorderLayout());
            connDialogPanel.add(msgLabel, BorderLayout.CENTER);
            connDialogPanel.add(getDbConnPanel(), BorderLayout.NORTH);
            connDialogPanel.add(getOkCancelPanel(), BorderLayout.SOUTH);
        }

        return connDialogPanel;
    }

    /**
     *     Method description
     *     @return
     */
    public JPanel getDbConnPanel() {
        if (dbConnPanel == null) {
            final GridBagConstraints gridBagConstraints3 = new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            gridBagConstraints3.insets = new Insets(4, 10, 4, 4);
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.gridx = 0;
            final GridBagConstraints gridBagConstraints2 = new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            gridBagConstraints2.insets = new Insets(4, 10, 4, 4);
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.gridx = 0;
            final GridBagConstraints gridBagConstraints1 = new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.insets = new Insets(4, 4, 4, 10);
            final GridBagConstraints gridBagConstraints = new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new Insets(4, 4, 4, 10);
            dbConnPanel = new JPanel();
            dbConnPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                    new Color(148, 145, 140)), "Database"));
            dbConnPanel.setLayout(new GridBagLayout());

            final JLabel labelUserName = new JLabel();
            labelUserName.setText("User Name:");

            final JLabel labelPassword = new JLabel();
            labelPassword.setText("Password:");
            dbConnPanel.add(labelUserName, gridBagConstraints2);
            dbConnPanel.add(labelPassword, gridBagConstraints3);
            dbConnPanel.add(getEditPassword(), gridBagConstraints1);
            dbConnPanel.add(getUserNameTextField(), gridBagConstraints);
        }

        return dbConnPanel;
    }

    /**
     *     Method description
     *     @return
     */
    public JPasswordField getEditPassword() {
        if (editPassword == null) {
            editPassword = new JPasswordField();
            editPassword.setToolTipText(
                "Leave blank if you do not wish to set a password for this account.  Do not use your MBARI network password.");
            editPassword.setMinimumSize(new Dimension(220, 21));
            editPassword.setPreferredSize(new Dimension(220, 21));
            editPassword.setMaximumSize(new Dimension(220, 21));
            editPassword.setColumns(10);
        }

        return editPassword;
    }

    /**
     *     @return  the okActionListener
     */
    public ActionListener getOkActionListener() {
        if (okActionListener == null) {
            okActionListener = new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    createNewUser();
                }
            };
        }

        return okActionListener;

    }

    /**
     *     Method description
     *     @return
     */
    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new JFancyButton();
            okButton.setText("OK");
            okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));

            okButton.addActionListener(getOkActionListener());
        }

        return okButton;
    }

    /**
     *     Method description
     *     @return
     */
    public JPanel getOkCancelPanel() {
        if (okCancelPanel == null) {
            okCancelPanel = new JPanel();
            okCancelPanel.setPreferredSize(new Dimension(100, 50));
            okCancelPanel.setLayout(new BoxLayout(getOkCancelPanel(), BoxLayout.X_AXIS));
            okCancelPanel.setMinimumSize(new Dimension(100, 50));
            okCancelPanel.add(Box.createHorizontalGlue());
            okCancelPanel.add(getOkButton(), null);
            okCancelPanel.add(Box.createHorizontalStrut(10));
            okCancelPanel.add(getCancelButton(), null);
            okCancelPanel.add(Box.createHorizontalStrut(20));
        }

        return okCancelPanel;
    }

    /**
     *     Method description
     *     @return
     */
    public JTextField getUserNameTextField() {
        if (userNameTextField == null) {
            userNameTextField = new JTextField();
            userNameTextField.setText(System.getProperty("user.name"));
            userNameTextField.setMinimumSize(new Dimension(220, 21));
            userNameTextField.setPreferredSize(new Dimension(220, 21));
            userNameTextField.setMaximumSize(new Dimension(220, 21));
            userNameTextField.setColumns(10);
        }

        return userNameTextField;
    }

    private void initialize() throws Exception {

        this.getContentPane().add(getConnDialogPanel(), null);
        setSize(new Dimension(400, 300));

    }
}
