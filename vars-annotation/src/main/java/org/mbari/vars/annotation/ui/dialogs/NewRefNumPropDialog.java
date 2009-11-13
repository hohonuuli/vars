/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui.dialogs;

import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JTextField;
import org.mbari.swing.JFancyButton;
import org.mbari.vars.annotation.ui.actions.AddNewRefNumPropAction;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: NewRefNumPropDialog.java 378 2006-10-26 20:53:17Z hohonuuli $
 */
public class NewRefNumPropDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1058890312877879823L;
    private static final Logger log = LoggerFactory.getLogger(NewRefNumPropDialog.class);

    /**
     *     @uml.property  name="btnCancel"
     *     @uml.associationEnd
     */
    private javax.swing.JButton btnCancel = null;

    /**
     *     @uml.property  name="btnOk"
     *     @uml.associationEnd
     */
    private javax.swing.JButton btnOk = null;

    /**
     *     @uml.property  name="jContentPane"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jContentPane = null;

    /**
     *     @uml.property  name="jLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel jLabel = null;

    /**
     *     @uml.property  name="jPanel"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel = null;

    /**
     *     @uml.property  name="jTextField"
     *     @uml.associationEnd
     */
    private javax.swing.JTextField jTextField = null;

    /**
     * This is the default constructor
     */
    public NewRefNumPropDialog() {
        super(AppFrameDispatcher.getFrame(), true);
        initialize();
    }

    /**
     *     This method initializes btnCancel
     *     @return   javax.swing.JButton
     *     @uml.property  name="btnCancel"
     */
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

    /**
     *     This method initializes btnOk
     *     @return   javax.swing.JButton
     *     @uml.property  name="btnOk"
     */
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

    /**
     *     This method initializes jContentPane
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jContentPane"
     */
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

    /**
     *     This method initializes jLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="jLabel"
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("Enter a Reference Number");
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        }

        return jLabel;
    }

    /**
     *     This method initializes jPanel
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel"
     */
    private javax.swing.JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new javax.swing.JPanel();
            jPanel.add(getBtnOk(), null);
            jPanel.add(getBtnCancel(), null);
        }

        return jPanel;
    }

    /**
     *     This method initializes jTextField
     *     @return   javax.swing.JTextField
     *     @uml.property  name="jTextField"
     */
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

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        this.setSize(250, 100);
        this.setContentPane(getJContentPane());
        this.setTitle("VARS - Input");
    }

    /**
     * Method description
     *
     *
     * @param b
     */
    @Override public void setVisible(final boolean b) {
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
    @Override @SuppressWarnings(value = { "deprecated}" })
    @Deprecated public void show() {
        setVisible(true);
    }
}
