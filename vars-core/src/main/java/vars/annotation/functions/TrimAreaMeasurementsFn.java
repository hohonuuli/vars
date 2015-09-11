package vars.annotation.functions;

import com.google.common.base.Preconditions;
import org.mbari.geometry.Point2D;
import vars.ILink;
import vars.annotation.AreaMeasurement;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

/**
 * Trims an AreaMeasurement association to only the part of the area that intersects
 * the provided polygon.
 *
 * @author Brian Schlining
 * @since 2015-09-09T15:57:00
 */
public class TrimAreaMeasurementsFn implements Function<ILink, Optional<ILink>> {

    private final Area acceptableBounds;
    private final Function<ILink, Polygon> toPolygon = new LinkToAWTPolygonFn();

    public TrimAreaMeasurementsFn(java.util.List<Point2D<Integer>> bounds) {
        Preconditions.checkArgument(bounds != null, "The bounds can not be null");
        Preconditions.checkArgument(!bounds.isEmpty(), "The bounds can not be empty");


        // Build a java.awt.Area object that we can use it to calculate the intersections
        Polygon boundingPolygon = new Polygon();
        bounds.stream()
                .forEach(p -> boundingPolygon.addPoint(p.getX(), p.getY()));
        acceptableBounds = new Area(boundingPolygon);
    }

    /**
     * Calculates the intersection of an AreaMeasurement with the boundaries passed to
     * the constructor. If an AreaMeasurement Association has no intersection then
     * None is returned. If the link is NOT an AreaMeasurement then it is returned
     * as is. Otherwise a new Link representing the AreaMeasurement intersection is
     * returned
     *
     * @param link The link to convert
     * @return A link of either the intersection, unmodified if not an area-measurement or
     *  None if there is no intersection.
     */
    @Override
    public Optional<ILink> apply(ILink link) {
        if (AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE.test(link)) {
            Polygon polygon = toPolygon.apply(link);
            Area area = new Area(polygon);
            area.intersect(acceptableBounds);

            Polygon trimmedPolygon = areaToPolygon(area);
            if (trimmedPolygon.npoints >= 3) {
                AreaMeasurement trimmedAreaMeasurement = polygonToAreaMeasurement(trimmedPolygon);
                return Optional.of(trimmedAreaMeasurement.toLink());
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.of(link);
        }
    }

    private Polygon areaToPolygon(Area area) {
        PathIterator i = area.getPathIterator(null);
        float[] floats = new float[6];
        Polygon polygon = new Polygon();
        while(!i.isDone()) {
            int type = i.currentSegment(floats);
            int x = (int) floats[0];
            int y = (int) floats[1];
            if (type != PathIterator.SEG_CLOSE) {
                polygon.addPoint(x, y);
            }
            i.next();
        }
        return polygon;
    }

    private AreaMeasurement polygonToAreaMeasurement(Polygon polygon) {
        int n = polygon.npoints;
        int[] xs = polygon.xpoints;
        int[] ys = polygon.ypoints;
        java.util.List<Point2D<Integer>> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            points.add(new Point2D<>(xs[i], ys[i]));
        }

        return new AreaMeasurement(points);
    }
}
