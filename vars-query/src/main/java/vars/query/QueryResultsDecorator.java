package vars.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.results.QueryResults;

import javax.inject.Inject;

public class QueryResultsDecorator {
    
    private static final Logger log = LoggerFactory.getLogger(QueryResultsDecorator.class);
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;
    private final QueryPersistenceService queryPersistenceService;


    @Inject
    public QueryResultsDecorator(KnowledgebaseDAOFactory knowledgebaseDAOFactory, QueryPersistenceService queryPersistenceService) {
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.queryPersistenceService = queryPersistenceService;
    }


    public QueryResults addHierarchy(QueryResults queryResults)  {
        Set<String> columnNames = queryResults.getColumnNames();
        Optional<String> column  = columnNames.stream()
                .filter(s -> s.equalsIgnoreCase("conceptname"))
                .findFirst();

        if (!column.isPresent()) {
            return queryResults;
        }

        /*
         * Create a list of all the unique conceptNames
         */
        List<String> conceptNameColumn = (List<String>) queryResults.getValues(column.get());
        Set<String> uniqueNames = new HashSet<String>();
        uniqueNames.addAll(conceptNameColumn);
        Map<String, String> hierarchy = new HashMap<>();
        for (String n : uniqueNames) {
            final CompletableFuture<List<Concept>> ancestorsF = queryService.findAncestors(n);
            // block on each future
            try {
                List<Concept> ancestors = ancestorsF.get(5, TimeUnit.SECONDS);
                String h = ancestors.stream()
                        .map(c -> c.getPrimaryConceptName().getName())
                        .collect(Collectors.joining(","));
                hierarchy.put(n, h);
            }
            catch (Exception e) {
                log.warn("Failed to find ancestors for " + n, e);
                hierarchy.put(n, null);
            }
        }

        List<Object> hierarchyColumn = new ArrayList<>(queryResults.getRows());
        for (String n : conceptNameColumn) {
            String h = hierarchy.get(n);
            hierarchyColumn.add(h);
        }

        final Map<String, List<Object>> resultsMap = queryResults.copyData();
        resultsMap.put("Hierarchy", hierarchyColumn);

        return new QueryResults(resultsMap);
           

    }
    
    private void addPhylogeny(QueryResults queryResults, List<String> phylogeny) {
        Set<String> columnNames = (Set<String>) queryResults.getColumnNames();
        for (String name : columnNames) {
            if (name.equalsIgnoreCase("conceptname")) {

                /*
                 * Create a list of all the unique conceptNames
                 */
                List<String> conceptNames = queryResults.getValues(name);
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
                Map resultsMap = queryResults.copyData();
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
            
            List data = queryResults.getValues(column);
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
