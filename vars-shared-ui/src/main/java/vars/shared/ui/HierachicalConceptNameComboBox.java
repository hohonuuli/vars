/*
 * @(#)HierachicalConceptNameComboBox.java   2009.12.02 at 10:38:27 PST
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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.mbari.swing.SortedComboBoxModel;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

/**
 * <p>Displays the Concept and all it's children in the drop-down list. Names are stored internally as Strings</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: HierachicalConceptNameComboBox.java 265 2006-06-20 05:30:09Z hohonuuli $
 */
public class HierachicalConceptNameComboBox extends ConceptNameComboBox {

    private final AnnotationPersistenceService annotationPersistenceService;
    private Concept concept;

    /**
     * Constructs ...
     *
     *
     * @param annotationPersistenceService
     */
    public HierachicalConceptNameComboBox(AnnotationPersistenceService annotationPersistenceService) {
        super();
        this.annotationPersistenceService = annotationPersistenceService;
        initialize();
    }

    /**
     *
     *
     * @param concept
     * @param annotationPersistenceService
     */
    public HierachicalConceptNameComboBox(Concept concept, AnnotationPersistenceService annotationPersistenceService) {

        // WARNING!! getDescendentNames can be very slow the first time it is called.
        super();
        this.annotationPersistenceService = annotationPersistenceService;
        setConcept(concept);
        initialize();
    }

    /**
     * @return
     */
    public Concept getConcept() {
        return concept;
    }

    protected void initialize() {
        setEditable(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (isPopupVisible()) {
                    return;
                }

                showPopup();
            }

        });
        setMaximumRowCount(12);

        // When focused all characters should be selected so that
        // a user can just start typing.
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent evt) {
                getEditor().selectAll();
            }
        });
    }

    /**
     *
     * @param concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;

        if (concept != null) {
            Collection<ConceptName> conceptNames = annotationPersistenceService.getReadOnlyConceptDAO().findDescendentNames(concept);
            List<String> namesAsStrings = new ArrayList<String>(conceptNames.size());

            for (ConceptName cn : conceptNames) {
                namesAsStrings.add(cn.getName());
            }

            ((SortedComboBoxModel) getModel()).setItems(namesAsStrings);
        }
        else {
            removeAllItems();
        }
    }
}
