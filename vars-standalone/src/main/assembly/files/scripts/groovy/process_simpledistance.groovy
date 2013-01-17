import org.mbari.vars.SimpleLinearMeasurementProcessor

/**
 * 
 * @author Brian Schlining
 * @since 2012-12-10
 */

if (args.size() < 6) {
    println("""
    | Script that converts any 'measurement' associations found in your VARS query results
    | and converts them to pixel distances. The results are appended to the end of each row
    | in a new text file.
    |
    | Usage:
    |   gsh process_area <inputFile> <outputFile>
    |
    | Arguments:
    |    inputFile:    The data file you saved from VARS query
    |    outputFile:   The file to save the results into.
    """.stripMargin('|'))
    return
}

def inputFile = new File(args[0])
def outputFile = new File(args[1])

def a = new SimpleLinearMeasurementProcessor()
a.apply(inputFile, outputFile)
