package vars.annotation;

import java.util.List;
import vars.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 1:59:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AssociationDAO extends DAO, ConceptNameValidator<Association> {

    List<Association> findAllByConceptNameAndValues(String conceptName,
            String linkName, String toConcept, String linkValue);

}
