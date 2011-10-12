/*
 * @(#)ChangeCameraDirectionsCmd.java   2011.10.11 at 03:11:43 PDT
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



package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.CameraData;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.VideoFrameDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.VideoFramesChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class ChangeCameraDirectionsCmd implements Command {

    private final Collection<DataBean> originalData = new ArrayList<DataBean>();
    private final String cameraDirection;

    /**
     * Constructs ...
     *
     * @param cameraDirection
     * @param videoFrames
     */
    public ChangeCameraDirectionsCmd(String cameraDirection, Collection<VideoFrame> videoFrames) {
        this.cameraDirection = cameraDirection;
        for (VideoFrame videoFrame : videoFrames) {
            originalData.add(new DataBean(videoFrame.getPrimaryKey(), videoFrame.getCameraData().getDirection()));
        }
    }

    /**
     *
     * @param toolBelt
     */
    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);
    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        Collection<VideoFrame> modifiedVideoFrames = new ArrayList<VideoFrame>(originalData.size());
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoFrameDAO videoFrameDao = toolBelt.getAnnotationDAOFactory().newVideoFrameDAO(dao.getEntityManager());
        dao.startTransaction();
        for (DataBean bean : originalData) {
            VideoFrame videoFrame = videoFrameDao.findByPrimaryKey(bean.videoFramePrimaryKey);
            CameraData cameraData = videoFrame.getCameraData();
            if (isApply) {
                cameraData.setDirection(cameraDirection);
            }
            else {
                cameraData.setDirection(bean.originalDirection);
            }
            if (!dao.isPersistent(cameraData)) {
                dao.persist(cameraData);
            }
            modifiedVideoFrames.add(videoFrame);
        }
        dao.endTransaction();
        dao.close();
        EventBus.publish(new VideoFramesChangedEvent(null, modifiedVideoFrames));
    }

    /**
     * @return
     */
    @Override
    public String getDescription() {
        return "Set camera direction of " + originalData.size() + " VideoFrames to " + cameraDirection;
    }

    /**
     *
     * @param toolBelt
     */
    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    private class DataBean {

        String originalDirection;
        Object videoFramePrimaryKey;

        private DataBean(Object videoFramePrimaryKey, String originalDirection) {
            this.videoFramePrimaryKey = videoFramePrimaryKey;
            this.originalDirection = originalDirection;
        }
    }
}
