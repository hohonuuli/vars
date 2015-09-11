package org.mbari.smith

import java.time.Instant
import java.util.Date

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}


/**
 * Convert the contents of a case class into a delimited string. Helpful
 * for dumping case classes to a text file.
 *
 * @param fields A List of the fields to dump out. The strings should match
 *               the fields to dump exactly. If no match is found an empty
 *               _cell_ is added to the string.
 * @param delimiter The delimiter used to separate fields is the string. The
 *                  default is a tab
 *
 * @author Brian Schlining
 * @since 2015-09-11T10:39:00
 */
class ClassToDelimStringFn(fields: List[String], delimiter: String = "\t") {

  /**
   * Dump object to a string.
   * @param obj The object to stringify
   * @tparam A The type of the object
   * @return A delimited string
   */
  def apply[A : ru.TypeTag : ClassTag](obj: A): String = {

    val sb = new StringBuilder

    // Build a map of field names -> symbol so we can dynamically call the fields
    val methodSymbols = ru.typeOf[A].members.collect{ case m: ru.MethodSymbol if m.isGetter => m }
    val symbolMap = methodSymbols.map(s => s.name.toString -> s).toMap

    // Use reflection to extract the values
    val mirror = ru.runtimeMirror(obj.getClass.getClassLoader)
    val im = mirror.reflect(obj)
    for (i <- fields.indices) {
      val f = fields(i)
      val d = if (i == fields.size - 1) "" else delimiter
      symbolMap.get(f) match {
        case Some(x) =>
          val value = im.reflectField(x).get
          // Apply special formatting to certain types (like date)
          val s = stringValue(value)
          sb.append(s)
        case None => // Do nothing
      }
      sb.append(d)
    }
    sb.toString()
  }

  private def stringValue(obj: Any): String = obj match {
    case Some(x) => stringValue(x) // Extract options
    case None => ""
    case d: Date => Instant.ofEpochMilli(d.getTime).toString
    case _ => obj.toString
  }

}
