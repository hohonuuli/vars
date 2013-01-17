import org.mbari.vars.AreaMeasurementProcessor
import org.mbari.vars.tripod.DropNonFovAnnotations

/**
 *
 * @author Brian Schlining
 * @since 2013-01-16
 */

if (args.size() != 2) {
    println("""
    | Script that drops any lines that do not have area-measurements that overlap the
    | field of view
    |
    | Usage:
    |   gsh process_fov <inputFile> <outputFile>
    |
    | Arguments:
    |    inputFile:    The data file you saved from VARS query
    |    outputFile:   The file to save the results into.
    """.stripMargin('|'))
    return
}

def inputFile = new File(args[0])
def outputFile = new File(args[1])

def a = new DropNonFovAnnotations()
a.apply(inputFile, outputFile)

