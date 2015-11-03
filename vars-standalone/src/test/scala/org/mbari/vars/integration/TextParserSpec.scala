package org.mbari.vars.integration

import java.time.Instant

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by rachelorange on 11/2/15.
  */
@RunWith(classOf[JUnitRunner])
class TextParserSpec  extends FlatSpec with Matchers {



  "TextParser" should "parse correctly" in {
    val url = getClass.getResource("/org/mbari/vars/integration/genericmergedata.csv")
    val parser = new TextParser(url, ",")
    parser.columns.size should be (7)
    parser.columns should contain theSameElementsInOrderAs Vector("salinity", "temperature", "depth",
      "latitude", "longitude", "date", "oxygen")
    parser.rows.size should be (20)
    parser.header.isEmpty should be (true)
  }

  it should "parser HURL's messed up CSV files" in {
    val url = getClass.getResource("/org/mbari/vars/integration/genericmergedata_hurlsmessedupformat.csv")
    val parser = new TextParser(url, ",", "Cp1252")
    parser.columns.size should be (6)
    parser.columns should contain theSameElementsInOrderAs Vector("Depth", "Temperature", "Oxygen",
      "Salinity", "Nitrogen", "Date")
    parser.rows.size should be (39)
    parser.header.isEmpty should be (true)
    println(parser.values(0)(5))
  }

}
