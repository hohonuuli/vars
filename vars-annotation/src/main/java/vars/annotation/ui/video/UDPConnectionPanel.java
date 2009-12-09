package vars.annotation.ui.video;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

public class UDPConnectionPanel extends JPanel implements ConnectionParameters {
	private JLabel lblHost;
	private JTextField hostTextField;
	private JLabel lblPort;
	private JTextField portTextField;
	
	public UDPConnectionPanel() {
		initialize();
	}
	
	private void initialize() {
		setBorder(new TitledBorder(null, "UDP", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getLblHost())
						.addComponent(getLblPort()))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getPortTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
						.addComponent(getHostTextField(), GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblHost())
						.addComponent(getHostTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblPort())
						.addComponent(getPortTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(248, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	private JLabel getLblHost() {
		if (lblHost == null) {
			lblHost = new JLabel("Host: ");
		}
		return lblHost;
	}
	private JTextField getHostTextField() {
		if (hostTextField == null) {
			hostTextField = new JTextField();
			hostTextField.setColumns(10);
		}
		return hostTextField;
	}
	private JLabel getLblPort() {
		if (lblPort == null) {
			lblPort = new JLabel("Port: ");
		}
		return lblPort;
	}
	private JTextField getPortTextField() {
		if (portTextField == null) {
			portTextField = new JTextField();
			portTextField.setColumns(10);
		}
		return portTextField;
	}

    public Object[] getConnectionParameters() {
        return new Object[] {getHostTextField().getText(), 
                Integer.valueOf(getPortTextField().getText()),
                Double.valueOf(29.97)};
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getHostTextField().setEnabled(enabled);
        getPortTextField().setEnabled(enabled);
    }
}

