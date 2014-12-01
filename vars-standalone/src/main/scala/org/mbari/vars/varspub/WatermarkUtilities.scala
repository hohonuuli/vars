package org.mbari.vars.varspub

import java.awt.image.BufferedImage

import org.imgscalr.Scalr

import scala.math._

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-21T14:01:00
 */
object WatermarkUtilities {

  def scaleOverlay(scaleValue: Double, overlay: BufferedImage): BufferedImage = {
    val w = ceil(overlay.getWidth * scaleValue).toInt
    val h = ceil(overlay.getHeight * scaleValue).toInt
    Scalr.resize(overlay, Scalr.Method.ULTRA_QUALITY, w)
  }

}
