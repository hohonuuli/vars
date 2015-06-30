package org.mbari.vars.arctic

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone, GregorianCalendar, Date}

import org.mbari.math.Matlib
import org.mbari.movie.Timecode

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-26T09:03:00
 */
object MergeUtilities {

  private[this] val dateFormat = new SimpleDateFormat("HH:mm:ss")


  /**
   * Looks for midnight rollovers in the records. It does this by
   * checking to see if the GPS clock time decreases. If there's
   * problems with the ordering of your records this method will
   * report false rollovers
   *
   * @param records The log records to check
   * @return A sequences of indexes where the rollover occurred
   */
  def findClockRollover(records: Iterable[ILogRecord]): Seq[Int] = {

    // Returns rollovers in reverse order. Remember to flip the returned array!!
    def findRollovers(xs: Array[Double], accum: List[Int] = Nil, i: Int = 0): Seq[Int] = {
      val r = xs.indexWhere(_ < 0) // index of sign change in local Array
      val s = r + i                // index of sign change in original Array
      if (r == -1) accum
      else if (r == xs.size - 1) s :: accum
      else findRollovers(xs.slice(r + 1, xs.size), s :: accum, s + 1)
    }

    val frames = records.map(r => new Timecode(s"${r.gpsTime}:00").getFrames).toArray
    val df = Matlib.diff(frames)
    findRollovers(df).reverse.map(_ + 1) // Account for index of diff
    
  }

  def toFullLogRecords(records: Seq[RawLogRecord], startDate: Date): Seq[FullLogRecord] = {

    // --- Extract just the Y/M/D portion of the date
    val calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    calendar.setTime(startDate)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    val baseDate = calendar.getTime
    val msInDay = 24 * 60 * 60 * 1000L

    // --- Calculate base date at each index
    val rollovers = 0 +: findClockRollover(records)
    val rolloverBaseDates = for (i <- 0 until rollovers.size) yield {
      val t = baseDate.getTime + i * msInDay
      new Date(t)
    }

    // --- Transform function
    def transform(r: RawLogRecord, d: Date): FullLogRecord = {
      val tc = new Timecode(s"${r.gpsTime}:00")
      val msOffset = math.round(tc.getFrames / tc.getFrameRate * 1000L)
      val gpsDate = new Date(d.getTime + msOffset)
      r.asFullLogRecord(gpsDate)
    }

    // --- Apply a base date to each rollover
    // 0 :: indices of rollovers :: records.size - 1
    val rollovers2 = rollovers :+ records.size
    val groupedRecords = for (i <- 0 to rollovers2.size - 2) yield records.slice(rollovers2(i), rollovers2(i + 1))
    val rs = for (i <- 0 until groupedRecords.size) yield {
      val xs = groupedRecords(i)
      val baseDate = rolloverBaseDates(i)
      xs.map(r => transform(r, baseDate))
    }

    rs.flatten
  }

  /**
   * Simple mapping that does not look for GMT rollover.
   *
   * @param simple
   * @return
   */
  def toFullLogRecords(simple: Seq[SimplePosition]): Seq[FullLogRecord] =
    simple.map(s =>
      FullLogRecord(None,
        None,
        None,
        Option(s.latitude),
        Option(s.longitude),
        dateFormat.format(s.time),
        s.time))
}
