/**
 * Usage: export_simpa [videoArchiveName] [targetFileName]
 */

def t = new vars.ToolBox()
def dao = t.toolBelt.annotationDAOFactory.newVideoArchiveDAO()       
def f = new File(args[1])
def va = dao.findByName(args[0])
def se = new org.mbari.vars.simpa.SimpaExporter(t.toolBelt, va.videoArchiveSet)
se.apply(f)