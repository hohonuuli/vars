/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.dialogs;

import java.awt.BorderLayout;
import java.util.Collection;
import org.mbari.swing.SortedComboBoxModel;
import vars.shared.ui.ConceptNameComboBox;

/**
 *
 * @author brian
 */
public class ConceptNameSelectionDialog extends StandardDialog {

    ConceptNameComboBox conceptNameComboBox;

    private void intialize() {
        add(getConceptNameComboBox(), BorderLayout.CENTER);
        pack();
    }

    public ConceptNameComboBox getConceptNameComboBox() {
        if (conceptNameComboBox == null) {
            conceptNameComboBox = new ConceptNameComboBox();
        }
        return conceptNameComboBox;
    }

    public void setItems(Collection<String> items) {
        SortedComboBoxModel<String> model = (SortedComboBoxModel<String>) getConceptNameComboBox().getModel();
        model.clear();
        model.addAll(items);
    }

    public String getSelectedItem() {
        return (String) getConceptNameComboBox().getSelectedItem();
    }


}
