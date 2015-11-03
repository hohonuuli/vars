package org.mbari.vars.integration

import java.io.{InputStreamReader, BufferedReader}
import java.nio.charset.{CodingErrorAction, Charset}

import scala.collection.mutable

/**
  * Created by rachelorange on 11/2/15.
  */
object HURLParseDemo {

  def main(args: Array[String]) {
    val url = getClass.getResource("/org/mbari/vars/integration/genericmergedata_hurlsmessedupformat.csv")
    val inputStream = url.openStream()
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    val reader = new BufferedReader(new InputStreamReader(inputStream, decoder))
    var line: String = null
    val lines = new mutable.ArrayBuffer[String]
    do {
      line = reader.readLine()
      if (line != null) {
        lines += line
      }
    } while (line != null)
    inputStream.close()
    val lineList = lines.toList
    println(lineList)

    val a = lines.filter(s => !s.startsWith("#"))
      .head
      .split(",")
      .map(_.trim)
      .toList
    println(a)
  }

}
