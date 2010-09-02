package vars.knowledgebase

import org.slf4j.LoggerFactory

/**
 * 
 * @author Brian Schlining
 * @since Sep 2, 2010
 */
class DatabaseUtility {
    final log = LoggerFactory.getLogger(DatabaseUtility.class)
    final toolBox = new vars.ToolBox()

    /**
     * Displays a list of LinkRealizations that use the give 'linkValue'
     *
     * @param linkValue A string representing the linkValue to search for. Only exact matches are returned.
     */
    void listLinkRealizations(String linkValue) {
        def dao = toolBox.toolBelt.knowledgebaseDAOFactory.newLinkRealizationDAO()
        def linkRealizations = dao.findAllByLinkName(linkValue)

        println "==========================================================================="
        println "CONCEPT | LINK NAME | TO CONCEPT | LINKVALUE"
        linkRealizations.each {lr ->
            def concept = lr.conceptMetadata.concept
            def name = concept.primaryConceptName.name
            println "${name} | ${lr.stringValue()}"
        }
        println "==========================================================================="
    }

    
}
