package org.mbari.math

import java.util.Date

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-02-25T15:02:00
 */
@RunWith(classOf[JUnitRunner])
class CollatorSpec extends FlatSpec with Matchers {


  "Collator" should "collate doubles" in {

    val a = Array[Double](10, 20, 30, 40, 50, 60)
    val b = a.map(_ + 4)

    def fn(v: Double) = v

    val actual = Collator(a, fn, b, fn, 5).toArray

    val expected = a.zip(b.map(Option(_)))

    actual should be (expected)

  }

  it should "collate mixed types" in {

    val a = Array[Int](1, 3, 4, 5, 6, 20, 21, 22, 26)
    val b = Array[Double](1, 7, 18, 21, 25)

    def fnA(v: Int) = v.toDouble
    def fnB(v: Double) = v

    val actual = Collator(a, fnA, b, fnB, 1).toArray

    val expected = Array[(Int, Option[Double])](1 -> Some(1D),
      3 -> None,
      4 -> None,
      5 -> None,
      6 -> Some(7D),
      20 -> Some(21D),
      21 -> Some(21D),
      22 -> Some(21D),
      26 -> Some(25D))

    actual should be (expected)

  }

  it should "collate mixed types with large offsets" in {

    val a = Array[Int](1, 3, 4, 5, 6, 20, 21, 22, 26)
    val b = Array[Double](1, 7, 18, 21, 25, 40)

    def fnA(v: Int) = v.toDouble
    def fnB(v: Double) = v

    val actual = Collator(a, fnA, b, fnB, 5).toArray

    val expected = Array[(Int, Option[Double])](1 -> Some(1D),
      3 -> Some(1D),
      4 -> Some(7D),
      5 -> Some(7D),
      6 -> Some(7D),
      20 -> Some(21D),
      21 -> Some(21D),
      22 -> Some(21D),
      26 -> Some(25D))

    actual should be (expected)

  }

  it should "coallate numeric values" in {
    val a = Array(10, 20, 30, 40, 50, 60)
    val b = a.map(_.toDouble + 4)

    // Coallating numeric values does not require conversion functions
    val actual = Collator(a, b, 5).toArray

    val expected = a.zip(b.map(Option(_)))

    actual should be (expected)
  }

  it should "collate non-numeric objects" in {
    val dt = 24 * 60 * 60 * 1000L // 1 day in ms
    val d0 = (1424910349953L to (1424910349953L + dt * 10) by dt).map(t => TestBean(new Date(t)))
    val d1 = d0.tail
    def fn(t: TestBean) = t.date.getTime.toDouble
    val actual = Collator(d0, fn, d1, fn, dt / 3)
    actual.head._2 should be(None)
  }

}

case class TestBean(date: Date)