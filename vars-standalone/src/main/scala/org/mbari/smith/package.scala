package org.mbari

import java.util.Optional

/**
 * This package contains utilities used by Ken Smith's lab for analyzing VARS data
 *
 * @author Brian Schlining
 * @since 2012-11-27
 */
package object smith {

  /**
   * Implicit conversion of a java Optional to a scala Option
   * @param opt
   * @tparam A
   * @return
   */
  implicit def toOption[A](opt: Optional[A]): Option[A] = if (opt.isPresent) Some(opt.get()) else None

}
