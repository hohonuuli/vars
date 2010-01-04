/*
 * @(#)FrameGrabPanel.java   2009.11.13 at 03:38:42 PST
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



/*
Created on October 30, 2003, 2:20 PM
 */
package vars.annotation.ui.ppanel;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import org.mbari.swing.SwingWorker;
import org.mbari.util.ImageCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.CameraData;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.imagepanel.ImageAnnotationFrame;

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
public class FrameGrabPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *     Displays a no-image icon when a framegrab is not present.
     */
    private ImageCanvas defaultImageCanvas;
    private ImageCanvas imageCanvas;
    private VideoFrame videoFrame;
    private final ImageAnnotationFrame imageFrame;

    /**
     * Creates new form FrameGrabPanel
     */
    public FrameGrabPanel(ToolBelt toolBelt) {
        this.imageFrame = new ImageAnnotationFrame(toolBelt);
        imageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        initialize();

        // Register for notifications when the Selected Observations changes
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(this);

    }


    ImageCanvas getImageCanvas() {
        if (imageCanvas == null) {
            defaultImageCanvas = new ImageCanvas(getClass().getResource("/images/vars/annotation/no_image.jpg"));
            imageCanvas = defaultImageCanvas;
            imageCanvas.setSize(getSize());
        }

        return imageCanvas;
    }

    /**
     *     @return   Returns the videoFrame.
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
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
     * @param evt
     */
    public void propertyChange(PropertyChangeEvent evt) {
        final Object object = evt.getNewValue();
        VideoFrame vf = null;

        if (object != null) {

            // If one observation is found show it's image
            final Collection<Observation> observations = (Collection<Observation>) object;
            Set<VideoFrame> videoFrames = new HashSet<VideoFrame>(Collections2.transform(observations,
                    new Function<Observation, VideoFrame>() {
                public VideoFrame apply(Observation from) {
                    return from.getVideoFrame();
                }
            }));

            if (videoFrames.size() == 1) {
                vf = videoFrames.iterator().next();
            }
        }

        setVideoFrame(vf);
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

    private void setImageCanvas(ImageCanvas newImageCanvas) {
        if (newImageCanvas == null) {
            newImageCanvas = defaultImageCanvas;
        }

        if (newImageCanvas != imageCanvas) {
            newImageCanvas.setSize(imageCanvas.getSize());
            remove(imageCanvas);
            add(newImageCanvas, java.awt.BorderLayout.CENTER);
            imageCanvas = newImageCanvas;
            validate();
        }

        repaint();
    }

    /**
     *     @param videoFrame              The videoFrame to set.
     */
    public void setVideoFrame(final VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
        imageFrame.setVideoFrame(videoFrame);

        /*
         *  Don't hang the GUI waiting for the image to load. Load the
         *  image in the background.
         */
        final SwingWorker worker = new ImageWorker(videoFrame);

        worker.start();
    }


    private class ImageWorker extends SwingWorker {

        private VideoFrame myVideoFrame;
        private ImageCanvas newImageCanvas;

        ImageWorker(final VideoFrame videoFrame_) {
            super();
            this.myVideoFrame = videoFrame_;
        }

        public Object construct() {
            boolean updateImage = false;
            CameraData myCamera = null;

            if (myVideoFrame != null) {
                myCamera = videoFrame.getCameraData();
            }

            if ((myCamera != null) && myVideoFrame.hasImageReference()) {

                /*
                 *  Only update if the image url is different from the currently
                 *  displayed image.
                 */
                final String newUrl = myCamera.getImageReference();

                if (newUrl != null) {
                    if (imageCanvas != null) {
                        final String currentUrl = imageCanvas.getUrl().toExternalForm();

                        if (newUrl.equals(currentUrl)) {
                            newImageCanvas = imageCanvas;
                        }
                        else {
                            updateImage = true;
                        }
                    }
                    else {
                        updateImage = true;
                    }
                }
                else {
                    newImageCanvas = defaultImageCanvas;
                }

                if (updateImage) {
                    createImageCanvas();
                }
            }
            else {
                newImageCanvas = defaultImageCanvas;
            }

            return imageCanvas;
        }

        private void createImageCanvas() {
            try {
                final URL frameGrabUrl = new URL(myVideoFrame.getCameraData().getImageReference());

                newImageCanvas = new ImageCanvas(frameGrabUrl);
            }
            catch (final MalformedURLException e) {
                log.warn(videoFrame + " references a malformed URL");
                newImageCanvas = defaultImageCanvas;
            }
            catch (final Exception e) {
                log.warn("Unable to add an image to the FrameGrabPanel.", e);
                newImageCanvas = defaultImageCanvas;
            }
        }

        /**
         *  Description of the Method
         */
        @Override
        public void finished() {
            setImageCanvas(newImageCanvas);
        }
    }
}
