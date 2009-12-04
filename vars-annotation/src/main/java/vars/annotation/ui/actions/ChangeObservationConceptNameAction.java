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
 * <p>Changes the conceptName of the currently selected Observation</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeObservationConceptNameAction extends ActionAdapter {

 
    private final String conceptName;
    private final PersistenceController persistenceController;

    /**
     * Constructs ...
     *
     *
     * @param conceptName
     */
    public ChangeObservationConceptNameAction(final String conceptName, PersistenceController persistenceController) {
        this.conceptName = conceptName;
        this.persistenceController = persistenceController;
    }

    /**
     * Changes the conceptName of the <code>Observation</code> retrieved
     * from the ObservationDispatcher.
     *
     */
    public void doAction() {
        
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        observations = new ArrayList<Observation>(observations); // Make collection copy to avoid threading issues
        for (Observation observation : observations) {
            observation.setConceptName(conceptName);
        }
        persistenceController.updateObservations(observations);

    }

    public String getConceptName() {
        return conceptName;
    }
}
