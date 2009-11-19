/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.vars.annotation.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.actions.AddPropertyAction;
import org.mbari.vars.annotation.ui.dialogs.ToConceptSelectionDialog;

import vars.annotation.AnnotationPersistenceService;

/**
 *
 * @author brian
 */
public class SurfacePropButton extends PropButton {

    private ActionAdapter showDialogAction;
    private AddPropertyAction addPropertyAction;
    private final AnnotationPersistenceService specialAnnotationDAO;

    public SurfacePropButton(AnnotationPersistenceService specialAnnotationDAO) {
        super();
        this.specialAnnotationDAO = specialAnnotationDAO;
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
            addPropertyAction = new AddPropertyAction("upon", "physical object", "nil");
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
                dialog = new ToConceptSelectionDialog(specialAnnotationDAO);
                dialog.setLocationRelativeTo(SurfacePropButton.this);
                dialog.getOkButton().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        getAddPropertyAction().setLinkValue(dialog.getSelectedConcept().getPrimaryConceptName().getName());
                        dialog.setVisible(false);
                        getAddPropertyAction().doAction();
                    }
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
