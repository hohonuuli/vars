package vars.queryfx;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Brian Schlining
 * @since 2016-10-17T13:58:00
 */
public class StateLookupTest {

    @Test
    public void testGetConfig() {
        assertNotNull(StateLookup.getConfig());
    }
}
