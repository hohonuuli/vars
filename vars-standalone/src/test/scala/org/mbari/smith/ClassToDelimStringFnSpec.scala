package org.mbari.smith

import java.net.URL
import java.util.Date

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-11T14:14:00
 */
@RunWith(classOf[JUnitRunner])
class ClassToDelimStringFnSpec extends FlatSpec with Matchers {

  case class Foo(recordedDate: Date, bar: String, observer: String, depth: Float, opt: Option[String] = None)


  "ClassToDelimStringFn" should "dump a single field correctly" in {
    val fn = new ClassToDelimStringFn(List("bar"), ",")
    val foo = Foo(new Date, "wow", "bob", 100)
    val s = fn(foo)
    s should be ("wow")
  }

  it should "dump a date field as iso8601" in {
    val fn = new ClassToDelimStringFn(List("recordedDate"), ",")
    val foo = Foo( new Date(0L), "wow", "bob", 101)
    val s = fn(foo)
    s should be ("1970-01-01T00:00:00Z")
  }

  it should "delimit fields correctly" in {
    val fn = new ClassToDelimStringFn(List("bar", "observer"), ",")
    val foo = Foo( new Date(0L), "wow", "bob", 101)
    val s = fn(foo)
    s should be ("wow,bob")
  }

  it should "extract values from Option" in {
    val fn = new ClassToDelimStringFn(List("opt"), ",")
    val foo = Foo(new Date, "", "", 0, Some("wow"))
    val s = fn(foo)
    s should be ("wow")
  }

  it should "handle different types" in {
    val fn = new ClassToDelimStringFn(List("depth", "observer", "recordedDate"), ",")
    val foo = Foo(new Date(0L), "wow", "bob", 101)
    val ann = Annotation("", Option(new Date(0)), "00:00:00:00",
      new URL("http://www.mbari.org"), 0, new Date, "bob", "Grimpoteuthis",
      0, 0, 101)

    val sFoo = fn(foo)
    val sAnn = fn(ann)

    sFoo should be (sAnn)
  }

}
