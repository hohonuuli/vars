package vars.query.ui.db;

import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mbari.sql.QueryResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Brian Schlining
 * @since 2015-05-22T15:45:00
 */
public class AssociationColumnRemapperTest {

    private final String ASSOC = "Associations";

    @Test
    public void testWithNoAssociationColumn() {
        Map<String, List<Object>> map = new TreeMap<>();
        List list = Lists.newArrayList("a", "b", "c");
        map.put("Foo", list);
        QueryResults queryResults = new QueryResults(map);
        AssociationColumnRemapper.apply(queryResults);
        assertTrue("Expected one column", queryResults.columnCount() == 1);
    }

    @Test
    public void testWithOneAssociation() {
        List list = Lists.newArrayList("assoc1 | foo | bar", null, "assoc1 | not foo | not bar");
        QueryResults queryResults = buildQueryResults(list);
        AssociationColumnRemapper.apply(queryResults);
        assertFalse("Found Association Column", queryResults.containsColumnName(ASSOC));
        assertTrue("Did not find new assoc1 column", queryResults.containsColumnName("assoc1"));
        assertTrue("Did not find correct # of associations", queryResults.getResults("assoc1").size() == 3);
    }


    @Test
    public void testWithMultipleAssociations() {
        List list = Lists.newArrayList("assoc1 | foo | bar", null, "assoc2 | not foo | not bar", "assoc2 | foo foo | bar bar");
        QueryResults queryResults = buildQueryResults(list);
        AssociationColumnRemapper.apply(queryResults);
        assertFalse("Found Association Column", queryResults.containsColumnName(ASSOC));
        assertTrue("Did not find new assoc1 column", queryResults.containsColumnName("assoc1"));
        assertTrue("Did not find new assoc2 column", queryResults.containsColumnName("assoc2"));
        assertTrue("Did not find correct # of associations", queryResults.getResults("assoc1").size() == 4);
    }

    public QueryResults buildQueryResults(List assoc) {
        Map<String, List<Object>> map = new TreeMap<>();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        List abc = new ArrayList();
        for (int i = 0; i< assoc.size(); i++) {
            abc.add(alphabet.substring(i, i + 1));
        }
        map.put("Foo", abc);
        map.put("Bar", abc);
        map.put(ASSOC, assoc);
        return new QueryResults(map);
    }
}
