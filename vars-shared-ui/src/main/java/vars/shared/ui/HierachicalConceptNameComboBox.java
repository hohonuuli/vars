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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mbari.swing.SortedComboBoxModel;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConceptName;

//~--- classes ----------------------------------------------------------------

/**
 * <p>Displays the Concept and all it's children in the drop-down list</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: HierachicalConceptNameComboBox.java 265 2006-06-20 05:30:09Z hohonuuli $
 */
public class HierachicalConceptNameComboBox extends ConceptNameComboBox {

    private static final long serialVersionUID = -3394497521010481199L;
    /**
	 * @uml.property  name="concept"
	 * @uml.associationEnd  
	 */
    private IConcept concept;
    private final IConceptDAO conceptDAO;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     */
    public HierachicalConceptNameComboBox(IConceptDAO conceptDAO) {
        super();
        this.conceptDAO = conceptDAO;
        initialize();
    }

    /**
     *
     *
     * @param concept
     */
    public HierachicalConceptNameComboBox(IConcept concept, IConceptDAO conceptDAO) {
        // WARNING!! getDescendentNames can be very slow the first time it is called.
        super();
        this.conceptDAO = conceptDAO;
        setConcept(concept);

        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * @return
	 * @uml.property  name="concept"
	 */
    public IConcept getConcept() {
        return concept;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
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

    //~--- set methods --------------------------------------------------------

    /**
	 * @param  concept
	 * @uml.property  name="concept"
	 */
    public void setConcept(IConcept concept) {
        this.concept = concept;

        if (concept != null) {
            Collection<IConceptName> conceptNames = conceptDAO.findDescendentNames(concept);
            List<String> namesAsStrings = new ArrayList<String>(conceptNames.size());
            for (IConceptName cn : conceptNames) {
                namesAsStrings.add(cn.getName());
            }
            ((SortedComboBoxModel) getModel()).setItems(namesAsStrings);
        }
        else {
            removeAllItems();
        }
    }
}
