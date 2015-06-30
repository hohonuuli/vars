package org.mbari.vars.arctic

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import org.mbari.vars.arctic.SpecialShipLogReader1._
import org.slf4j.LoggerFactory

import scala.io.Source

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-04-20T15:48:00
 */
object SpecialShipLogReader2 {

  private[this] val dateFormat = new SimpleDateFormat("yyyy dd-MMM  HH:mm:ss")
  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(file: File, year: Int): Seq[SimplePosition] = {
    val lines = Source.fromFile(file).getLines()
    lines.flatMap(l => parseLine(l, year)).toSeq
  }

  def parseLine(line: String, year: Int): Option[SimplePosition] = try {

    // 0        1         2         3
    // Year_day	Latitude	Longitude	Date
    // 271.919850	69.666900	-120.000080	28-Sep  22:04:35
    val p = line.split("\t").map(_.trim)
    val t = dateFormat.parse(s"$year ${p(3)}")
    val lat = p(1).toDouble
    val lon = p(2).toDouble
    Option(SimplePosition(t, lat, lon))
  }
  catch {
    case e: Exception => {
      log.debug(s"Unable to parse: $line")
      None
    }
  }

}

