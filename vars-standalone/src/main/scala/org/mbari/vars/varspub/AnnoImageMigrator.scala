package org.mbari.vars.varspub

import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths, Path}
import java.sql.{ResultSet, DriverManager}
import java.text.SimpleDateFormat
import java.util.{TimeZone, GregorianCalendar, Date}
import javax.imageio.ImageIO

import com.google.inject.Injector
import org.slf4j.LoggerFactory
import vars.annotation.ui.ToolBelt
import vars.knowledgebase.ui.{Lookup}

import scala.collection.mutable
import scala.math._
import scala.util.Try
import scala.sys.process._
import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2014-11-21T11:01:00
 */
class AnnoImageMigrator(target: Path, overlayImageURL: URL, pathKey: String = "framegrabs",
                        overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt) {

  val useExiftool = WatermarkUtilities.canUseExiftool()
  private[this] val now = new Date()
  private[this] val log = LoggerFactory.getLogger(classOf[AnnoImageMigrator])
  private[this] val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  private[this] val timestampFormat = {
    val f = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    f.setTimeZone(TimeZone.getTimeZone("UTC"))
    f
  }
  private[this] val yearFormat = new SimpleDateFormat("yyyy")
  private[this] val alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5F)
  private[this] val overlayImage = ImageIO.read(overlayImageURL)
  private[this] val calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))

  implicit class ResultSetRowMapper(resultSet: ResultSet) {

    def map[A](rowMapper: ResultSet => A): Seq[A] = {
      // -- Read the results.
      val results = new mutable.ArrayBuffer[A]
      while (resultSet.next()) {
        results += rowMapper(resultSet)
      }
      results.toSeq
    }

  }

  private[this] val internalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://equinox.shore.mbari.org:1433/VARS",
      "everyone", "guest")

  private[this] val externalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://dione.mbari.org:51001/VARS",
      "everyone", "NeWW1stLst")

  def run(): Unit = {
    for {
      (e, i) <- mapURLs()
      p <- toTargetPath(e, i) if (i != null)
    } {

      try {
        val image = ImageIO.read(new URL(i))
        watermark(image, overlayImage).foreach( watermarked =>
            WatermarkUtilities.saveImage(watermarked, p.toFile, () => addExif(i, p)))
      }
      catch {
        case e: Exception => log.info("Failed to watermark {}", e)
      }
    }
  }


  private def toTargetPath(external: String, internal: String): Option[Path] = {
    val externalURL = new URL(external)
    if (imageExistsAt(externalURL)) None
    else {
      val idx = internal.indexOf(pathKey)
      val parts = internal.substring(idx + pathKey.size).split("/")
      val externalTarget = Paths.get(target.toString, parts:_*)
      val externalDir = externalTarget.getParent
      if (Files.notExists(externalDir)) {
        log.info("Creating {}", externalDir)
        Files.createDirectories(externalDir)
      }
      Some(externalTarget)
    }
  }


  /**
   * Create a Map of [external image URL] -> [internal image URL] for all annotation images
   * @return
   */
  private def mapURLs(): Seq[(String, String)] = {
    log.info("Starting database lookup")
    val urls = new mutable.ArrayBuffer[(String, String)]
    val external = externalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    val internal = internalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    val ers = external.executeQuery(
      """
        |SELECT
        |  id,
        |  StillImageURL
        |FROM
        |  CameraData
        |WHERE
        |  StillImageURL IS NOT NULL
        |ORDER BY
        |  id DESC
      """.stripMargin)
    while (ers.next()) {
      val (id, externalURL) = (ers.getLong(1), ers.getString(2))
      val irs = internal.executeQuery(
        s"""
          |SELECT
          |  id,
          |  StillImageURL
          |FROM
          |  CameraData
          |WHERE
          |  id = $id
        """.stripMargin)
      if (irs.next()) {
        urls += externalURL -> irs.getString(2)
      }
      irs.close()
    }
    external.close()
    internal.close()
    log.info("Finished database lookup")
    urls
  }

  private def imageExistsAt(url: URL): Boolean = {
    log.info("Attempting to read image at {}", url)
    Try {
      val inputStream = url.openStream()
      val buf = Array.ofDim[Byte](6)
      inputStream.read(buf)
      inputStream.close()
      true
    } getOrElse {
      log.info("Failed to read {}", url)
      false
    }
  }

  def watermark(image: BufferedImage, overlay: BufferedImage) : Option[BufferedImage] = {
    Try {
      addWatermarkImage(image, overlay)
      //val text = dateFormat.format(new Date)
      //addWatermarkText(i0, text, 0.15, 0.5)
    } toOption
  }


  def addWatermarkText(image: BufferedImage, text: String, rightPercent: Double, bottomPercent: Double, color: Color = Color.WHITE,
                       font: Font = new Font("Arial", Font.BOLD, 16)): BufferedImage = {

    val g2 = image.getGraphics.asInstanceOf[Graphics2D]
    g2.setColor(color)
    g2.setComposite(alphaComposite)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g2.setFont(font)
    val fontMetrics = g2.getFontMetrics
    val rect = fontMetrics.getStringBounds(text, g2)
    val w = image.getWidth
    val h = image.getHeight
    val x = (w - w * rightPercent - rect.getWidth).toFloat
    val y = (h - h * bottomPercent).toFloat
    g2.drawString(text, x, y)
    g2.dispose()
    image
  }

  def addWatermarkImage(image: BufferedImage, overlay: BufferedImage): BufferedImage = {
    val g2 = image.getGraphics.asInstanceOf[Graphics2D]
    val v = image.getWidth * overlayPercentWidth / overlay.getWidth
    val so = WatermarkUtilities.scaleOverlay(v, overlay)
    val x = round(image.getWidth * 0.05).toInt
    val y = round(image.getHeight * 0.85 - so.getHeight).toInt
    g2.setComposite(alphaComposite)
    g2.drawImage(so, x, y, null)
    g2.dispose()
    image
  }

  def addExif(internalUrl: String, target: Path): Unit = {

    if (useExiftool) {


      // -- Grab parameters via JPA
      val dao = toolBelt.getAnnotationDAOFactory.newCameraDataDAO()
      dao.startTransaction()
      val cd = dao.findByImageReference(internalUrl)
      if (cd == null) {
        dao.endTransaction()
        return
      }

      val vf = cd.getVideoFrame
      val pd = vf.getPhysicalData
      val va = vf.getVideoArchive
      val vas = va.getVideoArchiveSet
      val dives = vas.getCameraDeployments.asScala.map(_.getSequenceNumber).mkString(", ")
      val obs = vf.getObservations.asScala
      dao.endTransaction()

      // -- Extract values, substitute placeholders if needed.

      // Use the earliest observation date in the videoframe. If none are available will use now as a placeholder
      val createDate = Try(vf.getObservations
          .asScala
          .map(_.getObservationDate)
          .sortBy(_.getTime)
          .head)
          .getOrElse(now)

      // Use recorded date, if not available use now as a placeholder
      val dateTimeOriginal = Option(vf.getRecordedDate).getOrElse(now)

      val altitude: Float = Option(pd.getDepth.floatValue()).getOrElse(0F)
      val latitude: Double = Option(pd.getLatitude.doubleValue()).getOrElse(0D)
      val longitude: Double = Option(pd.getLongitude.doubleValue()).getOrElse(0D)
      val temperature: Float = Option(pd.getTemperature.floatValue()).getOrElse(-999F)
      val salinity: Float = Option(pd.getSalinity.floatValue()).getOrElse(-999F)
      val oxygen: Float = Option(pd.getOxygen.floatValue()).getOrElse(-999F)

      val dateTimeStrForComment = Option(vf.getRecordedDate).map("and time " + timestampFormat.format(_)).getOrElse("")
      val yearsString = yearFormat.format(dateTimeOriginal)
      val createDateStr = timestampFormat.format(createDate)
      val dateTimeOriginalStr = timestampFormat.format(dateTimeOriginal)
      val conceptStr = obs.map(o => {
        val ass = o.getAssociations.asScala.mkString(", ")
        s"${o.getConceptName}: $ass"
      }).mkString("'", ",", "'")

      val cmd = Seq("exiftool",
        s"-CameraLabel=${vas.getPlatformName}",
        f"-Comment=Image captured from a video camera mounted on underwater remotely operated vehicle ${vas.getPlatformName} on dive number $dives. The original MBARI video tape number is ${va.getName}. This image is from timecode ${vf.getTimecode} $dateTimeStrForComment. The recorded edited location and environmental measurements at time of capture are Lat=$latitude%.7f  Lon=$longitude%.7f  Depth=$altitude%.1f m  Temp=$temperature%.3f C  Sal=$salinity%.3f PSU  Oxy=$oxygen%.3f ml/l. The Video Annotation and Reference system annotations for this image is/are $conceptStr.",
        s"-CreateDate=$createDateStr",
        s"-DateTimeOriginal=$dateTimeOriginalStr",
        f"-GPSAltitude=${altitude}%.1f",
        "-GPSAltitudeRef=-1",
        f"-GPSLatitude=${latitude}%.7f",
        "-GPSLatitudeRef=N",
        f"-GPSLongitude=${longitude}%.7f",
        "-GPSLongitudeRef=W",
        "-GPSProcessingMethod=MANUAL",
        "-TimeZoneOffset=0",
        s"-Copyright=Copyright $yearsString Monterey Bay Aquarium Research Institute",
        target.toString)

      try {
        log.info("Adding EXIf to " + target + " using " + cmd.mkString(" "))
        cmd.!!
      } catch {
        case e: Exception => log.info(s"Failed to add EXIF to $target")
      }
    }
    else {
      Thread.sleep(50) // REQUIRED!! Otherwise Apache on Eione ends up hanging after a few hundred files
    }

  }


}


object AnnoImageMigrator {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
    if (args.size != 2) {
      println(
        """
          |Process all annotation images, watermark them and add EXIF metadata for
          | VARS Pub.
          |
          | Usage:
          |     AnnoImageMigrator(Array(target, overlay))
          |
          | Inputs:
          |     target = The root directory to write the new images to
          |     overlay = The path to the overlay image to use for watermarking
        """.stripMargin)
      return
    }

    val target = Paths.get(args(0))
    val overlayImageURL = new File(args(1)).toURI.toURL
    implicit val toolbelt = {
      val injector = Lookup.getGuiceInjectorDispatcher.getValueObject.asInstanceOf[Injector]
      injector.getInstance(classOf[ToolBelt])
    }
    val imageMigrator = new AnnoImageMigrator(target, overlayImageURL)
    val msg = if (imageMigrator.useExiftool) "Found 'exiftool' on this system" else "No 'exiftool' on this system"
    logger.info(msg)
    imageMigrator.run()

  }


}