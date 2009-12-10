/*
 * VideoSourcePanel.java
 *
 * Created on March 19, 2007, 3:45 PM
 */

package vars.annotation.ui.dialogs;

import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.AnnotationDAOFactory;

/**
 *
 * @author  brian
 */
public class VideoSourceSelectionPanel extends JPanel {
    
    private final VideoSourceSelectionPanelController controller = new VideoSourceSelectionPanelController(this);
    private VideoSourcePanelTape videoSourcePanel;
    private JPanel selectedPanel;
    private javax.swing.JLabel jLabel1;
    private final String videoSource = "Video-Tape";
    
    private static final Logger log = LoggerFactory.getLogger(VideoSourceSelectionPanel.class);
    
    /** Creates new form VideoSourcePanel */
    public VideoSourceSelectionPanel(AnnotationDAOFactory annotationDAOFactory) {
    	videoSourcePanel = new VideoSourcePanelTape(annotationDAOFactory);
        initialize();
        
        // Set up the default VideoSourcePanel
        String defaultVideoSource = controller.getProperty("video.source.default");
        setVideoSource(defaultVideoSource);
    }
    
    private void initialize() {

 
    }
    
    
    public void setVideoSource(String source) {

        if (source == null || source.equalsIgnoreCase("tape")) {
            videoSourcePanel.getHdCheckBox().setSelected(false);
        }
        else {
            videoSourcePanel.getHdCheckBox().setSelected(true);
        }


        videoSourcePanel.removeAll();
        videoSourcePanel.add(selectedPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public void open() {
        IVideoSourcePanel p = (IVideoSourcePanel) selectedPanel;
        if (p.isValidVideoSource()) {
            p.open();
        }
        else {
            log.info("Tried calling open on an invalid IVideoSourcePanel. The request was ignored");
        }
    }
    
   

    
}
