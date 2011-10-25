/*
 * @(#)CopyObservationAction.java   2009.12.10 at 11:38:49 PST
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
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.CopyObservationsCmd;
import vars.shared.ui.video.VideoControlService;
import vars.shared.ui.video.VideoTime;

/**
 * <p>
 * Performs a deep copy of an selected observation to a new time code.
 * Copies the selected Observations and adds it to a new VidoeFrame.
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public final class DeepCopyObservationsAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public DeepCopyObservationsAction(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        putValue(Action.NAME, "Copy observations to a new timecode");
        putValue(Action.ACTION_COMMAND_KEY, "copy observations");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     *  Initiate the action
     *
     */
    public void doAction() {

        // Need a videoArchive to add a VideoFrame too.
        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        final VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        if ((videoArchive != null) && (videoControlService != null)) {


            Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
            if (observations.size() == 0) {
                return;
            }
            final VideoTime videoTime = videoControlService.requestVideoTime();
            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            String user = userAccount != null ? userAccount.getUserName() : UserAccount.USERNAME_DEFAULT;

            Command command = new CopyObservationsCmd(videoArchive.getName(), videoTime, user, observations, true);
            CommandEvent commandEvent = new CommandEvent(command);
            EventBus.publish(commandEvent);

        }
        else {
            log.warn("Missing either a VideoArchive or a VideoControlService; unable to copy observations");
        }
    }
}
