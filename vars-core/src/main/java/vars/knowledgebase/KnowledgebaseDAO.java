package vars.knowledgebase;

import vars.query.IQueryable;

/**
 * DAO used by the Knowledgebase Application for special operations
 */
public interface KnowledgebaseDAO extends IQueryable {

    void updateConceptNameUsedByAnnotations(Concept concept);

}
