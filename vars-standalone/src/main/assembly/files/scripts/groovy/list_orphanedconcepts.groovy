/**
 * This script lists all annotations that contain conceptnames no longer in 
 * the knowledgebase
 *
 * @author Brian Schlining
 * @since 2014-11-11
 */

import mbarix4j.sql.QueryFunction

def toolBox = new vars.ToolBox()
def kbdao = toolBox.toolBelt.knowledgebasePersistenceService
def annodao = toolBox.toolBelt.annotationPersistenceService
def sql = "SELECT DISTINCT ConceptName FROM ConceptName ORDER BY ConceptName"
def cs = kbdao.executeQueryFunction(sql, {rs ->
    def conceptNames = []
    while (rs.next()) {
      conceptNames << rs.getString(1)
    }
    return conceptNames
  } as QueryFunction)
  
sql = "SELECT DISTINCT ConceptName FROM Observation ORDER BY ConceptName"
def xs = annodao.executeQueryFunction(sql, {rs ->
    def conceptNames = []
    while (rs.next()) {
      conceptNames << rs.getString(1)
    }
    return conceptNames
  } as QueryFunction)
  
xs.removeAll(cs)
println("# Concepts used in Annotations but not in Knowledgebase")
xs.sort()
xs.each { println(it) }  