package vars.annotation.ui.imagepanel;


import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;

import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.event.UpdateObservationsEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class ImageAnnotationFrameController implements MeasurementCompletedListener {

    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;
    private MeasurementCommentDialog dialog;
    private final ImageAnnotationFrame imageAnnotationFrame;
    private final AddMeasurementAction measurementAction = new AddMeasurementAction();



    public ImageAnnotationFrameController(ToolBelt toolBelt, ImageAnnotationFrame imageAnnotationFrame) {
        this.toolBelt = toolBelt;
        this.imageAnnotationFrame = imageAnnotationFrame;
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

    @Override
    public void onComplete(MeasurementCompletedEvent event) {
        // TODO show dialog for capturing comment about measurement
        measurementAction.setMeasurement(event.getMeasurement());
        measurementAction.setObservation(event.getObservation());
        getMeasurementCommentDialog().setVisible(true);
    }

    private MeasurementCommentDialog getMeasurementCommentDialog() {
        if (dialog == null) {
            dialog = new MeasurementCommentDialog(imageAnnotationFrame);
            dialog.getOkayButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    measurementAction.setComment(dialog.getComment());
                    dialog.setVisible(false);
                    measurementAction.apply();
                }
            });
        }
        return dialog;
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
                Association association = toolBelt.getAnnotationFactory().newAssociation(measurement.toLink());
                DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                dao.startTransaction();
                Observation obs = dao.find(observation);
                obs.addAssociation(association);
                dao.persist(association);
                dao.endTransaction();
                Collection<Observation> observations = new ArrayList<Observation>();
                observations.add(obs);
                //toolBelt.getPersistenceController().updateObservations(observations);
                UpdateObservationsEvent updateEvent = new UpdateObservationsEvent(imageAnnotationFrame, observations);
                EventBus.publish(updateEvent);
                //imageAnnotationFrame.getMeasurementLayerUI().setObservation(obs);
            }
        }
    }
}
