import vars.ToolBox

def conceptName = args[0]

def t = new ToolBox()
def kbf = t.toolBelt.knowledgebaseDAOFactory
def dao = kbf.newConceptDAO()
def critter = dao.findByName(conceptName)
if (!critter) {
    println("Unable to find $conceptName in the knowledgebase")
}
dao.close()
def aps = t.toolBelt.annotationPersistenceService
def f = aps.findDescendantNamesFor(critter)
f.each { println it}
