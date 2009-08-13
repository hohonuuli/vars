package vars.knowledgebase.jpa;

import vars.jpa.DAO;
import vars.knowledgebase.ILinkRealizationDAO;
import vars.knowledgebase.ILinkRealization;
import vars.knowledgebase.IConceptDAO;
import vars.knowledgebase.IConcept;
import org.mbari.jpax.EAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:46:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkRealizationDAO extends DAO implements ILinkRealizationDAO {

    private final IConceptDAO conceptDAO;
    public final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    public LinkRealizationDAO(EAO eao, IConceptDAO conceptDao) {
        super(eao);
        this.conceptDAO = conceptDao;
    }

    public Collection<ILinkRealization> findAllByLinkName() {
        Map<String, Object> params = new HashMap<String, Object>();
        return getEAO().findByNamedQuery("LinkRealization.findByLinkName", params);
    }

    public void validateName(ILinkRealization object) {
        IConcept concept = conceptDAO.findByName(object.getToConcept());
        if (concept != null) {
            object.setToConcept(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(object + " contains a 'conceptName', " + object.getToConcept() + " that was not found in the knowlegebase");
        }
    }
}
