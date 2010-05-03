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
import javax.swing.JTextField;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.ui.Lookup;
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
    public void persistPreferences() {
        final UserAccount userAccount = panel.getUserAccount();
        final JTextField imageTargetTextField = panel.getImageTargetTextField();
        final JTextField imageTargetMappingTextField = panel.getImageTargetMappingTextField();

        // Parse and set the imageTarget
        File imageTarget = new File(imageTargetTextField.getText());
        if (!imageTarget.exists() && !imageTarget.canWrite()) {
            EventBus.publish(Lookup.TOPIC_WARNING,
                                 "The location, " + imageTargetTextField.getText() +
                                 ", that you specified is not valid");
        }

        preferencesService.persistImageTarget(userAccount.getUserName(), preferencesService.getHostname(), imageTarget);

        // Parse and set the imageTargetMapping
        URL imageMappingTarget = null;
        try {
            imageMappingTarget = new URL(imageTargetMappingTextField.getText());
        }
        catch (MalformedURLException ex) {
            log.warn("The user specified and invalid URL as an imageTarget. The bogus URL is '" +
                     imageTargetMappingTextField.getText() + "'");
            EventBus.publish(Lookup.TOPIC_WARNING,
                             "The location, " + imageTargetTextField.getText() +
                             ", that you specified is not a valid URL");
            return;
        }

        preferencesService.persistImageTargetMapping(userAccount.getUserName(), imageMappingTarget);

    }

    protected void setUserAccount(UserAccount userAccount) {
        File imageTarget = preferencesService.findImageTarget(userAccount.getUserName(),
            preferencesService.getHostname());
        URL imageTargetMapping = preferencesService.findImageTargetMapping(userAccount.getUserName());
        try {
            panel.getImageTargetTextField().setText(imageTarget.getCanonicalPath());
        } catch (IOException ex) {
            panel.getImageTargetTextField().setText("");
        }
        panel.getImageTargetMappingTextField().setText(imageTargetMapping.toExternalForm());
    }
}
