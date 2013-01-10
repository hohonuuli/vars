/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.dialogs;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class VideoSourceSelectionPanelController {

    private final VideoSourceSelectionPanel videoSourceSelectionPanel;
    private final Logger log = LoggerFactory.getLogger(VideoSourceSelectionPanelController.class);

    public VideoSourceSelectionPanelController(final VideoSourceSelectionPanel videoSourceSelectionPanel) {
        this.videoSourceSelectionPanel = videoSourceSelectionPanel;
    }

    public String getProperty(String key) {
        String property = System.getProperty(key);
        try {
            if (property == null) {
                ResourceBundle bundle = ResourceBundle.getBundle("vars-annotation", Locale.US);
                property = bundle.getString(key);
            }
            if (log.isDebugEnabled()) {
                log.debug("VARS Annotation Property found: " + key + " = " + property);
            }
        }
        catch (MissingResourceException e) {
            log.debug("The property, '" + key + "', was not found in vars-annotation.properties");
        }
        return property;
    }

}
