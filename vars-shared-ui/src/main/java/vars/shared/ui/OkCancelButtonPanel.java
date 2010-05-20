package vars.shared.ui;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class OkCancelButtonPanel extends JPanel {


    private JButton cancelButton = null;

    private JButton okayButton = null;

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
    	setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(getCancelButton());
        add(Box.createHorizontalStrut(10));
        add(getOkayButton());
    } 


    
    /**
     * @return
     */
    public JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new FancyButton("Cancel");
            try {
            	cancelButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/delete2.png")));
            }
            catch (Exception e) {
            	// BUMMER. Do nothing.
            }
            cancelButton.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        cancelButton.doClick();
                    }
                }
            });
        }

        return cancelButton;
    }

    /**
     * @return
     */
    public JButton getOkayButton() {
        if (okayButton == null) {
            okayButton = new FancyButton("OK");
            try {
            	okayButton.setIcon(new ImageIcon(getClass().getResource("/vars/images/24/check2.png")));
            }
            catch (Exception e) {
            	// BUMMER. Do nothing.
            }
            okayButton.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        okayButton.doClick();
                    }
                }
            });
        }

        return okayButton;
    }
    

}
