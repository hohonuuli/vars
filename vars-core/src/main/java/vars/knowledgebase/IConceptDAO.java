package vars.knowledgebase;

import vars.IDAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:59:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IConceptDAO extends IDAO {

    IConcept findByName(String name);

    IConcept findRoot();

    

}
