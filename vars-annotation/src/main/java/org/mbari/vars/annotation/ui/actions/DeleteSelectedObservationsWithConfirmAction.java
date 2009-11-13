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
Created on Feb 27, 2004
 *
To change the template for this generated file go to
Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.mbari.vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.event.IAction;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * <p>Provides a wrapper around the DeleteSelectedObservationsAction so that the
 * user must confirm that the delete should occur.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: DeleteSelectedObservationsWithConfirmAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @see DeleteSelectedObservationsAction
 */
public class DeleteSelectedObservationsWithConfirmAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="action"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    final IAction action = new DeleteSelectedObservationsAction();

    /**
     * Constructs ...
     *
     */
    public DeleteSelectedObservationsWithConfirmAction() {
        putValue(Action.NAME, "Delete observations");
        putValue(Action.ACTION_COMMAND_KEY, "delete observations");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     * @see org.mbari.awt.event.IAction#doAction()
     */
    public void doAction() {
        final JTable table = ObservationTableDispatcher.getInstance().getObservationTable();
        final int count = table.getSelectedRowCount();
        final Object[] options = { "OK", "CANCEL" };
        final int confirm = JOptionPane.showOptionDialog(AppFrameDispatcher.getFrame(),
                                "Do you want to delete " + count + " observation(s)?", "VARS - Confirm Delete",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (confirm == JOptionPane.YES_OPTION) {
            action.doAction();
        }
    }
}
