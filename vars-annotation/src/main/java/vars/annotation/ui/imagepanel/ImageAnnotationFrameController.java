package vars.annotation.ui.imagepanel;


import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddAssociationCmd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class ImageAnnotationFrameController implements MeasurementCompletedListener {

    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;
    private MeasurementCommentDialog measurementDialog;
    private MeasurementCommentDialog areaMeasurementDialog;
    private final ImageAnnotationFrame imageAnnotationFrame;
    private final AddMeasurementAction measurementAction = new AddMeasurementAction();
    private AddAreaMeasurementEvent addAreaMeasurementEvent;


    public ImageAnnotationFrameController(ToolBelt toolBelt, ImageAnnotationFrame imageAnnotationFrame) {
        this.toolBelt = toolBelt;
        this.imageAnnotationFrame = imageAnnotationFrame;
        AnnotationProcessor.process(this);
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    @EventSubscriber(eventClass = AddAreaMeasurementEvent.class)
    public void respondTo(AddAreaMeasurementEvent event) {
        addAreaMeasurementEvent = event;
        getAreaMeasurementCommentDialog().setVisible(true);
    }
    
    private MeasurementCommentDialog getAreaMeasurementCommentDialog() {
        if (areaMeasurementDialog == null) {
            areaMeasurementDialog = new MeasurementCommentDialog(imageAnnotationFrame);
            areaMeasurementDialog.getOkayButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (addAreaMeasurementEvent != null) {
                        AreaMeasurement areaMeasurement = addAreaMeasurementEvent.getAreaMeasurement();
                        Observation observation = addAreaMeasurementEvent.getObservation();
                        addAreaMeasurementEvent = null; // Free reference
                        areaMeasurement.setComment(areaMeasurementDialog.getComment());
                        AddAssociationCmd command = new AddAssociationCmd(areaMeasurement.toLink(), ImmutableList.of(observation));
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                    areaMeasurementDialog.setVisible(false);

                }
            });
        }
        return areaMeasurementDialog;
    }

    @Override
    public void onComplete(MeasurementCompletedEvent event) {
        measurementAction.setMeasurement(event.getMeasurement());
        measurementAction.setObservation(event.getObservation());
        getMeasurementCommentDialog().setVisible(true);
    }

    private MeasurementCommentDialog getMeasurementCommentDialog() {
        if (measurementDialog == null) {
            measurementDialog = new MeasurementCommentDialog(imageAnnotationFrame);
            measurementDialog.getOkayButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    measurementAction.setComment(measurementDialog.getComment());
                    measurementDialog.setVisible(false);
                    measurementAction.apply();
                }
            });
        }
        return measurementDialog;
    }


    private class AddMeasurementAction {
        private String comment;
        private Measurement measurement;
        private Observation observation;

        void setComment(String comment) {
            this.comment = comment;
        }

        void setMeasurement(Measurement measurement) {
            this.measurement = measurement;
        }


        void setObservation(Observation observation) {
            this.observation = observation;
        }

        public void apply() {
            if (measurement != null && observation != null) {
                measurement.setComment(comment);
                Command command = new AddAssociationCmd(measurement.toLink(), ImmutableList.of(observation));
                CommandEvent commandEvent = new CommandEvent(command);
                EventBus.publish(commandEvent);
            }
        }
    }
}
