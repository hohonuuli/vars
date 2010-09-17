/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import java.awt.*;
import java.util.prefs.PreferencesFactory;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import vars.shared.preferences.PreferenceUpdater;

/**
 *
 * @author brian
 */
public class VideoConnectionPreferencesPanel extends JPanel implements PreferenceUpdater {

    private JRadioButton radioButton;
    private final VideoConnectionPreferencesPanelController controller;

    public VideoConnectionPreferencesPanel(PreferencesFactory preferencesFactory) {
        this.controller = new VideoConnectionPreferencesPanelController(this, preferencesFactory);
        initialize();
    }


    private void initialize() {
        setLayout(new BorderLayout());
        add(getRadioButton(), BorderLayout.CENTER);
    }
    
    protected JRadioButton getRadioButton() {
        if (radioButton == null) {
            radioButton = new JRadioButton();
            radioButton.setText("Auto-connect to VCR on startup");
            radioButton.setToolTipText("When checked VARS will attempt to connect"
                    + " to the last used VCR on startup");
            radioButton.setSelected(controller.isAutoconnected());
        }
        return radioButton;
    }


    public void persistPreferences() {
        controller.persistPreferences();
    }
    
    
    

}
