/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.kbtree;

import java.util.LinkedList;
import java.util.List;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.KnowledgebaseDAOFactory;

/**
 *
 * @author brian
 */
class ConceptTreeController {

    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final ConceptTree conceptTree;

    ConceptTreeController(ConceptTree conceptTree, KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.conceptTree = conceptTree;
    }

    KnowledgebaseDAOFactory getKnowledgebaseDAOFactory() {
        return knowledgebaseDAOFactory;
    }

    void refresh() {
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept root = conceptDAO.findRoot();
        conceptDAO.endTransaction();
        conceptTree.loadModel(root);
    }

    void addedConceptName(final ConceptName conceptName) {
        ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
        dao.startTransaction();
        Concept concept = dao.findByName(conceptName.getName());
        dao.endTransaction();
        conceptTree.updateTreeNode(concept);
    }

    /**
     * Gets the list of <code>Concept</code> objects from the root down to the
     * <code>Concept</code> for the specified concept name.
     *
     * @param  name           The name of the concept for the tree.
     * @return  The list of concepts from the root to the parameter concept.
     */
    List<Concept> findConceptFamilyTree(final String name) {
        final LinkedList conceptList = new LinkedList();
        ConceptDAO conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(name);
        conceptList.add(concept);

        while (concept.hasParent()) {
            concept = (Concept) concept.getParentConcept();
            conceptList.addFirst(concept);
        }
        conceptDAO.endTransaction();

        return conceptList;
    }
    

}
