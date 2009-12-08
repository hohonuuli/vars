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


package vars.annotation.ui.buttons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;

import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.actions.NewObservationAction;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;

/**
 * <p>Create a new observation using the current time-code from the VCR</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class NewObservationButton extends JFancyButton {



    /**
     * Constructor for the NewObservationButton object
     */
    public NewObservationButton(ToolBelt toolBelt) {
        super();
        setAction(new NewObservationAction(toolBelt));
        setToolTipText("Create an Observation using the selected timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copytc.png")));
        setEnabled(false);
        setText("");

        /* 
         * Enable this button if someone is logged in AND the Observation
         * in the ObservationDispather is not null and the VCR is enabled.
         */
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                final Collection<Observation> observations = (Collection<Observation>) evt.getNewValue();
                setEnabled ((userAccount != null) && (observations.size() > 0));
                
            }
        });

    }
}
