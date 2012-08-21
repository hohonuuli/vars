package vars.annotation.ui.imagepanel;

import javax.swing.JPanel;

/**
 * Builder used to fetch a customs settings JPanel for an instance of MultiLayerUI compenents such as
 * AnnotationLayerUI. The JPanel is then used by the ImageAnnotationFrame.
 * @author Brian Schlining
 * @since 2012-08-13
 */
public interface UISettingsBuilder {

    public JPanel getPanel();

    public void clearPainters();

}
