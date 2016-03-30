/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.PreferencesFactory;
import javax.swing.ImageIcon;
import vars.UserAccount;
import vars.annotation.ui.StateLookup;
import vars.shared.ui.FancyButton;

/**
 *
 * @author brian
 */
public class PreferenceFrameButton extends FancyButton {
    
    private PreferencesFrame preferencesFrame;
    private final PreferencesFactory preferencesFactory;

    
    public PreferenceFrameButton() {
        this.preferencesFactory = StateLookup.PREFERENCES_FACTORY;
        initialize();
    }

    private void initialize() {
        setEnabled(false);
        setToolTipText("Edit Preferences");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/24px/preferences.png")));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPreferencesFrame().setVisible(true);
            }
        });

        // Turn button off if no UserAccount is open
        StateLookup.userAccountProperty()
                .addListener((obs, oldVal, newVal) -> setEnabled(newVal != null));

    }
    
    
    public PreferencesFrame getPreferencesFrame() {
        if (preferencesFrame == null) {
            preferencesFrame = new PreferencesFrame(preferencesFactory);
            preferencesFrame.pack();
            UserAccount userAccount = StateLookup.getUserAccount();
            preferencesFrame.setUserAccount(userAccount);

            /*
             * Listen to changes to the UserAccount and relay them to the
             * ImagePreferencePanel
             */
            StateLookup.userAccountProperty()
                    .addListener((obs, oldVal, newVal) -> preferencesFrame.setUserAccount(newVal));

        }
        return preferencesFrame;
    }


}
