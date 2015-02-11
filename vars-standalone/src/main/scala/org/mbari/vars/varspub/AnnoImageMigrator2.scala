package org.mbari.vars.varspub

import java.awt.image.BufferedImage
import java.io._
import java.net.URL
import java.nio.file.{ Files, Paths, Path }
import java.sql.{ ResultSet, DriverManager }
import java.text.SimpleDateFormat
import java.util.{ TimeZone, Date }
import javax.imageio.ImageIO

import com.google.inject.Injector
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.common.RationalNumber
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter
import org.apache.commons.imaging.formats.tiff.constants.{ GpsTagConstants, TiffTagConstants, TiffEpTagConstants, ExifTagConstants }
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet
import org.slf4j.LoggerFactory
import vars.annotation.ui.ToolBelt
import vars.knowledgebase.ui.{ Lookup }

import scala.collection.mutable
import scala.math._
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

/**
 * A class that watermarks images for VARS pub and also adds EXIF data to the images.
 *
 * @param target The base directory to write the processed images into
 * @param overlayImageURL The URL to the image that will be used for an overlay
 * @param pathKey This is some key in the internal urls that we used to split the url. the part before
 *                this key is discarded, the part after this key is split into directories.
 *                The directories will be created in target to store the image
 * @param overlayPercentWidth The width of the overlay as percent of the framegrab
 * @param toolBelt The VARS toolbelt object used to create DAO objects
 *
 * @author Brian Schlining
 * @since 2014-11-21T11:01:00
 */
class AnnoImageMigrator2(target: Path,
  overlayImageURL: URL,
  pathKey: String = "framegrabs",
  overlayPercentWidth: Double = 0.4)(implicit toolBelt: ToolBelt)
  extends Runnable {

  private[this] val now = new Date()
  private[this] val log = LoggerFactory.getLogger(getClass)
  private[this] val timestampFormat = {
    val f = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
    f.setTimeZone(TimeZone.getTimeZone("UTC"))
    f
  }
  private[this] val yearFormat = new SimpleDateFormat("yyyy")
  private[this] val overlayImage = ImageIO.read(overlayImageURL)

  private[this] val internalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://equinox.shore.mbari.org:1433/VARS",
    "everyone", "guest")

  private[this] val externalConnection = DriverManager.getConnection("jdbc:jtds:sqlserver://dione.mbari.org:51001/VARS",
    "everyone", "NeWW1stLst")

  /**
   * This is the method used to execute the class. This processing will take a
   * long time as it checks EVERY to see if every image listed in VARS Pub exists
   * on the external web server.
   */
  def run(): Unit = {
    for {
      (e, i) <- mapURLs()
      p <- toTargetPath(e, i) if i != null
    } {

      try {
        val image = ImageIO.read(new URL(i))
        watermark(image, overlayImage).foreach(image => {
          val jpegBytes = addExif(i, image)
          val os = new BufferedOutputStream(new FileOutputStream(p.toFile))
          os.write(jpegBytes)
          os.close()
        })
        log.debug(s"Prepped image from $i to $p for external release")
      } catch {
        case e: Exception => log.debug("Failed to watermark {}", e)
      }
    }
  }

  /**
   * Maps the URL's to a fully-qualified path that a watermarked image will
   * be written to
   * @param external The external VARSpub framegrab URL
   * @param internal The internal framegrab URL
   * @return A path to write an external framegrab too. Some is returned
   *         if the image is missing and needs to be created. None if
   *         the image already exists (so there's no need to updated it)
   */
  private def toTargetPath(external: String, internal: String): Option[Path] = {

    // True if the image alreay exists on the external web server  
    // If an external URL is bogus this will also return True so that no copy is attempted  
    val externalImageExists: Boolean = Try {
      val externalURL = new URL(external)
      WatermarkUtilities.imageExistsAt(externalURL)
    } match {
      case Success(a) => a
      case Failure(_) => true
    }

    if (externalImageExists) None
    else {
      val idx = internal.toLowerCase.indexOf(pathKey.toLowerCase)
      val parts = internal.substring(idx + pathKey.size).split("/")

      // TODO: externalTarget doesn't always seem to map correctly
      val externalTarget = Paths.get(target.toString, parts: _*)
      val externalDir = externalTarget.getParent
      if (Files.notExists(externalDir)) {
        log.info("Creating {}", externalDir)
        Files.createDirectories(externalDir)
      }
      Some(externalTarget)
    }
  }

  /**
   * Takes a watermarked image and adds EXIF data.
   *
   * @param internalUrl The internal URL of the framegrab. Used to look up
   *                    VARS information about the framegrab
   * @param image The BufferedImage that we will be turning into a JPEG and writing
   *              EXIF metadata to
   * @return A byte array representing the bufferedimage as JPEG data. EXIF info
   *         will be included in this representation.
   */
  private def addExif(internalUrl: String, image: BufferedImage): Array[Byte] = {

    // -- Convert BufferedImage to a jpeg in a byte array
    val jpegBytes = WatermarkUtilities.toJpegByteArray(image)

    // -- Create a TiffOutputSet from byte array
    val outputSet = WatermarkUtilities.getOrCreateOutputSet(jpegBytes)

    // -- Lookup metadata from VARS
    val exifInfo = lookupMetadataFromDatabase(internalUrl).getOrElse(
      ExifInfo("No annotation information is available for this image",
        now, now, 0F, 0D, 0D))

    // -- Add EXIF
    val exifDirectory = outputSet.getOrCreateExifDirectory()

    // Create Date
    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
    exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
      timestampFormat.format(exifInfo.dateTimeOriginal))

    // DateTimeDigitized
    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED)
    exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED,
      timestampFormat.format(exifInfo.createDate))

    // Time Zone offset
    exifDirectory.removeField(TiffEpTagConstants.EXIF_TAG_TIME_ZONE_OFFSET)
    exifDirectory.add(TiffEpTagConstants.EXIF_TAG_TIME_ZONE_OFFSET, 0.shortValue)

    // UserComment
    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT)
    exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, exifInfo.userComment)

    // -- Add ROOT to EXIF
    val rootDirectory = outputSet.getOrCreateRootDirectory()

    rootDirectory.removeField(TiffTagConstants.TIFF_TAG_COPYRIGHT)
    rootDirectory.add(TiffTagConstants.TIFF_TAG_COPYRIGHT,
      s"Copyright ${yearFormat.format(exifInfo.dateTimeOriginal)} Monterey Bay Aquarium Research Institute")

    // -- Add GPS to EXIF
    val gpsDirectory = outputSet.getOrCreateGPSDirectory()

    val altAsInt = round(exifInfo.gpsAltitude * 10)
    gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE)
    gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE, new RationalNumber(altAsInt, 10))

    gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF)
    gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF,
      GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF_VALUE_BELOW_SEA_LEVEL.byteValue)

    outputSet.setGPSInDegrees(exifInfo.gpsLongitude, exifInfo.gpsLatitude)

    gpsDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_PROCESSING_METHOD)
    gpsDirectory.add(GpsTagConstants.GPS_TAG_GPS_PROCESSING_METHOD, "MANUAL")

    WatermarkUtilities.addExif(jpegBytes, outputSet)

  }

  /**
   * Retrieves VARS data about an image
   * @param internalUrl The URL to the framegrab, used as the database key
   * @return An object containing all the info needed to populate the EXIF metadata. None
   *         if the internalUrl wasn't found in VARS
   */
  private def lookupMetadataFromDatabase(internalUrl: String): Option[ExifInfo] = {
    // -- Grab parameters via JPA
    val dao = toolBelt.getAnnotationDAOFactory.newCameraDataDAO()
    dao.startTransaction()
    val cd = dao.findByImageReference(internalUrl)
    if (cd == null) {
      dao.endTransaction()
      return None
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

    val altitude: Float = Try(pd.getDepth.floatValue()).getOrElse(0F)
    val latitude: Double = Try(pd.getLatitude.doubleValue()).getOrElse(0D)
    val longitude: Double = Try(pd.getLongitude.doubleValue()).getOrElse(0D)
    val temperature: Float = Try(pd.getTemperature.floatValue()).getOrElse(-999F)
    val salinity: Float = Try(pd.getSalinity.floatValue()).getOrElse(-999F)
    val oxygen: Float = Try(pd.getOxygen.floatValue()).getOrElse(-999F)

    val dateTimeStrForComment = Option(vf.getRecordedDate).map("and time " + timestampFormat.format(_)).getOrElse("")
    val yearsString = yearFormat.format(dateTimeOriginal)
    val createDateStr = timestampFormat.format(createDate)
    val dateTimeOriginalStr = timestampFormat.format(dateTimeOriginal)
    val conceptStr = obs.map(o => {
      val ass = o.getAssociations.asScala.mkString(", ")
      s"${o.getConceptName}: $ass"
    }).mkString("'", ",", "'")

    Option(ExifInfo("Image captured from a video camera mounted on underwater remotely operated " +
      s"vehicle ${vas.getPlatformName} on dive number $dives. The original MBARI video " +
      s"tape number is ${va.getName}. This image is from timecode ${vf.getTimecode} " +
      s"$dateTimeStrForComment. The recorded edited location and environmental " +
      f"measurements at time of capture are Lat=$latitude%.7f  Lon=$longitude%.7f  " +
      f"Depth=$altitude%.1f m  Temp=$temperature%.3f C  Sal=$salinity%.3f PSU  " +
      f"Oxy=$oxygen%.3f ml/l. The Video Annotation and Reference system annotations for" +
      s" this image is/are $conceptStr.",
      createDate,
      dateTimeOriginal,
      altitude,
      latitude,
      longitude))

  }

  /**
   * Create a Map of [external image URL] -> [internal image URL] for all annotation images
   * @return Mapping between internal and external URLs
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

  /**
   * Watermark and image
   * @param image The bufferedimage to modify
   * @param overlay The overlay to use
   * @return The watermarked image, None is returned if the watermarking fails
   */
  private def watermark(image: BufferedImage, overlay: BufferedImage): Option[BufferedImage] =
    Try(WatermarkUtilities.addWatermarkImage(image, overlay, overlayPercentWidth)).toOption

}

object AnnoImageMigrator2 {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]) {
    if (args.size != 2) {
      println(
        """
          |Process all annotation images, watermark them and add EXIF metadata for
          | VARS Pub.
          |
          | Usage:
          |     AnnoImageMigrator2(Array(target, overlay))
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
    val imageMigrator = new AnnoImageMigrator2(target, overlayImageURL)
    imageMigrator.run()

  }

}

case class ExifInfo(userComment: String,
  createDate: Date,
  dateTimeOriginal: Date,
  gpsAltitude: Float,
  gpsLatitude: Double,
  gpsLongitude: Double)