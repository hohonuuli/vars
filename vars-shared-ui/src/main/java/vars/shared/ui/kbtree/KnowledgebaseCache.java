/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.kbtree;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.PersistenceCache;
import vars.knowledgebase.ConceptDAO;
import vars.query.QueryDAO;

/**
 *
 * @author brian
 */
public class KnowledgebaseCache {

    private final PersistenceCache persistenceCache;
    private final ConceptDAO conceptDAO;
    private final QueryDAO queryDAO;
    private List<String> conceptNames;


    @Inject
    public KnowledgebaseCache(ConceptDAO conceptDAO, PersistenceCache persistenceCache, QueryDAO queryDAO) {
        this.conceptDAO = conceptDAO;
        this.persistenceCache = persistenceCache;
        this.queryDAO = queryDAO;
    }

    public List<String> findAllConceptNames() {
        if (conceptNames == null) {
            conceptNames = queryDAO.findAllConceptNamesAsStrings();
            Collections.sort(conceptNames, new IgnoreCaseToStringComparator());
        }
        return conceptNames;
    }

    

}
