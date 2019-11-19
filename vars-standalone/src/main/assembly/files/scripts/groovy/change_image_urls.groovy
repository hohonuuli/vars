if (args.size() != 2) {
  println("""
  Usage: gsh change_image_urls <oldUrlPrefix> <newUrlPrefix>

  Args:
    oldUrlPrefix =  The string that we're matching for and want to replace. e.g
      file://Path/to/
    
    newUrlPrefix = The replacement prefix. e.g. http://shiny.newserver.com/path/to/

  Example: 
    Say we have the following batch of images:
      file://Path/to/a.jpg
      file://Path/to/b.png
      file://Path/to/dir/c.jpg
      file://different/path/d.jpg

    If we run: change_image_urls 'file://Path/to' 'file://home/dir' The images will now be
      file://home/dir/a.jpg
      file://home/dir/b.png
      file://home/dir/dir/c.jpg
      file://different/path/d.jpg

  """)
  return
}

def oldUrlPrefix = args[0]
def newUrlPrefix = args[1]

def toolBelt = new vars.ToolBox().toolBelt
def dao = toolBelt.annotationDAOFactory.newCameraDataDAO()
dao.startTransaction()
def matchingImages = dao.findByImageReferencePrefix(oldUrlPrefix)
def n = 0
for (image in matchingImages) {
  def newUrl = image.imageReference.replace(oldUrlPrefix, newUrlPrefix)
  println("Changing ${image.imageReference} to ${newUrl}")
  image.imageReference = newUrl
  n = n + 1
}
dao.endTransaction()
dao.close()
println("Changed ${n} image URLs")
