def names = ["T35103-01-tripod", "Tripod Pulse 02 Station 211", 
    "Tripod Pulse 04-2", "Tripod Pulse 07", "Tripod Pulse 26-1", 
    "Tripod Pulse 27", "Tripod Pulse 33-1", "Tripod Pulse 42-1", 
    "Tripod Pulse 42-2", "Tripod Pulse 42-3", "Tripod Pulse 45-2", 
    "Tripod Pulse 45-3", "T0021-01-tripod", "T0051-01-tripod", 
    "T0068-01-tripod", "T0301-01-tripod", "T0302-01-tripod", 
    "T0303-01-tripod", "T0432-01-tripod", "T0677-01-tripod", 
    "T0688-01-tripod", "T0697-01-tripod", "T0701-01-tripod", 
    "T1070-02", "T35103-01-tripod"]

def t = new vars.ToolBox()
def dao = t.toolBelt.annotationDAOFactory.newVideoArchiveDAO()

for (n in names) {
  println("-- Deleting ${n}")
  def dao = t.toolBelt.annotationDAOFactory.newVideoArchiveDAO()
  dao.startTransaction()
  def va = dao.findByName(n)
  print(va)
  if (va) {
    def vas = va.videoArchiveSet
    dao.remove(vas)
  }
  dao.endTransaction()
  dao.close()
}


