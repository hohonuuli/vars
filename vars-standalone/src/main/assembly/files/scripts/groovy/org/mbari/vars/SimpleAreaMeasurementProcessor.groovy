package org.mbari.vars

import vars.annotation.AreaMeasurement

/**
 *
 * @author Brian Schlining
 * @since 2013-01-15
 */
class SimpleAreaMeasurementProcessor extends AbstractAreaMeasurementProcessor {

    @Override
    def toArea(AreaMeasurement areaMeasurement, URL image) {
        def coords = areaMeasurement.coordinates
        def n = coords.size()

        def i1 = (0..<n).step(1)       // 0, 1, 2, ... , n
        def i2 = (1..<n).step(1) << 0  // 1, 2, 3, ... , n, 0

        def p1 = 0
        def p2 = 0
        for (i in 0..<n) {
            p1 = p1 + (coords[i1[i]].x * coords[i2[i]].y)
            p2 = p2 + (coords[i2[i]].x * coords[i1[i]].y)
        }
        // The sign just indicates the clockwise/counter-clockwises arrangement of
        // coordinates. So we just return the abs value
        return Math.abs((p1 - p2) / 2)
    }
}
