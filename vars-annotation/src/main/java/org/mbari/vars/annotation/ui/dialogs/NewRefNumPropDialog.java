/*
 * @(#)NewRefNumPropDialog.java   2009.11.19 at 08:40:34 PST
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

import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import org.mbari.swing.JFancyButton;
import org.mbari.vars.annotation.ui.actions.AddNewRefNumPropAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;

/**
 * <p>This dialog is used to set the Reference Number property for the
 * <code>org.mbari.vars.annotation.ui.action.AddNewRefNumPropAction.</code> The
 * reference number is the link value in a 'identity-reference|self|[reference number]"
 * association</p>
 *
 *
 * <h2><u>UML</u></h2>
 * <pre>
 *
 *  [NewRefNumPropDialog]
 *     |
 *     |
 *     |
 *  [AddNewRefNumPropAction]&lt--[NewRefNumPropButton]
 * </pre>
 *
 */
public class NewRefNumPropDialog extends JDialog {

    private javax.swing.JButton btnCancel = null;
    private javax.swing.JButton btnOk = null;
    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JLabel jLabel = null;
    private javax.swing.JPanel jPanel = null;
    private javax.swing.JTextField jTextField = null;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is the default constructor
     */
    public NewRefNumPropDialog() {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), true);
        initialize();
    }

    private javax.swing.JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new JFancyButton();
            btnCancel.setText("Cancel");
            btnCancel.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }

        return btnCancel;
    }

    private javax.swing.JButton getBtnOk() {
        if (btnOk == null) {
            btnOk = new JFancyButton();
            btnOk.setText("OK");
            btnOk.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    try {
                        AddNewRefNumPropAction.setRefNumber(Integer.parseInt(getJTextField().getText()));
                    }
                    catch (final Exception ex) {
                        log.warn("Unable to convert the contents of the dialog to an integer.");
                    }

                    dispose();
                }

            });
        }

        return btnOk;
    }

    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new javax.swing.BoxLayout(jContentPane, javax.swing.BoxLayout.Y_AXIS));
            jContentPane.add(getJLabel(), null);
            jContentPane.add(getJTextField(), null);
            jContentPane.add(getJPanel(), null);
        }

        return jContentPane;
    }

    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("Enter a Reference Number");
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        }

        return jLabel;
    }

    private javax.swing.JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new javax.swing.JPanel();
            jPanel.add(getBtnOk(), null);
            jPanel.add(getBtnCancel(), null);
        }

        return jPanel;
    }

    private javax.swing.JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new javax.swing.JTextField();
            jTextField.setPreferredSize(new java.awt.Dimension(80, 19));
            jTextField.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyTyped(final KeyEvent e) {
                    final char c = e.getKeyChar();
                    if (!((Character.isDigit(c)) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        getToolkit().beep();
                        e.consume();
                    }
                }

            });
        }

        return jTextField;
    }

    private void initialize() {
        this.setSize(250, 100);
        this.setContentPane(getJContentPane());
        this.setTitle("VARS - Input");
    }

    /**
     *
     * @param b
     */
    @Override
    public void setVisible(final boolean b) {
        super.setVisible(b);

        if (b) {
            final JTextField tf = getJTextField();
            tf.setText(AddNewRefNumPropAction.getRefNumber() + "");
            tf.setRequestFocusEnabled(true);
            tf.requestFocus();
        }
    }

    /**
     *  Overridden show method.
     * @deprecated
     */
    @Override
    @Deprecated
    public void show() {
        setVisible(true);
    }
}
