package vars.queryfx.ui.db.results;

import vars.ILink;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Schlining
 * @since 2015-07-30T15:47:00
 */
public class AssociationColumnRemappingDecorator {

    /**
     * Execute this <b>before</b> using <i>coalesce</i>. This method will take
     * the associations and put each unique linkName into it's own column
     * @param queryResults
     */
    public static QueryResults apply(QueryResults queryResults) {
        Map<String, List<Object>> resultsMap = queryResults.copyData();
        Map<String, List<Object>> associationsMap = new TreeMap<>();

        // --- Find all distinct linkNames used in the Associations
        if (resultsMap.containsKey("Associations")) {
            List associationResults = resultsMap.get("Associations");
            Set<String> linkNames = new HashSet<>();
            for (Object a : associationResults) {
                if (a != null) {
                    String s = (String) a;
                    String[] parts = s.split(ILink.DELIMITER);
                    linkNames.add(parts[0].trim());
                }
            }

            // --- Generate an empty list of the correct size to account for null values
            for (String linkName : linkNames) {
                List<Object> list = new ArrayList<>(queryResults.getRows());
                for (int i = 0; i < queryResults.getRows(); i++) {
                    list.add(null);
                }
                associationsMap.put(linkName, list);
            }

            // --- Put the association in the correct column
            for (int i = 0; i < queryResults.getRows(); i++) {
                String association = (String) associationResults.get(i);
                if (association != null) {
                    String[] parts = association.split(ILink.DELIMITER);
                    String linkName = parts[0];
                    List<Object> list = associationsMap.get(linkName);
                    list.set(i, association);
                }
            }

            resultsMap.remove("Associations");
            resultsMap.putAll(associationsMap);
        }

        return new QueryResults(resultsMap);
    }
}
