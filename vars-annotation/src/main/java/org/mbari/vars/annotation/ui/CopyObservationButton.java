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

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.actions.CopyObservationAction;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vcr.IVCR;

/**
 * <p>Performs a deep copy of an selected observation to a new time code.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: CopyObservationButton.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class CopyObservationButton extends JFancyButton {

    /**
     *
     */
    private static final long serialVersionUID = -1695586630810906712L;

    /**
     * Constructor
     */
    public CopyObservationButton() {
        super();
        setAction(new CopyObservationAction());
        setToolTipText("Copy an observation to a new timecode [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_copyanno.png")));
        setEnabled(false);
        setText("");

        // Enable this button if someone is logged in AND the Observation
        // in the ObservationDispather is not null and the VCR is enabled.
        ObservationDispatcher.getInstance().addObserver(new IObserver() {

            public void update(final Object obj, final Object changeCode) {
                final String p = PersonDispatcher.getInstance().getPerson();
                final IVCR vcr = VcrDispatcher.getInstance().getVcr();
                if ((p != null) && (obj != null) && (vcr != null)) {
                    setEnabled(true);
                }
                else {
                    setEnabled(false);
                }
            }

        });
    }
}
