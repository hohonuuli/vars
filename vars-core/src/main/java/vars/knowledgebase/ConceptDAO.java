package vars.knowledgebase;

import vars.DAO;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:59:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConceptDAO extends DAO {

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


    /**
     * Get a Collection of {@link Concept} objects from the specifed name on down to the end of
     * the branches that starts at the concept with the specifiec name
     *
     * @param concept The name of the concept
     * @return A Set of {@link Concept} objects from the named one to the end of the branch.
     */
    Collection<Concept> findDescendents(Concept concept);
    
    /**
     * May need to bring all lazy loaded children into the transaction in order to delete them. If you need
     * to remove a concept use this method (unless you've put a casacade delete trigger on your database)
     * @param concept
     */
    void cascadeRemove(Concept concept);

    List<Concept> findAllByNameContaining(final String nameGlob);

    List<Concept> findAllByNameStartingWith(final String nameGlob);

    List<Concept> findAllByNameEndingWith(final String nameGlob);

}
