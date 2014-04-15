/*
 * @(#)FrameGrabPanel.java   2011.10.24 at 03:22:02 PDT
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.ppanel;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.imagepanel.ImageAnnotationFrame;

import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import vars.annotation.ui.AnnotationImageCanvas;

/**
 * <p>
 * Displays a FrameGrab for the currently selected Observation. Use as:
 *
 * <pre>
 *  FrameGragPanel p = new FrameGrabPanel();
 * </pre>
 *
 * Whenever the ObservationDispatcher has its setObservation method
 * called this class will try to load a new image.
 * </p>
 *
 *
 * @author  Brian Schlining, <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: FrameGrabPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class FrameGrabPanel extends javax.swing.JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *     Displays a no-image icon when a framegrab is not present.
     */
    private JImageUrlCanvas imageCanvas;
    private final ImageAnnotationFrame imageFrame;
    //private final JXImageAnnotationFrame imageFrame;

    /**
     * Creates new form FrameGrabPanel
     *
     * @param toolBelt
     */
    public FrameGrabPanel(ToolBelt toolBelt) {
        this.imageFrame = new ImageAnnotationFrame(toolBelt);
        //this.imageFrame = new JXImageAnnotationFrame(toolBelt);
        imageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        imageFrame.pack();
        initialize();
        AnnotationProcessor.process(this);
    }

    JImageUrlCanvas getImageCanvas() {
        if (imageCanvas == null) {
            imageCanvas = new AnnotationImageCanvas(getClass().getResource("/images/vars/annotation/no_image.jpg"));
            imageCanvas.setSize(getSize());
        }

        return imageCanvas;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("image"));
        setMinimumSize(new Dimension(60, 50));
        setPreferredSize(new Dimension(60, 50));
        add(getImageCanvas(), BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent evt) {
                resizeHandler(evt);
            }
        });

        /*
         * This allows the user to double click on the image and bring up a full
         * sized version in it's own window.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                // Ensure double-click
                if (event.getClickCount() < 2) {
                    return;
                }
                imageFrame.setVisible(true);
            }

        });
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param evt
     */
    private void resizeHandler(final java.awt.event.ComponentEvent evt) {
        imageCanvas.setSize(getSize());
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondTo(ObservationsSelectedEvent event) {
        updateObservations(event.get());
    }

    /**
     *
     * @param observations
     */
    public void updateObservations(Collection<Observation> observations) {

        URL imageReference = null;
        if (observations != null) {

            // If one observation is found show it's image
            Set<VideoFrame> videoFrames = new HashSet<VideoFrame>(Collections2.transform(observations,
                new Function<Observation, VideoFrame>() {

                public VideoFrame apply(Observation from) {
                    return from.getVideoFrame();
                }

            }));

            if (videoFrames.size() == 1) {
                VideoFrame videoFrame = videoFrames.iterator().next();
                try {
                    imageReference = new URL(videoFrame.getCameraData().getImageReference());
                }
                catch (Exception e) {
                    // DO nothing
                }
            }
        }
        getImageCanvas().setUrl(imageReference);

    }
}
