package vars.knowledgebase;

import org.mbari.sql.IQueryable;

/**
 * DAO used by the Knowledgebase Application for special operations
 */
public interface KnowledgebasePersistenceService extends IQueryable {

    void updateConceptNameUsedByAnnotations(Concept concept);

}
