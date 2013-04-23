package org.mbari.smith

import java.text.SimpleDateFormat
import scala.collection.JavaConverters._
import java.lang.{Double => JDouble}
import java.net.URL

import scala.util.{Failure, Success, Try}
import vars.annotation.ui.imagepanel.AreaMeasurement
import vars.annotation.Association
import java.io.{FileWriter, BufferedWriter, File}

/**
 *
 * @author Brian Schlining
 * @since 2013-04-22
 */
object PointsApp {

  private[this] val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'")

  def read(videoArchiveName: String,
      cameraHeight: Double,
      alpha: Double,
      beta: Double,
      theta: Double): Stream[AMPoint[Association, JDouble]] = {

    val videoFrames = CoverageEstimator.fetchAnnotations(videoArchiveName)

    val associations = videoFrames.toStream.map { vf =>
      for {
        obs <- vf.getObservations.asScala
        ass <- obs.getAssociations.asScala
        if (ass.getLinkName == AreaMeasurement.AREA_MEASUREMENT_LINKNAME)
      } yield ass
    } flatten

    val camera = Camera.fromRadians(cameraHeight, alpha, beta, theta)
    val url = {
      val vf = videoFrames.find { v =>
        Try(v.getCameraData.getImageReference) match {
          case Success(i) => i != null
          case Failure(e) => false
        }
      }
      vf.map(v => new URL(v.getCameraData.getImageReference)).get
    }

    val imageDimensions = CanadianGrid.imageDimensions(url)
    val cameraView = CameraView(camera, imageDimensions._1, imageDimensions._2)

    associations.map(RawPoint.centerOfMass(_, cameraView)).flatten
  }

  def write(points: Stream[AMPoint[Association, JDouble]], file: File) {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write("VideoArchiveName\tTimecode\tRecordedDate\tConceptName\tLatitude\tLongitude\tDepth\tXOffset\tYOffset\tComment\n")
    for (p <- points) {
      val obs = p.link.get.getObservation
      val vf = obs.getVideoFrame
      val va = vf.getVideoArchive

      val d = if (vf.getRecordedDate == null) "" else dateFormat.format(vf.getRecordedDate)


      val (lat, lon, depth) = Option(vf.getPhysicalData) match {
        case None => (Double.NaN, Double.NaN, Double.NaN)
        case Some(p) => (p.getLatitude, p.getLongitude, p.getDepth)
      }

      val line = s"${va.getName}\t${vf.getTimecode}\t$d\t${obs.getConceptName}\t$lat\t$lon\t$depth\t${p.point.getX}\t${p.point.getY}\t${p.areaMeasurement.getComment}\n"
      writer.write(line)
    }
    writer.close()
  }





}
