package org.mbari.vars.arctic

import java.io.File
import java.util.{TimeZone, GregorianCalendar, Date}

import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try

/**
 * [https://oceana.mbari.org/jira/browse/VARS-692]
 *
 * @author Brian Schlining
 * @since 2015-03-16T09:04:00
 */
object SpecialCSVLogReader2 {

  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(file: File): Seq[RawLogRecord] = {
    val lines = Source.fromFile(file).getLines()
    lines.flatMap(parseLine).toSeq
  }


  def parseLine(line: String): Option[RawLogRecord] = try {

    // 0         1 2  3      4 5         6 7          8 9    10  11     12   13 14   15
    // ROV DIVE#1,,90,164424,A,7027.0642,N,13633.4136,W,00.1,290,300910,034.4,E,D*27,periscope for sonar
    // 2 is depth, 3 is UTC as HHmmss, 5 is lat, 7 is lon.
    val ps = line.split(',')
    val depth = ps(2).toFloat
    val hms = ps(3)
    val ns = if (ps(6).toUpperCase == "N") 1 else -1
    val ew = if (ps(8).toUpperCase == "E") 1 else -1
    val latitude = parseLatLon(ps(5), ns)
    val longitude = parseLatLon(ps(7), ew)
    val gpsTime = hms.substring(0, 2) + ":" + hms.substring(2, 4) + ":" + hms.substring(4)
    Option(RawLogRecord(None, None, Option(depth), latitude, longitude, gpsTime))
  }
  catch {
    case e: Exception => {
      log.debug(s"Unable to parse: $line")
      None
    }
  }

  private def parseLatLon(ddmm: String, direction: Int): Option[Double] = Try {
    val idx = ddmm.indexOf('.')
    val degrees = ddmm.take(idx - 2).toDouble
    val minutes = ddmm.drop(idx - 2).toDouble
    direction * (degrees + minutes / 60.0)
  } toOption

}
