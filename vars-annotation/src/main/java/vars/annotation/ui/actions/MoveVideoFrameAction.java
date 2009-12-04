/*
 * @(#)MoveVideoFrameAction.java   2009.11.19 at 11:13:56 PST
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
import java.util.Iterator;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.annotation.CameraData;
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
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if ((videoArchive != null) && (videoFrames != null)) {
        	
        	Collection<Observation> observations = new ArrayList<Observation>();


            // DAOTX
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();

            for (VideoFrame sourceFrame : videoFrames) {
                sourceFrame = dao.merge(sourceFrame);
                VideoArchive sourceArchive = sourceFrame.getVideoArchive();
                if ((sourceArchive != null) && !sourceArchive.equals(videoArchive)) {
                    if (log.isInfoEnabled()) {
                        log.info("Moving " + sourceFrame + " from " + sourceArchive + " to " + videoArchive);
                    }
                    
                    observations.addAll(sourceFrame.getObservations());

                    /*
                     * Remove the VideoFrame from it's original VideoArchive
                     */
                    sourceArchive.removeVideoFrame(sourceFrame);

                    /*
                     * Check to see if the target VideoArchive already has a
                     * VideoFrame with that timecode.
                     */
                    VideoFrame targetFrame = videoArchive.findVideoFrameByTimeCode(sourceFrame.getTimecode());
                    if (targetFrame != null) {
                        mergeFrames(sourceFrame, targetFrame);
                        dao.remove(sourceFrame);
                    }
                    else {
                        sourceArchive.removeVideoFrame(sourceFrame);
                        videoArchive.addVideoFrame(sourceFrame);
                    }


                }
            }

            dao.endTransaction();
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
     * Merges the information contained in 2 different videoFrames. <strong>This should be called within
     * a persistence transaction!!!</strong>
     * @param sourceFrame The VideoFrame whose contents will be moved to the target
     *  VideoFrame
     * @param targetFrame The destination for the information from the source
     *  VideoFrame
     */
    private void mergeFrames(final VideoFrame sourceFrame, final VideoFrame targetFrame) {

        /*
         * If a VideoFrame with the same timecode was found in the
         * target(and if you reached this point, one was found) copy the
         * observations to the new videoFrame
         */
        final Collection<Observation> observations = new ArrayList<Observation>(sourceFrame.getObservations());
        for (final Iterator<Observation> j = observations.iterator(); j.hasNext(); ) {
            final Observation observation = j.next();
            sourceFrame.removeObservation(observation);
            targetFrame.addObservation(observation);
        }

        /*
         * If the target does not have an image, but the source does, copy the image
         * to the target.
         */
        final CameraData srcCameraData = sourceFrame.getCameraData();
        if (srcCameraData != null) {
            final String sourceImage = srcCameraData.getImageReference();
            final CameraData targetCamData = targetFrame.getCameraData();
            final String targetImage = targetCamData.getImageReference();
            if ((sourceImage != null) && (targetImage == null)) {
                targetCamData.setImageReference(sourceImage);
            }
        }
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
