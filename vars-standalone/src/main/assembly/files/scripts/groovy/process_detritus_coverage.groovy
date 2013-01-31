import org.mbari.smith.Camera
import org.mbari.smith.CoverageEstimator
import org.mbari.vars.AreaMeasurementProcessor
import scala.None

/**
 * 
 * @author Brian Schlining
 * @since 2013-01-30
 */

if (args.size() < 6) {
    println("""
    | Script that converts any 'area measurement' associations found in your VARS query results
    | and converts them to area. The results are appended to the end of each row in a new text file.
    |
    | Usage:
    |   gsh process_area <cameraHeight> <alpha> <beta> <theta> <inputFile> <outputFile>
    |
    | Arguments:
    |    cameraHeight:     The height of the camera above the seafloor. All area measurements will
    |                      be in the same units as cameraHeight (i.e. you should use centimeters
    |                      instead of meters)
    |    alpha:            The vertical angular field of view in degrees
    |    beta:             The horizontal angular field of view in degrees
    |    theta:            The tilt of the camera from horizontal in degrees
    |    videoArchiveName: The VideoArchive to process
    |    outputFile:       The file to save the results into.
    """.stripMargin('|'))
    return
}

def cameraHeight = Double.parseDouble(args[0])
def alpha = Math.toRadians(Double.parseDouble(args[1]))
def beta = Math.toRadians(Double.parseDouble(args[2]))
def theta = Math.toRadians(Double.parseDouble(args[3]))
def videoArchiveName = args[4]
def outputFile = new File(args[5])


def camera = new Camera(cameraHeight, alpha, beta, theta)
def actualAreas = CoverageEstimator.apply(videoArchiveName, camera)
for (i in 0..<actualAreas.size()) {
    def a = actualAreas.get(i)
    def pct = a.detritalArea / a.fovArea * 100
    println("${a.videoFrame.timecode}\t${a.fovArea}\t${a.detritalArea}\t$pct")
}

