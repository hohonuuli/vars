package vars.knowledgebase

import org.slf4j.LoggerFactory
import vars.RawSQLQueryFunction

/**
 * 
 * @author Brian Schlining
 * @since Sep 2, 2010
 */
class DatabaseUtility {
    final log = LoggerFactory.getLogger(DatabaseUtility.class)
    final toolBox = new vars.ToolBox()

    /**
     * Run a raw SQL Query against the VARS Annotation Database
     * @param sql THe query to run
     * @return A string of the data, suitable for writing to a file
     */
    String sqlquery(sql) {
        def sb = new StringBuilder("# Query Results from VARS Knowledgbase database\n")
        sb << "# SQL: ${sql.replaceAll(/\n/) { "\n# " }}\n"
        sb << toolBox.toolBelt.knowledgebasePersistenceService.executeQueryFunction(sql, new RawSQLQueryFunction())
        return sb.toString()
    }

    /**
     * Displays a list of LinkRealizations that use the given 'linkName'
     *
     * @param linkName A string representing the linkName to search for. Only exact matches are returned.
     */
    void listLinkRealizations(String linkName) {
        def dao = toolBox.toolBelt.knowledgebaseDAOFactory.newLinkRealizationDAO()
        def linkRealizations = dao.findAllByLinkName(linkName)
        dao.close()

        println "==========================================================================="
        println "CONCEPT | LINK NAME | TO CONCEPT | LINKVALUE"
        linkRealizations.each {lr ->
            def concept = lr.conceptMetadata.concept
            def name = concept.primaryConceptName.name
            println "${name} | ${lr.stringValue()}"
        }
        println "==========================================================================="
    }

    /**
     * Change all LinkRealizations that used 'oldLinkName' to use the
     * 'newLinkName'
     *
     * @param oldLinkName The links to replace
     * @param newLinkName The value to replace the old LinkName with
     */
    void changeLinkRealizations(String oldLinkName, String newLinkName) {

        if (!newLinkName) {
            throw new IllegalArgumentException("Need to specify a new link name")
        }

        println "==========================================================================="
        println "\tCONCEPT | LINK NAME | TO CONCEPT | LINKVALUE"
        def dao = toolBox.toolBelt.knowledgebaseDAOFactory.newLinkRealizationDAO()
        dao.startTransaction()
        def linkRealizations = (new ArrayList(dao.findAllByLinkName(oldLinkName))).sort { LinkRealization link ->
            link.conceptMetadata.concept.primaryConceptName.name
        }
        linkRealizations.each { LinkRealization link ->
            def name = link.conceptMetadata.concept.primaryConceptName.name
            println "OLD >> ${name} | ${link.stringValue()}"
            link.linkName = newLinkName
            println "NEW << ${name} | ${link.stringValue()}"
        }
        dao.endTransaction()
        dao.close()
    }

    
}
