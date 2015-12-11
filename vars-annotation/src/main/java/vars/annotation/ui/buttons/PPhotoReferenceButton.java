package vars.annotation.ui.buttons;

import org.mbari.awt.event.ActionAdapter;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rachelorange on 10/30/15.
 */
public class PPhotoReferenceButton extends PropButton {

    private AddPropertyAction addPropertyAction;
    private ActionAdapter showDialogAction;

    /**
     * Constructs ...
     */
    public PPhotoReferenceButton() {
        super();
        addPropertyAction = new AddPropertyAction(getToolBelt(), "photo-reference", "self", "");
        setAction(getShowDialogAction());
        setToolTipText("add photo reference");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/photo_reference.png")));
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
                dialog.setTitle("VARS - Add Photo Reference");
                dialog.setLocationRelativeTo(PPhotoReferenceButton.this);
                dialog.getOkayButton().addActionListener(e -> {
                    getAddPropertyAction().setLinkValue(dialog.getComment());
                    dialog.setVisible(false);
                    getAddPropertyAction().doAction();
                });
            }

            return dialog;
        }
    }
}