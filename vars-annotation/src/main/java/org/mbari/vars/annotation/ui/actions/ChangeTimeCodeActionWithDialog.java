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


package org.mbari.vars.annotation.ui.actions;

import javax.swing.JFrame;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.ChangeTimeCodeFrame;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;

/**
 * <p>Pops up a dialog that allows the user to change the timecode of the
 * currently selected VideoFrame. The VideoFrame is retrieved through the
 * ObservationDispatcher</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: ChangeTimeCodeActionWithDialog.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ChangeTimeCodeActionWithDialog extends ActionAdapter implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="frame"
     *     @uml.associationEnd
     */
    private JFrame frame;

    /**
     * Constructor for the ChangeTimeCodeAction object
     */
    public ChangeTimeCodeActionWithDialog() {
        super("Edit Time-code");
        final ObservationDispatcher d = ObservationDispatcher.getInstance();
        d.addObserver(this);
        update(d.getObservation(), "");
    }

    /**
     *  Description of the Method
     */
    public void doAction() {
        getFrame().setVisible(true);
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="frame"
     */
    private JFrame getFrame() {
        if (frame == null) {
            frame = new ChangeTimeCodeFrame();
            frame.setTitle("VARS - Edit Time-code");
            ((ChangeTimeCodeFrame) frame).update(ObservationDispatcher.getInstance().getObservation(), "");
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
    public void update(final Object obj, final Object changeCode) {
        setEnabled((obj != null));
    }
}
