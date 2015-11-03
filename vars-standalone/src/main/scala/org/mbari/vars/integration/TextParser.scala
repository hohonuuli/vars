package org.mbari.vars.integration

import java.io.{InputStreamReader, BufferedReader, FileInputStream, File}
import java.net.URL
import java.nio.charset.{CodingErrorAction, Charset}


import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.control.NonFatal
import scala.collection.mutable

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-10-12T15:05:00
 */
class TextParser(val url: URL, delimiter: String = "\t", encoding: String = "UTF-8") {

  private[this] val log = LoggerFactory.getLogger(getClass)

  def this(file: File) = this(file.toURI.toURL)
  def this(file: File, delimiter: String) = this(file.toURI.toURL, delimiter)

  //lazy val columns = new ColumnsParser(url, delimiter).columns

  lazy val  columns: List[String] =  lines.filter(s => !s.startsWith("#"))
      .head
      .split(delimiter)
      .map(_.trim)
      .toList


  lazy val rows: List[String] = lines.filter(s => !s.startsWith("#"))
      .tail // 1st line is the columns header

  lazy val header: List[String] = lines.filter(s => s.startsWith("#"))

  lazy val values: Array[Array[String]] = rows.map(r => r.split(delimiter).map(_.trim))
      .toArray

  lazy val lines: List[String] = {
    try {
      val inputStream = url.openStream()
      val decoder = Charset.forName(encoding).newDecoder()
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
      lines.toList
    }
    catch {
      case (NonFatal(e)) => log.error(s"Failed to read $url", e); Nil
    }
  }

}