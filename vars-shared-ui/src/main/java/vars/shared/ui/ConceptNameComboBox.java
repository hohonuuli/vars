/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.shared.ui;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Arrays;

import java.util.Collection;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptName;

//~--- classes ----------------------------------------------------------------

/**
 * A <code>ConceptNameComboBox</code> displays all the current
 * <code>ConceptName</code>s in the <code>KnowledgeBase</code>.
 *
 * @author  brian
 * @created  February 22, 2005
 */
public class ConceptNameComboBox extends FancyComboBox
        implements ConceptChangeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 3453850210369068276L;

    /**
     * Constructor for the ConceptNameComboBox object
     */
    public ConceptNameComboBox() {
        super(new IgnoreCaseToStringComparator());
        setToolTipText("Select Concept by name");
    }

    /**
     * Constructs a <code>ConceptNameComboBox</code> initially populated by the
     * specified array of <code>ConceptName</code> items.
     *
     * @param  items Description of the Parameter
     */
    public ConceptNameComboBox(String[] items) {
        this();
        updateModel(items);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Adds the specified <code>ConceptName</code> item to the model for this
     * <code>ConceptNameComboBox</code>.
     *
     * @param  item   The item to add to the model.
     */
    public void addItem(IConceptName item) {
        ((SortedComboBoxModel) getModel()).addElement(item.getName());
    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void addedConcept(IConcept concept) {
        Collection<? extends IConceptName> conceptNames = concept.getConceptNames();
        for (IConceptName cn : conceptNames) {
            addItem(cn);
        }
    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void addedConceptName(IConceptName conceptName) {
        addItem(conceptName);
    }

    /**
     * Removes an ActionListener to the editor controlling the text input for
     * this <code>ConceptChangeListener</code>.
     *
     * @param  listener   The ActionListener to remove.
     */
    public void removeEditorActionListener(ActionListener listener) {
        editor.removeActionListener(listener);
    }

    /**
     * Removes a FocusListener to the editor controlling the text input for
     * this <code>ConceptChangeListener</code>.
     *
     * @param  listener   The ActionListener to remove.
     */
    public void removeEditorFocusListener(FocusListener listener) {
        editor.getEditorComponent().removeFocusListener(listener);
    }

    /**
     * Removes the specified <code>ConceptName</code> item from the model.
     *
     * @param  item   The item to remove from the model.
     */
    public void removeItem(IConceptName item) {
        ((SortedComboBoxModel) getModel()).removeElement(item.getName());
    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void removedConcept(IConcept concept) {
        // Recursive for all children Concept objects.
        Collection<IConcept> concepts = concept.getChildConcepts();
        for (IConcept c : concepts) {
            removedConcept(c);
        }

        // Remove all names of the specified Concept
        Collection<IConceptName> conceptNames = concept.getConceptNames();
        for (IConceptName cn : conceptNames) {
            removeItem(cn);
        }

    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void removedConceptName(IConceptName conceptName) {
        removeItem(conceptName);
    }

    /**
     * Updates the model to contain the specified items.
     *
     * @param  items   An array of <code>ConceptName</code> items for this
     *  <code>ConceptNameComboBox</code>.
     */
    public void updateModel(String[] items) {
        ((SortedComboBoxModel) getModel()).setItems(Arrays.asList(items));
    }
}
