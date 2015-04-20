package vars.knowledgebase

import vars.ToolBox

/**
 * 
 * @author Brian Schlining
 * @since 2012-09-10
 */
class KBUtilities {

    static findConceptsByRank(rank) {
        def tb = new ToolBox()
        def dao = tb.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
        def allConcepts = dao.findAll()
        return allConcepts.findAll { c ->
            def r = "${c.rankLevel}${c.rankName}"
            return r.contains(rank)
        }

    }

    static showConceptsByRank(rank) {
        def concepts = findConceptsByRank(rank)
        concepts.sort { it.primaryConceptName.name }
        println("-" * 72)
        println("Found ${concepts.size()} concepts with a rank of '${rank}'")
        concepts.each { c ->
            def r = (c.rankLevel ?: "") + c.rankName
            println("${c.primaryConceptName.name}\t[$r]")
        }
    }

}
