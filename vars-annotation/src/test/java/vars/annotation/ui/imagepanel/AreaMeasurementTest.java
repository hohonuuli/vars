package vars.annotation.ui.imagepanel;

import org.junit.Test;
import vars.ILink;
import vars.LinkBean;

import static org.junit.Assert.*;


/**
 * @author Brian Schlining
 * @since 2011-12-20
 */
public class AreaMeasurementTest {
    
    @Test
    public void parseTest() {
        String linkRep = "area measurement coordinates [x0 y0 ... xn yn, comment] | self | 184 284 212 269 240 274 237 297 191 313, seastart";
        ILink link = new LinkBean(linkRep);
        try {
            AreaMeasurement areaMeasurement = AreaMeasurement.fromLink(link);
        }
        catch (Exception e) {
            fail("Failed to parse " + linkRep + ". Reason: " + e.getMessage());
        }
    }
}
