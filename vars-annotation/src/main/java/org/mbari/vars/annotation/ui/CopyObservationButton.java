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
import java.util.Collection;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.ui.actions.CopyObservationAction;
import org.mbari.vcr.IVCR;

import vars.UserAccount;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.VideoService;

/**
 * <p>Performs a deep copy of an selected observation to a new time code.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class CopyObservationButton extends JFancyButton {


    /**
     * Constructor
     */
    public CopyObservationButton(ToolBelt toolBelt) {
        super();
        setAction(new CopyObservationAction(toolBelt));
        setToolTipText("Copy an observation to a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copyanno.png")));
        setEnabled(false);
        setText("");

        /* 
         * Enable this button if someone is logged in AND the Observation
         * in the ObservationDispather is not null and the VCR is enabled.
         */
        Lookup.getSelectedObservationsDispatcher().addPropertyChangeListener(new PropertyChangeListener() {
            
            public void propertyChange(PropertyChangeEvent evt) {
                
                Collection<Observation> obs = (Collection<Observation>) evt.getNewValue();
                
                final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher();
                final VideoService videoService = (VideoService) Lookup.getVideoServiceDispatcher();
                final IVCR vcr = videoService.getVCR();
                if ((userAccount != null) && (obs.size() == 1) && (vcr != null)) {
                    setEnabled(true);
                }
                else {
                    setEnabled(false);
                }
                
            }
        });
        
    }
}
