package org.mbari.vars.arctic

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers, PrivateMethodTester}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-25T16:58:00
 */
@RunWith(classOf[JUnitRunner])
class CSVLogReaderSpec extends FlatSpec with Matchers with PrivateMethodTester {

  val parseLatLon = PrivateMethod[Option[Double]]('parseLatLon)
  val parseFloat = PrivateMethod[Option[Float]]('parseFloat)
  val lineToLogRecord = PrivateMethod[Option[RawLogRecord]]('lineToLogRecord)


  "CVSLogReader" should "parse that funky position format" in {

    val actual = CSVLogReader invokePrivate parseLatLon("13506.8604")
    val expected = Option(135 + 6.8604 / 60D)

    actual should not be (None)
    actual.get should be(expected.get +- 0.0001)

  }

  it should "parse floats" in {
    val v = "2.431100"
    val expected = v.toFloat
    val actual = CSVLogReader invokePrivate parseFloat(v)
    actual should not be (None)
    actual.get should be (expected +- 0.000001F)
  }

  it should "return None when parsing invalid floats" in {
    val v = "2.431100"
    val bad = "a" + v
    val actual = CSVLogReader invokePrivate parseFloat(bad)
    actual should be(None)
  }

  it should "parse a logs line correctly" in {
    val line = "$MINIROV_SHIP,26.785400,-1.280400,136.354000,17:25:02,33.323200,00:22:24,343.22,144,7050.0172,13506.9040,00.1"
    val actual = CSVLogReader invokePrivate lineToLogRecord(line)
    actual should not be (None)
    val r = actual.get
    r.salinity should not be (None)
    r.salinity.get should be (33.323200F +- 0.000001F)
    r.temperature should not be (None)
    r.temperature.get should be (-1.280400F +- 0.000001F)
    r.depth should not be (None)
    r.depth.get should be (136.354000F +- 0.000001F)
    val lat = 70 + 50.0172 / 60D
    r.latitude should not be (None)
    r.latitude.get should be (lat +- 0.0001)
    r.longitude should not be (None)
    val lon = 135 + 6.9040 / 60D
    r.longitude.get should be (lon +- 0.0001)
    r.gpsTime should be ("00:22:24")
  }

  it should "return None when parsing an invalid line" in {
    val line = "$EVENT,36,9/28/2012 5:52:31 PM,#ME,cobble"
    val actual = CSVLogReader invokePrivate lineToLogRecord(line)
    actual should be (None)
  }

  val lines =
    """
      |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:20,320.11,152,7050.0622,13506.9025,00.2
      |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:20,320.11,152,7050.0622,13506.9025,00.2
      |$MINIROV_SHIP,26.390500,4.158900,1.367000,16:23:57,27.625300,23:21:22,319.66,152,7050.0622,13506.9015,00.2
      |$MINIROV_SHIP,26.385900,4.157500,1.354000,16:24:00,27.621600,23:21:22,319.66,152,7050.0622,13506.9015,00.2
      |$MINIROV_SHIP,26.385900,4.157500,1.354000,16:24:00,27.621600,23:21:24,320.18,152,7050.0618,13506.9022,00.2
      |$MINIROV_SHIP,26.385900,4.157500,1.354000,16:24:00,27.621600,23:21:24,320.18,152,7050.0618,13506.9022,00.2
      |$MINIROV_SHIP,26.386500,4.157400,1.365000,16:24:03,27.622200,23:21:26,320.98,153,7050.0616,13506.9018,00.3
      |$MINIROV_SHIP,26.386500,4.157400,1.365000,16:24:03,27.622200,23:21:26,320.98,153,7050.0616,13506.9018,00.3
      |$MINIROV_SHIP,26.386500,4.157400,1.365000,16:24:03,27.622200,23:21:28,320.93,153,7050.0618,13506.9011,00.3
      |$MINIROV_SHIP,26.390100,4.159300,1.329000,16:24:06,27.624300,23:21:28,320.93,153,7050.0618,13506.9011,00.3
      |$MINIROV_SHIP,26.390100,4.159300,1.329000,16:24:06,27.624300,23:21:30,320.79,152,7050.0620,13506.9018,00.1
      |$MINIROV_SHIP,26.389600,4.159400,1.446000,16:24:08,27.624200,23:21:30,320.79,152,7050.0620,13506.9018,00.1
      |$MINIROV_SHIP,26.389600,4.159400,1.446000,16:24:08,27.624200,23:21:32,321.17,152,7050.0615,13506.9015,00.1
      |$MINIROV_SHIP,26.389400,4.160900,1.365000,16:24:10,27.622700,23:21:32,321.17,152,7050.0615,13506.9015,00.1
      |$MINIROV_SHIP,26.389400,4.160900,1.365000,16:24:10,27.622700,23:21:34,321.68,152,7050.0612,13506.9010,00.3
      |$MINIROV_SHIP,26.389200,4.160100,1.471000,16:24:12,27.623600,23:21:34,321.68,152,7050.0612,13506.9010,00.3
      |$MINIROV_SHIP,26.389200,4.160100,1.471000,16:24:12,27.623600,23:21:36,322.05,153,7050.0613,13506.9012,00.3
      |$MINIROV_SHIP,26.391000,4.160600,1.439000,16:24:14,27.624500,23:21:36,322.05,153,7050.0613,13506.9012,00.3
      |$MINIROV_SHIP,26.391000,4.160600,1.439000,16:24:14,27.624500,23:21:38,322.32,153,7050.0614,13506.9008,00.2
      |$MINIROV_SHIP,26.388900,4.161300,1.352000,16:24:16,27.622100,23:21:38,322.32,153,7050.0614,13506.9008,00.2
      |$MINIROV_SHIP,26.388900,4.161300,1.352000,16:24:16,27.622100,23:21:40,322.41,153,7050.0611,13506.9001,00.2
      |$MINIROV_SHIP,26.388900,4.161300,1.352000,16:24:16,27.622100,23:21:40,322.41,153,7050.0611,13506.9001,00.2
      |$MINIROV_SHIP,26.392500,4.161300,1.366000,16:24:19,27.625500,23:21:42,322.42,152,7050.0609,13506.9009,00.2
      |$MINIROV_SHIP,26.392500,4.161300,1.366000,16:24:19,27.625500,23:21:42,322.42,152,7050.0609,13506.9009,00.2
      |$MINIROV_SHIP,26.392500,4.161300,1.366000,16:24:19,27.625500,23:21:44,323.11,152,7050.0610,13506.9006,00.1
      |$MINIROV_SHIP,26.389500,4.160200,1.428000,16:24:22,27.623500,23:21:44,323.11,152,7050.0610,13506.9006,00.1
      |$MINIROV_SHIP,26.389500,4.160200,1.428000,16:24:22,27.623500,23:21:46,323.12,153,7050.0609,13506.8997,00.1
      |$MINIROV_SHIP,26.389500,4.160200,1.428000,16:24:22,27.623500,23:21:46,323.12,153,7050.0609,13506.8997,00.1
      |$MINIROV_SHIP,26.388500,4.160300,1.459000,16:24:25,27.622800,23:21:48,323.34,154,7050.0604,13506.9002,00.3
      |$MINIROV_SHIP,26.388500,4.160300,1.459000,16:24:25,27.622800,23:21:48,323.34,154,7050.0604,13506.9002,00.3
    """.stripMargin


  it should "process multiple lines correctly" in {
    val records = lines.split('\n').map(l => CSVLogReader invokePrivate lineToLogRecord(l))
    records.size should be (32)                // 32 lines total
    records.filter(_.isDefined) should be (29) // 2 lines are empty and should map to null
  }



}
