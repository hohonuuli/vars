/*
 * @(#)ChangeVideoArchiveNameAction.java   2009.11.22 at 01:47:34 PST
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

import java.util.Collection;
import org.bushe.swing.event.EventBus;
import vars.annotation.AnnotationFactory;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangeVideoArchiveNameCmd;

/**
 * <p>Changes the videoArchvieName property of  a VideoArchive. At MBARI,
 * the videoArchiveName is a composite key of platform, seqNumber and
 * tapeNumber, so we have to be sure that the renamed archive gets associated
 * with the correct properties.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeVideoArchiveNameAction extends OpenVideoArchiveUsingParamsAction {

    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public ChangeVideoArchiveNameAction(ToolBelt toolBelt) {
        super(toolBelt.getAnnotationDAOFactory());
        this.toolBelt = toolBelt;
    }

    /**
     *  Initiates the action.
     */
    @Override
    public void doAction() {
        if (!verifyParams()) {
            return;
        }

        VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        String newName = PersistenceController.makeVideoArchiveName(getPlatform(), getSeqNumber(), getTapeNumber(), getPostfix());
        Command command = new ChangeVideoArchiveNameCmd(videoArchive, newName, getPlatform(), getSeqNumber());
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);

    }

    /**
     * @return  true if all the params are valid
     */
    private boolean verifyParams() {
        boolean ok = true;

        /*
         *  Verify that all the need parameters are present
         */
        final String p = getPlatform();
        final int sn = getSeqNumber();
        final int tn = getTapeNumber();

        // Check that all required info is entered
        if ((p == null) || (sn == 0) || (tn == 0)) {
            EventBus.publish(Lookup.TOPIC_WARNING, "Some of the information " +
                    "required to carry out this action is missing. You're request is being ignored.");
            ok = false;
        }

        return ok;
    }
}
