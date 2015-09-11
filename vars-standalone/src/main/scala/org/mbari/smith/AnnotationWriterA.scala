package org.mbari.smith

import java.io.{BufferedWriter, FileWriter, File}
import java.time.Instant


/**
 *
 *
 * @author Brian Schlining
 * @since 2015-09-11T09:57:00
 */
class AnnotationWriterA(file: File, delimiter: String = "\t") {
  
  private lazy val writer = new BufferedWriter(new FileWriter(file))
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
      "observationID")

  def writeHeader(): Unit = {
    writer.write(s"# Creation Date: ${Instant.now()}")

    writer.write("#VideoArchiveName\tTimecode\tRecordedDate\tConceptName\tLatitude\tLongitude\tDepth\tComment\tAssociation\tImage\tObserver\tObservationID\n")
  }

  def write(annotation: Annotation): Unit = {
    //writer.write()
  }



}
