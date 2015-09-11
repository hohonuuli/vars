package org.mbari.smith

import java.net.URL
import java.util.Date

import vars.ILink

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-11T16:01:00
 */
case class FlatAnnotation(videoArchiveName: String,
    recordedDate: Option[Date],
    timecode: String,
    imageURL: URL,
    observationID: Long,
    observationDate: Date,
    observer: String,
    conceptName: String,
    latitude: Double,
    longitude: Double,
    depth: Float,
    comment: Option[String] = None,
    x: Option[Double] = None,
    y: Option[Double] = None,
    associations: String = "")


object FlatAnnotation {

  def from(annotation: Annotation, delim: String = ";"): FlatAnnotation = {
    val associations = annotation.links.mkString("")
    FlatAnnotation(annotation.videoArchiveName,
      annotation.recordedDate,
      annotation.timecode,
      annotation.imageURL,
      annotation.observationID,
      annotation.observationDate,
      annotation.observer,
      annotation.conceptName,
      annotation.latitude,
      annotation.longitude,
      annotation.depth,
      annotation.comment,
      annotation.x,
      annotation.y,
      associations)
  }
}