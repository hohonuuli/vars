package vars.annotation;

import vars.ConceptNameValidator;
import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 1:59:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AssociationDAO extends DAO, ConceptNameValidator<Association> {
}
