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


/*
Created on Feb 25, 2004
 */
package org.mbari.vars.annotation.locale;

import java.awt.Container;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.actions.OpenVideoArchiveUsingParamsAction;
import org.mbari.vars.annotation.ui.dialogs.OkayCancelDialog;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.util.VARSProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This dialog is used to open or create a video archive set.
 * WARNING: It's been subclasses so becarefull about the changes that you make.
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI </a>
 * @version  $Id: OpenVideoArchiveSetUsingParamsDialog.java,v 1.2 2004/04/09
 *          22:24:31 brian Exp $
 */
public class OpenVideoArchiveSetUsingParamsDialog extends OkayCancelDialog {

    /**
     *
     */
    private static final long serialVersionUID = 3089267384853022800L;
    private static final Logger log = LoggerFactory.getLogger(OpenVideoArchiveSetUsingParamsDialog.class);

    /**
     *     @uml.property  name="okButtonAction"
     *     @uml.associationEnd
     */
    protected ActionAdapter okButtonAction;    //  @jve:decl-index=0:

    /**
     *     @uml.property  name="cbCameraPlatform"
     *     @uml.associationEnd
     */
    private javax.swing.JComboBox cbCameraPlatform = null;
    private JCheckBox cbHD = null;

    /**
     *     @uml.property  name="jLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel jLabel = null;
    private JLabel jLabel1 = null;

    /**
     *     @uml.property  name="jPanel"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel = null;

    /**
     *     @uml.property  name="jPanel1"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel1 = null;

    /**
     *     @uml.property  name="jPanel3"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel3 = null;
    private JPanel jPanel4 = null;    //  @jve:decl-index=0:visual-constraint="161,368"

    /**
     *     @uml.property  name="lblCameraPlatform"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel lblCameraPlatform = null;

    /**
     *     @uml.property  name="lblDiveNumber"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel lblDiveNumber = null;

    /**
     *     @uml.property  name="tfDiveNumber"
     *     @uml.associationEnd
     */
    private javax.swing.JTextField tfDiveNumber = null;

    /**
     *     @uml.property  name="tfTapeNumber"
     *     @uml.associationEnd
     */
    private javax.swing.JTextField tfTapeNumber = null;

    /**
     * This is the default constructor
     */
    public OpenVideoArchiveSetUsingParamsDialog() {
        this(AppFrameDispatcher.getFrame());
    }

    /**
     * Constructs ...
     *
     *
     * @param owner
     */
    public OpenVideoArchiveSetUsingParamsDialog(Frame owner) {
        super(owner, "VARS - Create Video Archive", true);
        initialize();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param b
     */
    private void enableOkayButton(boolean b) {
        final JButton btn = getOkayButton();
        btn.setEnabled(b);
        btn.setFocusable(b);
        btn.setRequestFocusEnabled(b);
    }

    /**
     *     This method initializes and returns cbCameraPlatform
     *     @return   javax.swing.JComboBox
     *     @uml.property  name="cbCameraPlatform"
     */
    public javax.swing.JComboBox getCbCameraPlatform() {
        if (cbCameraPlatform == null) {
            cbCameraPlatform = new javax.swing.JComboBox();
            cbCameraPlatform.setFocusable(true);
            Collection cameraPlatforms = VARSProperties.getCameraPlatforms();
            for (Iterator i = cameraPlatforms.iterator(); i.hasNext(); ) {
                cbCameraPlatform.addItem((String) i.next());
            }
        }

        return cbCameraPlatform;
    }

    /**
     * This method initializes cbHD
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbHD() {
        if (cbHD == null) {
            cbHD = new JCheckBox();
            cbHD.setSelected(false);

            //cbHD.setEnabled(false);
        }

        return cbHD;
    }

    /**
     *     This method initializes jLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="jLabel"
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("Tape number:");
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
            jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.X_AXIS));
            jPanel.add(Box.createHorizontalStrut(10));
            jPanel.add(getLblCameraPlatform(), null);
            jPanel.add(Box.createHorizontalStrut(5));
            jPanel.add(getCbCameraPlatform(), null);
            jPanel.add(Box.createHorizontalStrut(10));
        }

        return jPanel;
    }

    /**
     *     This method initializes jPanel1
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel1"
     */
    private javax.swing.JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new javax.swing.JPanel();
            jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.X_AXIS));
            jPanel1.add(Box.createHorizontalStrut(10));
            jPanel1.add(getLblDiveNumber(), null);
            jPanel1.add(Box.createHorizontalStrut(5));
            jPanel1.add(getTfDiveNumber(), null);
            jPanel1.add(Box.createHorizontalStrut(10));
        }

        return jPanel1;
    }

    /**
     *     This method initializes jPanel3
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel3"
     */
    private javax.swing.JPanel getJPanel3() {
        if (jPanel3 == null) {
            jPanel3 = new javax.swing.JPanel();
            jPanel3.setLayout(new BoxLayout(getJPanel3(), BoxLayout.X_AXIS));
            jPanel3.add(Box.createHorizontalStrut(10));
            jPanel3.add(getJLabel(), null);
            jPanel3.add(Box.createHorizontalStrut(5));
            jPanel3.add(getTfTapeNumber(), null);
            jPanel3.add(Box.createHorizontalStrut(10));
        }

        return jPanel3;
    }

    /**
     * This method initializes jPanel4
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel4() {
        if (jPanel4 == null) {
            jLabel1 = new JLabel();
            jLabel1.setText("Check if High Definition");
            jPanel4 = new JPanel();
            jPanel4.setLayout(new BoxLayout(getJPanel4(), BoxLayout.X_AXIS));
            jPanel4.add(Box.createHorizontalStrut(10));
            jPanel4.add(jLabel1, null);
            jPanel4.add(Box.createHorizontalStrut(5));
            jPanel4.add(getCbHD(), null);
            jPanel4.add(Box.createHorizontalStrut(10));
        }

        return jPanel4;
    }

    /**
     *     This method initializes lblCameraPlatform
     *     @return   javax.swing.JLabel
     *     @uml.property  name="lblCameraPlatform"
     */
    private javax.swing.JLabel getLblCameraPlatform() {
        if (lblCameraPlatform == null) {
            lblCameraPlatform = new javax.swing.JLabel();
            lblCameraPlatform.setText("Camera platform: ");

            // Generated
        }

        return lblCameraPlatform;
    }

    /**
     *     This method initializes lblDiveNumber
     *     @return   javax.swing.JLabel
     *     @uml.property  name="lblDiveNumber"
     */
    private javax.swing.JLabel getLblDiveNumber() {
        if (lblDiveNumber == null) {
            lblDiveNumber = new javax.swing.JLabel();
            lblDiveNumber.setText("Dive number:");

            // Generated
        }

        return lblDiveNumber;
    }

    /**
     *     Gets the okButtonAction attribute of the OpenVideoArchiveSetUsingParamsDialog object
     *     @return   The okButtonAction value
     *     @uml.property  name="okButtonAction"
     */
    public ActionAdapter getOkButtonAction() {
        if (okButtonAction == null) {
            okButtonAction = new ActionAdapter() {

                private static final long serialVersionUID = 1L;
                public void doAction() {
                    int seqNumber = Integer.parseInt(getTfDiveNumber().getText());
                    String platform = (String) getCbCameraPlatform().getSelectedItem();
                    int tapeNumber = Integer.parseInt(getTfTapeNumber().getText());
                    action.setPlatform(platform);
                    action.setSeqNumber(seqNumber);
                    action.setTapeNumber(tapeNumber);
                    final String postfix = getCbHD().isSelected() ? "HD" : null;
                    action.setPostfix(postfix);
                    action.doAction();
                    dispose();
                }
                private final OpenVideoArchiveUsingParamsAction action = new OpenVideoArchiveUsingParamsAction();
            };
        }

        return okButtonAction;
    }

    /**
     *     This method initializes tfDiveNumber
     *     @return   javax.swing.JTextField
     *     @uml.property  name="tfDiveNumber"
     */
    protected javax.swing.JTextField getTfDiveNumber() {
        if (tfDiveNumber == null) {
            tfDiveNumber = new javax.swing.JTextField();
            tfDiveNumber.setFocusable(true);
            tfDiveNumber.setMinimumSize(new java.awt.Dimension(70, 19));

            // Generated
            tfDiveNumber.setPreferredSize(new java.awt.Dimension(70, 19));

            // Generated
            tfDiveNumber.addKeyListener(makeKeyListener(tfDiveNumber));
        }

        return tfDiveNumber;
    }

    /**
     *     This method initializes tfTapeNumber
     *     @return   javax.swing.JTextField
     *     @uml.property  name="tfTapeNumber"
     */
    protected javax.swing.JTextField getTfTapeNumber() {
        if (tfTapeNumber == null) {
            tfTapeNumber = new javax.swing.JTextField();
            tfTapeNumber.setPreferredSize(new java.awt.Dimension(70, 19));
            tfTapeNumber.addKeyListener(makeKeyListener(tfTapeNumber));
        }

        return tfTapeNumber;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }

        this.setSize(300, 172);
        initializeOkayButton();
        initializeContentPane();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initializeContentPane() {
        JPanel jContentPane = new JPanel();
        getContentPane().add(jContentPane, BorderLayout.CENTER);
        jContentPane.add(getJPanel(), null);

        // Generated
        jContentPane.add(getJPanel1(), null);

        // Generated
        jContentPane.add(getJPanel3(), null);
        jContentPane.add(getJPanel4(), null);
        jContentPane.setLayout(new javax.swing.BoxLayout(jContentPane, javax.swing.BoxLayout.Y_AXIS));
    }

    /**
     * This method initializes btnOk
     *
     *
     */
    private void initializeOkayButton() {
        setCloseDialogOnOkay(false);
        getOkayButton().addActionListener(getOkButtonAction());
        updateOkayButtonsEnabledProperty();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param tf
     *
     * @return
     */
    private KeyListener makeKeyListener(final JTextField tf) {
        KeyListener keyListener = new KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                final char c = e.getKeyChar();
                if (c == KeyEvent.VK_ENTER) {

                    // Do nothing. Enter is normally handled by ActionListeners
                }
                else if (Character.isDigit(c)) {
                    enableOkayButton(true);
                }
                else if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
                    final String text = tf.getText();
                    if (text.length() > 1) {
                        enableOkayButton(false);
                    }
                }
                else {
                    getToolkit().beep();
                    e.consume();
                }
            }
        };

        return keyListener;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void updateOkayButtonsEnabledProperty() {

        // Toggle the state of the ok button as
        // approapriate.
        final JButton btn = getOkayButton();
        if ((getTfDiveNumber().getText() != null) &&!getTfDiveNumber().getText().equals("") &&
                (getTfTapeNumber().getText() != null) &&!getTfTapeNumber().getText().equals("")) {
            btn.setEnabled(true);
            btn.setFocusable(true);
            btn.setRequestFocusEnabled(true);
        }
        else {
            btn.setEnabled(false);
            btn.setFocusable(false);
            btn.setRequestFocusEnabled(false);
        }
    }
}
