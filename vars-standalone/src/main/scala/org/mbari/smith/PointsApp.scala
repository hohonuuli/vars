package org.mbari.smith

import java.text.SimpleDateFormat
import scala.collection.JavaConverters._
import java.lang.{Double => JDouble}
import java.net.URL

import scala.util.{Failure, Success, Try}
import vars.annotation.ui.imagepanel.AreaMeasurement
import vars.annotation.Association
import java.io.{FileWriter, BufferedWriter, File}
import org.mbari.geometry.Point2D

/**
 *
 * @author Brian Schlining
 * @since 2013-04-22
 */
object PointsApp {

  private[this] val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'")

  /**
   *
   * @param videoArchiveName
   * @param cameraHeight
   * @param alpha
   * @param beta
   * @param theta
   * @return A tuple of (distance from center of polygon to camera, distance from center of
   *         polygon to bottom center of image, distance from bottom center of image to farthest
   *         vertex in polygon)
   */
  def read(videoArchiveName: String,
      cameraHeight: Double,
      alpha: Double,
      beta: Double,
      theta: Double): Iterable[(AMPoint[Association, JDouble], AMPoint[Association, JDouble], AMPoint[Association, JDouble])] = {

    val videoFrames = CoverageEstimator.fetchAnnotations(videoArchiveName)

    val associations = videoFrames.map { vf =>
      for {
        obs <- vf.getObservations.asScala
        ass <- obs.getAssociations.asScala
        if (ass.getLinkName == AreaMeasurement.AREA_MEASUREMENT_LINKNAME)
      } yield ass
    } flatten

    val camera = Camera.fromRadians(cameraHeight, alpha, beta, theta)

    // Assume all images are the same size, just get the first one
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

    // Distances of polygon centers from Camera
    val pointsCC = associations.map(RawPoint.centerOfMass(_, cameraView)).flatten.toIndexedSeq

    // Distance of polygon centers from bottom center of image
    val imageBottomCenter = new Pixel(camera,
          imageDimensions._1,
          imageDimensions._2,
          math.round(imageDimensions._1 / 2D).toInt,
          imageDimensions._2)
    val pointsCI = pointsCC.map { p =>
      val newPoint = new Point2D[JDouble](p.point.getX, p.point.getY - imageBottomCenter.yDistance)
      BasicPoint(p.link, p.areaMeasurement, newPoint, cameraView)
    } toIndexedSeq

    // Distance of farthest point in polygon from bottom center of image
    val pointsFI = associations.map(RawPoint.farthestPointFrom(_, cameraView, imageBottomCenter)).flatten.toIndexedSeq

    for (i <- 0 until pointsCC.size) yield (pointsCC(i), pointsCI(i), pointsFI(i))
  }

  def write(points: Iterable[(AMPoint[Association, JDouble], AMPoint[Association, JDouble], AMPoint[Association, JDouble])], file: File) {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write("VideoArchiveName\tTimecode\tRecordedDate\tConceptName\tLatitude\tLongitude\tDepth\tXOffsetCenterToCamera\tYOffsetCenterToCamera\tXOffsetCenterToImageBC\tYOffsetCenterToImageBC\tXOffsetFarthestToImageBC\tYOffsetFarthestToImageBC\tComment\tAssociation\tImage\n")
    for (p <- points) {
      require((p._1.link == p._2.link) && (p._2.link == p._3.link))
      val obs = p._1.link.get.getObservation
      val vf = obs.getVideoFrame
      val va = vf.getVideoArchive

      val d = if (vf.getRecordedDate == null) "" else dateFormat.format(vf.getRecordedDate)
      val img = if (vf.getCameraData == null) "" else vf.getCameraData.getImageReference


      val (lat, lon, depth) = Option(vf.getPhysicalData) match {
        case None => (Double.NaN, Double.NaN, Double.NaN)
        case Some(p) => (p.getLatitude, p.getLongitude, p.getDepth)
      }

      val line = s"${va.getName}\t${vf.getTimecode}\t$d\t${obs.getConceptName}\t$lat\t$lon\t$depth\t${p._1.point.getX}\t${p._1.point.getY}\t${p._2.point.getX}\t${p._2.point.getY}\t${p._3.point.getX}\t${p._3.point.getY}\t${p._1.areaMeasurement.getComment}\t${p._1.link.getOrElse("")}\t${img}\n"
      writer.write(line)
    }
    writer.close()
  }

  /*
    import vars.annotation.ui._
    import org.mbari.smith._
    import scala.collection.JavaConverters._
    val inj = Lookup.getGuiceInjectorDispatcher.getValueObject.asInstanceOf[com.google.inject.Injector]
    val adf = inj.getInstance(classOf[vars.annotation.AnnotationDAOFactory])
    val dao = adf.newVideoArchiveSetDAO()
    val xs = dao.findAll()
    xs.asScala.foreach( vas => vas.getVideoArchives.asScala.foreach( va => println(s"${va.name}")))
    val p = PointsApp.read("Pulse 58 Rover transect", 1.5, 50.toRadians, 30.toRadians, 45.toRadians)

   */

  def main(args: Array[String]) {


    if (args.size != 6) {
        println("""
                  | Script that converts any 'area measurement' associations found in your VARS query results
                  | and converts them to x, y distances from the point directly under the camera.
                  |
                  | Usage:
                  |   PointsApp <cameraHeight> <alpha> <beta> <theta> <inputFile> <outputFile>
                  |
                  | Arguments:
                  |    cameraHeight:     The height of the camera above the seafloor. All area measurements will
                  |                      be in the same units as cameraHeight (i.e. you should use centimeters
                  |                      instead of meters)
                  |    alpha:            The vertical angular field of view in degrees
                  |    beta:             The horizontal angular field of view in degrees
                  |    theta:            The tilt of the camera from horizontal in degrees
                  |    videoArchiveName: The VideoArchive to process
                  |    targetFile:       The name of the file to write to
                """.stripMargin('|'))
    }
    else {
      val cameraHeight = args(0).toDouble
      val alpha = args(1).toDouble.toRadians
      val beta = args(2).toDouble.toRadians
      val theta = args(3).toDouble.toRadians
      val videoArchiveName = args(4)
      val targetFile = new File(args(5))

      val amPoints = read(videoArchiveName, cameraHeight, alpha, beta, theta)
      write(amPoints, targetFile)
    }

  }





}
