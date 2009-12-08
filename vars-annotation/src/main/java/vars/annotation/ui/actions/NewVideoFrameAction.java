/*
 * @(#)NewVideoFrameAction.java   2009.11.19 at 10:09:07 PST
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
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.movie.Timecode;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.UserAccount;
import vars.annotation.CameraData;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.knowledgebase.ConceptName;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.video.VideoControlService;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>Action to add a new VideoFrame and a 'nearly' empty Observation
 * to the ObservationTable. Persistence is NOT done in the action but
 * is handled by the table.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public final class NewVideoFrameAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Timecode timeCodeObj = new Timecode();
    private String defaultConceptName;
    private final ToolBelt toolBelt;

    /**
     * Constructor for the NewVideoFrameAction object
     *
     * @param toolBelt
     */
    public NewVideoFrameAction(final ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;

        try {
            defaultConceptName = toolBelt.getAnnotationPersistenceService().findRootConcept().getPrimaryConceptName().getName();
        }
        catch (final Exception e) {
            defaultConceptName = ConceptName.NAME_DEFAULT;

            if (log.isWarnEnabled()) {
                log.warn("Failed to lookup root concept from database", e);
            }
        }

        /*
         * If the cache is cleared the default concept name MAY change. We need to listen for that change
         */
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                try {
                    defaultConceptName = toolBelt.getAnnotationPersistenceService().findRootConcept().getPrimaryConceptName().getName();
                }
                catch (Exception e) {
                    defaultConceptName = ConceptName.NAME_DEFAULT;

                    if (log.isWarnEnabled()) {
                        log.warn("Failed to lookup root concept from database", e);
                    }
                }
            }

            public void beforeClear(CacheClearedEvent evt) {
                defaultConceptName = ConceptName.NAME_DEFAULT;
            }

        });

        putValue(Action.NAME, "New video-frame");
        putValue(Action.ACTION_COMMAND_KEY, "new video-frame");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     * Initiates the action. using the DEFAULT_CONCEPTNAME
     * @see  org.mbari.awt.event.IAction
     */
    public void doAction() {
        doAction(defaultConceptName);
    }

    /**
     * Inserts a new Observation using the supplied conceptName. The new
     * observation will be attached to a new VideoFrame if no matching time
     * code was found. Otherwise a pre-existing videoFrame will be used.
     *
     * NOTE: The insertion and naming needs to be done in one step. Originally, the
     * code was inserting an observation o f'object' then renaming it.
     * However, during certain observations the row would not get moved to the
     * newly inserted observation and we would end up renaming the wrong object.
     *
     * @param  conceptName
     * @return The observation created. null if none was created.
     */
    public Observation doAction(final String conceptName) {
        Observation observation = null;

        // Need the VCR to get a current timecode
        VideoControlService videoService = (VideoControlService) Lookup.getVideoServiceDispatcher().getValueObject();
        final IVCR vcr = videoService;
        if (vcr != null) {
            final String timecode = vcr.getVcrTimecode().toString();
            observation = doAction(conceptName, timecode);
        }

        return observation;
    }

    /**
     * Inserts a new Observation using the supplied conceptName and timecode. The new
     * observation will be attached to a new VideoFrame if no matching time
     * code was found. Otherwise a pre-existing videoFrame will be used.
     *
     * @param  conceptName
     * @param timecode A timecode in the format of HH:MM:SS:FF
     * @return The observation created. null if none was created.
     */
    public Observation doAction(final String conceptName, final String timecode) {
        Observation observation = null;

        // Need a videoArchive to add a VideoFrame too.
        final VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (va != null) {

            /*
             * Verify that the timecode is acceptable
             */
            boolean isTimeOK = false;
            try {
                timeCodeObj.setTimecode(timecode);
                isTimeOK = true;
            }
            catch (final Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Invalid timecode of " + timecode, e);
                }
            }

            final VideoControlService videoService = (VideoControlService) Lookup.getVideoServiceDispatcher().getValueObject();
            final IVCR vcr = videoService;
            if ((conceptName != null) && (timecode != null) && isTimeOK) {
                UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                final PersistenceController persistenceController = toolBelt.getPersistenceController();

                String person = userAccount.getUserName();
                if (person == null) {
                    person = UserAccount.USERNAME_DEFAULT;
                }

                // See if a VideoFrame with the given time code already exists
                VideoFrame vf = va.findVideoFrameByTimeCode(timecode);

                // If none are found create a new one.
                if (vf == null) {
                    vf = toolBelt.getAnnotationFactory().newVideoFrame();
                    Date utcDate = null;

                    /*
                     * If the VCR is recording we'll grab the time off of the
                     * computer clock. Otherwise we'll get it off of the
                     * userbits.
                     */
                    if (vcr.getVcrState().isRecording()) {
                        utcDate = new Date();
                    }
                    else {

                        /*
                         *  Try to grab the userbits off of the tape. The userbits
                         *  may have the time that the frame was recorded stored as a
                         *  little-endian 4-byte int.
                         */
                        vcr.requestVUserbits();
                        final int epicSeconds = NumberUtilities.toInt(vcr.getVcrUserbits().getUserbits(), true);
                        utcDate = new Date((long) epicSeconds * 1000L);
                    }

                    vf.setRecordedDate(utcDate);
                    vf.setTimecode(timecode);

                    CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
                    final String cameraDirection = cameraDirections.getDirection();
                    CameraData cd = vf.getCameraData();
                    cd.setDirection(cameraDirection);

                    vf = persistenceController.insertVideoFrame(va, vf);

                }

                // Create a new observation and add it to the videoFrame
                observation = toolBelt.getAnnotationFactory().newObservation();
                observation.setConceptName(conceptName);
                observation.setObserver(person);
                observation.setObservationDate(new Date());
                observation = toolBelt.getPersistenceController().insertObservation(vf, observation);


            }
            else {
                log.warn("No VCR is available to get a timecode from; unable to create a VideoFrame");
            }
        }
        else {
            log.warn("A VideoArchive has no been assigned; unable to create a VideoFrame");
        }

        return observation;
    }
}
