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
import javax.swing.JButton;
import vars.UserAccount;
import vars.annotation.ui.Lookup;

/**
 *
 * @author brian
 */
public class PreferenceFrameButton extends JButton {
    
    private PreferencesFrame preferencesFrame;
    private final PreferencesFactory preferencesFactory;

    
    public PreferenceFrameButton() {
        this.preferencesFactory = Lookup.getPreferencesFactory();
        initialize();
    }

    private void initialize() {
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getPreferencesFrame().setVisible(true);
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
                public void propertyChange(PropertyChangeEvent evt) {
                    UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                    preferencesFrame.setUserAccount(userAccount);
                }
            });
        }
        return preferencesFrame;
    }


}
