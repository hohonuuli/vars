package vars.annotation.functions;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mbari.geometry.Point2D;
import vars.ILink;
import vars.annotation.AreaMeasurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Brian Schlining
 * @since 2015-09-10T11:16:00
 */
public class TrimAreaMeasurementsFnTest {

    private List<Point2D<Integer>> myBounds = new ArrayList<>();
    private Function<ILink, Optional<ILink>> fn;

    @Before
    public void init() {
        myBounds.add(new Point2D<>(0, 0));
        myBounds.add(new Point2D<>(10, 0));
        myBounds.add(new Point2D<>(10, 10));
        myBounds.add(new Point2D<>(0, 10));
        fn = new TrimAreaMeasurementsFn(myBounds);
    }

    @Test
    public void testNoIntersection() {
        // Test no intersection
        List<Point2D<Integer>> bounds = new ArrayList<>();
        bounds.add(new Point2D<>(11, 0));
        bounds.add(new Point2D<>(15, 0));
        bounds.add(new Point2D<>(11, 10));
        AreaMeasurement am = new AreaMeasurement(bounds);
        ILink link = am.toLink();
        Optional<ILink> result = fn.apply(link);
        assertTrue(!result.isPresent());
    }

    @Test
    public void testCompleteIntersection() {
        // Test complete overlap
        List<Point2D<Integer>> bounds = new ArrayList<>();
        bounds.add(new Point2D<>(1, 1));
        bounds.add(new Point2D<>(9, 1));
        bounds.add(new Point2D<>(9, 9));
        bounds.add(new Point2D<>(1, 9));
        AreaMeasurement am = new AreaMeasurement(bounds);
        ILink link = am.toLink();
        Optional<ILink> result = fn.apply(link);

        // We expect a result
        assertTrue(result.isPresent());

        // The coordinates of the areameasurement should be unchanged.
        // Note that the order may be different
        ILink link0 = result.get();
        AreaMeasurement am0 = AreaMeasurement.fromLink(link0);
        List<Point2D<Integer>> bounds0 = am0.getCoordinates();
        assertTrue(bounds0.size() == bounds.size());
        bounds0.stream()
                .forEach(p -> assertTrue(bounds.contains(p)));

    }

    @Test
    public void testParialIntersection() {
        List<Point2D<Integer>> bounds = new ArrayList<>();
        bounds.add(new Point2D<>(0, 0));
        bounds.add(new Point2D<>(15, 0));
        bounds.add(new Point2D<>(10, 10));
        bounds.add(new Point2D<>(0, 10));
        AreaMeasurement am = new AreaMeasurement(bounds);
        ILink link = am.toLink();
        Optional<ILink> result = fn.apply(link);

        // The trimmed coordinates should match myBounds
        ILink link0 = result.get();
        AreaMeasurement am0 = AreaMeasurement.fromLink(link0);
        List<Point2D<Integer>> bounds0 = am0.getCoordinates();
        assertTrue(bounds0.size() == myBounds.size());
        bounds0.stream()
                .forEach(p -> assertTrue(myBounds.contains(p)));
    }
}
