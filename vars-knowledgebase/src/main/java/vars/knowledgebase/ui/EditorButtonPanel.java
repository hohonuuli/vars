/*
 * EditorButtonPanel.java
 *
 * Created on May 23, 2006, 1:21 PM
 */

package vars.knowledgebase.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import vars.shared.ui.FancyButton;


/**
 *
 * @author  brian
 */
public class EditorButtonPanel extends javax.swing.JPanel {

    private JButton deleteButton;

    private JButton newButton;

    private JButton updateButton;

    /** Creates new form EditorButtonPanel */
    public EditorButtonPanel() {
        initialize();
    }

    /**
	 * @return  the deleteButton
	 */
    public javax.swing.JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new FancyButton();
            deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
            deleteButton.setText("Delete");
        }
        return deleteButton;
    }

    /**
	 * @return  the newButton
	 */
    public javax.swing.JButton getNewButton() {
        if (newButton == null) {
            newButton = new FancyButton();
            newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vars/knowledgebase/add2.png")));
            newButton.setText("New");
        }
        return newButton;
    }

    /**
	 * @return  the updateButton
	 */
    public javax.swing.JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new FancyButton();
            updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vars/knowledgebase/replace2.png")));
            updateButton.setText("Update");
        }
        return updateButton;
    }
    

    private void initialize() {

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(Box.createHorizontalGlue());
        this.add(getNewButton());
        this.add(Box.createHorizontalStrut(10));
        this.add(getUpdateButton());
        this.add(Box.createHorizontalStrut(10));
        this.add(getDeleteButton());
        this.add(Box.createHorizontalStrut(20));
        
    }
    
    



    
}
