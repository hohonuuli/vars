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
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
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

            final AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();


            Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
            if (observations.size() == 0) {
                return;
            }

            observations = new ArrayList<Observation>(observations);    // Copy collection to avoid threading issues

            /*
             * DAOTX See if a VideoFrame with the given time code already exists
             */
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            videoArchive = dao.find(videoArchive);

            final VideoTime videoTime = videoControlService.requestVideoTime();
            VideoFrame videoFrame = videoArchive.findVideoFrameByTimeCode(videoTime.getTimecode());
            if (videoFrame == null) {
                videoFrame = annotationFactory.newVideoFrame();
                videoFrame.setTimecode(videoTime.getTimecode());
                videoFrame.setRecordedDate(videoTime.getDate());
                CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
                videoFrame.getCameraData().setDirection(cameraDirections.getDirection());
                videoArchive.addVideoFrame(videoFrame);
                dao.persist(videoFrame);
            }

            UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            Date date = new Date();

            Collection<Observation> newObservations = new ArrayList<Observation>();    // List used to notify UI
            for (Observation observation : observations) {
                Observation copyObservation = annotationFactory.newObservation();
                copyObservation.setObserver(userAccount.getUserName());
                copyObservation.setObservationDate(date);
                copyObservation.setConceptName(observation.getConceptName());
                videoFrame.addObservation(copyObservation);
                dao.persist(copyObservation);

                // Deep copy
                for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                    Association copyAssociation = annotationFactory.newAssociation();
                    copyAssociation.setLinkName(association.getLinkName());
                    copyAssociation.setLinkValue(association.getLinkValue());
                    copyAssociation.setToConcept(association.getToConcept());
                    copyObservation.addAssociation(copyAssociation);
                    dao.persist(copyAssociation);
                }

                newObservations.add(copyObservation);
            }

            dao.endTransaction();

            toolBelt.getPersistenceController().updateUI(newObservations);

        }
        else {
            log.warn("Missing either a VideoArchive or a VideoControlService; unable to copy observations");
        }
    }
}
