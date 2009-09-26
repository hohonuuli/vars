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
public interface ConceptDAO extends IDAO {

    Concept findByName(String name);

    Concept findRoot();

    Collection<Concept> findAll();

    /**
     * Lookup all @{link ConceptName}s objects that are associated with
     * this {@link Concept} and its children
     *
     *
     * @param concept The concept whos descendant names are returne
     * @return
     */
    Collection<ConceptName> findDescendentNames(Concept concept);

}
