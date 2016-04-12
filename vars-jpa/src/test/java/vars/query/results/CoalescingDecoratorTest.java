package vars.query.results;

import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2015-08-04T16:06:00
 */
public class CoalescingDecoratorTest {

    @Test
    public void test01() {
        Map<String, List<Object>> data = new HashMap<>();
        data.put("Foo", Lists.newArrayList(1, 1, 3, 3, 4));
        data.put("Bar", Lists.newArrayList("1a", "1b", "3a", "3b", "4a"));
        QueryResults queryResults = new QueryResults(data);
        QueryResults cqr = CoalescingDecorator.coalesce(queryResults, "Foo");
        assertEquals(cqr.getColumns(), 2);
        assertEquals(cqr.getRows(), 3);
    }
}
