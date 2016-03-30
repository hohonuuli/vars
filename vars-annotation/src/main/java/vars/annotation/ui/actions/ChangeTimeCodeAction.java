/*
 * @(#)ChangeTimeCodeAction.java   2009.12.03 at 02:08:05 PST
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
import javax.swing.Icon;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vcr4j.time.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangeTimeCodeCmd;

/**
 *
 * @author brian
 */
public class ChangeTimeCodeAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is the timecode we want to change to
     */
    private Timecode timeCode;

    /**
     *
     *
     * @param toolBelt
     */
    public ChangeTimeCodeAction(ToolBelt toolBelt) {
        super();
    }

    /**
     * @param name
     * @param toolBelt
     */
    public ChangeTimeCodeAction(final String name, ToolBelt toolBelt) {
        super(name);
    }

    /**
     * @param name
     * @param icon
     * @param toolBelt
     */
    public ChangeTimeCodeAction(final String name, final Icon icon, ToolBelt toolBelt) {
        super(name, icon);
    }

    /**
     *
     */
    public void doAction() {
        Collection<Observation> observations = StateLookup.getSelectedObservations();
        Command command = new ChangeTimeCodeCmd(observations, getTimeCode());
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);
    }

    /**
     * @return Returns the timeCode.
     */
    public String getTimeCode() {
        return (timeCode == null) ? null : timeCode.toString();
    }

    /**
     *
     * @param timeCodeString
     */
    public void setTimeCode(final String timeCodeString) {
        timeCode = new Timecode(timeCodeString);
    }
}
