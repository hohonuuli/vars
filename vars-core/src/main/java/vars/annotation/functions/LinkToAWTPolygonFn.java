package vars.annotation.functions;

import com.google.common.base.Preconditions;
import vars.ILink;
import vars.annotation.AreaMeasurement;
import vars.annotation.Association;

import java.awt.*;
import java.util.function.Function;

/**
 * Convert an Association representing an AreaMeasurement to a java.awt.Polygon.
 * I implemented this so that I could calculate the intersection between polygons
 * using AWT (rather than writing the intersection code myself)
 * @author Brian Schlining
 * @since 2015-09-09T16:36:00
 */
public class LinkToAWTPolygonFn implements Function<ILink, Polygon> {

    @Override
    public Polygon apply(ILink link) {
        Preconditions.checkArgument(AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE.test(link),
                "Association is not an area measurement");

        Polygon polygon = new Polygon();
        AreaMeasurement areaMeasurement = AreaMeasurement.fromLink(link);
        areaMeasurement.getCoordinates().stream()
                .forEach(p -> polygon.addPoint(p.getX(), p.getY()));

        return polygon;
    }
}
