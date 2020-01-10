if (args.size() != 2) {
  println("""
  Usage: gsh change_video_urls <oldUrlPrefix> <newUrlPrefix>

  Args:
    oldUrlPrefix =  The string that we're matching for and want to replace. e.g
      file://Path/to/
    
    newUrlPrefix = The replacement prefix. e.g. http://shiny.newserver.com/path/to/

  Example: 
    Say we have the following batch of videos:
      file://Path/to/a.mp4
      file://Path/to/b.mov
      file://Path/to/dir/c.mp4
      file://different/path/d.mp4

    If we run: change_video_urls 'file://Path/to' 'file://home/dir' The images will now be
      file://home/dir/a.mp4
      file://home/dir/b.mov
      file://home/dir/dir/c.mp4
      file://different/path/d.mp4

  """)
  return
}

def oldUrlPrefix = args[0]
def newUrlPrefix = args[1]

def toolBelt = new vars.ToolBox().toolBelt
def dao = toolBelt.annotationDAOFactory.newVideoArchiveSetDAO()
dao.startTransaction()
def matchingVideoSets = dao.findAll()
def n = 0
for (videoSet in matchingVideoSets) {
  for (videoArchive in videoSet.videoArchives) {
    if (videoArchive.name.startsWith(oldUrlPrefix)) {
      def newName = videoArchive.name.replace(oldUrlPrefix, newUrlPrefix)
      println("Changing ${videoArchive.name} to ${newUrl}")
      videoArchive.name = newName
      n = n + 1
    }
  }

}
dao.endTransaction()
dao.close()
println("Changed ${n} video URLs")
