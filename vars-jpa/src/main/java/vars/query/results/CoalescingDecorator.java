package vars.query.results;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2016-04-11T16:24:00
 */
public class CoalescingDecorator {

    public static QueryResults coalesce(QueryResults queryResults, String keyColumnName) {
        final Map<String, List<Object>> resultsMap = queryResults.copyData();
        final Map<String, List<Object>> combinedMap = newEmptyResultMap(resultsMap);

        List<Object>  keyColumn = resultsMap.get(keyColumnName);
        List<Object> distinctKeys = keyColumn.stream()
                .distinct()
                .collect(Collectors.toList());
        distinctKeys.stream()
                .forEach(key -> {
                    //System.out.print("key = " + key);
                    Map<String, List<Object>> rows = extractRowsWithSameKey(resultsMap, keyColumnName, key);
                    //System.out.println(". Found " + rows.values().iterator().next().size() + " rows");
                    if (rows.values().iterator().next().size() == 1) {
                        addRowToMap(combinedMap, rows);
                    }
                    else {
                        Map<String, Object> row = combineRows(rows);
                        addNewRowToMap(combinedMap, row);
                    }

                });

        return new QueryResults(combinedMap);


    }

    private static Map<String, Object> combineRows(Map<String, List<Object>> rowsToCombine) {
        Map<String, Object> combinedValues = new HashMap<>();
        for (String key: rowsToCombine.keySet()) {
            List<Object> values = rowsToCombine.get(key);
            //System.out.println(values);
            String combinedValue = values.stream()
                    .filter(obj -> obj != null)
                    .distinct()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.joining(", "));
            combinedValues.put(key, combinedValue);
        }
        return combinedValues;
    }

    private static Map<String, List<Object>> extractRowsWithSameKey(Map<String, List<Object>> resultsMap, String keyColumnName, Object key) {

        // --- Get indices of rows with key
        List<Object> keyColumn = resultsMap.get(keyColumnName);
        List<Integer> rows = new ArrayList<>();
        for (int i = 0; i < keyColumn.size(); i++) {
            Object key0 = keyColumn.get(i);
            if (key != null && key.equals(key0)) {
                rows.add(i);
            }
        }

        // -- Fill a new map with only those rows that have the same key
        Map<String, List<Object>> matches = newEmptyResultMap(resultsMap);
        for (String columnName : resultsMap.keySet()) {
            List<Object> allRows = resultsMap.get(columnName);
            List<Object> matchedRows = matches.get(columnName);
            rows.stream().forEach(i -> matchedRows.add(allRows.get(i)));
        }

        return matches;

    }

    private static Map<String, List<Object>> newEmptyResultMap(Map<String, List<Object>> resultsMap) {
        Map<String, List<Object>> emptyMap = new HashMap<>(resultsMap.size());
        for (String key: resultsMap.keySet()) {
            emptyMap.put(key, new ArrayList<>());
        }
        return emptyMap;
    }

    private static void addNewRowToMap(Map<String, List<Object>> combinedMap, Map<String, Object> row) {
        for (String key: combinedMap.keySet()) {
            combinedMap.get(key).add(row.get(key));
        }
    }

    private static void addRowToMap(Map<String, List<Object>> combinedMap, Map<String, List<Object>> row) {
        // assume row only has one value
        for (String key: combinedMap.keySet()) {
            combinedMap.get(key).add(row.get(key).get(0));
        }

    }
}
