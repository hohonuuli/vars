package org.mbari.vars.benthicrover

import vars.annotation.ui.imagepanel.AreaMeasurement
import vars.LinkBean

/**
 * Converts AreaMeasurement annotations into actual area measurements. This processor makes the
 * following assumptions:
 *   1. The camera is oriented perpendicular to the surface (i.e. it's pointed straight down)
 *   2. The aspect ration of the image is square. (i.e. each side of the pixel has the same
 *      dimensions e.g. 1cm x 1cm; NOT 1cm x 2cm)
 *
 * References: 
 *   http://askville.amazon.com/calculate-area-irregular-polygon/AnswerViewer.do?requestId=2432521
 *   http://mathworld.wolfram.com/PolygonArea.html
 *
 * @author Brian Schlining
 * @since 2012-11-26
 */
class BasicAreaMeasurementProcessor {

    def metersWidth = null
    def pixelsWidth = null

    /**
     * If arguments are provided to the constructor then all images are assumed to view the same
     * area.
     *
     * @param metersWidth The width of the image
     * @param pixelsWidth
     */
    public BasicAreaMeasurementProcessor(double metersWidth, int pixelsWidth) {
        this.metersWidth = metersWidth
        this.pixelsWidth = pixelsWidth
    }


    public apply(File file, File target) {
        def isHeader = true
        def associationColumn = -1
        file.eachLine { line ->
            if (!line.startsWith('#')) { // skip comments
                // Split line

                if (isHeader) {
                    associationColumn = findAssociationColumn(line)
                    if (associationColumn > -1) {
                        isHeader = false
                    }
                    target << "$line\tMeasurements\n"
                }
                else {
                    def parts = line.split('\t')
                    def associations = parts[associationColumn]


                    def addendum = ""
                    if (associations != null &&
                            !associations.isEmpty() &&
                            associations.contains(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {

                        def xs = associations.split(",")
                        def areaMeasurements = []
                        for (x in xs) {
                            if (x.startsWith(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {
                                def link = new LinkBean(x)
                                def areaMeasurement = AreaMeasurement.fromLink(link)
                                //println("$link: ${areaMeasurement}: ${toArea(areaMeasurement)}")
                                areaMeasurements << AreaMeasurement.fromLink(link)
                            }
                        }

                        def areas = areaMeasurements.collect { toArea(it) }

                        addendum = areas.join(",")

                    }

                    target << "$line\t$addendum\n"
                }
            }
            else {
                target << line + "\n"
            }
        }
    }

    static findAssociationColumn(String line) {
        def p = line.split("\t") as List
        return p.indexOf("Associations")
    }

    /**
     * Calculates area from areaMeasurement
     * @param areaMeasurement
     * @return THe planar area
     */
    def toArea(AreaMeasurement areaMeasurement) {
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
        return Math.abs((p1 - p2) / 2 * metersWidth / pixelsWidth * metersWidth / pixelsWidth)

    }

}
