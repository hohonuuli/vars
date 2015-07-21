package vars.queryfx.controllers;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

import java.util.List;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2015-07-20T15:55:00
 */
public interface QueryService {

    /**
     * Lookup all @{link ConceptName}s objects that are associated with
     * this {@link Concept} and its children. Ordered alphabetically.
     *
     *
     * @param concept The concept whos descendant names are returne
     * @return
     */
    List<ConceptName> findDescendantNames(Concept concept);

    Optional<Concept> findConcept(String name);
}
