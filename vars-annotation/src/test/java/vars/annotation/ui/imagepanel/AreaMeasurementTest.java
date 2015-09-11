package vars.annotation.ui.imagepanel;

import org.junit.Test;
import org.mbari.geometry.Point2D;
import vars.ILink;
import vars.LinkBean;
import vars.annotation.AreaMeasurement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * @author Brian Schlining
 * @since 2011-12-20
 */
public class AreaMeasurementTest {
    
    @Test
    public void parseTest() {
        List<Point2D<Integer>> expected = new ArrayList<Point2D<Integer>>();
        expected.add(new Point2D<Integer>(184, 284));
        expected.add(new Point2D<Integer>(212, 269));
        expected.add(new Point2D<Integer>(240, 274));
        expected.add(new Point2D<Integer>(237, 297));
        expected.add(new Point2D<Integer>(191, 313));
        String linkRep = "area measurement coordinates [x0 y0 ... xn yn, comment] | self | 184 284 212 269 240 274 237 297 191 313; seastart";
        ILink link = new LinkBean(linkRep);
        try {
            AreaMeasurement areaMeasurement = AreaMeasurement.fromLink(link);
            List<Point2D<Integer>> points = areaMeasurement.getCoordinates();
            assertEquals("Did not parse the expected number of points", points.size(), 5);
            for (int i = 0; i < points.size(); i++) {
                assertEquals(expected.get(i), points.get(i));
            }
            assertEquals(" seastart", areaMeasurement.getComment());
        }
        catch (Exception e) {
            fail("Failed to parse " + linkRep + ". Reason: " + e.getMessage());
        }
    }
}
