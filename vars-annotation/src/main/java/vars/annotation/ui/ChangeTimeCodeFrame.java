/*
 * @(#)ChangeTimeCodeFrame.java   2009.11.18 at 01:44:10 PST
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



package vars.annotation.ui;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Optional;

import org.mbari.vcr4j.time.HMSF;
import org.mbari.vcr4j.ui.swing.TimeCodeSelectionFrame;
import org.mbari.vcr4j.ui.swing.TimeSelectPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.actions.ChangeTimeCodeAction;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

/**
 * <p>A dialog that allows the annotator to edit the time-code for the currently
 * selected observation.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeTimeCodeFrame extends TimeCodeSelectionFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;
    private ChangeTimeCodeAction changeTimeCodeAction;

    /**
     * Constructor for the ChangeTimeCodeFrame object
     */
    public ChangeTimeCodeFrame(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        AnnotationProcessor.process(this); // Create EventBus Proxy
    }

    /**
     * EventBus method
     * @param selectionEvent
     */
    @EventSubscriber(eventClass = ObservationsSelectedEvent.class)
    public void updateObservationSelection(ObservationsSelectedEvent selectionEvent) {
        if (selectionEvent.getSelectionSource() != this) {
            update(selectionEvent.get());
        }
    }

    /**
     *  Gets the okActionListener attribute of the ChangeTimeCodeFrame object
     *
     * @return  The okActionListener value
     */
    public ActionListener getOkActionListener() {
        if (okActionListener == null) {
            okActionListener = new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final Collection<Observation> observations = StateLookup.getSelectedObservations();

                    if (observations.size() == 1) {
                        getChangeTimeCodeAction().setTimeCode(getTimePanel().getTimeAsString());
                        getChangeTimeCodeAction().doAction();
                    }

                    setVisible(false);
                }

            };
        }

        return okActionListener;
    }

    private ChangeTimeCodeAction getChangeTimeCodeAction() {
        if (changeTimeCodeAction == null) {
            changeTimeCodeAction = new ChangeTimeCodeAction(toolBelt);
        }
        return changeTimeCodeAction;
    }

    /**
     *  Receives notifications when the selected observation changes.
     *
     *
     * @param selectedObservations
     */
    public void update(final Object selectedObservations) {
        if (selectedObservations == null) {
            return;
        }

        final Collection<Observation> obs = (Collection<Observation>) selectedObservations;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int frame = 0;

        if (obs.size() == 1) {


            final VideoFrame vf = obs.iterator().next().getVideoFrame();
            if (vf != null) {
                final String stc = vf.getTimecode();
                if (stc != null) {
                    try {
                        Optional<HMSF> hmsfOpt = HMSF.from(stc);
                        if (hmsfOpt.isPresent()) {
                            HMSF hmsf = hmsfOpt.get();
                            hour = hmsf.getHour();
                            minute = hmsf.getMinute();
                            second = hmsf.getSecond();
                            frame = hmsf.getFrame();
                        }
                    }
                    catch (Exception e) {
                        log.info("Failed to parse timecode of " + stc);
                    }
                }
            }

            final TimeSelectPanel tp = getTimePanel();
            tp.getHourWidget().setTime(hour);
            tp.getMinuteWidget().setTime(minute);
            tp.getSecondWidget().setTime(second);
            tp.getFrameWidget().setTime(frame);
            tp.getHourWidget().getTextField().requestFocus();
        }
    }
}
