package org.mbari.vars.integration

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.{Date, TimeZone}

import org.mbari.math.FastCollator
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.annotation.{AnnotationDAOFactory, VideoFrame}

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Merge a text file with annotations by date.
 *
 * @author Brian Schlining
 * @since 2015-10-12T16:50:00
 */
class GenericMerge(val url: URL, val delimiter: String = ",") {

  private[this] val parser = new TextParser(url, delimiter)
  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val missingDouble = Double.NaN
  private[this] val missingFloat = Float.NaN
  private[this] val dateParser = {
    val s = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    s.setTimeZone(TimeZone.getTimeZone("UTC"))
    s
  }

  /**
   * Apply the merge. This does everything and is typically the method that should be called.
   *
   * @param videoArchiveName The videoArchive to merge
   * @param offsetSeconds The maximum time distance between a record and an annotation. If no match
   *                      is found within that distance the annotation's values are set to null
   * @param daoFactory  implicit param for creating DAOs
   */
  def apply(videoArchiveName: String, offsetSeconds: Double, resetNulls: Boolean = true)(implicit daoFactory: AnnotationDAOFactory): Unit = {
    update(collate(videoArchiveName, offsetSeconds), resetNulls)
  }

  /**
   * Matches annotations with corresponding rows in the text file. I exposed this as it's useful
   * for debugging
   *
   * @param videoArchiveName The videoArchive to merge
   * @param offsetSeconds The maximum time distance between a record and an annotation. If no match
   *                      is found within that distance the annotation's values are set to null
   * @param daoFactory  implicit param for creating DAOs
   * @return Tuples of the videoframe and the data row (GenericData). None is returned if no match
   *         for a videoframe was found.
   */
  def collate(videoArchiveName: String, offsetSeconds: Double)
             (implicit daoFactory: AnnotationDAOFactory): Seq[(VideoFrame, Option[GenericData])] = {

    val videoFrames = findVideoFrames(videoArchiveName)
    val vfMillis = videoFrames.map(_.getRecordedDate.getTime).toArray
    val logData = loadGenericData()
    val logMillis = logData.map(_.date.getTime).toArray
    def vfToSecs(vf: VideoFrame): Double = vf.getRecordedDate.getTime / 1000D
    def logToSecs(gd: GenericData): Double = gd.date.getTime / 1000D
    FastCollator(videoFrames, vfToSecs, logData, logToSecs, offsetSeconds)

  }


  /**
   * Loads all videoframes into memory for the names videoarchive
   *
   * @param videoArchiveName
   * @param daoFactory
   * @return
   */
  private def findVideoFrames(videoArchiveName: String)
                             (implicit daoFactory: AnnotationDAOFactory): Seq[VideoFrame] = {
    val dao = daoFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val videoArchive = dao.findByName(videoArchiveName)
    val videoFrames = if (videoArchive != null) videoArchive.getVideoFrames.asScala else Nil
    dao.endTransaction()
    dao.close()
    videoFrames
  }

  /**
   * Parses the log file and converts each row to a case class.
   * @return
   */
  private def loadGenericData(): Seq[GenericData] = {

    val columns = parser.columns.map(_.toLowerCase)
    val date = load("date", parseDate, new Date(0), parser.rows.size)
    val lat = load("latitude", s => s.toDouble, Double.NaN, parser.rows.size)
    val lon = load("longitude", s => s.toDouble, Double.NaN, parser.rows.size)
    val depth = load("depth", s => s.toFloat, Float.NaN, parser.rows.size)
    val salinity = load("salinity", s => s.toFloat, Float.NaN, parser.rows.size)
    val temperature = load("temperature", s => s.toFloat, Float.NaN, parser.rows.size)
    val oxygen = load("oxygen", s => s.toFloat, Float.NaN, parser.rows.size)
    for (i <- date.indices) yield GenericData(date(i),
      lat(i),
      lon(i),
      depth(i),
      salinity(i),
      temperature(i),
      oxygen(i))
  }

  /**
    * First trys to parse yyyy-MM-ddTHH:mm:ssZ then tries yyyyMMddTHHmmssZ
    * @param s
    * @return
    */
  private def parseDate(s: String): Date =  Try(Date.from(Instant.parse(s))).getOrElse(dateParser.parse(s))

  /**
   *
   * @param columnName The case-insensitive column of interest
   * @param fn A function for converting a string value in the column into some other type
   * @param missingValue A missing value for the given type
   * @param expectedSize The expected number of values to be read from the database
   * @tparam T The type that is returned
   * @return A list of values for a given column. The list will be exactly the specifiec size. Values
   *         that were missing or could not be parsed will be replaced with missingValue
   */
  private def load[T](columnName: String, fn: String => T, missingValue: T, expectedSize: Int): List[T] = {
    val columns = parser.columns.map(_.toLowerCase)
    val idx = columns.indexOf(columnName.toLowerCase)
    if (idx >= 0) parser.values
        .map(row => Try(fn(row(idx))).getOrElse(missingValue))
        .toList
    else List.fill(expectedSize)(missingValue)
  }

  /**
   * Updates the fields of a VideoFrame in the database
   * @param data
   * @param resetNulls If true null/missing values in GenericData will reset the value in the
   *                   database to null. If false the value in the database will not be modified
   *                   if the GenericData contains a missingValue for that field.
   * @param daoFactory
   * @return
   */
  def update(data: Seq[(VideoFrame, Option[GenericData])], resetNulls: Boolean = true)
                    (implicit daoFactory: AnnotationDAOFactory): Unit = {
    val dao = daoFactory.newVideoArchiveDAO()
    val vfDao = daoFactory.newVideoFrameDAO(dao.getEntityManager)
    dao.startTransaction()

    for ((vf, gdOpt) <- data) {
      val gd = gdOpt.getOrElse(NullGenericData)
      val videoFrame = vfDao.find(vf)
      val pd = videoFrame.getPhysicalData

      if (resetNulls) {
        if (gd.latitude.isNaN) pd.setLatitude(null) else pd.setLatitude(gd.latitude)
        if (gd.longitude.isNaN) pd.setLongitude(null) else pd.setLongitude(gd.longitude)
        if (gd.depth.isNaN) pd.setDepth(null) else pd.setDepth(gd.depth)
        if (gd.salinity.isNaN) pd.setSalinity(null) else pd.setSalinity(gd.salinity)
        if (gd.temperature.isNaN) pd.setTemperature(null) else pd.setTemperature(gd.temperature)
        if (gd.oxygen.isNaN) pd.setOxygen(null) else pd.setOxygen(gd.oxygen)
      }
      else {
        if (!gd.latitude.isNaN) pd.setLatitude(gd.latitude)
        if (!gd.longitude.isNaN) pd.setLongitude(gd.longitude)
        if (!gd.depth.isNaN) pd.setDepth(gd.depth)
        if (!gd.salinity.isNaN) pd.setSalinity(gd.salinity)
        if (!gd.temperature.isNaN) pd.setTemperature(gd.temperature)
        if (!gd.oxygen.isNaN) pd.setOxygen(gd.oxygen)
      }
    }

    dao.endTransaction()
    dao.close()
  }

}

object GenericMerge {

  def main(args: Array[String]) {

    if (args.size < 2 || args.size > 4) {
      println(
        """
          | Merge video annotations with data in a CSV file. The annotation information
          | will be updated in the database using values found in the text file. The
          | merge uses the annotations recordedDate field and the files date column to
          | map data to each other.
          |
          | Usage:
          |   GenericMerge [videoArchiveName] [csv file] [window seconds] [reset missing]
          |
          | Inputs:
          |   videoArchiveName = The video archive name as stored in VARS
          |   csv file = a comma separated text file with a header line that contains
          |     at least a column named 'date' (no quotes) and may have one or more
          |     of the following columns: salinity, temperature, pressure, latitude, longitude,
          |     oxygen. Columns with other names will be ignored. Lines starting with # will
          |     be ignored.
          |   window seconds = the maximum allowed time between an annotation and a record in the text file.
          |     If an annotation is not found within this window of a row in the CSV file then the annotations
          |     position and CTD data will be set to null. Typically this window is about the same as
          |     you sample frequency. Default is a 15 seconds window (i.e +/- 7.5 seconds of an annotation)
          |   reset missing = Allowed values: true, false. If true, then if matched row in the CSV contains a missing
          |     value or null for a field that field in the database will be set to null. If false, the database
          |     value for a missing field will not be altered. This is useful if you need to first merge
          |     with a navigation file and then with a ctd file, where each file only contains some of the
          |     data fields. The default value is false (i.e. unmatched fields in database will not be altered)
        """.stripMargin)
      return
    }

    val deltaSeconds = if (args.size == 3) args(2).toDouble / 2D else 7.5

    val resetNulls = if (args.size == 4) Try(args(3).toBoolean).getOrElse(false) else false

    // --- Parse params
    val videoArchiveName = args(0)
    val datafile = args(1)
    val url: URL = if (datafile.toLowerCase.startsWith("http")) new URL(datafile)
        else new File(datafile).toURI.toURL

    // --- Get DAOFactory
    val tb = new ToolBox
    implicit val daoFactory = tb.getToolBelt.getAnnotationDAOFactory

    // --- Run Merge
    val gm = new GenericMerge(url)
    gm(videoArchiveName, deltaSeconds, resetNulls)
  }
}

/**
 *
 * @param date
 * @param latitude
 * @param longitude
 * @param depth
 * @param salinity
 * @param temperature
 * @param oxygen
 */
case class GenericData(date: Date,
                       latitude: Double,
                       longitude: Double,
                       depth: Float,
                       salinity: Float,
                       temperature: Float,
                       oxygen: Float)


object NullGenericData extends GenericData(new Date(0),
  Double.NaN,
  Double.NaN,
  Float.NaN,
  Float.NaN,
  Float.NaN,
  Float.NaN)