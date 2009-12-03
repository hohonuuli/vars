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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
    public LinkTemplateDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.conceptDAO = new ConceptDAOImpl(entityManager);
    }

    @SuppressWarnings("unchecked")
	public Collection<LinkTemplate> findAllByLinkFields(String linkName, String toConcept, String linkValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        params.put("toConcept", toConcept);
        params.put("linkValue", linkValue);
        return (Collection<LinkTemplate>) findByNamedQuery("LinkTemplate.findByFields", params);
    }

    @SuppressWarnings("unchecked")
	public Collection<LinkTemplate> findAllByLinkName(String linkName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        return (Collection<LinkTemplate>) findByNamedQuery("LinkTemplate.findByLinkName", params);
    }

    /**
     * Find {@link LinkTemplate}s containing 'linkName' that are applicable to the 
     * provided concept. You should call this within a transaction
     */
    public Collection<LinkTemplate> findAllByLinkName(final String linkName, Concept concept) {
    	
    	Collection<LinkTemplate> linkTemplates = findAllApplicableToConcept(concept);
    	return Collections2.filter(linkTemplates, new Predicate<LinkTemplate>() {
        	public boolean apply(LinkTemplate linkTemplate) {
        		return linkTemplate.getLinkName().equals(linkName);
        	}
		});
    }

    /**
     * Call this inside a transaction
     */
    public Collection<LinkTemplate> findAllApplicableToConcept(Concept concept) {

        Collection<LinkTemplate> linkTemplates = new ArrayList<LinkTemplate>();
        while (concept != null) {
            linkTemplates.addAll(concept.getConceptMetadata().getLinkTemplates());
            concept = concept.getParentConcept();
        }

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
