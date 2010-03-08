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

import foxtrot.Job;
import foxtrot.Worker;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
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
    private final MoveVideoFramesFunction function;
    private Collection<VideoFrame> videoFrames = new ArrayList<VideoFrame>();

    /**
     *
     * @param owner
     * @param toolBelt
     */
    public MoveVideoFrameWithDialogAction(final Frame owner, ToolBelt toolBelt) {
        super(ACTION_NAME);
        function = new MoveVideoFramesFunction(toolBelt.getAnnotationDAOFactory());
        dialog = new OpenVideoArchiveDialog(owner, toolBelt);
        dialog.getOkayButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                WaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator((JFrame) owner, "Moving ...");
                Worker.post(new Job() {
                    @Override
                    public Object run() {
                        VideoArchive videoArchive = dialog.openVideoArchive();
                        return function.apply(videoArchive, videoFrames);
                    }
                    
                });
                waitIndicator.dispose();
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
        videoFrames.clear();
        videoFrames.addAll(videoFrames);
    }

}
