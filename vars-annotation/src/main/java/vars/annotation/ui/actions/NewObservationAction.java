/*
 * @(#)NewObservationAction.java   2009.11.19 at 10:37:09 PST
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



package vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.swing.Action;
import javax.swing.KeyStroke;
import org.mbari.awt.event.ActionAdapter;
import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Locates all the VideoFrames selected and adds a new Observation to them</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public final class NewObservationAction extends ActionAdapter {

    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public NewObservationAction(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        putValue(Action.NAME, "New observation");
        putValue(Action.ACTION_COMMAND_KEY, "new observation");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     */
    public void doAction() {

        final PersistenceController persistenceController = toolBelt.getPersistenceController();
        final AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        final Date date = new Date();

        // TODO this is wrong. Verify what this is supposed to do.
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        observations = new ArrayList<Observation>(observations);    // Copy to avoid threading issues
        
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>();
        for (Observation observation : observations) {
			videoFrames.add(observation.getVideoFrame());
		}
        
        for (Observation observation : observations) {
			VideoFrame videoFrame = observation.getVideoFrame();
			if (videoFrames.contains(videoFrame)) {
				Observation newObservation = annotationFactory.newObservation();
	            newObservation.setConceptName(observation.getConceptName());
	            newObservation.setObservationDate(date);
	            newObservation.setObserver(userAccount.getUserName());
	            videoFrames.remove(videoFrame);
	            persistenceController.insertObservation(videoFrame, newObservation);
			}
		}
        
    }
}
