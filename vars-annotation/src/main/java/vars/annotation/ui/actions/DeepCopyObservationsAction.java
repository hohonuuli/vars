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
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vcr4j.VideoIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.CopyObservationsCmd;
import vars.avplayer.VideoController;

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
                 KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     *  Initiate the action
     *
     */
    public void doAction() {

        // Need a videoArchive to add a VideoFrame too.
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        final VideoController videoController = StateLookup.getVideoController();
        //final VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        if ((videoArchive != null) && (videoController != null)) {


            Collection<Observation> observations = StateLookup.getSelectedObservations();
            if (observations.size() == 0) {
                return;
            }

            try {
                final Future<VideoIndex> videoIndexFuture = videoController.getVideoIndex();
                final VideoIndex videoIndex = videoIndexFuture.get(3, TimeUnit.SECONDS);
                UserAccount userAccount = StateLookup.getUserAccount();

                //final VideoTime videoTime = videoControlService.requestVideoTime();
                String user = userAccount != null ? userAccount.getUserName() : UserAccount.USERNAME_DEFAULT;

                Command command = new CopyObservationsCmd(videoArchive.getName(), videoIndex, user, observations, true);
                CommandEvent commandEvent = new CommandEvent(command);
                EventBus.publish(commandEvent);
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR,
                        "Timed-out waiting for videoindex from " + videoController);
            }

        }
        else {
            log.warn("Missing either a VideoArchive or a VideoControlService; unable to copy observations");
        }
    }
}
