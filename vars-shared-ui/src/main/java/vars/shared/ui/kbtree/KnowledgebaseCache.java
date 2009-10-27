/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.kbtree;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.mbari.text.IgnoreCaseToStringComparator;
import vars.PersistenceCache;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
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

     /**
     * Search for any conceptnames starting with substring (case-insensitive search)
     *
     * @param substring
     * @return A collection of IConceptNames that match. AN empty
     *                 collection will be returned if no matches are found.
     *
     * @throws DAOException
     */
    public synchronized Collection<ConceptName> findNamesStartingWith(String substring) throws DAOException {
        substring = substring.toLowerCase();
        final Collection<ConceptName> names = new ArrayList<ConceptName>();       // Collection<LWConceptName>


        for (final Iterator i = keys.iterator(); i.hasNext();) {
            String name  = (String) i.next();
            if (name.toLowerCase().startsWith(substring)) {
                names.add(allNames.get(name));
            }
        }
        return names;
    }

    /**
     * Search for any conceptnames containing a match to substring
     *
     * @param substring
     * @return A collection of IConceptNames that match. AN empty
     *                 collection will be returned if no matches are found.
     *
     * @throws DAOException
     */
    public synchronized Collection findNamesBySubString(String substring) throws DAOException {
        substring = substring.toLowerCase();
        final Collection names = new ArrayList();       // Collection<LWConceptName>
        final Map allNames = findAllConceptNamesAsMap(); // Map<String, LWConceptName>
        final Set keys = allNames.keySet();
        for (final Iterator i = keys.iterator(); i.hasNext();) {
            String name  = (String) i.next();
            if (name.toLowerCase().indexOf(substring) > -1) {
                names.add(allNames.get(name));
            }
        }
        return names;
    }

    

}
