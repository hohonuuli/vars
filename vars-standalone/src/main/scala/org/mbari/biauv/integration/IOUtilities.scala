package org.mbari.biauv.integration

import java.io.{FileWriter, BufferedWriter, File}
import org.mbari.math.Matlib
import scala.math._

/**
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */

object IOUtilities {

    /**
     * Dump cameradata out to a text file.
     */
    def main(args: Array[String]) {
        if (args.length != 1 && args.length != 3 && args.length != 4) {
            println("""
                | Generate a camera data file from an BIAUV mission for loading images
                | into VARS
                |
                | Usage: generate-camdata.scala [missiondir]
                |        generate-camdata.scala [navigation log] [camera log] [ouput file]
                |        generate-camdata.scala [navigation log] [camera log] [ouput file] [mission name]
                |
                | Inputs:
                |        missiondir = The path to a mission directory
                |        navigation log = Path and/or name of the navigation.log to use
                |        camera log = Path and/or name of the camera.log to use
                |        output file = Path and/or name of the file to create
                |        mission name = The name of the mission
            """.stripMargin)
            System.exit(-1)
        }

        val (missionName, navigationFile, cameraFile, targetFile) = parseArgs(args)

        val mergeData = extractLogRecords(navigationFile, cameraFile)

        // Write out results
        val writer = new BufferedWriter(new FileWriter(targetFile))
        writer.write("# Data file for photographs from Mission " + missionName + "\n")
        writer.write("# All units are in SI, with angles in radians, unless otherwise noted.\n")
        writer.write("# UnixTime\tPhoto No.\tLat(deg)\tLon(deg)\tDepth\tAltitude\tYaw\tPitch\tRoll\tFOVheight\tFOVwidth\n")
        for (i <- 0.until(mergeData.time.length)) {
            val msg = "%15.2f\t%4.0f\t%13.8f\t%13.8f\t%7.3f\t%7.3f\t%7.4f\t%7.4f\t%7.4f\t%5.2f\t%5.2f\n".format(
                    mergeData.time(i),
                    mergeData.photoNumber(i),
                    mergeData.latitude(i),
                    mergeData.longitude(i),
                    mergeData.depth(i),
                    mergeData.altitude(i),
                    mergeData.yaw(i),
                    mergeData.pitch(i),
                    mergeData.roll(i),
                    mergeData.viewHeight(i),
                    mergeData.viewWidth(i))
            writer.write(msg)
        }
        writer.close()

    }

    /**
     * Extracts the information needed for a VARS load/merge from the BIAUV log files
     *
     * @param navigationLog The navigation log to read
     * @param cameraLog THe camera log to read. Navigation information will be
     *          linearly interpolated to the timestamps in this log
     */
    def extractLogRecords(navigationLog: File, cameraLog: File): MergeData = {
        val navigationData = LogRecordReader.read(navigationLog)
        val cameraData = LogRecordReader.read(cameraLog)

        // Extract variables of interest
        val navTime = extractArray(navigationData, "time")
        val mAltitude = extractArray(navigationData, "mAltitude")
        val mPhi = extractArray(navigationData, "mPhi")
        val mTheta = extractArray(navigationData, "mTheta")
        val mPsi = extractArray(navigationData, "mPsi")
        val cameraTime = extractArray(cameraData, "time")
        val cameraPhotoNumber = extractArray(cameraData, "cam.number")
        val cameraLatitude = extractArray(cameraData, "cam.latitude").map { _ * 180D / Pi }
        val cameraLongitude = extractArray(cameraData, "cam.longitude").map { _ * 180D / Pi }
        val cameraDepth = extractArray(cameraData, "cam.depth")

        // Interpolate to cameraTime and calc needed params
        val cameraAltitude = Matlib.interpolate(navTime, mAltitude, cameraTime).map { _  / cos(Pi / 6) }
        val cameraRoll = Matlib.interpolate(navTime, mPhi, cameraTime)
        val cameraPitch = Matlib.interpolate(navTime, mTheta, cameraTime)
        val cameraYaw = Matlib.interpolate(navTime, mPsi, cameraTime)
        val focalLength = 28D // in mm
        val viewHeight = cameraAltitude.map { _ * 24D / focalLength }
        val viewWidth = cameraAltitude.map { _ * 36D / focalLength }

        new MergeData(cameraTime, cameraPhotoNumber, cameraLatitude, cameraLongitude, cameraDepth,
                cameraAltitude, cameraYaw, cameraPitch, cameraRoll, viewHeight, viewWidth)
    }

    /**
     * Parse the input arguments.
     *
     * @return A Tuple of (mission name, navigation log, camera log, output file)
     */
     private def parseArgs(args: Array[String]) = {
        if (args.length == 1) {
            val missionDir = new File(args(0))
            val missionName = missionDir.getName
            val navigationFile = new File(missionDir, "navigation.log")
            val cameraFile = new File(missionDir, "camera.log")
            val targetFile = new File(missionDir, "camera.txt")
            (missionName, navigationFile, cameraFile, targetFile)
        }
        else {
            val navigationFile = new File(args(0))
            val cameraFile = new File(args(1))
            val targetFile = new File(args(2))
            val missionName = if (args.length == 4) args(3) else "UNKNOWN"
            (missionName, navigationFile, cameraFile, targetFile)
        }

    }

    /**
     * Get the log record of interest. Located by it's name
     *
     * @param records A list of log records
     * @param name The name of the log record of interest
     * @return The array of data from the log record of interest. If no match
     *      was found an empty double array is returned.
     */
    def extractArray(records: List[LogRecord], name: String): Array[Double] = {
        println("Extracting " + name)
        records.find { _.shortName == name } match {
            case Some(x) => x.data.toArray.map { _.asInstanceOf[Double] }
            case None => {
                println("Unable to find LogRecord.shortName == " + name)
                Array[Double]()
            }
        }
    }

}