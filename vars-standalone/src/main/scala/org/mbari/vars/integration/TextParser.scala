package org.mbari.vars.integration

import java.io.File
import java.net.URL


import scala.io.Source

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-10-12T15:05:00
 */
class TextParser(val url: URL, delimiter: String = "\t") {

  def this(file: File) = this(file.toURI.toURL)
  def this(file: File, delimiter: String) = this(file.toURI.toURL, delimiter)

  lazy val columns = new ColumnsParser(url, delimiter).columns

  lazy val rows: List[String] = {
    val source = Source.fromURL(url)
    val lines = source.getLines.filter(s => !s.startsWith("#")).toList
    source.close()
    lines.tail // 1st line is the columns header
  }

  lazy val header: List[String] = {
    val source = Source.fromURL(url)
    val lines = source.getLines.filter(s => s.startsWith("#")).toList
    source.close()
    lines
  }

  lazy val values: Array[Array[String]] =
    rows.map(r => r.split(delimiter)).toArray

}