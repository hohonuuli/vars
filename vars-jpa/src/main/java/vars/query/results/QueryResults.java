package vars.query.results;

import com.google.common.base.Preconditions;
import org.mbari.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-07-30T15:52:00
 */
public class QueryResults {
    private Map<String, List<Object>> resultsMap = new TreeMap<>();
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private transient final int rows;

    public QueryResults(Map<String, List<Object>> data) {

        // --- Check data
        Preconditions.checkArgument(data != null, "Data can not be null");
        Preconditions.checkArgument(!data.isEmpty(), "Data can not be empty");
        Map.Entry<String, List<Object>> firstEntry = data.entrySet().iterator().next();
        rows = firstEntry.getValue().size();
        for (Map.Entry<String, List<Object>> entry : data.entrySet()) {
            Preconditions.checkArgument(entry.getValue().size() == rows, "Expected column to have " +
                    rows + " elements. Found " + entry.getValue().size());
        }

        // --- Set data
        resultsMap.putAll(data);

    }

    public List getValues(String columnName) {

        Set<String> columnNames = resultsMap.keySet().stream()
                .filter(s -> s.equalsIgnoreCase(columnName))
                .collect(Collectors.toSet());

        if (columnNames.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            return resultsMap.get(columnNames.iterator().next());
        }

    }

    public Tuple2<List<String>, List<String[]>> toRowOrientedData() {
        List<String[]> data = new ArrayList<>();
        int rows = getRows();
        int cols = getColumns();
        for (int r = 0; r < rows; r++) {
            data.add(new String[cols]);
        }

        List<String> columnNames = new ArrayList<>(getColumnNames());
        for (int c = 0; c < cols; c++) {
            List d = getValues(columnNames.get(c));
            for (int r = 0; r < rows; r++) {
                Object val = d.get(r);
                String s = val == null ? "" : val.toString();
                data.get(r)[c] = s;
            }
        }
        return new Tuple2<>(columnNames, data);
    }

    /**
     * Convert data to an array
     * @return Row-orderd data where the size is String[rows][columns]
     */
    public String[][] toRowOrientedArray() {
        Tuple2<List<String>, List<String[]>> t = toRowOrientedData();
        List<String[]> array = t.getB();
        int r = array.size();
        int c = array.get(0).length;
        String[][] data = new String[r][c];
        for (int i = 0; i < r; i++) {
            data[i] = array.get(i);
        }
        return data;
    }

    public Set<String> getColumnNames() {
        return new TreeSet<>(resultsMap.keySet());
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return resultsMap.keySet().size();
    }

    /**
     * Returns a new map of the underlying data. Each data column is also a copy of
     * the original list. This is not a full deep copy; modifying a mutable object
     * will change it everywhere. But adding to the map or modifying the contents of each
     * list in the map will not change the values in the source QueryResults.
     *
     * @return A semi-deep copy of the underlying data map
     */
    public Map<String, List<Object>> copyData() {
        Map<String, List<Object>> newMap = new TreeMap<>();
        resultsMap.entrySet().stream()
                .forEach(entry -> newMap.put(entry.getKey(), new ArrayList<>(entry.getValue())));
        return newMap;
    }

    public static QueryResults fromResultSet(ResultSet resultSet) throws SQLException {

        /*
         * Use the metadata to generate a container to store the data into
         */
        List<String> columnNames = new ArrayList<>();
        List<String> returnTypes = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnLabel(i));
            returnTypes.add(metaData.getColumnClassName(i).toLowerCase());
        }

        /*
         * To speed up access we'll pull the Lists out of QueryResults and keep
         * them in an orderlist.
         */
        List<List> data = new ArrayList<>();
        Map<String, List<Object>> resultsMap = new TreeMap<>();
        columnNames.stream()
                .forEach(n -> {
                    List list = new ArrayList();
                    resultsMap.put(n, list);
                    data.add(list);
                });


        int colCount = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 0; i < colCount; i++) {
                List d = data.get(i);

                /*
                 * ORACLE JDBC Driver requires special handling of TIMESTAMPS:
                 *
                    ColumnTypeName: getObject Classname / MetaData Classname

                                        ==> 9.2.0.3.0
                                        DATE: java.sql.Timestamp / java.sql.Timestamp
                                        TIMESTAMP: oracle.sql.TIMESTAMP / oracle.sql.TIMESTAMP

                                        ==> 9.2.0.5.0
                                        DATE: java.sql.Timestamp / java.sql.Timestamp
                                        TIMESTAMP: oracle.sql.DATE / oracle.sql.TIMESTAMP

                                        ==> 10.2.0.1.0
                                        DATE: java.sql.Date / java.sql.Timestamp
                                        TIMESTAMP: oracle.sql.TIMESTAMP / oracle.sql.TIMESTAMP
                 */
                if (returnTypes.get(i).equals("oracle.sql.timestamp")) {
                    d.add(resultSet.getTimestamp(i + 1));
                }
                else {
                    d.add(resultSet.getObject(i + 1));
                }
            }
        }

        return new QueryResults(resultsMap);


    }


}
