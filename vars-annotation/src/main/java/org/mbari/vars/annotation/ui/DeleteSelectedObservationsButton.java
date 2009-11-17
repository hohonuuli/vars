/*
 * @(#)DeleteSelectedObservationsButton.java   2009.11.13 at 03:21:06 PST
 *
 * Copyright 2009 MBARI
 *
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
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SwingUtils;
import org.mbari.vars.annotation.ui.actions.DeleteSelectedObservationsWithConfirmAction;
import vars.UserAccount;
import vars.annotation.ui.Lookup;

/**
 * <p>Deletes the observations selected in the Table. This button will bring
 * up a dialog prompting the user to confirm the delete.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class DeleteSelectedObservationsButton extends JFancyButton {


    /**
     * Constructor
     */
    public DeleteSelectedObservationsButton() {
        super();
        setAction(new DeleteSelectedObservationsWithConfirmAction());
        setToolTipText("Delete selected observations [" +
                       SwingUtils.getKeyString((KeyStroke) getAction().getValue(Action.ACCELERATOR_KEY)) + "]");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/obs_delete.png")));
        setEnabled(false);
        setText("");
        
        ((JTable) Lookup.getObservationTableDispatcher().getValueObject()).getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {

            public void valueChanged(final ListSelectionEvent e) {
                final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                final JTable t = (JTable) Lookup.getObservationTableDispatcher().getValueObject();

                setEnabled((userAccount != null) && (t.getSelectedRowCount() > 0));
            }

        });
    }
}
