package vars.annotation.ui.preferences;

import javax.swing.JPanel;

import vars.shared.preferences.PreferenceUpdater;
import vars.shared.ui.UserAccountPreferencesPanel;

import java.awt.BorderLayout;
import javax.swing.JLabel;

public class UserPreferencesPanel extends JPanel implements PreferenceUpdater {
    
    private UserAccountPreferencesPanel panel;
    private UserPreferencesPanelController controller;
    private JLabel label;

    /**
     * Create the panel.
     */
    public UserPreferencesPanel() {
        this.controller = new UserPreferencesPanelController(this);
        initialize();
    }
    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        add(getPanel(), BorderLayout.CENTER);
        add(getLabel(), BorderLayout.NORTH);
    }

    public void persistPreferences() {
        controller.persistPreferences();
    }

    protected UserAccountPreferencesPanel getPanel() {
        if (panel == null) {
        	panel = new UserAccountPreferencesPanel();
        	panel.getRoleComboBox().setEnabled(false);
        	panel.getLoginTextField().setEnabled(false);
        }
        return panel;
    }
    
    private JLabel getLabel() {
        if (label == null) {
        	label = new JLabel("Modify your user account settings");
        }
        return label;
    }
}
