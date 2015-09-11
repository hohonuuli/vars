package org.mbari.smith

import java.io.{Writer, FileWriter, BufferedWriter, File}
import java.time.Instant

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-11T10:21:00
 */
class CaseClassTextFileWriter(file: File, fields: List[String], delimiter: String = "\t") {

  private lazy val writer = {
    val w = new BufferedWriter(new FileWriter(file))
    writeHeader(w: Writer)
    w
  }
  private[this] var closed: Boolean = false

  private[this] lazy val toStringFn = new ClassToDelimStringFn(fields, delimiter)

  private def writeHeader(w: Writer): Unit = {
    w.write(s"# Creation Date: ${Instant.now}")
    w.write(s"#${fields.mkString(delimiter)}")
  }

  def write[A : ru.TypeTag : ClassTag](cc: A): Unit = writer.write(toStringFn(cc))

  def close(): Unit = writer.close()

  def isClosed: Boolean = closed

}
