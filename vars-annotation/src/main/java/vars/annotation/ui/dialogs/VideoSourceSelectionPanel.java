/*
 * @(#)VideoSourceSelectionPanel.java   2010.01.19 at 02:05:24 PST
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



package vars.annotation.ui.dialogs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationDAOFactory;

/**
 *
 * @author  brian
 */
public class VideoSourceSelectionPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(VideoSourceSelectionPanel.class);
    private final VideoSourceSelectionPanelController controller = new VideoSourceSelectionPanelController(this);
    private final String videoSource = "Video-Tape";
    private JPanel selectedPanel;
    private VideoSourcePanelTape videoSourcePanel;

    /**
     * Creates new form VideoSourcePanel
     *
     * @param annotationDAOFactory
     */
    public VideoSourceSelectionPanel(AnnotationDAOFactory annotationDAOFactory) {
        videoSourcePanel = new VideoSourcePanelTape(annotationDAOFactory);
        initialize();

        // Set up the default VideoSourcePanel
        String defaultVideoSource = controller.getProperty("video.source.default");
        setVideoSource(defaultVideoSource);
    }

    private void initialize() {}

    /**
     */
    public void open() {
        IVideoSourcePanel p = (IVideoSourcePanel) selectedPanel;
        if (p.isValidVideoSource()) {
            p.open();
        }
        else {
            log.info("Tried calling open on an invalid IVideoSourcePanel. The request was ignored");
        }
    }

    /**
     *
     * @param source
     */
    public void setVideoSource(String source) {

        if ((source == null) || source.equalsIgnoreCase("tape")) {
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
}
