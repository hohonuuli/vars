package vars.migrations

import mbarix4j.sql.QueryFunction
import org.slf4j.LoggerFactory
import vars.ToolBox
import vars.knowledgebase.ConceptNameDAO
import vars.knowledgebase.ConceptNameTypes

/**
 * Drop duplicate selectedConcept names
 * @author Brian Schlining
 * @since 2011-12-05
 */
class DropDuplicateConceptNamesFn {

    private toolBox = new ToolBox();
    final log = LoggerFactory.getLogger(getClass())

    void apply() {
        findDuplicateNames().each { name ->
            dropDuplicate(name)
        }
    }

    def findDuplicateNames() {
        def handler = { rs ->
            def dups = []
            while (rs.next()) {
                dups << rs.getString(1)
            }
            return dups
        } as QueryFunction

        return toolBox.toolBelt.knowledgebasePersistenceService.executeQueryFunction("""
            SELECT
              ConceptName,
              count(ConceptName)
            FROM
              ConceptName
            GROUP BY
              ConceptName HAVING count(ConceptName) > 1
        """.stripIndent(), handler)

    }

    def dropDuplicate(name) {
        ConceptNameDAO dao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptNameDAO()
        dao.startTransaction()
        def duplicates = dao.findByNameContaining(name)
        def matches = duplicates.findAll { n ->
            n.name == name && (!n.nameType.equalsIgnoreCase(ConceptNameTypes.PRIMARY.name))
        }
        matches.each { n ->
            log.info("Dropping duplicate: ${n}")
            def concept = n.getSelectedConcept
            concept.removeConceptName(n)
            dao.remove(n)
        }
        
        duplicates.removeAll(matches)
        if (duplicates.size() > 0) {
            def sb = new StringBuilder("The following duplicates still exist:\n")
            duplicates.each { n ->
                sb << "\t${n.concept.parentConcept.primaryConceptName.name} -> ${n.name}\t[has ${n.concept.childConcepts.size()} children]\n"
            }
            log.warn(sb.toString())
        }
        
        dao.endTransaction()
        dao.close()
    }

}