/*
 * @(#)ImageAnnotationFrame.java   2010.03.19 at 11:40:26 PDT
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.table.events.CreateObservationListener;
import vars.annotation.ui.table.events.CreateObservationListenerImpl;
import vars.annotation.ui.table.events.SelectObservationsListener;
import vars.annotation.ui.table.events.SelectObservationsListenerImpl;
import vars.knowledgebase.Concept;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.ConceptNameComboBox;

/**
 * Main class for this package. This class wires together all dependant classes with the controller.
 * @author brian
 */
public class ImageAnnotationFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JImageUrlCanvas imageCanvas = new JImageUrlCanvas();
    private ConceptNameComboBox comboBox;
    private URL imageUrl;
    private JXLayer<JImageUrlCanvas> layer;
    private AnnotationLayerUI annotationLayerUI;
    private MeasurementLayerUI measurementLayerUI;
    private JToolBar toolBar;
    private final SelectObservationsListener selectObservationsListener = new SelectObservationsListenerImpl();
    private final CreateObservationListener createObservationListener;
    private final ImageAnnotationFrameController controller;
    private JToggleButton makeMeasurementButton;
    /**
     * Create the frame
     *
     * @param toolBelt
     */
    public ImageAnnotationFrame(final ToolBelt toolBelt) {
        super();
        controller = new ImageAnnotationFrameController(toolBelt, this);
        createObservationListener = new CreateObservationListenerImpl(toolBelt.getAnnotationFactory(), toolBelt.getPersistenceController());
        initialize();
    }


    /**
     * @return
     */
    public JXLayer<JImageUrlCanvas> getLayer() {
        if (layer == null) {
            layer = new JXLayer<JImageUrlCanvas>(imageCanvas);
            layer.setUI(getAnnotationLayerUI());
        }

        return layer;
    }

    public AnnotationLayerUI getAnnotationLayerUI() {
        if (annotationLayerUI == null) {
            annotationLayerUI = new AnnotationLayerUI<JImageUrlCanvas>(controller.getToolBelt());
            annotationLayerUI.setSelectObservationsListener(selectObservationsListener);
            annotationLayerUI.setCreateObservationListener(createObservationListener);
        }
        return annotationLayerUI;
    }

    public MeasurementLayerUI getMeasurementLayerUI() {
        if (measurementLayerUI == null) {
            measurementLayerUI = new MeasurementLayerUI<JImageUrlCanvas>(controller.getToolBelt());
            measurementLayerUI.addMeasurementCompletedListener(controller);
            
        }
        return measurementLayerUI;
    }

    public ConceptNameComboBox getComboBox() {
        if (comboBox == null) {
            final ToolBelt toolBelt = controller.getToolBelt();
            comboBox = new AllConceptNamesComboBox(toolBelt.getQueryPersistenceService());

            /*
             * When combo box changes, change the default concept used for point and
             * click annotations
             */
            comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        Concept concept = toolBelt.getAnnotationPersistenceService().findConceptByName(
                            (String) comboBox.getSelectedItem());
                        getAnnotationLayerUI().setConcept(concept);
                    }
                }
            });
        }
        return comboBox;
    }

    public JToggleButton getMakeMeasurementButton() {
        if (makeMeasurementButton == null) {
            makeMeasurementButton = new JToggleButton("Measure");
            makeMeasurementButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (makeMeasurementButton.isSelected()) {
                        useMeasurementLayer();
                    }
                    else {
                        useAnnotationLayer();
                    }
                }
            });
            /*
               Listen for selected Observations. If one is selected, set it as the GOTO observation.
               Otherwise disable the measurements
             */
            Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Collection<Observation> selectedObservations = (Collection<Observation>) evt.getNewValue();
                    if (selectedObservations.size() == 1) {
                        getMeasurementLayerUI().setObservation(selectedObservations.iterator().next());
                    }
                    else {
                        getMeasurementLayerUI().setObservation(null);
                        useAnnotationLayer();
                    }
                }
            });
        }
        return makeMeasurementButton;
    }

    /**
     * @return
     */
    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getComboBox());
            toolBar.add(getMakeMeasurementButton());
        }

        return toolBar;
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return controller.getVideoFrame();
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
        String title = (imageUrl == null) ? "" : imageUrl.toExternalForm();
        setTitle(title);
        if (imageUrl == null || !imageUrl.equals(oldUrl)) {
            imageCanvas.setUrl(imageUrl);
        }

    }

    /**
     *
     * @param videoFrame
     */
    public void setVideoFrame(VideoFrame videoFrame) {
        controller.setVideoFrame(videoFrame);
        getAnnotationLayerUI().setVideoFrame(videoFrame);
        URL url = null;
        if (videoFrame != null && videoFrame.getCameraData() != null) {
            try {
                url = new URL(videoFrame.getCameraData().getImageReference());
            }
            catch (Exception e) {
                log.info("Failed to display " + url, e);
            }
        }

        if (url == null || !url.equals(imageUrl)) {
            setImageUrl(url);
        }
        repaint();
    }
    

    public void useAnnotationLayer() {
        getMakeMeasurementButton().setSelected(false);
        getLayer().setUI(getAnnotationLayerUI());
    }


    public void useMeasurementLayer() {
        getMakeMeasurementButton().setSelected(true);
        getMeasurementLayerUI().resetUI();
        getLayer().setUI(getMeasurementLayerUI());
    }

}
