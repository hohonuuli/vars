/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.query;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import mbarix4j.sql.IQueryable;
import mbarix4j.sql.QueryableImpl;
import vars.ILink;
import vars.knowledgebase.Concept;

/**
 * DAO used by the Query Application for special operations
 */
public interface QueryPersistenceService extends IQueryable {

    Integer getCountOfUniqueValuesByColumn(String columnName);

    Map<String, String> getMetaData();

    Collection<?> getUniqueValuesByColumn(String columnName);

    Collection<ILink> findLinksByConceptNames(Collection<String> conceptNames);

    Collection<ILink> findAllLinkTemplates();

    List<Concept> findAncestors(String conceptName);

    Collection<Concept> findConcepts(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants);

    List<String> findConceptNamesAsStrings(String name,
            boolean extendToParent,
            boolean extendToSiblings,
            boolean extendToChildren,
            boolean extendToDescendants);

    List<String> findDescendantNamesAsStrings(String conceptName);

    /**
     *
     * @return A URL (or other identififier) for the database that's being queried.
     *      Originally this was the JDBC URL.
     */
    String getURL();

    Collection<String> findAllNamesUsedInAnnotations();

    List<String> findAllConceptNamesAsStrings();

    QueryableImpl getAnnotationQueryable();

}
