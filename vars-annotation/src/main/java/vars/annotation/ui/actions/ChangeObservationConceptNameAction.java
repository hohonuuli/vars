/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.annotation.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangeObservationNameCmd;

/**
 * <p>Changes the conceptName of the currently selected Observation</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeObservationConceptNameAction extends ActionAdapter {

 
    private final String conceptName;

    /**
     * Constructs ...
     *
     *
     * @param conceptName
     */
    public ChangeObservationConceptNameAction(final String conceptName) {
        this.conceptName = conceptName;
    }

    /**
     * Changes the conceptName of the <code>Observation</code> retrieved
     * from the ObservationDispatcher.
     *
     */
    public void doAction() {
        UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        String username = userAccount == null ? UserAccount.USERNAME_DEFAULT : userAccount.getUserName();
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        observations = new ArrayList<>(observations); // Make collection copy to avoid threading issues
        Command command = new ChangeObservationNameCmd(observations, conceptName, username, new Date());
        CommandEvent commandEvent = new CommandEvent(command);
        EventBus.publish(commandEvent);
    }

    public String getConceptName() {
        return conceptName;
    }
}
