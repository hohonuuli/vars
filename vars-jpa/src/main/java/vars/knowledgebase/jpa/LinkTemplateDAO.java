package vars.knowledgebase.jpa;

import vars.knowledgebase.ILinkTemplateDAO;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IConcept;
import vars.knowledgebase.IConceptDAO;
import vars.jpa.DAO;
import org.mbari.jpax.EAO;

import java.util.Set;
import java.util.Collection;

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
        return null;  // TODO implement this method.
    }

    public Collection<ILinkTemplate> findAllByLinkName(String linkName) {
        return null;  // TODO implement this method.
    }

    public Collection<ILinkTemplate> findAllByLinkName(String linkName, IConcept concept) {
        return null;  // TODO implement this method.
    }

    public void validateName(ILinkTemplate object) {
        // TODO implement this method.
    }
}
