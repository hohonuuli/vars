package org.mbari.smith

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

import org.mbari.geometry.Point2D
import vars.annotation.functions.TrimAreaMeasurementsFn

import scala.collection.JavaConverters._
import vars.ToolBox
import vars.annotation.{Observation, VideoFrame}

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-10T14:52:00
 */
object TrimAreaMeasurementApp {

  private[this] val toolBox: ToolBox = new ToolBox

  private[this] val columns = List("videoArchiveName",
    "timecode",
    "recordedDate",
    "conceptName",
    "latitude",
    "longitude",
    "depth",
    "comment",
    "association",
    "imageURL",
    "observer",
    "observationID",
    "associations")

  def apply(videoArchiveName: String, target: File, top: Double, bottom: Double): Unit = {

    val videoFrames = findVideoFrames(videoArchiveName)
    val observations = videoFrames.flatMap(_.getObservations.asScala)

    val firstImageObs = observations.toStream
        .filter(obs => obs.getVideoFrame.getCameraData.getImageReference != null)
        .head

    val (width, height) = imageSize(firstImageObs)

    val minX = 0
    val maxX = width
    val minY = (height * top).toInt
    val maxY = (height * bottom).toInt

    val boundaryPoints = List(new Point2D[Integer](minX, minY),
        new Point2D[Integer](maxX, minY),
        new Point2D[Integer](maxX, maxY),
        new Point2D[Integer](minX, maxY)).asJava

    val fn = new TrimAreaMeasurementsFn(boundaryPoints)

    val annotations = observations.map( obs => {
      val trimmedLinks = obs.getAssociations.asScala.flatMap(a => toOption(fn.apply(a)))
      Annotation.from(obs, trimmedLinks)
    })
    .map(a => FlatAnnotation.from(a))

    // open a file and write data to it
    val writer = new CaseClassTextFileWriter(target, columns)
    for (a <- annotations) {
      writer.write(a)
    }
    writer.close()

  }

  private def findVideoFrames(videoArchiveName: String): Iterable[VideoFrame] = {
    val factory = toolBox.getToolBelt.getAnnotationDAOFactory
    val dao = factory.newVideoArchiveDAO()
    dao.startTransaction()
    val videoArchive = dao.findByName(videoArchiveName)
    val videoFrames = videoArchive.getVideoFrames
    dao.endTransaction()
    dao.close()
    videoFrames.asScala
  }

  private def imageSize(observation: Observation): (Int, Int) = {
    val url = new URL(observation.getVideoFrame.getCameraData.getImageReference)
    val image = ImageIO.read(url)
    (image.getWidth, image.getHeight)
  }


  def main(args: Array[String]) {
    if (args.size != 4) {
      println(
        """
          | Dump out the results but trim the areaMeasurements to fit within the boundaries
          | provided.
          |
          | Usage:
          |   TrimAreaMeasurementApp [videoArchiveName] [file] [topPercent] [bottomPercent]
          |
          | Inputs:
          |   videoArchiveName = The video archive name to process
          |   file = The file to write to
          |   topPercent = The top boundary in the image (1 > % >= 0)
          |   bottomPercent = the bottom boundary in the image (1 >= % > 0)
        """.stripMargin)
      return
    }

    val videoArchiveName = args(0)
    val file = new File(args(1))
    val topPercent = args(2).toDouble
    val bottomPercent = args(3).toDouble

    require(topPercent >= 0 && topPercent < bottomPercent, s"topPercent must be > 0 and < $bottomPercent")
    require(bottomPercent <= 1, "bottomPercent must be less than 1")

    apply(videoArchiveName, file, topPercent, bottomPercent)
  }
}
