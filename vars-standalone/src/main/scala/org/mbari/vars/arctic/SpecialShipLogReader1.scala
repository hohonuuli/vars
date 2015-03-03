package org.mbari.vars.arctic

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.Try

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-02T13:58:00
 */
object SpecialShipLogReader1 {

  private[this] val dateFormat = new SimpleDateFormat("yyyyDDDHHmmss")
  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(file: File, year: Int): Seq[SimplePosition] = {
    val lines = Source.fromFile(file).getLines()
    lines.flatMap(l => lineToSimplePosition(l, year)).toSeq
  }

  def lineToSimplePosition(line: String, year: Int): Option[SimplePosition] = try {

    // 0    1   2    3      4
    // Time,Lat,Long,StnNum,ROVDiveNum
    // 272181000,70.936167,-134.444205,STN016,ROVD14
    val p = line.split(",")
    val t = parseDate(p(0), year)
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

  /**
   * The date is stored as DDDHHmmss. We have to manually specify the year.
   * @param s DDDHHmmss, e.g. "272181002"
   * @param year The int year e.g. 2013
   * @return The date
   */
  def parseDate(s: String, year: Int): Date = dateFormat.parse(year + s)

}
