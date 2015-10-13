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
class ColumnsParser(url: URL, delimiter: String = "\t") {

  def this(file: File) = this(file.toURI.toURL)
  def this(file: File, delimiter: String) = this(file.toURI.toURL, delimiter)

  lazy val columns: List[String] = {
    val source = Source.fromURL(url)
    val notHeaderLines = source.getLines().filter(s => !s.startsWith("#"))
    val cs = if (!notHeaderLines.hasNext) Nil
    else {
      val columnLine = notHeaderLines.next()
      columnLine.split(delimiter).toList
    }
    source.close()
    cs
  }

}