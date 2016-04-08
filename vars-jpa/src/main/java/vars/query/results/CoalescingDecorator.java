package vars.query.results;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Brian Schlining
 * @since 2015-07-30T16:16:00
 */
public class CoalescingDecorator {

    private static final Object NO_VALUE = "!_!!_!!!___NO_VALUE___!!!_!!_!";

    public static QueryResults coalesce(QueryResults queryResults, String key) {

        final Map<String, List<Object>> resultsMap = queryResults.copyData();
        Map<String, Map<Integer, Set<Object>>> duplicateMap = new HashMap<>();
        List keyColumn = resultsMap.get(key);
        Object[] objects = keyColumn.toArray(new Object[keyColumn.size()]);
        for (int row0 = 0; row0 < objects.length; row0++) {
            Object object = objects[row0];
            if (!NO_VALUE.equals(object)) {
                int row1;
                while ((row1 = keyColumn.lastIndexOf(object)) > row0) {

                    /*
                     * We must do the following if a duplicate is found:
                     * 1) Combine the data into a single comma-separate value.
                     * 2) Tag the duplicate in the object array so we don't process it agains
                     * 3) Keep track of rows marked as duplicates so that we can remove
                     *    them from the final dataset
                     */

                    // combine(row0, row1);
                    storeCoalescedValues(resultsMap, duplicateMap, row0, row1);
                    objects[row1] = NO_VALUE;
                }
            }
        }

        combineCoalescedValues(resultsMap, duplicateMap);
        removeDuplicates(resultsMap);
        return new QueryResults(resultsMap);
    }

    /**
     * When coalescing data on a key there may be multiple values that are the
     * same for example if a column has the following values: 500, 403, 500, 500
     * the result we want is '500, 403', not '500, 403, 500, 500'. So we store
     * values in a set to avoid duplication. These values need to be combined
     * after all rows have been coalesced.
     * @param row0
     * @param row1
     */
    private static void storeCoalescedValues(Map<String, List<Object>> resultsMap,
            Map<String, Map<Integer, Set<Object>>> duplicateMap,
            int row0,
            int row1) {

        Collection keys = resultsMap.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String columnName = (String) i.next();
            List<Object> data = resultsMap.get(columnName);
            Object obj0 = data.get(row0);
            Object obj1 = data.get(row1);

            /*
             * Only combine them if the values are actually different
             */
            if ((obj0 != null) && (obj1 != null) && !obj0.equals(obj1)) {

                /*
                 * Get the map of duplicate values for a particular column.
                 * The map returned has a key = row number and a value
                 * = set of the coalesced values
                 */
                Map<Integer, Set<Object>> map = duplicateMap.get(columnName);
                if (map == null) {

                    /*
                     * map = new HashMap<Integer (row), Set (values)>
                     */
                    map = new HashMap<>();
                    duplicateMap.put(columnName, map);
                }

                Set<Object> set = map.get(row0);
                if (set == null) {
                    set = new HashSet<>();
                    map.put(row0, set);
                }

                set.add(obj0);
                set.add(obj1);
            }

            data.set(row1, NO_VALUE);
        }
    }

    private static void combineCoalescedValues(Map<String, List<Object>> resultsMap,
            Map<String, Map<Integer, Set<Object>>> duplicateMap) {

        /*
         * Iterate through each columnName stored in the duplicateMap
         */
        Collection columnNames = duplicateMap.keySet();
        for (Iterator i = columnNames.iterator(); i.hasNext(); ) {
            String columnName = (String) i.next();
            Map<Integer, Set<Object>> map = duplicateMap.get(columnName);

            /*
             * Iterate through each row containing a duplicate stored for a
             * particular columnName
             */
            Collection rows = map.keySet();
            for (Object row1 : rows) {

                /*
                 * Combine all the values in a row into a comma-separated
                 * String
                 */
                Integer row = (Integer) row1;
                Set rowValues = new TreeSet<>(map.get(row));
                Iterator k = rowValues.iterator();
                String value = k.next().toString();
                while (k.hasNext()) {
                    value = value + ", " + k.next();
                }

                /*
                 * Store the coalesced value back in the correct row in the
                 * data storage Lists.
                 */
                resultsMap.get(columnName).set(row, value);
            }
        }
    }

    private static void removeDuplicates(Map<String, List<Object>> resultsMap) {
        Collection<Object> noValueList = new ArrayList<>();
        noValueList.add(NO_VALUE);
        Collection<List<Object>> dataLists = resultsMap.values();
        for (Iterator<List<Object>> j = dataLists.iterator(); j.hasNext(); ) {
            List<Object> data = j.next();
            data.removeAll(noValueList);
        }
    }
}
