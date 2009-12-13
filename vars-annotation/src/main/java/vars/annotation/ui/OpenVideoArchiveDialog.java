package vars.annotation.ui;

import vars.shared.ui.dialogs.StandardDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import org.mbari.awt.event.NonDigitConsumingKeyListener;
import org.mbari.text.IgnoreCaseToStringComparator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;

public class OpenVideoArchiveDialog extends StandardDialog {
	private JPanel panel;
	private JLabel lblCameraPlatform;
	private JComboBox comboBox;
	private JLabel lblSequenceNumber;
	private JTextField seqNumberTextField;
	private JLabel lblTapeNumber;
	private JTextField tapeNumberTextField;
	private JCheckBox chckbxHD;
	private final PersistenceController persistenceController;
	private final Controller controller;
	
	public OpenVideoArchiveDialog(final Window parent, final PersistenceController persistenceController) {
		super(parent);
		this.persistenceController = persistenceController;
		this.controller = new Controller();
		initialize();
		pack();
	}
	
	public void initialize() {
		setPreferredSize(new Dimension(450, 215));
		getOkayButton().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				controller.open();
				dispose();
			}
		});
		
		getCancelButton().addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		getContentPane().add(getPanel(), BorderLayout.CENTER);
	}

	private JPanel getPanel() {
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
									.addComponent(getTapeNumberTextField(), GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
									.addComponent(getSeqNumberTextField(), GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)))
							.addComponent(getChckbxHD(), Alignment.TRAILING))
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
							.addComponent(getSeqNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getLblTapeNumber())
							.addComponent(getTapeNumberTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(getChckbxHD())
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
			comboBox.setModel(new DefaultComboBoxModel(controller.listCameraPlatforms()));
		}
		return comboBox;
	}
	private JLabel getLblSequenceNumber() {
		if (lblSequenceNumber == null) {
			lblSequenceNumber = new JLabel("Sequence Number:");
		}
		return lblSequenceNumber;
	}
	private JTextField getSeqNumberTextField() {
		if (seqNumberTextField == null) {
			seqNumberTextField = new JTextField();
			seqNumberTextField.setColumns(10);
			seqNumberTextField.addKeyListener(new NonDigitConsumingKeyListener());
		}
		return seqNumberTextField;
	}
	private JLabel getLblTapeNumber() {
		if (lblTapeNumber == null) {
			lblTapeNumber = new JLabel("Tape Number: ");
		}
		return lblTapeNumber;
	}
	private JTextField getTapeNumberTextField() {
		if (tapeNumberTextField == null) {
			tapeNumberTextField = new JTextField();
			tapeNumberTextField.setColumns(10);
			tapeNumberTextField.addKeyListener(new NonDigitConsumingKeyListener());
			tapeNumberTextField.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						getOkayButton().doClick();
					}
					super.keyReleased(e);
				}
				
			});
		}
		return tapeNumberTextField;
	}
	private JCheckBox getChckbxHD() {
		if (chckbxHD == null) {
			chckbxHD = new JCheckBox("Check if High-Definition");
			chckbxHD.addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						getOkayButton().doClick();
					}
					super.keyReleased(e);
				}
				
			});
		}
		return chckbxHD;
	}
	

	/**
	 * Non-layout UI controller code.
	 * @author brian
	 *
	 */
	private class Controller {

		private String[] listCameraPlatforms() {
			final Collection<String> cameraPlatforms = VARSProperties.getCameraPlatforms();
			String[] cp = new String[cameraPlatforms.size()];
			cameraPlatforms.toArray(cp);
			Arrays.sort(cp, new IgnoreCaseToStringComparator());
			return cp;
		}

		public void open() {

			int seqNumber = Integer.parseInt(getSeqNumberTextField().getText());
			String platform = (String) getComboBox().getSelectedItem();
			int tapeNumber = Integer.parseInt(getTapeNumberTextField()
					.getText());
			final String postfix = getChckbxHD().isSelected() ? "HD" : null;
			String name = PersistenceController.makeVideoArchiveName(platform,
					seqNumber, tapeNumber, postfix);
			persistenceController.openVideoArchive(platform, seqNumber, name);

		}

	}
}
