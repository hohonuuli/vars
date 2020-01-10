package org.mbari.smith

/**
  * @author Brian Schlining
  * @since 2018-10-08T14:42:00
  */
object DropVideoArchiveSets {

  def main(args: Array[String]): Unit = {

    val toolbox = new vars.ToolBox()
    val annotationDAOFactory = toolbox.getToolBelt.getAnnotationDAOFactory

    // Drop bogus video archive sets (DANGER)
    val deployments = List("02", "06", "17-1", "17-2",
      "20-1", "20-2", "20-3",
      "26-1", "26-2", "26-3",
      "29-1", "29-2", "29-3",
      "33-1", "33-2", "33-3").map(n => s"Tripod Pulse $n") ++
      List("T26121-01-tripod", "T0612-01-tripod")


    for (d <- deployments) {
      val vasDao = annotationDAOFactory.newVideoArchiveSetDAO()
      val vaDao = annotationDAOFactory.newVideoArchiveDAO(vasDao.getEntityManager)
      vasDao.startTransaction()
      val videoArchive = vaDao.findByName(d)
      if (videoArchive != null) {
        val videoArchiveSet = videoArchive.getVideoArchiveSet
        vasDao.remove(videoArchiveSet)
      }
      vasDao.commit()
      vasDao.endTransaction()
      vasDao.close()
    }




  }

}
