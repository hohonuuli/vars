package vars.query.results;

import com.google.common.collect.Lists;
import org.junit.Test;
import vars.query.results.QueryResults;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Schlining
 * @since 2015-08-04T15:56:00
 */
public class QueryResultsTest {

    /**
     * Tests construction and parameters.
     */
    @Test
    public void testTheBasics() {
        Map<String, List<Object>> data = new HashMap<>();
        data.put("Foo", Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        data.put("Bar", Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        QueryResults queryResults = new QueryResults(data);
        assertEquals(9, queryResults.getRows());
        assertEquals(2, queryResults.getColumns());
        Set<String> columnNames = queryResults.getColumnNames();
        assertEquals(columnNames, data.keySet());
    }

    /**
     * Tests that if the data contains Lists of different sizes,
     * an exception should be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBadArgument() {
        Map<String, List<Object>> data = new HashMap<>();
        data.put("Foo", Lists.newArrayList(1, 2, 3, 4));
        data.put("Bar", Lists.newArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        QueryResults queryResults = new QueryResults(data);
    }

    @Test
    public void testCopyData() {
        Map<String, List<Object>> data = new HashMap<>();
        data.put("Foo", Lists.newArrayList(1, 2, 3, 4));
        data.put("Bar", Lists.newArrayList("a", "b", "c", "d"));
        QueryResults queryResults = new QueryResults(data);
        QueryResults copy = new QueryResults(queryResults.copyData());
        assertEquals(queryResults.getColumns(), copy.getColumns());
        assertEquals(queryResults.getRows(), copy.getRows());

    }


}
