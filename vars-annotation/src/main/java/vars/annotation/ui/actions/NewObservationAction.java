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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr4j.VideoIndex;
import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.UserAccount;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationCmd;
import vars.avplayer.VideoController;
import vars.knowledgebase.ConceptName;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Action to add a new VideoFrame and a 'nearly' empty Observation
 * to the ObservationTable. Persistence is NOT done in the action but
 * is handled by the table.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public final class NewObservationAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private String defaultConceptName;

    /**
     * Constructor for the NewVideoFrameAction object
     *
     * @param toolBelt
     */
    public NewObservationAction(final ToolBelt toolBelt) {
        super();

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
    public void doAction(final String conceptName) {

        // Need the VCR to get a current timecode
        VideoController videoController = StateLookup.getVideoController();
        //VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        if (videoController != null) {
            try {
                Future<VideoIndex> videoIndexFuture = videoController.getVideoIndex();
                // TODO this needs to grab all components of the videoIndex and update those values in VARS
                VideoIndex videoIndex = videoIndexFuture.get(3, TimeUnit.SECONDS);

                // HACK, if no timecode should I throw an exception
                Timecode timecode = videoIndex.getTimecode().orElse(Timecode.zero());
                doAction(conceptName, timecode.toString());
            }
            catch (Exception e) {
                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
            }
        }

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
    public void doAction(final String conceptName, final String timecode) {

        // Need a videoArchive to add a VideoFrame too.
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        final VideoController videoController = StateLookup.getVideoController();

        // TODO use new VCR4J v3 - should get and parse parts of video index
        //final VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
        if (videoArchive != null) {

            if ((conceptName != null) && (timecode != null)) {
                UserAccount userAccount = StateLookup.getUserAccount();

                String person = userAccount.getUserName();
                if (person == null) {
                    person = UserAccount.USERNAME_DEFAULT;
                }

                try {
                    Future<VideoIndex> videoIndexFuture = videoController.getVideoIndex();
                    // TODO this needs to grab all components of the videoIndex and update those values in VARS
                    VideoIndex videoIndex = videoIndexFuture.get(3, TimeUnit.SECONDS);
                    Date utcDate = videoIndex.getTimestamp()
                            .map(Date::from)
                            .orElse(new Date());

                    CameraDirections cameraDirections = StateLookup.getCameraDirection();
                    final String cameraDirection = cameraDirections.getDirection();


                    // Fire command to the CommandQueue
                    Command command = new AddObservationCmd(conceptName, timecode, utcDate,
                            videoArchive.getName(), person, cameraDirection, true);
                    CommandEvent commandEvent = new CommandEvent(command);
                    EventBus.publish(commandEvent);
                }
                catch (Exception e) {
                    EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
                }

            }
            else {
                log.warn("Missing parameters needed to create new Observation");
            }
        }
        else {
            log.warn("A VideoArchive has no been assigned; unable to create a VideoFrame");
        }

    }
}
