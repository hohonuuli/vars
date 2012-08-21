package vars.annotation.ui.imagepanel;

import javax.swing.JComponent;

/**
 * @author Brian Schlining
 * @since 2012-08-13
 */
public abstract class ImageFrameLayerUI<T extends JComponent> extends MultiLayerUI<T> {

    private UISettingsBuilder settingsBuilder;
    private String displayName;

    @Override
    public void clearPainters() {
        super.clearPainters();
        settingsBuilder.clearPainters();
    }

    public UISettingsBuilder getSettingsBuilder() {
        return settingsBuilder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setSettingsBuilder(UISettingsBuilder settingsBuilder) {
        this.settingsBuilder = settingsBuilder;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Subclasses can override if they need to reset the state of the UI.
     */
    public void resetUI() {
        // Empty implementation
    }
}
