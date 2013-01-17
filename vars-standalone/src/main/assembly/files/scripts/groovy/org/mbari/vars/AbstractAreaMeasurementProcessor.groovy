package org.mbari.vars

import vars.LinkBean
import vars.annotation.ui.imagepanel.AreaMeasurement

/**
 *
 * @author Brian Schlining
 * @since 2012-12-07
 */
abstract class AbstractAreaMeasurementProcessor {

    public apply(File file, File target) {
        def isHeader = true
        def associationColumn = -1
        def imageColumn = -1
        file.eachLine { line ->
            if (!line.startsWith('#')) { // skip comments
                // Split line
                def parts = line.split('\t') as List
                if (isHeader) {
                    associationColumn = parts.indexOf("Associations")
                    imageColumn = parts.indexOf("Image")
                    if (associationColumn > -1) {
                        isHeader = false
                    }
                    target << "$line\tMeasurements\n"
                }
                else {

                    def associations = parts[associationColumn]

                    def addendum = ""
                    if (associations != null &&
                            !associations.isEmpty() &&
                            associations.contains(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {

                        def xs = associations.split(",").collect { it.trim() }
                        //println("\t\t$xs\n")
                        //println("\t\tFound ${xs.size()} associations\n")
                        def areas = []
                        def url = new URL(parts[imageColumn])
                        for (x in xs) {
                            if (x.startsWith(AreaMeasurement.AREA_MEASUREMENT_LINKNAME)) {
                                def link = new LinkBean(x)
                                try {
                                    def areaMeasurement = AreaMeasurement.fromLink(link)
                                    def area = toArea(areaMeasurement, url)
                                    areas << area
                                    println("\t$area <= $link.linkValue")
                                }
                                catch (Exception e) {
                                    areas << Double.NaN
                                }
                            }
                        }
                        //println("\t\tFound ${areas.size()} areaMeasurements")

                        addendum = areas.join(",")
                        //println("\t\taddendum is: $addendum")

                    }

                    target << "$line\t$addendum\n"
                }
            }
            else {
                target << line + "\n"
            }
        }
    }

    abstract toArea(AreaMeasurement areaMeasurement, URL image)

}
