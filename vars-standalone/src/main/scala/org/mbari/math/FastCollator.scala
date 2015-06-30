package org.mbari.math

import org.slf4j.LoggerFactory

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-03-02T16:20:00
 */
object FastCollator {

  private[this] val log = LoggerFactory.getLogger(getClass)


  def apply[A: Numeric, B: Numeric](d0: Iterable[A], d1: Iterable[B], tolerance: Double): Seq[(A, Option[B])] = {
    val numericA = implicitly[Numeric[A]]
    val numericB = implicitly[Numeric[B]]

    def fa(v: A) = numericA.toDouble(v)
    def fb(v: B) = numericB.toDouble(v)

    apply(d0, fa, d1, fb, tolerance)

  }

  def apply[A, B](d0: Iterable[A], fn0: A => Double,
                  d1: Iterable[B], fn1: B => Double,
                  tolerance: Double): Seq[(A, Option[B])] = {

    val list0 = d0.toSeq.sortBy(fn0) // sorted d0
    val list1 = d1.toSeq.sortBy(fn1) // sorted d1

    val vals0 = list0.map(fn0).toArray  // transformed d0 in same order as list0
    val vals1 = list1.map(fn1).toArray  // transformed d1 in same order as list1

    val tmp = for {
      (val0, idx0) <- vals0.zipWithIndex
    } yield {
      val idx1 = Matlib.near(vals1, val0)
      val val1 = vals1(idx1)
      val nearest = if (math.abs(val0 - val1) <= tolerance) Option(list1(idx1))
          else None
      list0(idx0) -> nearest
    }
    tmp.toSeq
  }

}
