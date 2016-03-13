package org.mbari.vars.integration

import com.google.gson.Gson
import vars.ToolBox

/**
  * * First attempt at export KB using GSON. Crashes with stackoverflow
  *
  * @author Brian Schlining
  * @since 2016-03-09T14:07:00
  */
object KBExport {

  def main(args: Array[String]) {
    println(apply())
  }

  def apply(): String = {
    val gson = new Gson()
    val toolbox = new ToolBox
    val dao = toolbox.getToolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
    dao.startTransaction()
    val root = dao.findRoot()
    val json = gson.toJson(root)
    dao.endTransaction()
    dao.close()
    json
  }

}
