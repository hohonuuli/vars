/*
 * @(#)ImageAnnotationFrameController.java   2012.11.26 at 08:48:34 PST
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

import com.google.common.collect.ImmutableList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import vars.annotation.AreaMeasurement;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddAssociationCmd;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class ImageAnnotationFrameController implements MeasurementCompletedListener {

    private final AddMeasurementAction measurementAction = new AddMeasurementAction();
    private AddAreaMeasurementEvent addAreaMeasurementEvent;
    private MeasurementCommentDialog areaMeasurementDialog;
    private final JFrame imageAnnotationFrame;
    private MeasurementCommentDialog measurementDialog;
    private final ToolBelt toolBelt;
    private VideoFrame videoFrame;

    /**
     * Constructs ...
     *
     * @param toolBelt
     * @param imageAnnotationFrame
     */
    public ImageAnnotationFrameController(ToolBelt toolBelt, JFrame imageAnnotationFrame) {
        this.toolBelt = toolBelt;
        this.imageAnnotationFrame = imageAnnotationFrame;
        AnnotationProcessor.process(this);
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
                        addAreaMeasurementEvent = null;    // Free reference
                        areaMeasurement.setComment(areaMeasurementDialog.getComment());
                        AddAssociationCmd command = new AddAssociationCmd(areaMeasurement.toLink(),
                            ImmutableList.of(observation));
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                    areaMeasurementDialog.setVisible(false);

                }

            });
        }

        return areaMeasurementDialog;
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

    /**
     * @return
     */
    public ToolBelt getToolBelt() {
        return toolBelt;
    }

    /**
     * @return
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onComplete(MeasurementCompletedEvent event) {
        measurementAction.setMeasurement(event.getMeasurement());
        measurementAction.setObservation(event.getObservation());
        getMeasurementCommentDialog().setVisible(true);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = AddAreaMeasurementEvent.class)
    public void respondTo(AddAreaMeasurementEvent event) {
        addAreaMeasurementEvent = event;
        getAreaMeasurementCommentDialog().setVisible(true);
    }

    /**
     *
     * @param videoFrame
     */
    public void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    private class AddMeasurementAction {

        private String comment;
        private Measurement measurement;
        private Observation observation;

        /**
         */
        public void apply() {
            if ((measurement != null) && (observation != null)) {
                measurement.setComment(comment);
                Command command = new AddAssociationCmd(measurement.toLink(), ImmutableList.of(observation));
                CommandEvent commandEvent = new CommandEvent(command);
                EventBus.publish(commandEvent);
            }
        }

        void setComment(String comment) {
            this.comment = comment;
        }

        void setMeasurement(Measurement measurement) {
            this.measurement = measurement;
        }

        void setObservation(Observation observation) {
            this.observation = observation;
        }
    }
}
