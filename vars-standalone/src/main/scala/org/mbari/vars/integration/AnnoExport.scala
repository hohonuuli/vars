package org.mbari.vars.integration

import java.text.DateFormat

import com.google.gson.{FieldNamingPolicy, GsonBuilder, Gson}
import vars.ToolBox

/**
  * First attempt at export Annotations using GSON. Crashes with stackoverflow
  *
  * @author Brian Schlining
  * @since 2016-03-09T14:23:00
  */
object AnnoExport {

  def main(args: Array[String]) {
    val videoArchiveName = args(0)
    println(apply(videoArchiveName))
  }

  def apply(videoArchiveName: String): String = {
    val gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
              .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
          .setPrettyPrinting()
            .create()
    val toolbox = new ToolBox
    val dao = toolbox.getToolBelt.getAnnotationDAOFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val json = Option(dao.findByName(videoArchiveName))
        .map( va => {
          val vas = va.getVideoArchiveSet
          gson.toJson(vas)
        })
        .getOrElse("")
    dao.endTransaction()
    dao.close()
    json
  }

}
