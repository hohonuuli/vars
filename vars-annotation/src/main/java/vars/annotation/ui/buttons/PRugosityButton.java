package vars.annotation.ui.buttons;

import org.mbari.awt.event.ActionAdapter;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import javax.swing.*;

/**
 * @author Brian Schlining
 * @since 2015-11-03T07:44:00
 */
public class PRugosityButton extends PropButton {

    private ActionAdapter showDialogAction;
    private AddPropertyAction addPropertyAction;
    private final ToolBelt toolBelt;



    public PRugosityButton() {
        super();
        this.toolBelt = getToolBelt();
        setAction(getShowDialogAction());
        setToolTipText("S1 - Primary Substrate");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/s1button.png")));
        setEnabled(false);
    }

    /**
     * Action called when the OK button on the dialog is pressed.
     * @return
     */
    protected AddPropertyAction getAddPropertyAction() {
        if (addPropertyAction == null) {
            addPropertyAction = new AddPropertyAction(toolBelt, "rugosity", "self", "nil");
        }
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

        private ToConceptSelectionDialog dialog;

        protected ToConceptSelectionDialog getDialog() {
            if (dialog == null) {
                dialog = new ToConceptSelectionDialog(toolBelt.getAnnotationPersistenceService(), "Substrates");
                dialog.setLocationRelativeTo(PRugosityButton.this);
                dialog.getOkayButton().addActionListener(e -> {
                    getAddPropertyAction().setToConcept(dialog.getSelectedConcept().getPrimaryConceptName().getName());
                    dialog.setVisible(false);
                    getAddPropertyAction().doAction();
                });
            }
            return dialog;
        }

        @Override
        public void doAction() {
            final ToConceptSelectionDialog d = getDialog();
            d.setVisible(true);
        }
    }
}
