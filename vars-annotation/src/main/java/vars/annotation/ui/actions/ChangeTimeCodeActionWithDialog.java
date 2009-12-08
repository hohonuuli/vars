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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.JFrame;
import org.mbari.awt.event.ActionAdapter;

import vars.annotation.Observation;
import vars.old.annotation.ui.ChangeTimeCodeFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;

/**
 * <p>Pops up a dialog that allows the user to change the timecode of the
 * currently selected VideoFrame. The VideoFrame is retrieved through the
 * ObservationDispatcher</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeTimeCodeActionWithDialog extends ActionAdapter {

    private JFrame frame;
    private final ToolBelt toolBelt;

    /**
     * Constructor for the ChangeTimeCodeAction object
     */
    @SuppressWarnings("unchecked")
    public ChangeTimeCodeActionWithDialog(ToolBelt toolBelt) {
        super("Edit Time-code");
        this.toolBelt = toolBelt;
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                update((Collection<Observation>) evt.getNewValue());
                
            }
        });
        update((Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject());
    }

    /**
     *  Description of the Method
     */
    public void doAction() {
        getFrame().setVisible(true);
    }

 
    @SuppressWarnings("unchecked")
    private JFrame getFrame() {
        if (frame == null) {
            frame = new ChangeTimeCodeFrame(toolBelt);
            frame.setTitle("VARS - Edit Time-code");
            final Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
            ((ChangeTimeCodeFrame) frame).update(observations);
            frame.pack();
        }

        return frame;
    }

    /**
     * Registered to observation displatcher
     *
     * @param  changeCode
     * @param  obj Description of the Parameter
     */
    private void update(final Collection<Observation> observations) {
        
        setEnabled(observations.size() > 0);
    }
}
