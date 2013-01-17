package org.mbari.vars

import vars.LinkBean
import vars.annotation.ui.imagepanel.AreaMeasurement
import vars.annotation.ui.imagepanel.Measurement

/**
 *
 * @author Brian Schlining
 * @since 2013-01-15
 */
abstract class AbstractLinearMeasurementProcessor {
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
                        target << "$line\\tDistances\n"
                    }
                    else {

                        def associations = parts[associationColumn]


                        def addendum = ""
                        if (associations != null &&
                                !associations.isEmpty() &&
                                associations.contains(Measurement.MEASUREMENT_LINKNAME)) {

                            def xs = associations.split(",").collect { it.trim() }
                            //println("\t\t$xs\n")
                            //println("\t\tFound ${xs.size()} associations\n")
                            def distances = []
                            def url = new URL(parts[imageColumn])
                            for (x in xs) {
                                if (x.startsWith(Measurement.MEASUREMENT_LINKNAME)) {
                                    def link = new LinkBean(x)
                                    try {
                                        def areaMeasurement = Measurement.fromLink(link)
                                        def distance = toDistance(areaMeasurement, url)
                                        distances << distance
                                        println("\t$distance <= $link.linkValue")
                                    }
                                    catch (Exception e) {
                                        distances << Double.NaN
                                    }
                                }
                            }
                            //println("\t\tFound ${areas.size()} areaMeasurements")

                            addendum = distances.join(",")
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

        abstract toDistance(Measurement measurement, URL image)
}
