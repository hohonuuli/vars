/*
 * @(#)MoveVideoFrameWithDialogAction.java   2009.12.03 at 02:09:28 PST
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.MoveVideoFramesCmd;
import vars.annotation.ui.dialogs.OpenVideoArchiveDialog;

/**
 * Prompts a user with a dialog for platform, seqNumber and tapeNumber. Finds the
 * matching VideoArchive and moves the VideoFrames to the at VideoArchive. Use as:
 *
 * <pre>
 * MoveVideoFrameWithDialogAction action = new MoveVideoFrameWithDialogAction();
 * action.setVideoFrames(collectionOfVideoFrames);
 * action.doAction();
 * </pre>
 *
 * @author brian
 */
public class MoveVideoFrameWithDialogAction extends ActionAdapter {

    /** <!-- Field description --> */
    public static final String ACTION_NAME = "Move Frames";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final OpenVideoArchiveDialog dialog;
    private Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>();

    /**
     *
     * @param owner
     * @param toolBelt
     */
    public MoveVideoFrameWithDialogAction(final Frame owner, ToolBelt toolBelt) {
        super(ACTION_NAME);
        dialog = new OpenVideoArchiveDialog(owner, toolBelt);
        dialog.getOkayButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                VideoArchive videoArchive = dialog.openVideoArchive();
                Command command = new MoveVideoFramesCmd(videoArchive.getName(), videoFrames);
                CommandEvent commandEvent = new CommandEvent(command);
                dialog.dispose();
                EventBus.publish(commandEvent);
            }
        });
    }

    /**
     *
     */
    public void doAction() {
        dialog.setVisible(true);
    }

    /**
     *
     * @param videoFrames
     */
    public void setVideoFrames(final Collection<VideoFrame> videoFrames) {
        this.videoFrames.clear();
        this.videoFrames.addAll(videoFrames);
    }

}
