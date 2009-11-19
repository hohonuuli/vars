/*
 * @(#)ConceptNameSelectionPanel.java   2009.11.16 at 08:45:27 PST
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



package vars.query.ui;

import com.google.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.query.QueryPersistenceService;
import vars.shared.ui.ConceptNameComboBox;

/**
 * <p>This ui panel allows a user to select a concept name of interest and extends
 * it to it's various relations. Use as:</p>
 *
 * <pre>
 * ConceptNameSelectionPanel p = new ConceptNameSelectionPanel();
 * // The user would normally select an item in the combo box here.
 * Collection names = p.getConcepts(); // returns a collection of string concept-names
 * </pre>
 *
 * @author Brian Schlining
 * @version $Id: ConceptNameSelectionPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ConceptNameSelectionPanel extends JPanel {

    private static final long serialVersionUID = 5758588674236575449L;
    private static final Logger log = LoggerFactory.getLogger(ConceptNameSelectionPanel.class);
    private JCheckBox cChildren = null;
    private JCheckBox cDescendant = null;
    private JCheckBox cParent = null;
    private JCheckBox cSiblings = null;
    private JComboBox cbConceptName = null;
    private JLabel jLabel = null;
    private JPanel pCheckBoxes = null;
    private final ConceptDAO conceptDAO;
    private final QueryPersistenceService queryDAO;

    /**
     *
     *
     * @param queryDAO
     * @param conceptDAO
     */
    @Inject
    public ConceptNameSelectionPanel(QueryPersistenceService queryDAO, ConceptDAO conceptDAO) {
        super();
        this.queryDAO = queryDAO;
        this.conceptDAO = conceptDAO;

        // TODO Auto-generated constructor stub
        initialize();
    }

    /**
     * Convience method to load all concept-names from a concept into a collection
     * Used in support of getSelectedConceptNamesAsString
     *
     * @param storage
     * @param concept
     */
    private void addConceptNames(Collection storage, Concept concept) {
        Collection conceptNames = concept.getConceptNames();

        for (Iterator i = conceptNames.iterator(); i.hasNext(); ) {
            ConceptName conceptName = (ConceptName) i.next();

            storage.add(conceptName.getName());
        }
    }


    private void addDescendants(Collection storage, Concept concept) {
        addConceptNames(storage, concept);

        Collection childConcepts = concept.getChildConcepts();

        for (Iterator i = childConcepts.iterator(); i.hasNext(); ) {
            Concept child = (Concept) i.next();

            addDescendants(storage, child);
        }
    }

 
    protected JCheckBox getCChildren() {
        if (cChildren == null) {
            cChildren = new JCheckBox();
            cChildren.setText("Children");
        }

        return cChildren;
    }


    protected JCheckBox getCDescendant() {
        if (cDescendant == null) {
            cDescendant = new JCheckBox();
            cDescendant.setText("Descendants");
        }

        return cDescendant;
    }


    protected JCheckBox getCParent() {
        if (cParent == null) {
            cParent = new JCheckBox();
            cParent.setText("Parent");
        }

        return cParent;
    }

    protected JCheckBox getCSiblings() {
        if (cSiblings == null) {
            cSiblings = new JCheckBox();
            cSiblings.setText("Siblings");
        }

        return cSiblings;
    }

 
    protected JComboBox getCbConceptName() {
        if (cbConceptName == null) {
            Collection<String> conceptNames;

            try {
                conceptNames = queryDAO.findAllNamesUsedInAnnotations();
            }
            catch (Exception e) {

                // TODO report error to eventbus
                log.error("Failed to lookup conceptnames", e);
                conceptNames = new ArrayList();
            }

            /*
             * Nil is used as a wildcard. We need to add it if it's not already
             * there.
             */
            if (!conceptNames.contains(ConceptConstraints.WILD_CARD_STRING)) {
                conceptNames.add(ConceptConstraints.WILD_CARD_STRING);
            }

            cbConceptName = new ConceptNameComboBox((String[]) conceptNames.toArray(new String[conceptNames.size()]));
            cbConceptName.setSelectedItem(ConceptConstraints.WILD_CARD_STRING);
        }

        return cbConceptName;
    }

    private JPanel getPCheckBoxes() {
        if (pCheckBoxes == null) {
            jLabel = new JLabel();
            pCheckBoxes = new JPanel();
            pCheckBoxes.setLayout(new BoxLayout(pCheckBoxes, BoxLayout.X_AXIS));
            jLabel.setText("Extend to ");
            pCheckBoxes.add(jLabel, null);
            pCheckBoxes.add(getCParent(), null);
            pCheckBoxes.add(getCSiblings(), null);
            pCheckBoxes.add(getCChildren(), null);
            pCheckBoxes.add(getCDescendant(), null);
        }

        return pCheckBoxes;
    }

    /**
     * This returns a collection of concept-name objects that correspond to the
     * parameters selected. It looks up this information from the database.
     *
     * @return A collection of <code>String</code> Objects that correspond to
     * all the concept-names that should be searched for in the query
     *
     */
    public Collection<String> getSelectedConceptNamesAsStrings() {
        Collection<String> nameStorage = new HashSet<String>();
        String name = (String) getCbConceptName().getSelectedItem();
        Concept concept = conceptDAO.findByName(name);

        /*
         * Concept is null if it was not found in the knowledgebase. In general
         * this 'shouldn't happen' but I added this to support legacy VIFS.
         */
        if (concept == null) {
            nameStorage.add(name);
        }
        else {

            /*
             * Add our starting concept
             */
            addConceptNames(nameStorage, concept);

            /*
             * Add the parent
             */
            if (getCParent().isSelected()) {
                Concept parent = concept.getParentConcept();

                addConceptNames(nameStorage, parent);
            }

            /*
             * Add Siblings
             */
            if (getCSiblings().isSelected()) {
                Concept parent = concept.getParentConcept();
                Collection<Concept> siblings = parent.getChildConcepts();

                for (Iterator i = siblings.iterator(); i.hasNext(); ) {

                    /*
                     * Note, we're nto worried about processing our original
                     * concept here because we're storing names in a Set.
                     */
                    Concept sibling = (Concept) i.next();

                    addConceptNames(nameStorage, sibling);
                }
            }

            /*
             * Add descendants
             */
            if (getCDescendant().isSelected()) {
                addDescendants(nameStorage, concept);
            }
            else if (getCChildren().isSelected()) {

                /*
                 * Add children. We don't need to add children if we've already
                 * processed the descendants.
                 */
                Collection<Concept> siblings = concept.getChildConcepts();

                for (Iterator i = siblings.iterator(); i.hasNext(); ) {
                    Concept sibling = (Concept) i.next();

                    addConceptNames(nameStorage, sibling);
                }
            }
        }

        return nameStorage;
    }

    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(397, 47);
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search for",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), Color.RED));
        this.add(getCbConceptName(), null);
        this.add(getPCheckBoxes(), null);
    }

    /**
     *
     * @param conceptName
     */
    public void setSelectedConceptName(String conceptName) {
        getCbConceptName().setSelectedItem(conceptName);
    }
}
