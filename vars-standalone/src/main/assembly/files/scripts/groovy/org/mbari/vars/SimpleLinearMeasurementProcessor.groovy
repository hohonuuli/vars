package org.mbari.vars

import org.mbari.geometry.Point2D
import vars.annotation.ui.imagepanel.Measurement

/**
 *
 * @author Brian Schlining
 * @since 2013-01-15
 */
class SimpleLinearMeasurementProcessor extends AbstractLinearMeasurementProcessor {

    @Override
    def toDistance(Measurement measurement, URL image) {
        def p0 = new Point2D<Double>(measurement.x0, measurement.y0)
        def p1 = new Point2D<Double>(measurement.x1, measurement.y1)
        return p0.distance(p1)
    }
}
