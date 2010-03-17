/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.PreferencesFactory;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;

/**
 *
 * @author brian
 */
public class ImagePreferencesPanelController implements PreferenceUpdater {

    private final ImagePreferencesPanel panel;
    private final PreferencesService preferencesService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public ImagePreferencesPanelController(ImagePreferencesPanel panel, PreferencesFactory preferencesFactory) {
        this.panel = panel;
        this.preferencesService = new PreferencesService(preferencesFactory);

    }

    public void persistPreferences() {
        final UserAccount userAccount = panel.getUserAccount();
        final JTextField imageTargetTextField = panel.getImageTargetTextField();
        final JTextField imageTargetMappingTextField = panel.getImageTargetMappingTextField();

        URL imageTarget = null;
        try {
            imageTarget = new URL(imageTargetTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified and invalid URL as an imageTarget. The bogus URL is '" +
                    imageTargetTextField.getText() + "'");
            // TODO notify user of bad URL
        }
        preferencesService.persistImageTarget(userAccount.getUserName(), preferencesService.getHostname(), imageTarget);


        URL imageMappingTarget = null;
        try {
            imageMappingTarget = new URL(imageTargetMappingTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified and invalid URL as an imageTarget. The bogus URL is '" +
                    imageTargetMappingTextField.getText() + "'");
            // TODO notify user of bad URL
        }
        preferencesService.persistImageTargetMapping(userAccount.getUserName(), imageMappingTarget);

    }

    protected void setUserAccount(UserAccount userAccount) {
        URL imageTarget = preferencesService.findImageTarget(userAccount.getUserName(), preferencesService.getHostname());
        URL imageTargetMapping = preferencesService.findImageTargetMapping(userAccount.getUserName());
        panel.getImageTargetTextField().setText(imageTarget.toExternalForm());
        panel.getImageTargetMappingTextField().setText(imageTargetMapping.toExternalForm());
    }

}
