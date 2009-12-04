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

import org.mbari.awt.event.ActionAdapter;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>Deletes all the Observations selected in the table from the database.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class DeleteSelectedObservationsAction extends ActionAdapter {
    
    private final PersistenceController persistenceController;

    public DeleteSelectedObservationsAction(PersistenceController persistenceController) {
        super();
        this.persistenceController = persistenceController;
    }

    /**
     *
     */
    public void doAction() {
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        observations = new ArrayList<Observation>(observations); // Copy collection to avoid threading issues
        persistenceController.deleteObservations(observations);
    }
}
