package vars.knowledgebase;

import vars.IDAO;
import vars.IConceptNameValidator;

import java.util.Set;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:08:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LinkTemplateDAO extends IDAO, IConceptNameValidator<LinkTemplate> {

    Collection<LinkTemplate> findAllByLinkFields(String linkName, String toConcept, String linkValue);

    Collection<LinkTemplate> findAllByLinkName(String linkName);

    /**
     * Searches a concept for a LinkTemplate that matches a given linkName. This checks all the LinkTemplates
     * that a particular concept has access to (i.e. it's HierarchicalLinkTemplates)
     * @param concept The concept to search in
     * @param linkName The link name whos match you are looking for.
     * @return The matching LinkTemplate. null if no match is found
     */
    Collection<LinkTemplate> findAllByLinkName(String linkName, Concept concept);

    Collection<LinkTemplate> findAllApplicableToConcept(Concept concept);

}
