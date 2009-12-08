/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vars.annotation.ui.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import org.mbari.awt.event.ActionAdapter;
import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class PCommentButton extends PropButton {

    private ActionAdapter showDialogAction;
    private AddPropertyAction addPropertyAction;

    public PCommentButton(ToolBelt toolBelt) {
        super();
        addPropertyAction = new AddPropertyAction(toolBelt, "comment", "self", "");
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

        protected AddCommentAssociationDialog getDialog() {
            if (dialog == null) {
                dialog = new AddCommentAssociationDialog();
                dialog.setLocationRelativeTo(PCommentButton.this);
                dialog.getOkButton().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        getAddPropertyAction().setLinkValue(dialog.getComment());
                        dialog.setVisible(false);
                        getAddPropertyAction().doAction();
                    }
                });
            }
            return dialog;
        }

        @Override
        public void doAction() {
            final AddCommentAssociationDialog d = getDialog();
            d.setComment("");
            d.setVisible(true);
        }
    }
}
