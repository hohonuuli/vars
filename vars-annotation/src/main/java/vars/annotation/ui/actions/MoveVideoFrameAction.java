/*
 * @(#)MoveVideoFrameAction.java   2010.03.04 at 07:55:57 PST
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

import java.util.ArrayList;
import java.util.Collection;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;

/**
 * This action moves a Collection of VideoFrames to a single VideoArchive
 *
 * @author brian
 */
public class MoveVideoFrameAction extends ActionAdapter implements IVideoArchiveProperty {

    /** <!-- Field description --> */
    public static final String ACTION_NAME = "Move Video Frames";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;
    private final MoveVideoFramesFunction function;

    /**
     *     This is where we want to move the videoframes to
     */
    private VideoArchive videoArchive;

    /**
     *     These are the videoFrames to be moved.
     */
    private Collection<VideoFrame> videoFrames;

    /**
     *
     *
     * @param toolBelt
     */
    public MoveVideoFrameAction(ToolBelt toolBelt) {
        super(ACTION_NAME);
        this.toolBelt = toolBelt;
        function = new MoveVideoFramesFunction(toolBelt.getAnnotationDAOFactory());
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if ((videoArchive != null) && (videoFrames != null)) {


            Collection<Observation> observations = new ArrayList<Observation>();
            Collection<VideoFrame> modifiedVideoFrames = function.apply(videoArchive, videoFrames);
            for (VideoFrame videoFrame : modifiedVideoFrames) {
                observations.addAll(videoFrame.getObservations());
            }

            toolBelt.getPersistenceController().updateUI(observations);

        }
    }

    /**
     *     Returns the VideoArchive. This is the destination that the VideoFrames will/have been moved to.
     *     @see vars.annotation.ui.actions.IVideoArchiveProperty#getVideoArchive()
     *     @return  The VideoArchive object that the frames will be moved to.
     */
    public VideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *     @return  Returns the videoFrames.
     */
    public Collection<VideoFrame> getVideoFrames() {
        return videoFrames;
    }

    /**
     *     Sets the target VideoArchive. This is the destination that the VideoFrames will be moved to.
     *     @param  videoArchive
     */
    public void setVideoArchive(final VideoArchive videoArchive) {

        this.videoArchive = videoArchive;
    }

    /**
     *     @param videoFrames  The videoFrames to set.
     */
    public void setVideoFrames(final Collection<VideoFrame> videoFrames) {
        this.videoFrames = videoFrames;
    }
}
