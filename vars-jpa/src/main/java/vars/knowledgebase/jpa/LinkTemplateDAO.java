package vars.knowledgebase.jpa;

import vars.knowledgebase.ILinkTemplateDAO;
import vars.knowledgebase.ILinkTemplate;
import vars.knowledgebase.IConcept;
import vars.jpa.DAO;
import org.mbari.jpax.IEAO;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 4:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkTemplateDAO extends DAO implements ILinkTemplateDAO {

    public LinkTemplateDAO(IEAO eao) {
        super(eao);
    }

    public Set<ILinkTemplate> findAllByLinkFields(String linkName, String toConcept, String linkValue) {
        return null;  // TODO implement this method.
    }

    public Set<ILinkTemplate> findAllByLinkName(String linkName) {
        return null;  // TODO implement this method.
    }

    public Set<ILinkTemplate> findAllByLinkName(String linkName, IConcept concept) {
        return null;  // TODO implement this method.
    }

    public void validateName(ILinkTemplate object) {
        // TODO implement this method.
    }
}
