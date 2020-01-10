package org.mbari.vars.haddock

import java.io.File

import vars.{ILink, ToolBox}
import scala.collection.JavaConverters._
import scala.io.Source

/**
  * @see https://mbari1.atlassian.net/browse/VARS-758
  *
  * @author Brian Schlining
  * @since 2016-03-13T15:35:00
  */
class AddBiolumTags(tagMap: Map[String, String]) {

  val toolbox = new ToolBox

  def apply(): Unit = {
    val dao = toolbox.getToolBelt.getKnowledgebaseDAOFactory.newConceptDAO()
    val factory = toolbox.getToolBelt.getKnowledgebaseFactory

    dao.startTransaction()
    for ((genera, tag) <- tagMap) {
      val concept = Option(dao.findByName(genera))
      concept.foreach( c => {
        val existingTag = c.getConceptMetadata
            .getLinkRealizations
            .asScala
            .filter(r => r.getLinkName.equalsIgnoreCase(AddBiolumTags.BIOLUM_LINKNAME))
            .toList
        existingTag match  {
          case Nil =>
            val linkRealization = factory.newLinkRealization()
            linkRealization.setLinkName(AddBiolumTags.BIOLUM_LINKNAME)
            linkRealization.setToConcept(ILink.VALUE_SELF)
            linkRealization.setLinkValue(tag)
            c.getConceptMetadata.addLinkRealization(linkRealization)
            dao.persist(linkRealization)
          case _ => // Do nothing. the tag already exists
        }
      })
    }
    dao.endTransaction()
  }

}

object AddBiolumTags {


  val BIOLUM_LINKNAME = "is-bioluminescent"
  val LINK_NAME_UNDEFINED = "undefined"

  def main(args: Array[String]) {
    val file = new File(args(0))
    val tagMap = parseTagFile(file)
    val tagAdder = new AddBiolumTags(tagMap)
    tagAdder.apply()

  }

  def parseTagFile(file: File): Map[String, String] = {
    val source = Source.fromFile(file)
    val lines = source.getLines()
    val speciesTags = for (line <- lines) yield {
      val parts = line.split("\t").map(_.trim)
      parts(0) -> parts(1).toLowerCase()
    }

    speciesTags.toMap
        .filterNot({case (k, v) => v.equalsIgnoreCase(LINK_NAME_UNDEFINED)})
  }
}
