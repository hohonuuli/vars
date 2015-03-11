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
 * @since 2015-03-03T08:46:00
 */
object SpecialCSVLogReader1 {

  private[this] val dateFormat = new SimpleDateFormat("yyyyDDDHHmmss")
  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(file: File, year: Int): Seq[SimplePosition] = {
    val lines = Source.fromFile(file).getLines()
    lines.flatMap(l => parseLine(l, year)).toSeq
  }

  def parseLine(line: String, year: Int): Option[SimplePosition] = try {

    // 0    1        2             3   4    5          6           7            8
    // Date,Time_gmt,Calc_TapeCODE,Lat,Long,Vehic_Dive,Notes_atSea,CKP_Tapetime,CKP_Notes,
    // ,14:43:00,,,,MBARI_MINIROV_D6,start dive,,,
    // 270153628,15:36:28,0:52:23,70.80719167,-136.0989967,MBARI_MINIROV_D6,,,,
    val p = line.split(",")
    val t = parseDate(p(0), year)
    val lat = p(3).toDouble
    val lon = p(4).toDouble
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
