/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;


import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;

/**
 *
 * @author brian
 */
public class ConceptTreeConcept extends SimpleConceptBean {

    public ConceptTreeConcept(String primaryConceptName) {
        addConceptName(new SimpleConceptNameBean(primaryConceptName, ConceptNameTypes.PRIMARY.getName()));
    }

}
