/*
 * @(#)DuplicateObservationAction.java   2009.12.11 at 05:53:04 PST
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
import java.util.HashSet;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.DuplicateObservationsCmd;

/**
 * <p>Duplicates selected observations</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public final class DuplicateObservationAction extends ActionAdapter {

    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public DuplicateObservationAction(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        putValue(Action.NAME, "Duplicate Observations");
        putValue(Action.ACTION_COMMAND_KEY, "duplicate observations");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     */
    public void doAction() {

        //final PersistenceController persistenceController = toolBelt.getPersistenceController();
        //final AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();

        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher()
            .getValueObject();
        observations = new ArrayList<Observation>(observations);    // Copy to avoid threading issues

        //Date observationDate = new Date();
        String name = userAccount.getUserName();

        Command command = new DuplicateObservationsCmd(name, observations);
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);

//        Collection<Observation> newObservations = new ArrayList<Observation>(observations.size());
//
//        // DAOTX
//        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
//        dao.startTransaction();
//
//        for (Observation observation : observations) {
//            observation = dao.find(observation);
//            VideoFrame videoFrame = observation.getVideoFrame();
//            Observation newObservation = annotationFactory.newObservation();
//            newObservation.setConceptName(observation.getConceptName());
//            newObservation.setObservationDate(observationDate);
//            newObservation.setObserver(name);
//            videoFrame.addObservation(newObservation);
//            dao.persist(newObservation);
//            newObservations.add(newObservation);
//        }
//
//        dao.endTransaction();
//
//        persistenceController.updateUI(newObservations);

    }
}
