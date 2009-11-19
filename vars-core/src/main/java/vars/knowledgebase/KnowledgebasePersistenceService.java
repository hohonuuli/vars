package vars.knowledgebase;

import vars.query.IQueryable;

/**
 * DAO used by the Knowledgebase Application for special operations
 */
public interface KnowledgebasePersistenceService extends IQueryable {

    void updateConceptNameUsedByAnnotations(Concept concept);

}
