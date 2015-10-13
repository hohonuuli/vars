package org.mbari.vars.integration

import java.net.URL
import java.time.Instant
import java.util.Date

import org.mbari.expd.actions.CollateByDateFunction
import org.mbari.expd.math.NearestNeighbor
import org.mbari.math.FastCollator
import org.slf4j.LoggerFactory
import vars.annotation.{VideoFrame, AnnotationDAOFactory}
import scala.collection.JavaConverters._

/**
 * Merge a text file by date.
 *
 * @author Brian Schlining
 * @since 2015-10-12T16:50:00
 */
class GenericMerge(val url: URL, val delimter: String = ",") {

  private[this] val parser = new TextParser(url, delimter)
  private[this] val log = LoggerFactory.getLogger(getClass)

  def apply(videoArchiveName: String, offsetSeconds: Long)(implicit daoFactory: AnnotationDAOFactory): Unit = {
    val videoFrames = findVideoFrames(videoArchiveName)
    val vfMillis = videoFrames.map(_.getRecordedDate.getTime).toArray
    val logData = loadGenericData()
    val logMillis = logData.map(_.date.getTime).toArray
    def vfToSecs(vf: VideoFrame): Double = vf.getRecordedDate.getTime / 1000D
    def logToSecs(gd: GenericData): Double = gd.date.getTime / 1000D
    val mergedData = FastCollator(videoFrames, vfToSecs, logData, logToSecs, offsetSeconds)
    update(mergedData)
  }

  private def findVideoFrames(videoArchiveName: String)
                             (implicit daoFactory: AnnotationDAOFactory): Seq[VideoFrame] = {
    val dao = daoFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val videoArchive = dao.findByName(videoArchiveName)
    val videoFrames = if (videoArchive != null) videoArchive.getVideoFrames.asScala else Nil
    dao.endTransaction()
    dao.close()
    videoFrames
  }

  private def loadGenericData(): Seq[GenericData] = {
    val columns = parser.columns.map(_.toLowerCase)
    val date = load("date", s => Date.from(Instant.parse(s)))
    val lat = load("latitude", s => s.toDouble)
    val lon = load("longitude", s => s.toDouble)
    val depth = load("depth", s => s.toDouble)
    val salinity = load("salinity", s => s.toFloat)
    val temperature = load("temperature", s => s.toFloat)
    val oxygen = load("oxygen", s => s.toFloat)
    for (i <- date.indices) yield GenericData(date(i),
      Option(lat(i)),
      Option(lon(i)),
      Option(depth(i)),
      Option(salinity(i)),
      Option(temperature(i)),
      Option(oxygen(i)))
  }

  private def load[T](columnName: String, fn: String => T): List[T] = {
    val columns = parser.columns.map(_.toLowerCase)
    val idx = columns.indexOf(columnName.toLowerCase)
    if (idx >= 0) parser.values.map(r => fn(r(idx))).toList else Nil
  }

  private def update(data: Seq[(VideoFrame, Option[GenericData])]): Unit = {
    // TODO implement me
  }

}

object GenericMerge {

}

case class GenericData(date: Date,
                       latitude: Option[Double],
                       longitude: Option[Double],
                       depth: Option[Double],
                       salinity: Option[Float],
                       temperature: Option[Float],
                       oxygen: Option[Float])
