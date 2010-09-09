package vars.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mbari.sql.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;

public class QueryResultsDecorator {
    
    private static final Logger log = LoggerFactory.getLogger(QueryResultsDecorator.class);
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;


    public QueryResultsDecorator(KnowledgebaseDAOFactory knowledgebaseDAOFactory) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
    }


    public void addHierarchy(QueryResults queryResults)  {
        Set<String> columnNames = (Set<String>) queryResults.getColumnNames();
        for (String name : columnNames) {
            if (name.equalsIgnoreCase("conceptname")) {

                /*
                 * Create a list of all the unique conceptNames
                 */
                List<String> conceptNames = queryResults.getResults(name);
                Set<String> uniqueNames = new HashSet<String>();
                uniqueNames.addAll(conceptNames);

                /*
                 * Iterate through each conceptname and create a hierarchy for each.
                 */
                Map<String, String> map = new HashMap<String, String>();
                ConceptDAO dao = knowledgebaseDAOFactory.newConceptDAO();
                for (String n : uniqueNames) {

                    Concept concept = dao.findByName(n);
                    if (concept == null) {
                        log.info("Unable to find " + n + " in the knowledgebase");
                    }

                    /*
                     * Generate a List of Concepts as we walk up the heirarchy
                     */
                    List<Concept> ancestors = new ArrayList<Concept>();
                    while (concept != null) {
                        ancestors.add(concept);
                        concept = concept.getParentConcept();
                    }
                    // Flip the list so it goes from the top of the hierarcy down to our name
                    Collections.reverse(ancestors);

                    /*
                     * Create a comma-separated list of the hieracy and put it in the map for storage
                     */
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < ancestors.size() - 1; i++) {
                        Concept c = ancestors.get(i);
                        sb.append(c.getPrimaryConceptName().getName()).append(",");
                    }
                    sb.append(ancestors.get(ancestors.size() - 1).getPrimaryConceptName().getName()); // Append last element without a trailing ','
                    map.put(n, sb.toString());

                    /*
                     * Populate the queryResults with a new column
                     */
                    Map resultsMap = queryResults.getResultsMap();
                    List aList = new ArrayList();
                    for (String string : conceptNames) {
                        aList.add(map.get(string));
                    }
                    resultsMap.put("Hierarchy", aList);

                }
                dao.close();
                break; // exit for loop after processing the conceptname column
            }
           
        }
    }
    
    private void addPhylogeny(QueryResults queryResults, List<String> phylogeny) {
        Set<String> columnNames = (Set<String>) queryResults.getColumnNames();
        for (String name : columnNames) {
            if (name.equalsIgnoreCase("conceptname")) {

                /*
                 * Create a list of all the unique conceptNames
                 */
                List<String> conceptNames = queryResults.getResults(name);
                Set<String> uniqueNames = new HashSet<String>();
                uniqueNames.addAll(conceptNames);

                /*
                 * Iterate through each conceptname and create a hierarchy for each.
                 */
                Map<String, List<String>> map = new HashMap<String, List<String>>();
                ConceptDAO conceptDao = knowledgebaseDAOFactory.newConceptDAO();
                for (String n : uniqueNames) {
                    Concept concept = conceptDao.findByName(n);

                    /*
                     * Generate a List of Concepts as we walk up the heirarchy.
                     * Only use concepts with no rank level AND whos rank name
                     * is found within the phylogeny array
                     */
                    List<String> ancestors = new ArrayList<String>(phylogeny.size());
                    String primaryName = (concept == null) ? n : concept.getPrimaryConceptName().getName();
                    map.put(primaryName, ancestors);
                    for (int i = 0; i < phylogeny.size(); i++) {
                        ancestors.add(null);
                    }


                    while (concept != null) {
                        String rank = null;
                        if (concept.getRankLevel() == null || concept.getRankLevel().length() == 0) {
                            rank = concept.getRankName();
                        }
                        else {
                            rank = concept.getRankLevel() + concept.getRankName();
                        }
                                                    
                        if (phylogeny.contains(rank)) {
                            int idx = phylogeny.indexOf(rank);
                            if (log.isDebugEnabled()) {
                                log.debug("Found rank of '" + rank + "' for '" + concept + 
                                        "'. Adding as ancestors.add(" + idx + ", " +
                                        concept + ")");
                            }
                            ancestors.set(idx, concept.getPrimaryConceptName().getName());
                        }
                        concept = concept.getParentConcept();
                    }
                    
                    if (log.isDebugEnabled()) {
                        log.debug(n + ": " + ancestors.toString());
                    }

                }
                conceptDao.close();
               
                
                /*
                 * Populate the queryResults with the new columns
                 */
                Map resultsMap = queryResults.getResultsMap();
                for (int i = 0; i < phylogeny.size(); i++) {
                    List<String> terms = new ArrayList<String>(conceptNames.size());
                    for (String cn : conceptNames) {
                        List<String> ancestors = map.get(cn);
                        boolean added = false;
                        if (ancestors != null) {
                            String concept = ancestors.get(i);
                            if (concept != null) {
                                terms.add(concept);
                                added = true;
                            }
                        }
                        
                        if (!added) {
                            terms.add("");
                        }
                    }
                    resultsMap.put(phylogeny.get(i), terms);
                }
                
                break;

            }
        }
    }
    
    /**
     * Modifies a QueryResults object  by adding a column for storing the scientific 
     * name for each taxonimic level. This includes infra, sub and supra levels.
     * 
     * @param queryResults The QueryResults object from a VARS query. The
     *  queryResults object should contain a column named "conceptname" (case-insensitive)
     */
    public void addFullPhylogeny(QueryResults queryResults) {
        
        // TODO - This is not implemented correctly yet
        final List<String> phylogeny = new ArrayList<String>() {{
                add("superkingdom");
                add("kingdom");
                add("infrakingdom");
                add("subkingdom");
                add("superphylum");
                add("phylum");
                add("infraphylum");
                add("subphylum");
                add("superclass");
                add("class");
                add("infraclass");
                add("subclass");
                add("superorder");
                add("order");
                add("infraorder");
                add("suborder");
                add("superfamily");
                add("family");
                add("infrafamily");
                add("subfamily");
                add("supergenus");
                add("genus");
                add("infragenus");
                add("subgenus");
                add("superspecies");
                add("species");
                add("infraspecies");
                add("subspecies");
            } //superkingdom, infraphylum, subphylum, superclass, subclass, superorder, suborder, infraorder, superfamily, subfamily, subspecies
        };

       addPhylogeny(queryResults, phylogeny);
    }

    /**
     * Modifies a QueryResults object  by adding a column for storing the scientific 
     * name for each taxonimic level (KPCOFGS)
     * 
     * @param queryResults The QueryResults object from a VARS query. The
     *  queryResults object should contain a column named "conceptname" (case-insensitive)
     */
    public void addBasicPhylogeny(QueryResults queryResults) {

        final List<String> phylogeny = new ArrayList<String>() {{
                add("kingdom");
                add("phylum");
                add("class");
                add("order");
                add("family");
                add("genus");
                add("species");
            } //superkingdom, infraphylum, subphylum, superclass, subclass, superorder, suborder, infraorder, superfamily, subfamily, subspecies
        };
        
        addPhylogeny(queryResults, phylogeny);
    }
    
    /**
     * Drops all columns that do not contain any data. This includes columsn of
     * whitespace
     * 
     * @param queryResults
     */
    public static void dropEmptyColumns(QueryResults queryResults) {
        
        // Create a copy of the columns so that we don't get concurrent modification exceptions
        Collection<String> columns = new ArrayList<String>(queryResults.getColumnNames());
        
        for (String column : columns) {
            
            List data = queryResults.getResults(column);
            boolean hasData = false;
            
            for (Iterator i = data.iterator(); i.hasNext();) {
                Object object = i.next();
                if (object != null && object.toString().length() > 0) {
                    hasData = true;
                    break;
                }
            }
            
            if (!hasData) {
                queryResults.getResultsMap().remove(column);
            }
        }

        
    }
}
