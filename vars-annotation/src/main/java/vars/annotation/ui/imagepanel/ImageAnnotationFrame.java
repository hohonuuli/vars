/*
 * @(#)ImageAnnotationFrame.java   2012.11.26 at 08:48:34 PST
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



package vars.annotation.ui.imagepanel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.swing.JImageCanvas;
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoFrame;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.knowledgebase.Concept;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.ConceptNameComboBox;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vars.annotation.ui.AnnotationImageCanvas;

/**
 * Main class for this package. This class wires together all dependant classes with the controller.
 * @author brian
 */
public class ImageAnnotationFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ButtonGroup layersButtonGroup = new ButtonGroup();
    private final List<ImageFrameLayerUI<JImageUrlCanvas>> layers = new ArrayList<ImageFrameLayerUI<JImageUrlCanvas>>();
    private final PropertyChangeListener imageChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Image image = (Image) evt.getNewValue();
            BufferedImage bufferedImage = ImageUtilities.toBufferedImage(image);
            getMeasurementLayerUI().setImage(bufferedImage);
            getAreaMeasurementLayerUI().setImage(bufferedImage);
        }
    };
    private AnnotationLayerUI annotationLayerUI;
    private AreaMeasurementLayerUI2<JImageUrlCanvas> areaMeasurementLayerUI;
    private ConceptNameComboBox comboBox;
    private final ImageAnnotationFrameController controller;

    /** Relays events fromm the annotation UI to the various painters used by LayerUI's */
    private final UIDataCoordinator dataCoordinator;
    private JImageUrlCanvas imageCanvas;
    private JXLayer<JImageUrlCanvas> layer;
    private MeasurementLayerUI<JImageUrlCanvas> measurementLayerUI;
    private JPanel settingsPanel;
    private JToolBar toolBar;
    private CommonPainters<JImageUrlCanvas> commonPainters;


    /**
     * Create the frame
     *
     * @param toolBelt
     */
    public ImageAnnotationFrame(final ToolBelt toolBelt) {
        controller = new ImageAnnotationFrameController(toolBelt, this);
        dataCoordinator = new UIDataCoordinator(toolBelt.getAnnotationDAOFactory());

        // --- Build UI Layers

        layers.add(getAnnotationLayerUI());
        layers.add(getMeasurementLayerUI());
        layers.add(getAreaMeasurementLayerUI());

        AnnotationProcessor.process(this);
        initialize();
    }

    private CommonPainters<JImageUrlCanvas> getCommonPainters() {
        if (commonPainters == null) {
            commonPainters = new CommonPainters<JImageUrlCanvas>(
                    new JXHorizontalLinePainter<JImageUrlCanvas>(getImageCanvas()),
                    new JXCrossHairPainter<JImageUrlCanvas>());
        }
        return commonPainters;
    }

    /**
     * @return
     */
    protected AnnotationLayerUI getAnnotationLayerUI() {
        if (annotationLayerUI == null) {
            annotationLayerUI = new AnnotationLayerUI<JImageUrlCanvas>(controller.getToolBelt(),
                    dataCoordinator, getCommonPainters());
        }

        return annotationLayerUI;
    }

    protected AreaMeasurementLayerUI2<JImageUrlCanvas> getAreaMeasurementLayerUI() {
        if (areaMeasurementLayerUI == null) {
            areaMeasurementLayerUI = new AreaMeasurementLayerUI2<JImageUrlCanvas>(controller.getToolBelt(),
                    getImageCanvas(), getCommonPainters());
        }

        return areaMeasurementLayerUI;
    }

    /**
     * @return
     */
    protected ConceptNameComboBox getComboBox() {
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
                        log.debug("Using concept: " + concept.getPrimaryConceptName().getName());
                        getAnnotationLayerUI().setConcept(concept);
                    }
                }

            });
        }

        return comboBox;
    }

    protected JImageUrlCanvas getImageCanvas() {
        if (imageCanvas == null) {
            imageCanvas = new AnnotationImageCanvas();
            imageCanvas.addPropertyChangeListener(JImageCanvas.PROP_IMAGE, imageChangeListener);
        }

        return imageCanvas;
    }

    /**
     * @return
     */
    protected JXLayer<JImageUrlCanvas> getLayer() {
        if (layer == null) {
            layer = new JXLayer<JImageUrlCanvas>(getImageCanvas());
            layer.setUI(layers.get(0));    // Add first layer in the list as the default UI.
            setSettingsPanel(layers.get(0).getSettingsBuilder().getPanel());
        }

        return layer;
    }

    protected MeasurementLayerUI<JImageUrlCanvas> getMeasurementLayerUI() {
        if (measurementLayerUI == null) {
            measurementLayerUI = new MeasurementLayerUI<JImageUrlCanvas>(controller.getToolBelt(),
                    getCommonPainters());
            measurementLayerUI.addMeasurementCompletedListener(controller);
        }

        return measurementLayerUI;
    }


    /**
     * @return
     */
    protected JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getComboBox());

            // --- Add buttons to select the correct LayerUI
            for (int i = 0; i < layers.size(); i++) {
                final int j = i;
                ImageFrameLayerUI layerUI = layers.get(i);
                JRadioButton button = new JRadioButton(layerUI.getDisplayName());
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((ImageFrameLayerUI) getLayer().getUI()).resetUI();
                        getLayer().setUI(layers.get(j));
                        setSettingsPanel(layers.get(j).getSettingsBuilder().getPanel());
                    }

                });

                if (j == 0) {
                    button.setSelected(true);
                }

                toolBar.add(button);
                layersButtonGroup.add(button);
            }

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
        getImageCanvas().setUrl(null);
    }

    /**
     *  Listen for selected Observations. If one is selected, set it as the GOTO observation.
     *  Otherwise disable the measurements
     *
     * @param selectionEvent
     */
    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void respondTo(ObservationsSelectedEvent selectionEvent) {
        if ((selectionEvent.getSelectionSource() != this) &&
                !getAnnotationLayerUI().equals(selectionEvent.getSelectionSource())) {

            // If one observation is found show it's image
            Set<VideoFrame> videoFrames = PersistenceController.toVideoFrames(selectionEvent.get());

            if (videoFrames.size() == 1) {
                VideoFrame videoFrame = videoFrames.iterator().next();
                setVideoFrame(videoFrame);
            }
            else {
                setVideoFrame(null);
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = VideoFramesChangedEvent.class)
    public void respondTo(VideoFramesChangedEvent event) {
        if (event.get().contains(controller.getVideoFrame())) {
            List<VideoFrame> videoFrames = new ArrayList<VideoFrame>(event.get());
            int idx = videoFrames.indexOf(controller.getVideoFrame());
            if (idx > -1) {
                setVideoFrame(videoFrames.get(idx));
            }
        }
    }

    private void setSettingsPanel(JPanel panel) {
        if (settingsPanel != null) {
            remove(settingsPanel);
        }
        settingsPanel = panel;
        add(settingsPanel, BorderLayout.NORTH);
        validate();
        repaint();
    }

    private void setVideoFrame(VideoFrame videoFrame) {
        controller.setVideoFrame(videoFrame);
        URL imageReference = null;
        try {
            imageReference = new URL(videoFrame.getCameraData().getImageReference());
        }
        catch (Exception e) {

            // Do Nothing
        }
        getImageCanvas().setUrl(imageReference);
    }
}
