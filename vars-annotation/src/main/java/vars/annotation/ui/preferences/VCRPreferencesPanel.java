/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author brian
 */
public class VCRPreferencesPanel extends JPanel {

    private JRadioButton radioButton;

    public VCRPreferencesPanel() {
        
    }


    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(getRadioButton());

    }

    protected JRadioButton getRadioButton() {
        if (radioButton == null) {
            radioButton = new JRadioButton();
            radioButton.setText("Auto-connect to VCR on startup");
            radioButton.setToolTipText("When checked VARS will attempt to connect"
                    + " to the last used VCR on startup");
            // TODO on start up read from preferences and set teh radio button
            radioButton.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    // When state changes save the 'autoconnect' property to preferences
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        }
        return radioButton;

    }

}
