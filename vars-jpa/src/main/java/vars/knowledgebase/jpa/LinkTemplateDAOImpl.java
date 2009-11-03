package vars.knowledgebase.jpa;

import vars.knowledgebase.LinkTemplateDAO;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.jpa.DAO;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkTemplateDAOImpl extends DAO implements LinkTemplateDAO {

    private final ConceptDAO conceptDAO;
    
    @Inject
    public LinkTemplateDAOImpl(EntityManager entityManager, ConceptDAO conceptDAO) {
        super(entityManager);
        this.conceptDAO = conceptDAO; 
    }

    public Collection<LinkTemplate> findAllByLinkFields(String linkName, String toConcept, String linkValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        params.put("toConcept", toConcept);
        params.put("linkValue", linkValue);
        return findByNamedQuery("LinkTemplate.findByFields", params);
    }

    public Collection<LinkTemplate> findAllByLinkName(String linkName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        return findByNamedQuery("LinkTemplate.findByLinkName", params);
    }

    public Collection<LinkTemplate> findAllByLinkName(String linkName, Concept concept) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        Collection<LinkTemplate> linkTemplates0 = findByNamedQuery("LinkTemplate.findByLinkName", params);
        Collection<LinkTemplate> linkTemplates = new ArrayList<LinkTemplate>();
        for (LinkTemplate linkTemplate : linkTemplates0) {
            // TODO FInish implementation
        }
        return linkTemplates;

    }

    public Collection<LinkTemplate> findAllApplicableToConcept(Concept concept) {

        Collection<LinkTemplate> linkTemplates = new ArrayList<LinkTemplate>();
        startTransaction();
        while (concept != null) {
            linkTemplates.addAll(concept.getConceptMetadata().getLinkTemplates());
            concept = concept.getParentConcept();
        }
        endTransaction();

        return linkTemplates;
    }

    public void validateName(LinkTemplate object) {
        Concept concept = conceptDAO.findByName(object.getToConcept());
        if (concept != null) {
            object.setToConcept(concept.getPrimaryConceptName().getName());
        }
        else {
            log.warn(object + " contains a 'conceptName', " + object.getToConcept() + " that was not found in the knowlegebase");
        }
    }
}
