package org.mbari.vars.arctic

import org.junit.runner.RunWith
import org.mbari.movie.Timecode
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}
import vars.shared.ui.GlobalLookup

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-26T11:21:00
 */
@RunWith(classOf[JUnitRunner])
class MergeUtilitiesSpec extends FlatSpec with Matchers {


  private[this] val testRecords = {
    val times = (0 until 24 by 4).map(h => new Timecode(f"$h%02d:00:00:00").toString.substring(0, 8))
    times.map(t => RawLogRecord(None, None, None, None, None, t))
  }

  "findClockRollover" should "correctly locate  a single midnight rollovers" in {
    val xs = testRecords ++ testRecords
    val rs = MergeUtilities.findClockRollover(xs)
    rs.size should be(1)
    rs(0) should be (testRecords.size)
  }

  "findClockRollover" should "correctly locate multiple midnight rollover" in {
    val xs = testRecords ++ testRecords ++ testRecords
    val rs = MergeUtilities.findClockRollover(xs)
    rs.size should be(2)
    rs(0) should be (testRecords.size)
    rs(1) should be (testRecords.size * 2)
  }

  "toFullLogRecords" should "transform RawLogRecords with no rollover correctly" in {
    val now = GlobalLookup.DATE_FORMAT_UTC.parse("2015-02-26 00:00:00")
    val fullRecords = MergeUtilities.toFullLogRecords(testRecords, now)
    fullRecords.size should be (testRecords.size)
    for (i <- 0 until testRecords.size) {
      val hour = i * 4
      fullRecords(i).gpsDate.getTime should be (now.getTime + hour * 60 * 60 * 1000l)
    }
  }

  "toFullLogRecords" should "transform RawLogRecords with rollover correctly" in {
    val now = GlobalLookup.DATE_FORMAT_UTC.parse("2015-02-26 00:00:00")
    val xs = testRecords ++ testRecords
    val fullRecords = MergeUtilities.toFullLogRecords(xs, now)
    fullRecords.size should be (xs.size)
    for (i <- 0 until xs.size) {
      val hour = i * 4
      fullRecords(i).gpsDate.getTime should be (now.getTime + hour * 60 * 60 * 1000l)
    }
  }

  "toFullLogRecords" should "ignore hour, minutes and seconds in start date" in {
    val nowNoHours = GlobalLookup.DATE_FORMAT_UTC.parse("2015-02-26 00:00:00")
    val expected = MergeUtilities.toFullLogRecords(testRecords, nowNoHours)

    val now = GlobalLookup.DATE_FORMAT_UTC.parse("2015-02-26 12:11:31")
    val actual = MergeUtilities.toFullLogRecords(testRecords, now)

    actual.size should be (expected.size)
    actual should be (expected)
  }

  val lines = """
  |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:20,320.11,152,7050.0622,13506.9025,00.2
  |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:21,320.11,152,7050.0622,13506.9025,00.2
  |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:22,319.66,152,7050.0622,13506.9015,00.2
  """.stripMargin


  "toFullLogRecords" should "munge dates correctly" in {
    val df = GlobalLookup.DATE_FORMAT_UTC
    val startDate = df.parse("2012-09-28 12:34:56")
    val records = MergeUtilities.toFullLogRecords(
      lines.split('\n').flatMap(CSVLogReader.parseLine(_)), startDate)

    records(0).gpsDate should be(df.parse("2012-09-28 23:21:20"))
    records(1).gpsDate should be(df.parse("2012-09-28 23:21:21"))
    records(2).gpsDate should be(df.parse("2012-09-28 23:21:22"))
  }
}
