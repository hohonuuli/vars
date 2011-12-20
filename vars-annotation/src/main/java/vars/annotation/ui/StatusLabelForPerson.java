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
package vars.annotation.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.mbari.swing.SwingUtils;
import org.mbari.util.Dispatcher;

import vars.UserAccount;
import vars.shared.ui.dialogs.LoginAction;

/**
 * <p>Indicates the status of the annotator. green means an annotator is logged
 * in. red means that no one has logged in. Clicking on this widget will
 * bring up the a dialog allowing a user to connect.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusLabelForPerson.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class StatusLabelForPerson extends StatusLabel {


  
	private final LoginAction action;

    /**
     * Constructor
     */
    public StatusLabelForPerson(ToolBelt toolBelt) {
        super();
        action = new LoginAction(toolBelt.getMiscDAOFactory(), toolBelt.getMiscFactory(), false);
        final Dispatcher pd = Lookup.getUserAccountDispatcher();
        final UserAccount userAccount = (UserAccount) pd.getValueObject();
        update(userAccount);
        pd.addPropertyChangeListener(this);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent me) {
                final JDialog userDialog = action.getDialog();
                final Point mousePosition = me.getPoint();
                SwingUtilities.convertPointToScreen(mousePosition, StatusLabelForPerson.this);
                int x = mousePosition.x;
                int y = mousePosition.y - userDialog.getHeight();
                if (x < 1) {
                    x = 1;
                }
                if (y < 1) {
                    y = 1;
                }
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
     * @param  userAccount The UserAccount to use
     */
    public void update(final UserAccount userAccount) {
        boolean ok = true;
        String msg = "User: Not logged in";
        if (userAccount == null) {
            ok = false;
        }
        else {
            msg = "User: " + userAccount.getUserName();
        }

        setText(msg);
        setOk(ok);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        update((UserAccount) evt.getNewValue());
    }
}
