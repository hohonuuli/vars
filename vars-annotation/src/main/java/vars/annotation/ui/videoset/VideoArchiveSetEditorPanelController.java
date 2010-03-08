/*
 * @(#)VideoArchiveSetEditorPanelController.java   2010.03.04 at 07:22:03 PST
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



package vars.annotation.ui.videoset;

import com.google.common.collect.ImmutableList;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.annotation.Observation;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.MoveVideoFrameWithDialogAction;
import vars.annotation.ui.table.JXObservationTable;
import vars.annotation.ui.table.ObservationTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Mar 2, 2010
 * Time: 5:24:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class VideoArchiveSetEditorPanelController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final VideoArchiveSetEditorPanel panel;
    private final ToolBelt toolBelt;
    private final MoveVideoFrameWithDialogAction moveAction;

    /**
     * Constructs ...
     *
     * @param panel
     * @param toolBelt
     */
    public VideoArchiveSetEditorPanelController(VideoArchiveSetEditorPanel panel, ToolBelt toolBelt) {
        this.panel = panel;
        this.toolBelt = toolBelt;
        Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
        this.moveAction = new MoveVideoFrameWithDialogAction(frame, toolBelt);
    }

    protected void addAssociation() {}

    protected void delete() {

        Collection<Observation> observations = getSelectedObservations();
        final int count = observations.size();

        if (observations.size() < 1) {
            return;
        }

        final Object[] options = { "OK", "CANCEL" };
        final int confirm = JOptionPane.showOptionDialog((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(),
                                "Do you want to delete " + count + " observation(s)?", "VARS - Confirm Delete",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (confirm == JOptionPane.YES_OPTION) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            for (Observation observation : observations) {
                observation = dao.find(observation);
                observation.getVideoFrame().removeObservation(observation);
                dao.remove(observation);
            }
            dao.endTransaction();
        }

    }

    protected void moveObservations() {
        Collection<VideoFrame> videoFrames = getSelectedVideoFrames();
        moveAction.setVideoFrames(videoFrames);
        moveAction.doAction();
        refresh();
    }



    /*
     * Iplement the following:
     * - Bulk add associations
     * - Change camera directions
     * - Move frames
     * - Remove association from selected
     */
    protected void refresh() {

        /*
         * all observations that get added to the table, or that are currently
         * in the table get added to this set and removed from the
         * observationsInTable set. Then, observations left in the
         * observationsInTable set are removed from the table and the
         * observationsInTable variable is set to reference the new
         * observationsStillInTable set. (this is the most efficient thing I
         * could think of).
         */
        JXObservationTable myTable = panel.getTable();
        ((ObservationTableModel) myTable.getModel()).clear();

        VideoArchiveSet videoArchiveSet = panel.getVideoArchiveSet();

        if (videoArchiveSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving all video frames for " + videoArchiveSet);
            }

            // DAOTX
            VideoArchiveSetDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO();
            dao.startTransaction();
            videoArchiveSet = dao.find(videoArchiveSet);    // Bring it into the transaction
            final Collection<VideoFrame> videoFrames = ImmutableList.copyOf(videoArchiveSet.getVideoFrames());
            for (VideoFrame videoFrame : videoFrames) {
                final Collection<Observation> observations = ImmutableList.copyOf(videoFrame.getObservations());
                for (Observation observation : observations) {
                    myTable.addObservation(observation);
                }
            }

            dao.endTransaction();
        }

    }

    protected void removeAssociations() {}

    protected void renameAssociations() {}

    protected void search() {}

    protected Collection<Observation> getSelectedObservations() {
        JXObservationTable myTable = panel.getTable();
        ObservationTableModel model = (ObservationTableModel) myTable.getModel();
        int[] rows = myTable.getSelectedRows();
        Collection<Observation> observations = new ArrayList<Observation>();
        for (int i : rows) {
            observations.add(myTable.getObservationAt(i));
        }
        return observations;
    }

    protected Collection<VideoFrame> getSelectedVideoFrames() {
        Collection<VideoFrame> videoFrames = new HashSet<VideoFrame>();
        for (Observation observation : getSelectedObservations()) {
            videoFrames.add(observation.getVideoFrame());
        }
        return videoFrames;
    }

}
