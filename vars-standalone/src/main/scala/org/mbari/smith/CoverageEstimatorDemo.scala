package org.mbari.smith

/**
 * 
 * @author Brian Schlining
 * @since 2013-01-31
 */
object CoverageEstimatorDemo extends App {

  val camera = Camera.fromDegrees(112, 34, 44, 33, Option("cm"))
  val actualAreas = CoverageEstimator("Pulse 60 Rover Transect", camera)
  actualAreas.foreach { a =>
    println(a.videoFrame.getTimecode + "\t" + a.fovArea  + "\t" + a.detritalArea)
  }

}
