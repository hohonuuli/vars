package vars.shared.ui;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.mbari.swing.JFancyButton;

public class OkCancelButtonPanel extends JPanel {


    private JButton cancelButton = null;

    private JButton okButton = null;

    /**
     * This is the default constructor
     */
    public OkCancelButtonPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(Box.createHorizontalGlue());
        this.add(getOkButton(), null);
        this.add(Box.createHorizontalStrut(10));
        this.add(getCancelButton(), null);
        this.add(Box.createHorizontalStrut(20));
    } 

    /**
	 * This method initializes cancelButton	
	 * @return  javax.swing.JButton
	 */
    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton();
            cancelButton.setText("Cancel");
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
        }
        return cancelButton;
    }

    /**
	 * This method initializes okButton	
	 * @return  javax.swing.JButton
	 */
    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new JFancyButton();
            okButton.setText("OK");
            okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));
        }
        return okButton;
    }
    

}
