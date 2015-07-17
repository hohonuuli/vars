#!/usr/bin/env scala

if (args.size != 2) {
 println("""
  |
  | Usage:
  |  fetch_pngs.scala [queryfile] [targetroot]
  |
  | Inputs:
  |  queryfile = A file saved from VARS Query
  |  targetroot = The directory to save the images too. It will maintain the
  |              directory structure of [ROV/[dive number] when downloading images
  |
  """.stripMargin('|'))
}

import java.io.File
import java.net.URL
import java.nio.file.Files
import scala.util.{Try, Success, Failure}

val source = scala.io.Source.fromFile(args(0))
val lines = source.getLines.filter(_.length > 0)
val urls = lines.map(
   _.split("\t")
    .filter(u => u.startsWith("http") && u.endsWith(".jpg"))
    .map(_.replace(".jpg", ".png")))
  .flatten


val baseDir = new File(args(1))
for (png <- urls) {
  val url = new URL(png)
  val target = urlToLocalPath(baseDir, url)
  print(s"Copying $url to $target")

  Try {
    val in = url.openStream()
    val parent = target.getParentFile
    if (!parent.exists()) target.mkdirs()
    Files.copy(in, target.toPath)
    in.close()
  } match {
    case Success(_) => print(" ... DONE\n")
    case Failure(e) => {
      println(s" ... FAILED: $e")
      if (target.exists()) target.delete()
    }
  }
}

def urlToLocalPath(baseDir: File, url: URL): File = {

  val parts = url.toExternalForm().replace("%20", " ").split("/").toList;
  val idx = parts.map(_.toLowerCase).indexOf("framegrabs") + 1;

  def pathBuilder(dir: File, s: Seq[String]): File = {
    if (s.isEmpty) dir
    else pathBuilder(new File(dir, s.head), s.tail)
  }

  val goodParts = parts.drop(idx)
  pathBuilder(baseDir, goodParts)

}

source.close()
