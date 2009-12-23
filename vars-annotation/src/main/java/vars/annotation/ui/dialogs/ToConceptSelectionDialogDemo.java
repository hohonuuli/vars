/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.dialogs;

import com.google.inject.Injector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vars.annotation.ui.Lookup;

/**
 *
 * @author brian
 */
public class ToConceptSelectionDialogDemo {

    public static void main(String[] args) {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        final ToConceptSelectionDialog dialog = injector.getInstance(ToConceptSelectionDialog.class);
        dialog.getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        dialog.setVisible(true);

    }

}
