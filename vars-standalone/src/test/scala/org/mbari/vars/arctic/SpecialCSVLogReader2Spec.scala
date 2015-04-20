package org.mbari.vars.arctic

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-16T11:15:00
 */
@RunWith(classOf[JUnitRunner])
class SpecialCSVLogReader2Spec extends FlatSpec with Matchers {

  "SpecialCSVLogReader2" should "parse a line correctly" in {



    val line = "ROV DIVE#1,,90,164424,A,7027.0642,N,13633.4136,W,00.1,290,300910,034.4,E,D*27,periscope for sonar"
    val expectedlat = 70 + 27.0624 / 60D
    val expectedLon = -(136 + 33.4136D / 60D)
    val r = SpecialCSVLogReader2.parseLine(line)
    r should not be (None)
    val s = r.get
    s.latitude should not be (None)
    s.latitude.get should be (expectedlat +- 0.001D)
    s.longitude should not be (None)
    s.longitude.get should be (expectedLon +- 0.001D)
    s.depth should not be (None)
    s.depth.get should be (90F +- 0.001F)
    s.gpsTime should be ("16:44:24")

  }

}
