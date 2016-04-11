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
    private final QueryPersistenceService queryPersistenceService;


    @Inject
    public QueryResultsDecorator(QueryPersistenceService queryPersistenceService) {
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
            final List<Concept> ancestors = queryPersistenceService.findAncestors(n);
            String h = ancestors.stream()
                    .map(c -> c.getPrimaryConceptName().getName())
                    .collect(Collectors.joining(","));
            hierarchy.put(n, h);
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

    private QueryResults addPhylogeny(QueryResults queryResults, List<String> phylogeny) {

        Set<String> columnNames = queryResults.getColumnNames();
        Optional<String> column  = columnNames.stream()
                .filter(s -> s.equalsIgnoreCase("conceptname"))
                .findFirst();

        if (!column.isPresent()) {
            return queryResults;
        }

        Map<String, List<Object>> resultsMap = queryResults.copyData();
        String name = column.get();
        List<String> conceptNames = (List<String>) queryResults.getValues(name);
        Set<String> uniqueNames = new HashSet<>(conceptNames);
        Map<String, List<String>> map = new HashMap<>();
        for (String n : uniqueNames) {
            List<Concept> ancestors = queryPersistenceService.findAncestors(n);
            // block on each future
            try {
                List<String> ranks = ancestors.stream()
                        .map(c -> {
                            String rankLevel = c.getRankLevel();
                            String rankName = c.getRankName();
                            String rank = null;
                            if (rankLevel != null) {
                                rank = rankLevel + rankName;
                            }
                            else if (rankName != null){
                                rank = rankName;
                            }
                            return rank;
                        })
                        .collect(Collectors.toList());

                List<String> ancestorNames = new ArrayList<>(Collections.nCopies(phylogeny.size(), ""));
                for (int i = 0; i < ranks.size(); i++) {
                    String rank = ranks.get(i);
                    if (rank != null) {
                        int idx = phylogeny.indexOf(rank);
                        if (idx >= 0) {
                            //System.out.println("idx=" + idx + ",i=" + i + ",rank=" + rank + ",cn=" + ancestors.get(i).getPrimaryConceptName());
                            ancestorNames.set(idx, ancestors.get(i).getPrimaryConceptName().getName());
                        }
                    }
                }
                map.put(n, ancestorNames);

            }
            catch (Exception e) {
                log.warn("Failed to find ancestors for " + n, e);
            }

        }

        /*
         * Populate the queryResults with the new columns
         */
        for (int i = 0; i < phylogeny.size(); i++) {
            List<Object> terms = new ArrayList<>(conceptNames.size());
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

        return new QueryResults(resultsMap);
    }

    /**
     * Modifies a QueryResults object  by adding a column for storing the scientific
     * name for each taxonimic level. This includes infra, sub and supra levels.
     *
     * @param queryResults The QueryResults object from a VARS query. The
     *  queryResults object should contain a column named "conceptname" (case-insensitive)
     */
    public QueryResults addFullPhylogeny(QueryResults queryResults) {

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

        return addPhylogeny(queryResults, phylogeny);
    }
    /**
     * Modifies a QueryResults object  by adding a column for storing the scientific 
     * name for each taxonimic level (KPCOFGS)
     * 
     * @param queryResults The QueryResults object from a VARS query. The
     *  queryResults object should contain a column named "conceptname" (case-insensitive)
     */
    public QueryResults addBasicPhylogeny(QueryResults queryResults) {

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
        
        return addPhylogeny(queryResults, phylogeny);
    }

    /**
     * Drops all columns that do not contain any data. This includes columsn of
     * whitespace
     *
     * @param queryResults
     */
    public static QueryResults dropEmptyColumns(QueryResults queryResults) {

        // Create a copy of the columns so that we don't get concurrent modification exceptions
        Collection<String> columns = queryResults.getColumnNames();
        final Map<String, List<Object>> resultsMap = queryResults.copyData();

        boolean isModified = false;

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
                resultsMap.remove(column);
                isModified = true;
            }
        }

        if (isModified) {
            return new QueryResults(resultsMap);
        }
        else {
            return queryResults;
        }

    }
}
