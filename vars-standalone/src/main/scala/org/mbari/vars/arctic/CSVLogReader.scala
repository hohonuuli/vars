package org.mbari.vars.arctic

import java.io.File
import java.util.Date

import scala.io.Source
import scala.util.Try

/**
 * Reads in a log file as [[RawLogRecord]] objects. Invalid lines
 * and comments are ignored. Use as:
 * {{{
 *   val file: File = ...
 *   val records: Seq[RawLogRecord] = CSVLogReader(file)
 * }}}
 *
 * @author Brian Schlining
 * @since 2015-02-25T13:56:00
 */
object CSVLogReader {

  private def parseLatLon(ddmm: String): Option[Double] = Try {
    val idx = ddmm.indexOf('.')
    val degrees = ddmm.take(idx - 2).toDouble
    val minutes = ddmm.drop(idx - 2).toDouble
    degrees + minutes / 60.0
  } toOption

  private def parseFloat(s: String): Option[Float] = Try(s.toFloat).toOption

  def lineToLogRecord(line: String): Option[RawLogRecord] = Try {

    // 0         1 2 3 4         5   6         7   8     9   10   11
    // $(HEADER),C,T,D,TIME(CTD),SAL,TIME(GPS),HDG,DEPTH,LAT,LONG,SPEED
    val parts = line.split(",")
    val salinity = parseFloat(parts(5))
    val temperature = parseFloat(parts(2))
    val depth = parseFloat(parts(3))
    val time = parts(6)
    val latitude = parseLatLon(parts(9))
    val longitude = parseLatLon(parts(10))
    RawLogRecord(salinity, temperature, depth, latitude, longitude, time)
  } toOption

  def apply(file: File): Seq[RawLogRecord] = {
    val source = Source.fromFile(file)
    val lines = source.getLines()
    val records = lines.filter(!_.startsWith("#")).flatMap(lineToLogRecord)
    val rs = records.toSeq
    //source.close() // Do NOT close before evaluating records. (THey're lazy)
    rs
  }

}


