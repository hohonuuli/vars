/*
 * @(#)ImageAnnotationFrame.java   2011.10.25 at 09:31:48 PDT
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.jxlayer.JXLayer;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;
import vars.knowledgebase.Concept;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.ConceptNameComboBox;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class for this package. This class wires together all dependant classes with the controller.
 * @author brian
 */
public class ImageAnnotationFrame extends JFrame {

    private JImageUrlCanvas imageCanvas = new JImageUrlCanvas();
    private AnnotationLayerUI annotationLayerUI;
    private ConceptNameComboBox comboBox;
    private final ImageAnnotationFrameController controller;
    private JXLayer<JImageUrlCanvas> layer;
    private JToggleButton makeMeasurementButton;
    private JToggleButton makeAreaMeasurementButton;
    private MeasurementLayerUI measurementLayerUI;
    private AreaMeasurementLayerUI areaMeasurementLayerUI;
    private JToolBar toolBar;

    /**
     * Create the frame
     *
     * @param toolBelt
     */
    public ImageAnnotationFrame(final ToolBelt toolBelt) {
        controller = new ImageAnnotationFrameController(toolBelt, this);
        AnnotationProcessor.process(this);
        initialize();
    }

    /**
     * @return
     */
    protected AnnotationLayerUI getAnnotationLayerUI() {
        if (annotationLayerUI == null) {
            annotationLayerUI = new AnnotationLayerUI<JImageUrlCanvas>(controller.getToolBelt());
        }

        return annotationLayerUI;
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
                        getAnnotationLayerUI().setConcept(concept);
                    }
                }

            });
        }

        return comboBox;
    }

    /**
     * @return
     */
    protected JXLayer<JImageUrlCanvas> getLayer() {
        if (layer == null) {
            layer = new JXLayer<JImageUrlCanvas>(imageCanvas);
            layer.setUI(getAnnotationLayerUI());
        }

        return layer;
    }

    /**
     * @return
     */
    protected JToggleButton getMakeMeasurementButton() {
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
            makeMeasurementButton.setEnabled(false);

        }

        return makeMeasurementButton;
    }

    protected JToggleButton getMakeAreaMeasurementButton() {
        if (makeAreaMeasurementButton == null) {
            makeAreaMeasurementButton = new JToggleButton("Add Polygon");
            makeAreaMeasurementButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (makeAreaMeasurementButton.isSelected()) {
                        useAreaMeasurementLayer();
                    }
                    else {
                        useAnnotationLayer();
                    }
                }
            });
        }
        return makeAreaMeasurementButton;
    }

    /**
     * @return
     */
    protected MeasurementLayerUI getMeasurementLayerUI() {
        if (measurementLayerUI == null) {
            measurementLayerUI = new MeasurementLayerUI<JImageUrlCanvas>(controller.getToolBelt());
            measurementLayerUI.addMeasurementCompletedListener(controller);

        }

        return measurementLayerUI;
    }

    protected AreaMeasurementLayerUI getAreaMeasurementLayerUI() {
        if (areaMeasurementLayerUI == null) {
            areaMeasurementLayerUI = new AreaMeasurementLayerUI<JImageUrlCanvas>(controller.getToolBelt());
            // TODO handle measurement completed. Via eventbus?
        }
        return areaMeasurementLayerUI;
    }

    /**
     * @return
     */
    protected JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getComboBox());
            toolBar.add(getMakeMeasurementButton());
            toolBar.add(getMakeAreaMeasurementButton());
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
        imageCanvas.setUrl(null);
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
            Collection<Observation> selectedObservations = selectionEvent.get();

            // Toggle UI layer between Annotation or Measurement
            if (selectedObservations.size() == 1) {
                getMakeMeasurementButton().setEnabled(true);
                getMakeAreaMeasurementButton().setEnabled(true);

            }
            else {
                getMakeMeasurementButton().setEnabled(false);
                getMakeAreaMeasurementButton().setEnabled(false);
                useAnnotationLayer();
            }

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

    private void setVideoFrame(VideoFrame videoFrame) {
        controller.setVideoFrame(videoFrame);
        URL imageReference = null;
        try {
            imageReference = new URL(videoFrame.getCameraData().getImageReference());
        }
        catch (Exception e) {
            // Do Nothing
        }
        imageCanvas.setUrl(imageReference);
    }

    /**
     */
    private void useAnnotationLayer() {
        getMakeMeasurementButton().setSelected(false);
        getMakeAreaMeasurementButton().setSelected(false);
        getLayer().setUI(getAnnotationLayerUI());
    }

    /**
     */
    private void useMeasurementLayer() {
        getMakeMeasurementButton().setSelected(true);
        getMakeAreaMeasurementButton().setSelected(false);
        getMeasurementLayerUI().resetUI();
        getLayer().setUI(getMeasurementLayerUI());
    }

    private void useAreaMeasurementLayer() {
        getMakeMeasurementButton().setSelected(false);
        getMakeAreaMeasurementButton().setSelected(true);
        getAreaMeasurementLayerUI().resetUI();
        getLayer().setUI(getAreaMeasurementLayerUI());
    }
}
