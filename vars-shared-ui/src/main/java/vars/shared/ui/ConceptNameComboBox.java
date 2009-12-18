/*
 * @(#)ConceptNameComboBox.java   2009.12.16 at 10:57:55 PST
 *
 * Copyright 2009 MBARI
 *
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
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

/**
 * A <code>ConceptNameComboBox</code> displays all the current
 * <code>ConceptName</code>s in the <code>KnowledgeBase</code>.
 *
 * @author  brian
 * @created  February 22, 2005
 */
public class ConceptNameComboBox extends FancyComboBox implements ConceptChangeListener {

    /**
     * Constructor for the ConceptNameComboBox object
     */
    public ConceptNameComboBox() {
        super();
        setModel(new SortedComboBoxModel<String>(new IgnoreCaseToStringComparator()));
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

    /**
     * Adds the specified <code>ConceptName</code> item to the model for this
     * <code>ConceptNameComboBox</code>.
     *
     * @param  item   The item to add to the model.
     */
    public void addItem(ConceptName item) {
        ((SortedComboBoxModel<String>) getModel()).addElement(item.getName());
    }

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void addedConcept(Concept concept) {
        Collection<? extends ConceptName> conceptNames = concept.getConceptNames();

        for (ConceptName cn : conceptNames) {
            addItem(cn);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void addedConceptName(ConceptName conceptName) {
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
    public void removeItem(ConceptName item) {
        ((SortedComboBoxModel<String>) getModel()).removeElement(item.getName());
    }

    /**
     *  Description of the Method
     *
     * @param  concept Description of the Parameter
     */
    public void removedConcept(Concept concept) {

        // Recursive for all children Concept objects.
        Collection<Concept> concepts = concept.getChildConcepts();

        for (Concept c : concepts) {
            removedConcept(c);
        }

        // Remove all names of the specified Concept
        Collection<ConceptName> conceptNames = concept.getConceptNames();

        for (ConceptName cn : conceptNames) {
            removeItem(cn);
        }

    }

    // Impl for ConceptChangeListener

    /**
     *  Description of the Method
     *
     * @param  conceptName Description of the Parameter
     */
    public void removedConceptName(ConceptName conceptName) {
        removeItem(conceptName);
    }

    /**
     * Updates the model to contain the specified items.
     *
     * @param  items   An array of <code>ConceptName</code> items for this
     *  <code>ConceptNameComboBox</code>.
     */
    public void updateModel(String[] items) {
        ((SortedComboBoxModel<String>) getModel()).setItems(Arrays.asList(items));
    }
}
