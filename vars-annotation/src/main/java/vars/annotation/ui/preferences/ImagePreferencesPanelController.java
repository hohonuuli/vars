/*
 * @(#)ImagePreferencesPanelController.java   2010.03.18 at 09:01:14 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.preferences;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.PreferencesFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.eventbus.ImageInterpolationChangedEvent;
import vars.shared.awt.AWTUtilities;
import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;

/**
 *
 * @author brian
 */
public class ImagePreferencesPanelController implements PreferenceUpdater {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ImagePreferencesPanel panel;
    private final PreferencesService preferencesService;

    /**
     * Constructs ...
     *
     * @param panel
     * @param preferencesFactory
     */
    public ImagePreferencesPanelController(ImagePreferencesPanel panel, PreferencesFactory preferencesFactory) {
        this.panel = panel;
        this.preferencesService = new PreferencesService(preferencesFactory);

    }

    /**
     */
    @Override
    public void persistPreferences() {
        final UserAccount userAccount = panel.getUserAccount();
        final String username = userAccount.getUserName();
        final String hostname = preferencesService.getHostname();
        final JTextField imageTargetTextField = panel.getImageTargetTextField();
        final JTextField imageTargetMappingTextField = panel.getImageTargetMappingTextField();
        final JComboBox<String> imageInterpComboxBox = panel.getImageInterpComboBox();

        // --- Parse and set the imageTarget
        File imageTarget = new File(imageTargetTextField.getText());
        if (!imageTarget.exists() && !imageTarget.canWrite()) {
            EventBus.publish(StateLookup.TOPIC_WARNING,
                                 "The location, " + imageTargetTextField.getText() +
                                 ", that you specified is not valid");
        }

        preferencesService.persistImageTarget(username, hostname, imageTarget);

        // --- Parse and set the imageTargetMapping
        URL imageMappingTarget = null;
        try {
            imageMappingTarget = new URL(imageTargetMappingTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified an invalid URL as an imageTarget. The bogus URL is '" +
                     imageTargetMappingTextField.getText() + "'");
            EventBus.publish(StateLookup.TOPIC_WARNING,
                             "The location, " + imageTargetTextField.getText() +
                             ", that you specified is not a valid URL");
            return;
        }

        preferencesService.persistImageTargetMapping(username, hostname,
                imageMappingTarget);
        
        // --- Parse and set the imageInterpolation
        String interpHint = (String) imageInterpComboxBox.getSelectedItem();
        Object hint = AWTUtilities.IMAGE_INTERPOLATION_MAP.get(interpHint);
        if (hint != null) {
            preferencesService.persistImageInterpolation(username, hostname, hint);
            EventBus.publish(new ImageInterpolationChangedEvent(this, hint));
        }
    }

    protected void setUserAccount(UserAccount userAccount) {
        String username = userAccount.getUserName();
        String hostname = preferencesService.getHostname();
        File imageTarget = preferencesService.findImageTarget(username, hostname);
        URL imageTargetMapping = preferencesService.findImageTargetMapping(username, 
               hostname);
        Object imageInterp = preferencesService.findImageInterpolation(username, hostname);
        try {
            panel.getImageTargetTextField().setText(imageTarget.getCanonicalPath());
        } catch (IOException ex) {
            panel.getImageTargetTextField().setText("");
        }
        panel.getImageTargetMappingTextField().setText(imageTargetMapping.toExternalForm());
        panel.getImageInterpComboBox().setSelectedItem(imageInterp.toString());
    }

    protected void persistDefaults() {
        // Parse and set the defaultImageTarget
        final JTextField imageTargetTextField = panel.getImageTargetTextField();
        File defaultImageTarget = new File(imageTargetTextField.getText());
        if (!defaultImageTarget.exists() && !defaultImageTarget.canWrite()) {
            EventBus.publish(StateLookup.TOPIC_WARNING,
                                 "The location, " + imageTargetTextField.getText() +
                                 ", that you specified is not valid or does not exist");
        }
        preferencesService.persistDefaultImageTarget(preferencesService.getHostname(), defaultImageTarget);

        // Parse and set the defaultImageTargetMapping
        final JTextField imageTargetMappingTextField = panel.getImageTargetMappingTextField();
        URL imageMappingTarget = null;
        try {
            imageMappingTarget = new URL(imageTargetMappingTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified an invalid URL as an imageTarget. The bogus URL is '" +
                     imageTargetMappingTextField.getText() + "'");
            EventBus.publish(StateLookup.TOPIC_WARNING,
                             "The location, " + imageTargetTextField.getText() +
                             ", that you specified is not a valid URL");
            return;
        }
        preferencesService.persistDefaultImageTargetMapping(preferencesService.getHostname(),
                imageMappingTarget);


    }
}
