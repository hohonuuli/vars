package org.mbari.vars.tripod

import vars.LinkBean
import vars.annotation.ui.imagepanel.AreaMeasurement

import java.awt.Polygon

/**
 *
 * @author Brian Schlining
 * @since 2013-01-16
 */
class DropNonFovAnnotations {

    public apply(File file, File target) {

        // Figure out which lines to keep
        def linesToKeep = []
        def polygons = asPolygons(file)
        def timecodeMap = indexByTimecode(file)
        def fovMap = indexByFov(file)
        fovMap.each { timecode, i ->
            def fov = polygons[i]
            def others = timecodeMap[timecode].collect { polygons[it.value] }
            def intersect = others.findAll { p ->
                p != null && fov.intersects(p.bounds2D)
            }
            intersect.each { p ->
                linesToKeep << (polygons.indexOf(p))
            }
        }

        // Rewrite target
        int currentLine = -1
        file.eachLine { line ->
            currentLine += 1
            if(line.startsWith('#')) {
                target << line + '\n'
            }
            else {
                if (linesToKeep.contains(currentLine)) {
                    target << line + "\n"
                }
            }
        }

    }

    /**
     * Convert the first area-measurement association in each line to a
     * polygon (or null if it doesn't contain an areameasurement)
     *
     * @param file
     * @return
     */
    private List<Polygon> asPolygons(File file) {
        def isHeader = true
        def associationColumn = -1
        def polygons = []
        int currentLine = -1
        file.eachLine { line ->
            currentLine = currentLine + 1
            def polygon = null
            if(!line.startsWith('#')) { // Skip comments

                def parts = line.split('\t') as List
                if (isHeader) {
                    polygons << null
                    associationColumn = parts.indexOf("Associations")
                    if (associationColumn > -1) {
                        isHeader = false
                    }
                }
                else {
                    def associations = parts[associationColumn]
                    if (associations != null &&
                            !associations.isEmpty() &&
                            associations.contains(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {

                        def xs = associations.split(",").collect { it.trim() }

                        for (x in xs) {
                            if (x.startsWith(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {
                                def link = new LinkBean(x)
                                try {
                                    def areaMeasurement = AreaMeasurement.fromLink(link)
                                    polygon = toPolygon(areaMeasurement)
                                }
                                catch (Exception e) {
                                    // do nothing
                                }

                            }
                            if (polygon != null) {
                                break
                            }
                        }
                    }
                    polygons << polygon

                }
            }
            else {
                polygons << null
            }
        }
        return polygons
    }

    private Polygon toPolygon(AreaMeasurement areaMeasurement) {
        def coords = areaMeasurement.coordinates
        def polygon = new Polygon()
        coords.each { p ->
            polygon.addPoint(p.x, p.y)
        }
        return polygon
    }

    /**
     * Get a Map of timecode/int for each field of view annotation in the file
     * @param file
     * @return Map where the key is the timecode and value is the line number
     * of the field of view annotation for that timecode
     */
    private Map<String, Integer> indexByFov(File file) {
        def isHeader = true
        def timecodeColumn = -1
        def conceptnameColumn = -1
        def timecodeMap = [:]
        int currentLine = -1
        file.eachLine { line ->
            currentLine = currentLine + 1
            if(!line.startsWith('#')) { // Skip comments
                def parts = line.split('\t') as List
                if (isHeader) {
                    timecodeColumn = parts.indexOf("TapeTimeCode")
                    conceptnameColumn = parts.indexOf("ConceptName")
                    if (timecodeColumn > -1) {
                        isHeader = false
                    }
                }
                else {
                    def conceptName = parts[conceptnameColumn]
                    if (conceptName.equalsIgnoreCase("field of view")) {
                        def timecode = parts[timecodeColumn]
                        timecodeMap.put(timecode, currentLine)
                    }

                }
            }
        }
        return timecodeMap
    }

    /**
     * Get a Map of timecode/List<Int> where the key is the timecode and the value
     * is a list of line numbers for that timecode.
     * @param file
     * @return
     */
    private Map<String, List> indexByTimecode(File file) {
        def isHeader = true
        def timecodeColumn = -1
        def timecodeMap = [:]
        int currentLine = -1
        file.eachLine { line ->
            currentLine = currentLine + 1
            if(!line.startsWith('#')) { // Skip comments
                def parts = line.split('\t') as List
                if (isHeader) {
                    timecodeColumn = parts.indexOf("TapeTimeCode")
                    if (timecodeColumn > -1) {
                        isHeader = false
                    }
                }
                else {
                    def timecode = parts[timecodeColumn]
                    def indices = timecodeMap.get(timecode, [])
                    indices << currentLine
                }
            }
        }
        return timecodeMap
    }

}
