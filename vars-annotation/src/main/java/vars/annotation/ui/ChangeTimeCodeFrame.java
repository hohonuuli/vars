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

import vars.annotation.ui.ToolBelt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import org.mbari.movie.Timecode;
import org.mbari.util.Dispatcher;
import org.mbari.vcr.ui.TimeCodeSelectionFrame;
import org.mbari.vcr.ui.TimeSelectPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.actions.ChangeTimeCodeAction;
import vars.annotation.ui.Lookup;

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
        super();
        this.toolBelt = toolBelt;
        final Dispatcher dispatcher = Lookup.getSelectedObservationsDispatcher();
        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                update(evt.getNewValue());

            }
        });

    }

    /**
     *  Gets the okActionListener attribute of the ChangeTimeCodeFrame object
     *
     * @return  The okActionListener value
     * @see org.mbari.vcr.ui.TimeCodeSelectionFrame#getOkActionListener()
     */
    public ActionListener getOkActionListener() {
        if (okActionListener == null) {
            okActionListener = new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final Dispatcher dispatcher = Lookup.getSelectedObservationsDispatcher();
                    final Collection<Observation> observations = (Collection<Observation>) dispatcher.getValueObject();
                    if (observations.size() == 1) {
                        final VideoFrame vf = observations.iterator().next().getVideoFrame();
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

        if ((obs != null) && (obs.size() == 1)) {


            final VideoFrame vf = obs.iterator().next().getVideoFrame();
            if (vf != null) {
                final String stc = vf.getTimecode();
                if (stc != null) {
                    try {
                        final Timecode tc = new Timecode(stc);
                        hour = tc.getHour();
                        minute = tc.getMinute();
                        second = tc.getSecond();
                        frame = tc.getFrame();
                    }
                    catch (NumberFormatException e) {
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
