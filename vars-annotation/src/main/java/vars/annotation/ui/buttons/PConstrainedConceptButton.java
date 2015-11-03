package vars.annotation.ui.buttons;

import org.mbari.awt.event.ActionAdapter;
import vars.ILink;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import javax.swing.*;

/**
 * Created by rachelorange on 11/3/15.
 */
public class PConstrainedConceptButton extends PropButton {

    private AddPropertyAction addPropertyAction;
    private ActionAdapter showDialogAction;
    private final String baseConceptName;

    /**
     * Constructs ...
     */
    public PConstrainedConceptButton(String baseConceptName, ILink propertyTemplate, String toolTip, Icon icon) {
        super();
        this.baseConceptName = baseConceptName;
        addPropertyAction = new AddPropertyAction(getToolBelt(),
                propertyTemplate.getLinkName(),
                propertyTemplate.getToConcept(),
                propertyTemplate.getLinkValue());
        setAction(getShowDialogAction());
        setToolTipText(toolTip);
        setIcon(icon);
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

        private ToConceptSelectionDialog dialog;

        protected ToConceptSelectionDialog getDialog() {
            if (dialog == null) {
                dialog = new ToConceptSelectionDialog(getToolBelt().getAnnotationPersistenceService(), baseConceptName);
                dialog.setLocationRelativeTo(PConstrainedConceptButton.this);
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