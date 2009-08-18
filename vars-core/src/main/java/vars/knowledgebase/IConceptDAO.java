package vars.knowledgebase;

import vars.IDAO;

import java.util.Collection;

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

    Collection<IConcept> findAll();

    /**
     * Lookup all @{link IConceptName}s objects that are associated with
     * this {@link IConcept} and its children
     *
     *
     * @param concept The concept whos descendant names are returne
     * @return
     */
    Collection<IConceptName> findDescendentNames(IConcept concept);

}
