package vars.avplayer;

import mbarix4j.awt.event.NonDigitConsumingKeyListener;
import mbarix4j.swing.SpinningDialWaitIndicator;
import mbarix4j.swing.WaitIndicator;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.ToolBelt;


import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * @author Brian Schlining
 * @since 2016-04-19T10:30:00
 */
public class OpenVideoArchivePanel extends JPanel {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ItemListener rbItemListener = new SelectedRBItemListener();
    private JComboBox<String> cameraPlatformComboBox;
    private JComboBox<String> existingNamesComboBox;
    private boolean loadExistingNames = true;
    private JCheckBox hdCheckBox;
    private JLabel lblCameraPlatform;
    private JLabel lblName;
    private JLabel lblSelectName;
    private JLabel lblSequenceNumber;
    private JLabel lblTapeNumber;
    private JTextField nameTextField;
    private JRadioButton openByNameRB;
    private JRadioButton openByPlatformRB;
    private JRadioButton openExistingRB;
    private JTextField sequenceNumberTextField;
    private JTextField tapeNumberTextField;
    private JComboBox<String> cameraPlatformByNameComboBox;

    private enum OpenType { BY_PARAMS, BY_NAME, EXISTING; }

    private final ToolBelt toolBelt;
    private JTextField sequenceNumberByNameTextField;

    public OpenVideoArchivePanel(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        initialize();
        if (toolBelt != null) {
            toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {
                public void afterClear(CacheClearedEvent evt) {
                    loadExistingNames = true;
                }

                public void beforeClear(CacheClearedEvent evt) {
                    DefaultComboBoxModel model = (DefaultComboBoxModel) getExistingNamesComboBox().getModel();
                    model.removeAllElements();
                }
            });
        }
    }

    public ButtonGroup getButtonGroup() {
        return buttonGroup;
    }

    public JComboBox<String> getCameraPlatformComboBox() {
        if (cameraPlatformComboBox == null) {
            cameraPlatformComboBox = new JComboBox<>();
            cameraPlatformComboBox.setModel(new DefaultComboBoxModel<>(listCameraPlatforms()));
        }

        return cameraPlatformComboBox;
    }

    public JComboBox<String> getCameraPlatformByNameComboBox() {
        if (cameraPlatformByNameComboBox == null) {
            cameraPlatformByNameComboBox = new JComboBox<>();
            cameraPlatformByNameComboBox.setModel(new DefaultComboBoxModel<>(listCameraPlatforms()));
        }

        return cameraPlatformByNameComboBox;
    }

    public JComboBox<String> getExistingNamesComboBox() {
        if (existingNamesComboBox == null) {
            existingNamesComboBox = new JComboBox<>();
        }

        return existingNamesComboBox;
    }

    public JCheckBox getHdCheckBox() {
        if (hdCheckBox == null) {
            hdCheckBox = new JCheckBox("Check if High Definition");
            hdCheckBox.setSelected(true);
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

    private JLabel getLblTapeNumber() {
        if (lblTapeNumber == null) {
            lblTapeNumber = new JLabel("Tape Number:");
        }

        return lblTapeNumber;
    }

    public JTextField getNameTextField() {
        if (nameTextField == null) {
            nameTextField = new JTextField();
            nameTextField.setColumns(10);
        }

        return nameTextField;
    }

    public JRadioButton getOpenByNameRB() {
        if (openByNameRB == null) {
            openByNameRB = new JRadioButton("Open by Name");
            openByNameRB.setName(OpenType.BY_NAME.name());
            openByNameRB.addItemListener(rbItemListener);
            //openByNameRB.setEnabled(false); // TODO have to look into implementing this. there are some gotchas
        }

        return openByNameRB;
    }

    public JRadioButton getOpenByPlatformRB() {
        if (openByPlatformRB == null) {
            openByPlatformRB = new JRadioButton("Open by Platform and Sequence Number");
            openByPlatformRB.setName(OpenType.BY_PARAMS.name());
            openByPlatformRB.addItemListener(rbItemListener);
        }

        return openByPlatformRB;
    }

    public JRadioButton getOpenExistingRB() {
        if (openExistingRB == null) {
            openExistingRB = new JRadioButton("Open Existing");
            openExistingRB.setName(OpenType.EXISTING.name());
            openExistingRB.addItemListener(rbItemListener);
            openExistingRB.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (loadExistingNames) {
                        JComboBox<String> comboBox = getExistingNamesComboBox();
                        WaitIndicator waitIndicator = new SpinningDialWaitIndicator(comboBox);
                        java.util.List<String> names = toolBelt.getAnnotationPersistenceService().findAllVideoArchiveNames();
                        String[] van = new String[names.size()];
                        names.toArray(van);
                        comboBox.setModel(new DefaultComboBoxModel<String>(van));
                        waitIndicator.dispose();
                        loadExistingNames = false;
                    }
                }
            });
        }

        return openExistingRB;
    }



    public JTextField getSequenceNumberTextField() {
        if (sequenceNumberTextField == null) {
            sequenceNumberTextField = new JTextField();
            sequenceNumberTextField.setColumns(10);
            sequenceNumberTextField.addKeyListener(new NonDigitConsumingKeyListener());
        }

        return sequenceNumberTextField;
    }

    public JTextField getTapeNumberTextField() {
        if (tapeNumberTextField == null) {
            tapeNumberTextField = new JTextField();
            tapeNumberTextField.setColumns(10);
            tapeNumberTextField.addKeyListener(new NonDigitConsumingKeyListener());
        }

        return tapeNumberTextField;
    }

    protected void initialize() {
        JLabel cameraByNameLabel = new JLabel("Camera Platform:");
        JLabel sequenceNumberByNameLabel = new JLabel("Sequence Number:");


        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(getOpenByPlatformRB())
                                        .addComponent(getOpenByNameRB())
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(getOpenExistingRB())
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGap(29)
                                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(cameraByNameLabel)
                                                                        .addComponent(getLblName())
                                                                        .addComponent(sequenceNumberByNameLabel)))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGap(29)
                                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(getLblCameraPlatform())
                                                                        .addComponent(getLblSequenceNumber())
                                                                        .addComponent(getLblTapeNumber())))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGap(29)
                                                                .addComponent(getLblSelectName())))
                                                .addGap(18)
                                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(getCameraPlatformByNameComboBox(), 0, 299, Short.MAX_VALUE)
                                                        .addComponent(getNameTextField(), GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                                                        .addComponent(getSequenceNumberByNameTextField(), GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                                                        .addComponent(getExistingNamesComboBox(), 0, 299, Short.MAX_VALUE)
                                                        .addComponent(getSequenceNumberTextField(), GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                                                        .addComponent(getTapeNumberTextField(), GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                                                        .addComponent(getCameraPlatformComboBox(), 0, 299, Short.MAX_VALUE)
                                                        .addComponent(getHdCheckBox()))))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(getOpenByPlatformRB())
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblCameraPlatform())
                                        .addComponent(getCameraPlatformComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblSequenceNumber())
                                        .addComponent(getSequenceNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblTapeNumber())
                                        .addComponent(getTapeNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getHdCheckBox())
                                .addGap(4)
                                .addComponent(getOpenByNameRB())
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(getLblName()))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getCameraPlatformByNameComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cameraByNameLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sequenceNumberByNameLabel)
                                        .addComponent(getSequenceNumberByNameTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(getOpenExistingRB())
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(getLblSelectName())
                                        .addComponent(getExistingNamesComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addContainerGap())
        );

        this.setLayout(groupLayout);

        buttonGroup.add(getOpenByNameRB());
        buttonGroup.add(getOpenByPlatformRB());
        buttonGroup.add(getOpenExistingRB());
        buttonGroup.setSelected(getOpenByPlatformRB().getModel(), true);
    }

    public String[] listCameraPlatforms() {
        String[] cp = {};
        try {
            final java.util.List<String> cameraPlatforms = toolBelt.getAnnotationPersistenceService().findAllCameraPlatforms();
            cp = new String[cameraPlatforms.size()];
            cameraPlatforms.toArray(cp);
        }
        catch (NullPointerException e) {

        }
        return cp;
    }

    /**
     * This method call opens/creates a videoArchive based on the parameters
     * a user has set in this Dialog. This method is intended to be called from
     * an ActionListener to retrieve the VideoArchiveSet.
     * @return
     */

    public JTextField getSequenceNumberByNameTextField() {
        if (sequenceNumberByNameTextField == null) {
            sequenceNumberByNameTextField = new JTextField();
            sequenceNumberByNameTextField.setColumns(10);
            sequenceNumberByNameTextField.addKeyListener(new NonDigitConsumingKeyListener());
        }
        return sequenceNumberByNameTextField;
    }



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
                boolean tapeNumberTF = false;

                boolean cameraPlatformByNameCB = false;
                boolean sequenceNumberByNameTF = false;
                switch (openType) {
                    case BY_NAME:
                        nameTF = true;
                        cameraPlatformByNameCB = true;
                        sequenceNumberByNameTF = true;
                        break;
                    case BY_PARAMS:
                        cameraPlatformCB = true;
                        sequenceNumberTF = true;
                        tapeNumberTF = true;
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
                getCameraPlatformByNameComboBox().setEnabled(cameraPlatformByNameCB);
                getSequenceNumberByNameTextField().setEnabled(sequenceNumberByNameTF);
                getExistingNamesComboBox().setEnabled(existingNamesCB);
                getTapeNumberTextField().setEnabled(tapeNumberTF);

            }

        }
    }
}
