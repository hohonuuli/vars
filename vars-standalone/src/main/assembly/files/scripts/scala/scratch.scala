
def findCam(id: Long, dir: Option[String] = None): vars.annotation.jpa.CameraDataImpl = {
  import vars.ToolBox
  import vars.annotation.jpa.CameraDataImpl
  val tb = new ToolBox
  val adf = tb.getToolBelt.getAnnotationDAOFactory
  val camdao = adf.newCameraDataDAO
  camdao.startTransaction
  val cd = camdao.findByPrimaryKey(id)
  val cdi = cd.asInstanceOf[CameraDataImpl]
  val f = classOf[CameraDataImpl].getDeclaredField("updatedTime")
  f.setAccessible(true)
  println(f.get(cdi))
  dir.foreach(d => cdi.setDirection(d))
  camdao.endTransaction
  println(f.get(cdi))
  cdi
}