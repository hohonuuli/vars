package org.mbari.smith

import com.google.inject.Injector
import vars.annotation.ui.{StateLookup, ToolBelt}
import vars.annotation.ui.imagepanel.Measurement

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
 * Created by brian on 8/6/14.
 */
object FindBogusMeasurementAssociations extends App {


  val toolBelt = StateLookup.GUICE_INJECTOR.getInstance(classOf[ToolBelt])
  val dao = toolBelt.getAnnotationDAOFactory.newAssociationDAO()
  val videoArchiveName = args(0)
  val videoFrames = CoverageEstimator.fetchAnnotations(videoArchiveName)
  val associations = for {
    vf <- videoFrames
    obs <- vf.getObservations.asScala
    ass <- obs.getAssociations.asScala
    if ass.getLinkName == Measurement.MEASUREMENT_LINKNAME
  } yield ass

  val t = associations.map { a =>
    Try(Measurement.fromLink(a)) match {
      case Success(m) => Option(m)
      case Failure(e) => {
        println(s"FAIL: ${a.getPrimaryKey}: $a")
        dao.startTransaction()
        val b = dao.find(a)
        b.setLinkValue(a.getLinkValue + " undefined")
        dao.commit()
        dao.endTransaction()
      }
    }

  }
}
