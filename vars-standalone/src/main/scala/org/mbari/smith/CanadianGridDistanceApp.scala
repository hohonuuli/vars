package org.mbari.smith

import java.text.SimpleDateFormat
import scala.collection.JavaConverters._
import vars.annotation.Association
import vars.annotation.ui.imagepanel.Measurement
import scala.util.{Failure, Success, Try}
import java.net.URL
import scala.math
import java.io.{File, FileWriter, BufferedWriter}

/**
 *
 * @author Brian Schlining
 * @since 2013-04-22
 */
object CanadianGridDistanceApp {

  private[this] val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'")

  def read(videoArchiveName: String,
      cameraHeight: Double,
      alpha: Double,
      beta: Double,
      theta: Double): Iterable[(Association, Double)] = {

    // --- 1. Get all Measurement associations
    val videoFrames = CoverageEstimator.fetchAnnotations(videoArchiveName)

    val associations = for {
      vf <- videoFrames
      obs <- vf.getObservations.asScala
      ass <- obs.getAssociations.asScala
      if ass.getLinkName == Measurement.MEASUREMENT_LINKNAME
    } yield ass

    // --- 2. Gather all info needed to Canadian Grid calculation
    val camera = Camera.fromRadians(cameraHeight, alpha, beta, theta)

    // Assume all images are the same size so just get the first one
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

    // --- 3. Perform actual distance calculation
    val measurements: Iterable[(Association, Measurement)] = associations
        .map(a => a -> Try(Measurement.fromLink(a)).toOption)
        .filter(p => p._2.isDefined)
        .map(p => p._1 -> p._2.get)

    for ((a, m) <- measurements) yield {
      val p0 = new Pixel(cameraView.camera, cameraView.imageWidth, cameraView.imageHeight, m.getX0, m.getY0)
      val p1 = new Pixel(cameraView.camera, cameraView.imageWidth, cameraView.imageHeight, m.getX1, m.getY1)
      val x = math.abs(p0.xDistance - p1.xDistance)
      val y = math.abs(p0.yDistance - p1.yDistance)
      a -> math.sqrt(x * x + y * y)
    }

  }

  def write(distances: Iterable[(Association, Double)], file: File) {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write("VideoArchiveName\tTimecode\tRecordedDate\tConceptName\tLatitude\tLongitude\tDepth\tMeasurementLength\tComment\tAssociation\tImage\tObserver\tObservationID\n")
    for ((ass, dist) <- distances) {
      val obs = ass.getObservation
      val vf = obs.getVideoFrame
      val va = vf.getVideoArchive
      val meas = Measurement.fromLink(ass)
      val observer = obs.getObserver
      val observationID = obs.getPrimaryKey

      val date = if (vf.getRecordedDate == null) "" else dateFormat.format(vf.getRecordedDate)
      val img = if (vf.getCameraData == null) "" else vf.getCameraData.getImageReference
      val (lat, lon, depth) = Option(vf.getPhysicalData) match {
        case None => (Double.NaN, Double.NaN, Double.NaN)
        case Some(p) => (p.getLatitude, p.getLongitude, p.getDepth)
      }
      val line = s"${va.getName}\t${vf.getTimecode}\t$date\t${obs.getConceptName}\t$lat\t$lon\t$depth\t$dist\t${meas.getComment}\t$ass\t$img\t$observer\t$observationID\n"
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
                | Script that converts any 'measurement' associations found in your VARS query results
                | and converts them to distances in the same units as the cameraHeight
                |
                | Usage:
                |   CanadianGridDistanceApp <cameraHeight> <alpha> <beta> <theta> <videoArchiveName> <outputFile>
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

      val dists = read(videoArchiveName, cameraHeight, alpha, beta, theta)
      write(dists, targetFile)
    }

  }

}
