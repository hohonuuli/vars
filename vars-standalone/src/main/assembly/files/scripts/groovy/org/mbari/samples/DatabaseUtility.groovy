package org.mbari.samples

import vars.ToolBox
import vars.knowledgebase.Concept
import vars.knowledgebase.ConceptDAO
import vars.knowledgebase.ConceptName
import org.mbari.sql.QueryableImpl
import java.sql.DriverManager

/**
 * 
 * @author Brian Schlining
 * @since Sep 2, 2010
 */
class DatabaseUtility {

    def jdbcUrl = "jdbc:jtds:sqlserver://solstice.shore.mbari.org:1433/MBARI_Samples"
    def jdbcPassword = "samp"
    def jdbcUsername = "samp"
    def jdbcDriver = 'net.sourceforge.jtds.jdbc.Driver'

    def toolBox = new ToolBox()
    final samplesDatabase = new QueryableImpl(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver)

    def newConnection() {
        return DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)
    }

    /**
     *  Gets the keywords attribute of the GetVimsKeywords object
     *
     * @param  conceptName Description of the Parameter
     * @return  The keywords value
     */
    def getKeywords(String conceptName) {
        def keywords = new LinkedList()
        ConceptDAO dao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
        Concept concept = dao.findByName(conceptName)
        while (concept != null) {
            def conceptNames = concept.getConceptNames()
            conceptNames.each { name ->
                def sb = new StringBuilder(name.name.replace('-', ' '))
                // Capitalize 1st Letter
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)))
                keywords << sb.toString()
            }
            concept = concept.parentConcept
        }
        return keywords
    }

}
