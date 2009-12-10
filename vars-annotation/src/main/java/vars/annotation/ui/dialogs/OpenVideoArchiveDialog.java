package vars.annotation.ui.dialogs;

import vars.shared.ui.dialogs.StandardDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Dimension;

public class OpenVideoArchiveDialog extends StandardDialog {
	private JPanel panel;
	private JLabel lblCameraPlatform;
	private JComboBox comboBox;
	private JLabel lblSequenceNumber;
	private JTextField textField;
	private JLabel lblTapeNumber;
	private JTextField textField_1;
	private JCheckBox chckbxCheckIfHighdefinition;
	
	public OpenVideoArchiveDialog() {
		initialize();
	}
	
	public void initialize() {
		setPreferredSize(new Dimension(450, 215));
//		jLabel1.setText("Camera Platform:");
//
//        cameraPlatformComboBox.setModel(new DefaultComboBoxModel(listCameraPlatforms()));
//
//        jLabel2.setText("Dive Number:");
//
//        jLabel3.setText("Tape Number:");
//
//        hdCheckBox.setText("Check if High Definition");
		getContentPane().add(getPanel_1(), BorderLayout.CENTER);
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
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(getLblCameraPlatform())
								.addGap(12)
								.addComponent(getComboBox(), 0, 331, Short.MAX_VALUE))
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(getLblSequenceNumber())
									.addComponent(getLblTapeNumber()))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(getTextField_1(), GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
									.addComponent(getTextField(), GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)))
							.addComponent(getChckbxCheckIfHighdefinition(), Alignment.TRAILING))
						.addContainerGap())
			);
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getLblCameraPlatform())
							.addComponent(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getLblSequenceNumber())
							.addComponent(getTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getLblTapeNumber())
							.addComponent(getTextField_1(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(getChckbxCheckIfHighdefinition())
						.addContainerGap(100, Short.MAX_VALUE))
			);
			panel.setLayout(groupLayout);
		}
		return panel;
	}
	private JLabel getLblCameraPlatform() {
		if (lblCameraPlatform == null) {
			lblCameraPlatform = new JLabel("Camera Platform: ");
		}
		return lblCameraPlatform;
	}
	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
		}
		return comboBox;
	}
	private JLabel getLblSequenceNumber() {
		if (lblSequenceNumber == null) {
			lblSequenceNumber = new JLabel("Sequence Number:");
		}
		return lblSequenceNumber;
	}
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}
	private JLabel getLblTapeNumber() {
		if (lblTapeNumber == null) {
			lblTapeNumber = new JLabel("Tape Number: ");
		}
		return lblTapeNumber;
	}
	private JTextField getTextField_1() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
			textField_1.setColumns(10);
		}
		return textField_1;
	}
	private JCheckBox getChckbxCheckIfHighdefinition() {
		if (chckbxCheckIfHighdefinition == null) {
			chckbxCheckIfHighdefinition = new JCheckBox("Check if High-Definition");
		}
		return chckbxCheckIfHighdefinition;
	}
}
