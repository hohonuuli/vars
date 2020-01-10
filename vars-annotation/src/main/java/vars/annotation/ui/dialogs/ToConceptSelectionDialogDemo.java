/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.dialogs;

import com.google.inject.Injector;
import vars.annotation.ui.StateLookup;

/**
 *
 * @author brian
 */
public class ToConceptSelectionDialogDemo {

    public static void main(String[] args) {
        Injector injector = StateLookup.GUICE_INJECTOR;
        final ToConceptSelectionDialog dialog = injector.getInstance(ToConceptSelectionDialog.class);
        dialog.setBaseConceptName("Substrates");
        dialog.getCancelButton().addActionListener(e -> System.exit(0));
        dialog.setVisible(true);

    }

}
