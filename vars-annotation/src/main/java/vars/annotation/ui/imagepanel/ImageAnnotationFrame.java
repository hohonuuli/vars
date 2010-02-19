/*
 * @(#)ImageFrame.java   2009.12.29 at 05:01:47 PST
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



package vars.annotation.ui.imagepanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.util.ImageCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.ConceptNameComboBox;

/**
 *
 * @author brian
 */
public class ImageAnnotationFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private BufferedImage image;
    private URL imageUrl;
    private ImageCanvas imageCanvas = new ImageCanvas();
    private JXLayer<ImageCanvas> layer;
    private final AnnotationLayerUI layerUI;
    private JToolBar toolBar;


    private ConceptNameComboBox comboBox;
    /**
     * Create the frame
     */
    public ImageAnnotationFrame(final ToolBelt toolBelt) {
        super();
        layerUI = new AnnotationLayerUI<ImageCanvas>(toolBelt);
        comboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());

        /*
         * When combo box changes, change the default concept used for point and
         * click annotations
         */
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Concept concept = toolBelt.getAnnotationPersistenceService().findConceptByName((String) comboBox.getSelectedItem());
                    layerUI.setConcept(concept);
                }
            }
        });
        initialize();
    }

    public VideoFrame getVideoFrame() {
        return layerUI.getVideoFrame();
    }

    public void setVideoFrame(VideoFrame videoFrame) {
        layerUI.setVideoFrame(videoFrame);
        URL url = null;
        try {
            url = new URL(videoFrame.getCameraData().getImageReference());
            
        }
        catch (Exception e){
            // Do nothing
        }
        setImageUrl(url);
    }

    /**
     * The image is stored internally as a {@code BufferedImage}. This
     * call returns the underlying image
     * @return The image fetched in
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @return the imageUrl
     */
    private URL getImageUrl() {
        return imageUrl;
    }


    /**
     * @return
     */
    public JXLayer<ImageCanvas> getLayer() {
        if (layer == null) {
            layer = new JXLayer<ImageCanvas>(imageCanvas);
            layer.setUI(layerUI);
        }

        return layer;
    }

    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(comboBox);
        }
        return toolBar;
    }





    private void initialize() {
        setLayout(new BorderLayout());
        add(getLayer(), BorderLayout.CENTER);
        add(getToolBar(), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(640, 480));
        setImageUrl(null);
    }



    /**
     * Sets the URL of the image to display. The images is fetched from the
     * location specified. <b>IMPORTANT!! This method avoids the cacheing that
     * swing and/or {@code Toolkit} normally uses. So your image will not be
     * cached in memory</b>
     *
     * @param imageUrl the imageUrl to set
     */
    private void setImageUrl(final URL imageUrl) {
        URL oldUrl = this.imageUrl;
        this.imageUrl = imageUrl;
        log.debug("setImageUrl( " + imageUrl + " )");

        // Don't reload the same image
        if (imageUrl == null) {

            /*
             *  Clear out data if no value is set
             */
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setTitle("");
                    imageCanvas.setImage(null);
                }
            });
        }
        else if (!imageUrl.equals(oldUrl)) {

            /*
             * Use swingutilities to invoke changes on the EventDisplatch thread.
             *
             * Remove label from view and add progress bar
             */
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setTitle(imageUrl.toExternalForm());
                    (new ImageLoader(imageUrl)).execute();
                }
            });

        }
        
    }

    /**
     * @return
     */
    private class ImageLoader extends SwingWorker<BufferedImage, Object> {

        final BufferedImage oldImage;
        final URL url;

        /**
         * Constructs ...
         *
         * @param url
         */
        public ImageLoader(final URL url) {
            this.url = url;
            this.oldImage = getImage();
        }

        protected BufferedImage doInBackground() throws Exception {
            log.debug("Reading image from " + url);
            return ImageIO.read(url);
        }

        @Override
        protected void done() {

            try {
                image = get();
                log.debug("Image " + url + " [" + image.getWidth() + " x " + image.getHeight() +
                          " pixels] has been loaded");
                imageCanvas.setImage(image);
            }
            catch (Exception e) {
                log.debug("Failed to read image", e);
                imageCanvas.setImage(null);
            }

        }
    }
}