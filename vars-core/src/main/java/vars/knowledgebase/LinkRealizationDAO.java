package vars.knowledgebase;

import vars.DAO;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 3:08:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LinkRealizationDAO extends DAO, ConceptNameValidator<LinkRealization> {

    Collection<LinkRealization> findAllByLinkName(String linkName);

}
