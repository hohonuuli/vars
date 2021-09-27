package vars.knowledgebase;

import mbarix4j.sql.IQueryable;

/**
 * DAO used by the Knowledgebase Application for special operations
 */
public interface KnowledgebasePersistenceService extends IQueryable {

    boolean doesConceptNameExist(String conceptName);

    void updateConceptNameUsedByLinkTemplates(Concept concept);

}
