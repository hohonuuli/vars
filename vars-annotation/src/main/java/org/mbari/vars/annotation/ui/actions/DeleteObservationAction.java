/*
 * @(#)DeleteObservationAction.java   2009.11.19 at 01:50:57 PST
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

import java.util.ArrayList;
import java.util.Collection;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.PersistenceController;

/**
 *  <p>Deletes an observation from the database. Also deletes the associated video
 *  frame, if appropriate.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class DeleteObservationAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private Observation observation;
    private final PersistenceController persistenceController;

    /**
     * Constructs ...
     *
     * @param persistenceController
     */
    public DeleteObservationAction(PersistenceController persistenceController) {
        super();
        this.persistenceController = persistenceController;
    }

    /**
     * @see  org.mbari.awt.event.IAction
     */
    public void doAction() {
        if (observation != null) {
            final VideoFrame vf = observation.getVideoFrame();
            if (vf != null) {

                Collection<Observation> observationToDelete = new ArrayList<Observation>();
                observationToDelete.add(observation);
                persistenceController.deleteObservations(observationToDelete);
                persistenceController.deleteEmptyVideoFramesFrom(vf.getVideoArchive());

            }
            else {
                log.warn("Attempted to delete an observation without a parent" +
                         " video frame. How did you even create an observation " + " without a parent VideoFrame?");
            }
        }
        else {
            log.info("Attempted to delete an Observation without selecting" + "the observation to be deleted");
        }
    }

    /**
     *     @return
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     *     @param  observation
     */
    public void setObservation(final Observation observation) {
        this.observation = observation;
    }
}
