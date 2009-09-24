package vars.annotation.jpa;

import vars.annotation.IAssociationDAO;
import vars.annotation.IAssociation;
import vars.jpa.DAO;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.Concept;
import org.mbari.jpax.EAO;
import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:30:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssociationDAO extends DAO implements IAssociationDAO {

    private final ConceptDAO conceptDAO;

    @Inject
    public AssociationDAO(EAO eao, ConceptDAO conceptDao) {
        super(eao);
        this.conceptDAO = conceptDao;
    }

    public void validateName(IAssociation ass) {
        Concept concept = conceptDAO.findByName(ass.getToConcept());
        if (concept != null) {
            ass.setToConcept(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(ass + " contains a 'toConcept', " + ass.getToConcept() + " that was not found in the knowlegebase");
        }
    }
}
