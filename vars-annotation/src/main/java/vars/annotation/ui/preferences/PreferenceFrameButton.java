/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.PreferencesFactory;
import javax.swing.ImageIcon;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
import vars.shared.ui.FancyButton;

/**
 *
 * @author brian
 */
public class PreferenceFrameButton extends FancyButton {
    
    private PreferencesFrame preferencesFrame;
    private final PreferencesFactory preferencesFactory;

    
    public PreferenceFrameButton() {
        this.preferencesFactory = Lookup.getPreferencesFactory();
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
        Lookup.getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setEnabled(evt.getNewValue() != null);
            }
        });
    }
    
    
    public PreferencesFrame getPreferencesFrame() {
        if (preferencesFrame == null) {
            preferencesFrame = new PreferencesFrame(preferencesFactory);
            preferencesFrame.pack();
            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            preferencesFrame.setUserAccount(userAccount);

            /*
             * Listen to changes to the UserAccount and relay them to the
             * ImagePreferencePanel
             */
            Lookup.getUserAccountDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                    preferencesFrame.setUserAccount(userAccount);
                }
            });
        }
        return preferencesFrame;
    }


}
