/*
 * @(#)PCommentButton.java   2009.12.23 at 09:02:03 PST
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



package vars.annotation.ui.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import org.mbari.awt.event.ActionAdapter;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

/**
 *
 * @author brian
 */
public class PCommentButton extends PropButton {

    private AddPropertyAction addPropertyAction;
    private ActionAdapter showDialogAction;

    /**
     * Constructs ...
     */
    public PCommentButton() {
        super();
        addPropertyAction = new AddPropertyAction(getToolBelt(), "comment", "self", "");
        setAction(getShowDialogAction());
        setToolTipText("add comment");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/commentbutton.png")));
        setEnabled(false);
    }

    /**
     * Action called when the OK button on the dialog is pressed.
     * @return
     */
    protected AddPropertyAction getAddPropertyAction() {
        return addPropertyAction;
    }

    /**
     * Action called when the button is pressed. Show's a dialog
     * @return
     */
    protected ActionAdapter getShowDialogAction() {
        if (showDialogAction == null) {
            showDialogAction = new ShowDialogAction();
        }

        return showDialogAction;
    }

    /**
     * WHen the button is pressed this action is called
     */
    private class ShowDialogAction extends ActionAdapter {

        private AddCommentAssociationDialog dialog;

        /**
         */
        @Override
        public void doAction() {
            final AddCommentAssociationDialog d = getDialog();
            d.setComment("");
            d.setVisible(true);
        }

        protected AddCommentAssociationDialog getDialog() {
            if (dialog == null) {
                dialog = new AddCommentAssociationDialog();
                dialog.setLocationRelativeTo(PCommentButton.this);
                dialog.getOkayButton().addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        getAddPropertyAction().setLinkValue(dialog.getComment());
                        dialog.setVisible(false);
                        getAddPropertyAction().doAction();
                    }

                });
            }

            return dialog;
        }
    }
}
