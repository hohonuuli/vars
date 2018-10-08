package org.mbari.smith

/**
  * @author Brian Schlining
  * @since 2018-10-08T14:42:00
  */
object DropVideoArchiveSets {

  def main(args: Array[String]): Unit = {

    val toolbox = new vars.ToolBox()
    val annotationDAOFactory = toolbox.getToolBelt.getAnnotationDAOFactory

    val deployments = List("02", "06", "07", "17", "20", "26", "29", "33")
    val vasDao = annotationDAOFactory.newVideoArchiveSetDAO()
    val vaDao = annotationDAOFactory.newVideoArchiveDAO(vasDao.getEntityManager)

    for (d <- deployments) {
      val videoArchive = vaDao.findByName(s"Pulse $d")
      if (videoArchive != null) {
        val videoArchiveSet = videoArchive.getVideoArchiveSet
        vasDao.remove(videoArchiveSet)
      }
    }



  }

}
