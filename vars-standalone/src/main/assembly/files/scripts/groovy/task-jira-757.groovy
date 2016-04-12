import vars.ILink
import vars.ToolBox

// Find all Cydippida 2 and Mysidae 2 annotations
// Add a new Association of  surface-color | self | red

def toolbox = new ToolBox()
def adf = toolbox.toolBelt.annotationDAOFactory
def af = toolbox.toolBelt.annotationFactory

def dao = adf.newObservationDAO()

def names = ["Cydippida 2", "Mysidae 2"]

def i = 0
names.each { n ->
    dao.startTransaction()
    def observations = dao.findAllByConceptName("Cydippida 2")
    observations.each { obs ->
        //println(obs)
        def ass = af.newAssociation("surface-color", ILink.VALUE_SELF, "red")
        obs.addAssociation(ass)
        dao.persist(ass)
        i++
    }
    dao.endTransaction()
}

println("Found $i observations")



