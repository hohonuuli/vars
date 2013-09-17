import vars.knowledgebase.Media

def toolBox = new vars.ToolBox()
def conceptDao = toolBox.toolBelt.knowledgebaseDAOFactory.newConceptDAO()
conceptDao.startTransaction()
def concepts = conceptDao.findAll()
def conceptWithImages = []
for (c in concepts) {
    if (c.conceptMetadata.medias.find { it.type.equals(Media.TYPE_IMAGE) }) {
        conceptWithImages << c
    }
}
conceptDao.endTransaction()
conceptDao.close()
conceptWithImages.sort { it.primaryConceptName.name }

if (conceptWithImages.isEmpty()) {
    println("No concepts with images were found")
}
else {
    println("Found ${conceptWithImages.size()} concepts with images:")
}
for (c in conceptWithImages) {
    println(c.primaryConceptName.name)
}