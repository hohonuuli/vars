package org.mbari.smith

import java.net.URL
import java.util.Date
import scala.collection.JavaConverters._
import vars.{LinkBean, ILink}
import vars.annotation.Observation

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-10T14:35:00
 */
case class Annotation(videoArchiveName: String,
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
    links: Iterable[ILink] = Nil)

object Annotation {

  def from(observation: Observation): Annotation = {
    val links = observation.getAssociations
        .asScala
        .map(a => new LinkBean(a.getLinkName, a.getToConcept, a.getLinkValue))
    from(observation, links)
  }

  def from(observation: Observation, links: Iterable[ILink]): Annotation = {
    val videoFrame = observation.getVideoFrame
    require(videoFrame != null, "VideoFrame is null! That's not allowed.")
    val videoArchive = videoFrame.getVideoArchive
    require(videoArchive != null, "VideoArchive is null! That's not allowed.")

    Annotation(videoArchive.getName,
      Option(videoFrame.getRecordedDate),
      videoFrame.getTimecode,
      new URL(videoFrame.getCameraData.getImageReference),
      observation.getPrimaryKey.asInstanceOf[Long],
      observation.getObservationDate,
      observation.getObserver,
      observation.getConceptName,
      videoFrame.getPhysicalData.getLatitude,
      videoFrame.getPhysicalData.getLongitude,
      videoFrame.getPhysicalData.getDepth,
      Option(observation.getNotes),
      Option(observation.getX),
      Option(observation.getY),
      links)
  }
}