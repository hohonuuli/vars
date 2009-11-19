/*
 * @(#)CloseVideoArchiveAction.java   2009.11.19 at 01:17:51 PST
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



package org.mbari.vars.annotation.ui.actions;

import foxtrot.Job;
import foxtrot.Worker;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JComponent;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.mbari.vars.annotation.locale.UploadStillImageActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>This performs cleanup actions on the VideoArchive when it is closed.
 * These actions include, moving frame-grabs to a new location and updating
 * the StillImageURLs in the database.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class CloseVideoArchiveAction extends ActionAdapter implements IVideoArchiveProperty {

    /**  */
    public static final String ACTION_NAME = "Close Video-archive";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final org.mbari.vars.annotation.locale.UploadStillImageAction action2 = UploadStillImageActionFactory.getAction();
    private final PersistenceController persistenceController;
    private VideoArchive videoArchive;

    /**
     * Constructor
     *
     * @param persistenceController
     */
    public CloseVideoArchiveAction(PersistenceController persistenceController) {
        super(ACTION_NAME);
        this.persistenceController = persistenceController;
    }

    /**
     * Copies contents of CameraData.StillImageURL to image.archive.dir. This
     * also does a pattern match such that if any files exist with the same
     * name as the image but a different extension, they are also copied.
     */
    @SuppressWarnings("unchecked")
    public void doAction() {
        if ((videoArchive == null) || !isEnabled()) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Closing video archive, " + videoArchive);
        }

        JComponent component = (JComponent) Lookup.getApplicationFrameDispatcher().getValueObject();
        LabeledSpinningDialWaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(component);
        waitIndicator.setLabel("Closing " + videoArchive.getName());

        /*
         * Save the last annotations
         */
        waitIndicator.setLabel("Saving changes");
        final Collection<Observation> observations = (Collection<Observation>) Worker.post(new Job() {

            @Override
            public Object run() {
                Collection<Observation> observations = (Collection<Observation>) Lookup
                    .getSelectedObservationsDispatcher().getValueObject();
                observations = new ArrayList<Observation>(observations);    // Copy collection to avoid threading issues
                observations = persistenceController.updateObservations(observations);
                return observations;
            }
        });


        /*
         * We want to scroll through all VideoFrames. If they do not have any
         * observations attached we want to delete them
         */
        waitIndicator.setLabel("Removing empty video-frames");
        Worker.post(new Job() {

            @Override
            public Object run() {
                persistenceController.deleteEmptyVideoFramesFrom(videoArchive);
                return null;
            }
        });

        // Move images to server and update URL's
        waitIndicator.setLabel("Moving images");
        Worker.post(new Job() {

            @Override
            public Object run() {
                action2.doAction();
                return null;
            }
        });

    }

    /**
     *     @return  Returns the videoArchive.
     */
    public final VideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *     @param videoArchive  The videoArchive to set.
     */
    public final void setVideoArchive(final VideoArchive videoArchive) {
        this.videoArchive = videoArchive;

        action2.setVideoArchive(videoArchive);
    }
}
