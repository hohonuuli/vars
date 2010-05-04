/*
 * @(#)LoginDialog.java   2009.10.02 at 09:00:54 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.MiscDAOFactory;
import vars.MiscFactory;
import vars.UserAccount;
import vars.UserAccountDAO;
import vars.shared.ui.FancyButton;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.UserAccountComboBox;

/**
 *
 * @author  brian
 */
public class LoginDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel msgLabel;
    private UserAccountComboBox nameComboBox;
    private javax.swing.JButton newUserButton;
    private final JDialog newUserDialog;
    private javax.swing.JButton okButton;
    private javax.swing.JPasswordField passwordField;
    private final MiscDAOFactory miscDAOFactory;

    /**
     * Creates new form LoginDialog
     *
     * @param parent
     * @param modal
     * @param userAccountDAO
     * @param miscFactory
     */
    public LoginDialog(java.awt.Frame parent, boolean modal, MiscDAOFactory miscDAOFactory, MiscFactory miscFactory) {
        super(parent, modal);
        this.miscDAOFactory = miscDAOFactory;
        newUserDialog = new CreateNewUserDialog(parent, true, miscDAOFactory, miscFactory);
        initComponents();
        setLocationRelativeTo(parent);
        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {    
        close();
    }                                                                      

    private void cancelButtonKeyReleased(java.awt.event.KeyEvent evt) {    
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cancelButtonActionPerformed(null);
        }
    }    

    private void close() {
        setVisible(false);
        msgLabel.setText(" ");
        passwordField.setText("");
        getNameComboBox().setRequestFocusEnabled(true);
    }


    UserAccountComboBox getNameComboBox() {
        if (nameComboBox == null) {
            nameComboBox = new UserAccountComboBox(miscDAOFactory);
        }

        return nameComboBox;
    }



    private void initComponents() {
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        passwordField = new javax.swing.JPasswordField();

        //nameField = new javax.swing.JTextField();
        cancelButton = new FancyButton();
        okButton = new FancyButton();
        newUserButton = new FancyButton();
        jLabel1 = new javax.swing.JLabel();
        msgLabel = new javax.swing.JLabel();

        setTitle("VARS - Login");
        setResizable(false);
        jLabel2.setText("Name:");

        jLabel3.setText("Password:");

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setText(
            "To log in, enter your name and password. If you can not\nremember your password, you can log in as a read-only\nuser by using the password 'guest'");
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        cancelButton.setText("Cancel");
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/delete2.png")));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }

        });
        cancelButton.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cancelButtonKeyReleased(evt);
            }

        });

        okButton.setText("OK");
        okButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/check2.png")));
        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }

        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                okButtonKeyReleased(evt);
            }

        });

        passwordField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed(e);
            }

        });

        newUserButton.setIcon(
            new javax.swing.ImageIcon(getClass().getResource("/vars/images/24/user1_add.png")));
        newUserButton.setFocusable(false);
        newUserButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUserButtonActionPerformed(evt);
            }

        });

        jLabel1.setText("Create a new user account");

        msgLabel.setForeground(new java.awt.Color(153, 0, 0));
        msgLabel.setText(" ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup().addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(msgLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jLabel3)
                            .add(jLabel2)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(getNameComboBox(), org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287,
                                         Short.MAX_VALUE)
                                             .add(passwordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287,
                                                 Short.MAX_VALUE)))
                                                     .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                         layout.createSequentialGroup().add(okButton)
                                                             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                 .add(cancelButton))
                                                                     .add(layout.createSequentialGroup()
                                                                         .add(newUserButton)
                                                                             .addPreferredGap(org.jdesktop.layout
                                                                                 .LayoutStyle.RELATED).add(jLabel1)))
                                                                                     .addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().addContainerGap().add(
                    jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.RELATED).add(
                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel2).add(
                            getNameComboBox(), org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel3).add(
                                    passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(15, 15, 15).add(
                                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(
                                            newUserButton).add(jLabel1)).addPreferredGap(
                                                org.jdesktop.layout.LayoutStyle.RELATED).add(msgLabel).addPreferredGap(
                                                    org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE).add(
                                                        layout.createParallelGroup(
                                                            org.jdesktop.layout.GroupLayout.BASELINE).add(
                                                                cancelButton).add(okButton)).addContainerGap()));
        pack();
    }    

    private void newUserButtonActionPerformed(java.awt.event.ActionEvent evt) {    
        setVisible(false);
        newUserDialog.setVisible(true);
    }    

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {  
        UserAccount userAccount = null;
        String userName = (String) getNameComboBox().getSelectedItem();

        String password = new String(passwordField.getPassword());
        boolean success = false;
        try {
            UserAccountDAO userAccountDAO = miscDAOFactory.newUserAccountDAO();
            userAccountDAO.startTransaction();
            userAccount = userAccountDAO.findByUserName(userName.trim());
            userAccountDAO.endTransaction();
        }
        catch (Exception e) {
            msgLabel.setText("Database connection failed");
            log.error("Failed to look up '" + userName + "'", e);
        }

        if (userAccount == null) {
            msgLabel.setText("Unable to find '" + userName + "' in the database");
        }
        else {
            if (!userAccount.authenticate(password)) {
                msgLabel.setText("Invalid password");
            }
            else {
                success = true;
            }
        }

        if (success) {
            GlobalLookup.getUserAccountDispatcher().setValueObject(userAccount);
            close();
        }

    }                                                                  

    private void okButtonKeyReleased(java.awt.event.KeyEvent evt) {    
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okButtonActionPerformed(null);
        }
    }    

    @Override
    public void setVisible(boolean b) {
        if (b) {
            updateNameComboBox();
            nameComboBox.requestFocus();
            nameComboBox.getEditor().selectAll();
        }

        super.setVisible(b);
    }

    private void updateNameComboBox() {
        try {
            getNameComboBox().update();
        }
        catch (Exception e) {
            EventBus.publish(GlobalLookup.TOPIC_NONFATAL_ERROR, e);
        }
    }
}
