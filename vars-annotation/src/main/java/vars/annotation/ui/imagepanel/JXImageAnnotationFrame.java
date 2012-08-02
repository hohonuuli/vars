package vars.annotation.ui.imagepanel;

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
import java.util.List;
import java.util.Set;

/**
 * @author Brian Schlining
 * @since 2012-08-02
 */
public class JXImageAnnotationFrame  extends JFrame {

    private JXCrossHairPainter<JImageUrlCanvas> crossHairPainter;
    private JImageUrlCanvas imageCanvas = new JImageUrlCanvas();
    private JXAnnotationLayerPainter annotationLayerPainter;
    private ConceptNameComboBox comboBox;
    private final ImageAnnotationFrameController controller;
    private JXLayer<JImageUrlCanvas> layer;
    private JToggleButton makeMeasurementButton;
    private JToggleButton makeAreaMeasurementButton;
    private JXMeasurementLayerPainter measurementLayerPainter;
    private JXAreaMeasurementPainter areaMeasurementLayerPainter;
    private JToolBar toolBar;
    private MultiLayerUI<JImageUrlCanvas> layerUI;

    /**
     * Create the frame
     *
     * @param toolBelt
     */
    public JXImageAnnotationFrame(final ToolBelt toolBelt) {
        controller = new ImageAnnotationFrameController(toolBelt, this);
        AnnotationProcessor.process(this);
        initialize();
    }

    /**
     * @return
     */
    protected JXAnnotationLayerPainter getAnnotationLayerPainter() {
        if (annotationLayerPainter == null) {
            annotationLayerPainter = new JXAnnotationLayerPainter<JImageUrlCanvas>(controller.getToolBelt());
        }

        return annotationLayerPainter;
    }

    protected JXPainter<JImageUrlCanvas> getCrossHairPainter() {
        if (crossHairPainter == null) {
            crossHairPainter = new JXCrossHairPainter<JImageUrlCanvas>();
        }
        return crossHairPainter;
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
                        getAnnotationLayerPainter().setConcept(concept);
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
            layer.setUI(getLayerUI());
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
    protected JXMeasurementLayerPainter getMeasurementLayerPainter() {
        if (measurementLayerPainter == null) {
            measurementLayerPainter = new JXMeasurementLayerPainter<JImageUrlCanvas>(controller.getToolBelt());
            measurementLayerPainter.addMeasurementCompletedListener(controller);

        }

        return measurementLayerPainter;
    }

    protected JXAreaMeasurementPainter getAreaMeasurementLayerPainter() {
        if (areaMeasurementLayerPainter == null) {
            areaMeasurementLayerPainter = new JXAreaMeasurementPainter<JImageUrlCanvas>(controller.getToolBelt());
        }
        return areaMeasurementLayerPainter;
    }

    protected MultiLayerUI<JImageUrlCanvas> getLayerUI() {
        if (layerUI == null) {
            layerUI = new MultiLayerUI<JImageUrlCanvas>();
            layerUI.addPainter(new JXCrossHairPainter<JImageUrlCanvas>());
        }
        return layerUI;
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
                !getAnnotationLayerPainter().equals(selectionEvent.getSelectionSource())) {
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
        getLayerUI().clearPainters();
        getLayerUI().addPainter(getAnnotationLayerPainter());
        getLayerUI().addPainter(getCrossHairPainter());
    }

    /**
     */
    private void useMeasurementLayer() {
        getMakeMeasurementButton().setSelected(true);
        getMakeAreaMeasurementButton().setSelected(false);
        getMeasurementLayerPainter().resetUI();
        getLayerUI().clearPainters();
        getLayerUI().addPainter(getMeasurementLayerPainter());
        getLayerUI().addPainter(getCrossHairPainter());
    }

    private void useAreaMeasurementLayer() {
        getMakeMeasurementButton().setSelected(false);
        getMakeAreaMeasurementButton().setSelected(true);
        getAreaMeasurementLayerPainter().resetUI();
        getLayerUI().clearPainters();
        getLayerUI().addPainter(getAreaMeasurementLayerPainter());
        getLayerUI().addPainter(getCrossHairPainter());
    }
}
