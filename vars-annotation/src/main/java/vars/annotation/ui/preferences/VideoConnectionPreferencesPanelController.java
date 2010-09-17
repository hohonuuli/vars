package vars.annotation.ui.preferences;

import java.util.prefs.PreferencesFactory;

import vars.shared.preferences.PreferenceUpdater;
import vars.shared.preferences.PreferencesService;

public class VideoConnectionPreferencesPanelController implements PreferenceUpdater {
    
    private final VideoConnectionPreferencesPanel panel;
    private final PreferencesService preferencesService;
    
    
    public VideoConnectionPreferencesPanelController(VideoConnectionPreferencesPanel panel, PreferencesFactory preferencesFactory) {
        this.panel = panel;
        preferencesService = new PreferencesService(preferencesFactory);
    }

    public void persistPreferences() {
        preferencesService.persistAutoconnectVcr(preferencesService.getHostname(),
                panel.getRadioButton().isSelected());
        
    }
    
    public boolean isAutoconnected() {
        return preferencesService.findAutoconnectVcr(preferencesService.getHostname());
    }

}
