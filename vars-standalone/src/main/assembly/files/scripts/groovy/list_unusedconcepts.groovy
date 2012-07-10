
/**
 * This script lists all the primary concept names that have NOT been used in annotations
 * @author Brian Schlining
 * @since 2012-07-10
 */


// Find all primary conceptnames
def toolBox = new vars.ToolBox()
def conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
conceptDao.startTransaction()
def primaryConceptNames = conceptDao.findAll().collect { c -> c?.primaryConceptName?.name }
conceptDao.endTransaction()

// Find all conceptnames used in vars
def observationDao = toolBox.toolBelt.annotationDAOFactory.newObservationDAO()
observationDao.startTransaction()
def usedConceptNames = observationDao.findAllConceptNamesUsedInAnnotations()
observationDao.endTransaction()

primaryConceptNames.removeAll(usedConceptNames)

println("# Primary concept names not used in VARS annotations")
primaryConceptNames.sort { it?.toUpperCase() } each { cn ->
    println(cn)
}
