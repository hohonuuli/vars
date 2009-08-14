package vars.knowledgebase.jpa;

import vars.knowledgebase.ILinkTemplateDAO;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;

import java.util.Set;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkTemplateDAO extends DAO implements ILinkTemplateDAO {

    private final IConceptDAO conceptDAO;

    @Inject
    public LinkTemplateDAO(EAO eao, IConceptDAO conceptDAO) {
        super(eao);
        this.conceptDAO = conceptDAO; 
    }

    public Collection<ILinkTemplate> findAllByLinkFields(String linkName, String toConcept, String linkValue) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        params.put("toConcept", toConcept);
        params.put("linkValue", linkValue);
        return getEAO().findByNamedQuery("LinkTemplate.findByFields", params);
    }

    public Collection<ILinkTemplate> findAllByLinkName(String linkName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        return getEAO().findByNamedQuery("LinkTemplate.findByLinkName", params);
    }

    public Collection<ILinkTemplate> findAllByLinkName(String linkName, IConcept concept) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("linkName", linkName);
        Collection<ILinkTemplate> linkTemplates0 = getEAO().findByNamedQuery("LinkTemplate.findByLinkName", params);
        Collection<ILinkTemplate> linkTemplates = new ArrayList<ILinkTemplate>();
        for (ILinkTemplate linkTemplate : linkTemplates0) {
            // TODO FInish implementation
        }

    }

    public Collection<ILinkTemplate> findAllApplicableToConcept(IConcept concept) {
        // TODO return all linktemplates that can be applied to a particular concept
        return null
    }

    public void validateName(ILinkTemplate object) {
        // TODO implement this method.
    }
}
