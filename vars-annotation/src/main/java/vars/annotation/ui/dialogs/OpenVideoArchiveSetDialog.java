/*
 * @(#)OpenVideoArchiveSetDialog.java   2010.01.19 at 03:23:56 PST
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;
import vars.shared.ui.dialogs.StandardDialog;

/**
 *
 *
 * @version        Enter version here..., 2010.01.19 at 03:23:56 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class OpenVideoArchiveSetDialog extends StandardDialog {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ItemListener rbItemListener = new SelectedRBItemListener();
    private JComboBox cameraPlatformComboBox;
    private JComboBox existingNamesConceptBox;
    private JCheckBox hdCheckBox;
    private JLabel lblCameraPlatform;
    private JLabel lblName;
    private JLabel lblSelectName;
    private JLabel lblSequenceNumber;
    private JTextField nameTextField;
    private JRadioButton openByNameRB;
    private JRadioButton openByPlatformRB;
    private JRadioButton openExistingRB;
    private JPanel panel;
    private JTextField sequenceNumberTextField;

    private enum OpenType { BY_PARAMS, BY_NAME, EXISTING; }


    /**
     * Constructs ...
     */
    public OpenVideoArchiveSetDialog() {
        initialize();
    }

    private JComboBox getCameraPlatformComboBox() {
        if (cameraPlatformComboBox == null) {
            cameraPlatformComboBox = new JComboBox();
        }

        return cameraPlatformComboBox;
    }

    private JComboBox getExistingNamesConceptBox() {
        if (existingNamesConceptBox == null) {
            existingNamesConceptBox = new JComboBox();
        }

        return existingNamesConceptBox;
    }

    private JCheckBox getHdCheckBox() {
        if (hdCheckBox == null) {
            hdCheckBox = new JCheckBox("Check if High Definition");
        }

        return hdCheckBox;
    }

    private JLabel getLblCameraPlatform() {
        if (lblCameraPlatform == null) {
            lblCameraPlatform = new JLabel("Camera Platform:");
        }

        return lblCameraPlatform;
    }

    private JLabel getLblName() {
        if (lblName == null) {
            lblName = new JLabel("Name:");
        }

        return lblName;
    }

    private JLabel getLblSelectName() {
        if (lblSelectName == null) {
            lblSelectName = new JLabel("Select Name:");
        }

        return lblSelectName;
    }

    private JLabel getLblSequenceNumber() {
        if (lblSequenceNumber == null) {
            lblSequenceNumber = new JLabel("Sequence Number:");
        }

        return lblSequenceNumber;
    }

    private JTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new JTextField();
            nameTextField.setColumns(10);
        }

        return nameTextField;
    }

    private JRadioButton getOpenByNameRB() {
        if (openByNameRB == null) {
            openByNameRB = new JRadioButton("Open by Name");
            openByNameRB.setName(OpenType.BY_NAME.name());
            openByNameRB.addItemListener(rbItemListener);
        }

        return openByNameRB;
    }

    private JRadioButton getOpenByPlatformRB() {
        if (openByPlatformRB == null) {
            openByPlatformRB = new JRadioButton("Open by Platform and Sequence Number");
            openByPlatformRB.setName(OpenType.BY_PARAMS.name());
            openByPlatformRB.addItemListener(rbItemListener);
        }

        return openByPlatformRB;
    }

    private JRadioButton getOpenExistingRB() {
        if (openExistingRB == null) {
            openExistingRB = new JRadioButton("Open Existing");
            openExistingRB.setName(OpenType.EXISTING.name());
            openExistingRB.addItemListener(rbItemListener);
        }

        return openExistingRB;
    }

    private JPanel getPanel_1() {
            if (panel == null) {
                    panel = new JPanel();
                    GroupLayout groupLayout = new GroupLayout(panel);
                    groupLayout.setHorizontalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                    .addComponent(getOpenByPlatformRB())
                                                    .addComponent(getOpenByNameRB())
                                                    .addComponent(getOpenExistingRB())
                                                    .addGroup(groupLayout.createSequentialGroup()
                                                            .addGap(29)
                                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                    .addComponent(getLblSequenceNumber())
                                                                    .addComponent(getLblCameraPlatform())
                                                                    .addComponent(getLblName())
                                                                    .addComponent(getLblSelectName()))
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                    .addComponent(getCameraPlatformComboBox(), 0, 286, Short.MAX_VALUE)
                                                                    .addComponent(getSequenceNumberTextField(), GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                                                    .addComponent(getHdCheckBox())
                                                                    .addComponent(getNameTextField(), Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                                                    .addComponent(getExistingNamesConceptBox(), 0, 286, Short.MAX_VALUE))))
                                            .addContainerGap())
                    );
                    groupLayout.setVerticalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(getOpenByPlatformRB())
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblCameraPlatform())
                                                    .addComponent(getCameraPlatformComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblSequenceNumber())
                                                    .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(getHdCheckBox())
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(getOpenByNameRB())
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblName())
                                                    .addComponent(getNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(getOpenExistingRB())
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblSelectName())
                                                    .addComponent(getExistingNamesConceptBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addContainerGap(57, Short.MAX_VALUE))
                    );
                    panel.setLayout(groupLayout);

                    buttonGroup.add(getOpenByNameRB());
                    buttonGroup.add(getOpenByPlatformRB());
                    buttonGroup.add(getOpenExistingRB());
                    buttonGroup.setSelected(getOpenByPlatformRB().getModel(), true);

                    getOkayButton().addActionListener(new OpenActionListener());

            }
            return panel;
    }

    private JTextField getSequenceNumberTextField() {
        if (sequenceNumberTextField == null) {
            sequenceNumberTextField = new JTextField();
            sequenceNumberTextField.setColumns(10);
        }

        return sequenceNumberTextField;
    }

    protected void initialize() {
        getContentPane().add(getPanel_1(), BorderLayout.CENTER);
    }

    class OpenActionListener implements ActionListener {

        /**
         *
         * @param ev
         */
        public void actionPerformed(ActionEvent ev) {
            Enumeration<AbstractButton> e = buttonGroup.getElements();
            OpenType openType = null;
            while (e.hasMoreElements()) {
                AbstractButton b = e.nextElement();
                if (b.isSelected()) {
                    openType = OpenType.valueOf(b.getName());
                    break;
                }
            }

            switch (openType) {
            case BY_NAME:
                break;
            case BY_PARAMS:
                break;
            case EXISTING:
                break;
            default:
            }


        }
    }


    /**
     *
     * @author brian
     *
     */
    class SelectedRBItemListener implements ItemListener {

        /**
         *
         * @param e
         */
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JRadioButton radioButton = (JRadioButton) e.getItemSelectable();
                OpenType openType = OpenType.valueOf(radioButton.getName());
                boolean cameraPlatformCB = false;
                boolean sequenceNumberTF = false;
                boolean hdChckB = false;
                boolean nameTF = false;
                boolean existingNamesCB = false;
                switch (openType) {
                case BY_NAME:
                    nameTF = true;
                    break;
                case BY_PARAMS:
                    cameraPlatformCB = true;
                    sequenceNumberTF = true;
                    hdChckB = true;
                    break;
                case EXISTING:
                    existingNamesCB = true;
                    break;
                default:

                // Falls through
                }

                getCameraPlatformComboBox().setEnabled(cameraPlatformCB);
                getSequenceNumberTextField().setEnabled(sequenceNumberTF);
                getHdCheckBox().setEnabled(hdChckB);
                getNameTextField().setEnabled(nameTF);
                getExistingNamesConceptBox().setEnabled(existingNamesCB);

            }

        }
    }
}
