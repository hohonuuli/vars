import vars.knowledgebase.LinkRealization
import vars.knowledgebase.Concept
import vars.knowledgebase.ConceptDAO
/**
 * 
 * @author Brian Schlining
 * @since 2011-08-11
 */


def toolBox = new vars.ToolBox()
def linkRealzationFactory = toolBox.toolBelt.knowledgebaseFactory
ConceptDAO conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
conceptDao.startTransaction()
def jpql = 'SELECT c FROM Concept c WHERE c.reference IS NOT NULL'
def query = conceptDao.entityManager.createQuery(jpql)
def results = query.resultList.sort { it.primaryConceptName.name }
results.each { Concept concept ->
    def linkRealization = linkRealzationFactory.newLinkRealization()
    linkRealization.linkName = 'dsg-reference'
    linkRealization.toConcept = 'self'
    linkRealization.linkValue = concept.reference
    
    concept.conceptMetadata.addLinkRealization(linkRealization)
    conceptDao.persist(linkRealization)
    concept.reference = null
    
    println(concept.primaryConceptName.name + "\t\t -> dsg-reference | self | " + linkRealization.linkValue)

    
}
conceptDao.endTransaction()

