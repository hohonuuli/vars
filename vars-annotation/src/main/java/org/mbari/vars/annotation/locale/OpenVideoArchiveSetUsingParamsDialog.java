/*
 * @(#)OpenVideoArchiveSetUsingParamsDialog.java   2009.11.20 at 03:41:39 PST
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



package org.mbari.vars.annotation.locale;

import java.awt.BorderLayout;
import java.awt.Frame;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.ui.Lookup;
import vars.util.VARSProperties;

/**
 * <p>
 * This dialog is used to open or create a video archive set.
 * WARNING: It's been subclasses so becarefull about the changes that you make.
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI </a>
 */
public class OpenVideoArchiveSetUsingParamsDialog extends OkayCancelDialog {

    private javax.swing.JComboBox cbCameraPlatform = null;
    private JCheckBox cbHD = null;
    private javax.swing.JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private javax.swing.JPanel jPanel = null;
    private javax.swing.JPanel jPanel1 = null;
    private javax.swing.JPanel jPanel3 = null;
    private JPanel jPanel4 = null;
    private javax.swing.JLabel lblCameraPlatform = null;
    private javax.swing.JLabel lblDiveNumber = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private javax.swing.JTextField tfDiveNumber = null;
    private javax.swing.JTextField tfTapeNumber = null;
    private final AnnotationDAOFactory annotationDAOFactory;
    protected ActionAdapter okButtonAction;

    /**
     * This is the default constructor
     *
     * @param annotationDAOFactory
     */
    public OpenVideoArchiveSetUsingParamsDialog(AnnotationDAOFactory annotationDAOFactory) {
        this((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), annotationDAOFactory);
    }

    /**
     * Constructs ...
     *
     *
     * @param owner
     * @param annotationDAOFactory
     */
    public OpenVideoArchiveSetUsingParamsDialog(Frame owner, AnnotationDAOFactory annotationDAOFactory) {
        super(owner, "VARS - Create Video Archive", true);
        this.annotationDAOFactory = annotationDAOFactory;
        initialize();
    }

    private void enableOkayButton(boolean b) {
        final JButton btn = getOkayButton();
        btn.setEnabled(b);
        btn.setFocusable(b);
        btn.setRequestFocusEnabled(b);
    }

    /**
     *     This method initializes and returns cbCameraPlatform
     *     @return   javax.swing.JComboBox
     */
    @SuppressWarnings("unchecked")
    public javax.swing.JComboBox getCbCameraPlatform() {
        if (cbCameraPlatform == null) {
            cbCameraPlatform = new javax.swing.JComboBox();
            cbCameraPlatform.setFocusable(true);
            Collection<String> cameraPlatforms = VARSProperties.getCameraPlatforms();
            for (Iterator i = cameraPlatforms.iterator(); i.hasNext(); ) {
                cbCameraPlatform.addItem(i.next());
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
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("Tape number:");
        }

        return jLabel;
    }

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

    private javax.swing.JLabel getLblCameraPlatform() {
        if (lblCameraPlatform == null) {
            lblCameraPlatform = new javax.swing.JLabel();
            lblCameraPlatform.setText("Camera platform: ");

            // Generated
        }

        return lblCameraPlatform;
    }

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
     */
    public ActionAdapter getOkButtonAction() {
        if (okButtonAction == null) {
            okButtonAction = new ActionAdapter() {

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
                private final OpenVideoArchiveUsingParamsAction action = new OpenVideoArchiveUsingParamsAction(
                    annotationDAOFactory);
            };
        }

        return okButtonAction;
    }

    protected javax.swing.JTextField getTfDiveNumber() {
        if (tfDiveNumber == null) {
            tfDiveNumber = new javax.swing.JTextField();
            tfDiveNumber.setFocusable(true);
            tfDiveNumber.setMinimumSize(new java.awt.Dimension(70, 19));

            tfDiveNumber.setPreferredSize(new java.awt.Dimension(70, 19));

            tfDiveNumber.addKeyListener(makeKeyListener(tfDiveNumber));
        }

        return tfDiveNumber;
    }

    /**
     *     This method initializes tfTapeNumber
     *     @return   javax.swing.JTextField
     */
    protected javax.swing.JTextField getTfTapeNumber() {
        if (tfTapeNumber == null) {
            tfTapeNumber = new javax.swing.JTextField();
            tfTapeNumber.setPreferredSize(new java.awt.Dimension(70, 19));
            tfTapeNumber.addKeyListener(makeKeyListener(tfTapeNumber));
        }

        return tfTapeNumber;
    }

    private void initialize() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing " + getClass().getName());
        }

        this.setSize(300, 172);
        initializeOkayButton();
        initializeContentPane();
    }

    private void initializeContentPane() {
        JPanel jContentPane = new JPanel();
        getContentPane().add(jContentPane, BorderLayout.CENTER);
        jContentPane.add(getJPanel(), null);

        jContentPane.add(getJPanel1(), null);

        jContentPane.add(getJPanel3(), null);
        jContentPane.add(getJPanel4(), null);
        jContentPane.setLayout(new javax.swing.BoxLayout(jContentPane, javax.swing.BoxLayout.Y_AXIS));
    }

    private void initializeOkayButton() {
        setCloseDialogOnOkay(false);
        getOkayButton().addActionListener(getOkButtonAction());
        updateOkayButtonsEnabledProperty();
    }

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

    private void updateOkayButtonsEnabledProperty() {

        // Toggle the state of the ok button as appropriate.
        final JButton btn = getOkayButton();
        if ((getTfDiveNumber().getText() != null) && !getTfDiveNumber().getText().equals("") &&
                (getTfTapeNumber().getText() != null) && !getTfTapeNumber().getText().equals("")) {
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
