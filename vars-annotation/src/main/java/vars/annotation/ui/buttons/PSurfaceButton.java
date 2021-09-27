/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import mbarix4j.awt.event.ActionAdapter;
import vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class PSurfaceButton extends PropButton {

    private ActionAdapter showDialogAction;
    private AddPropertyAction addPropertyAction;
    private final ToolBelt toolBelt;

    public PSurfaceButton() {
        super();
        this.toolBelt = getToolBelt();
        setAction(getShowDialogAction());
        setToolTipText("upon");
        setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/surfacebutton.png")));
        setEnabled(false);
    }

    /**
     * Action called when the OK button on the dialog is pressed.
     * @return
     */
    protected AddPropertyAction getAddPropertyAction() {
        if (addPropertyAction == null) {
            addPropertyAction = new AddPropertyAction(toolBelt, "upon", "physical object", "nil");
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
                dialog = new ToConceptSelectionDialog(toolBelt.getAnnotationPersistenceService());
                dialog.setLocationRelativeTo(PSurfaceButton.this);
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
