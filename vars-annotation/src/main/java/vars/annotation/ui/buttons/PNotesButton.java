package vars.annotation.ui.buttons;

import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import vars.annotation.Observation;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.EditNotesCmd;
import vars.annotation.ui.dialogs.AddCommentAssociationDialog;

import javax.swing.*;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2015-11-05T12:59:00
 */
public class PNotesButton extends PropButton {

    private ActionAdapter showDialogAction;

    /**
     * Constructs ...
     */
    public PNotesButton() {
        super();
        setAction(getShowDialogAction());
        setToolTipText("edit notes");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/note.png")));
        setEnabled(false);
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
            Collection<Observation> observations = StateLookup.getSelectedObservations();
            if (observations.size() == 1) {
                Observation obs = observations.iterator().next();
                String notes = obs.getNotes();
                String note = (notes != null) ? notes : "";
                d.setComment(note);
                d.setVisible(true);
            }
        }

        protected AddCommentAssociationDialog getDialog() {
            if (dialog == null) {
                dialog = new AddCommentAssociationDialog();
                dialog.setLocationRelativeTo(PNotesButton.this);
                dialog.getOkayButton().addActionListener(e -> {
                    Command command = null;
                    Collection<Observation> observations = StateLookup.getSelectedObservations();
                    if (observations.size() == 1) {
                        Observation obs = observations.iterator().next();
                        String oldNotes = obs.getNotes();
                        String newNotes = dialog.getComment();
                        Long id = (Long) obs.getPrimaryKey();
                        command = new EditNotesCmd(newNotes, oldNotes, id);
                    }
                    dialog.setVisible(false);

                    if (command != null) {
                        CommandEvent commandEvent = new CommandEvent(command);
                        EventBus.publish(commandEvent);
                    }
                });
            }

            return dialog;
        }
    }
}