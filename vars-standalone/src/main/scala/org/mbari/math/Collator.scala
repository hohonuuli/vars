package org.mbari.math

import org.slf4j.LoggerFactory

import scala.util.control.Breaks._

/**
 * This performs a nearest neighbor coallation. You can coallate
 * distinct types (but you have to map them to some double value).
 * You need to provide an acceptable tolerance. If no neighbor is
 * found within that tolerance a value will be assigned a neighbor of
 * [[None]]
 *
 * @author Brian Schlining
 * @since 2015-02-25T14:36:00
 */
object Collator {

  private[this] val log = LoggerFactory.getLogger(getClass)


  def apply[A: Numeric, B: Numeric](d0: Iterable[A], d1: Iterable[B], tolerance: Double): Seq[(A, Option[B])] = {
    val numericA = implicitly[Numeric[A]]
    val numericB = implicitly[Numeric[B]]

    def fa(v: A) = numericA.toDouble(v)
    def fb(v: B) = numericB.toDouble(v)

    apply(d0, fa, d1, fb, tolerance)

  }


  // TODO: Switch to using binary search
  def apply[A, B](d0: Iterable[A], fn0: A => Double,
                  d1: Iterable[B], fn1: B => Double,
                  tolerance: Double): Seq[(A, Option[B])] = {

    val list0 = d0.toSeq.sortBy(fn0) // sorted d0
    val list1 = d1.toSeq.sortBy(fn1) // sorted d1

    val vals0 = list0.map(fn0)  // transformed d0 in same order as list0
    val vals1 = list1.map(fn1)  // transformed d1 in same order as list1

    var i = 0
    val temp = for {
      (val0, idx) <- vals0.zipWithIndex
    } yield {
      val t0 = val0
      var dtBest = tolerance

      var goodDatum: Option[B] = None
      breakable {
        for (row <- i until list1.size) {
          val val1 = vals1(row)
          val t1 = val1
          val dt = math.abs(t0 - t1)
          if (dt <= dtBest) {
            dtBest = dt
            i = row
            goodDatum = Option(list1(i))
          }
          else if ((dt > dtBest) && (t1 > t0)) {
            break()
          }
        }
      }

      goodDatum match {
        case Some(v) => list0(idx) -> Option(v)
        case None => {
          log.debug(s"No record was found to match ${list0(idx)}")
          list0(idx) -> None
        }
      }
    }

    temp // return
  }

}


