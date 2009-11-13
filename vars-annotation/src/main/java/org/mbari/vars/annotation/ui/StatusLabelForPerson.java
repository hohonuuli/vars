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


/*
Created on Dec 1, 2003
 */
package org.mbari.vars.annotation.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.ui.actions.OpenUserAccountAction;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;

/**
 * <p>Indicates the status of the annotator. green means an annotator is logged
 * in. red means that no one has logged in. Clicking on this widget will
 * bring up the a dialog allowing a user to connect.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusLabelForPerson.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StatusLabelForPerson extends StatusLabel {

    /**
     *
     */
    private static final long serialVersionUID = -1585624751599862740L;

    /**
     *     @uml.property  name="action"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final OpenUserAccountAction action = new OpenUserAccountAction();

    /**
     * Constructor
     */
    public StatusLabelForPerson() {
        super();
        final PersonDispatcher pd = PersonDispatcher.getInstance();
        update(pd.getPerson(), null);
        pd.addObserver(this);
        addMouseListener(new MouseAdapter() {

            public void mouseClicked(final MouseEvent me) {
                final JDialog userDialog = action.getDialog();
                final Point mousePosition = me.getPoint();
                SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForPerson.this);
                final int x = mousePosition.x;
                final int y = mousePosition.y - userDialog.getHeight();
                userDialog.setLocation(x, y);
                SwingUtils.flashJComponent(StatusLabelForPerson.this, 2);
                action.doAction();
            }

        });
    }

    /**
     * Developers should not call this method directly. This method is called
     * by the <code>PersonDispatcher</code>
     *
     *
     * @param  personString Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see  org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object personString, final Object changeCode) {
        boolean ok = true;
        String msg = "User: Not logged in";
        if ((personString == null) ||!(personString instanceof String)) {
            ok = false;
        }
        else {
            msg = "User: " + (String) personString;

            if (msg.equalsIgnoreCase(PersonDispatcher.DEFAULT_USER)) {
                ok = false;
            }
        }

        setText(msg);
        setOk(ok);
    }
}
