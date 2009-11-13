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


package org.mbari.vars.annotation.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.ui.actions.NewObservationAction;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;

/**
 * <p>Create a new observation using the current time-code from the VCR</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: NewObservationButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class NewObservationButton extends JFancyButton {

    /**
     *
     */
    private static final long serialVersionUID = 53820555698832688L;

    /**
     * Constructor for the NewObservationButton object
     */
    public NewObservationButton() {
        super();
        setAction(new NewObservationAction());
        setToolTipText("Create an Observation using the selected timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copytc.png")));
        setEnabled(false);
        setText("");

        // Enable this button if someone is logged in AND the Observation
        // in the ObservationDispather is not null and the VCR is enabled.
        PredefinedDispatcher.OBSERVATION.getDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                final String p = (String) PredefinedDispatcher.PERSON.getDispatcher().getValueObject();
                setEnabled ((p != null) && (evt.getNewValue() != null));
            }
        });

    }
}
