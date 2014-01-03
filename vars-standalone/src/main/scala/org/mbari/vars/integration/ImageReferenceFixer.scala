package org.mbari.vars.integration

import org.slf4j.LoggerFactory
import vars.ToolBelt
import vars.knowledgebase.ui.Lookup
import com.google.inject.Injector
import java.net.URL
import java.util.Arrays
import vars.annotation.{CameraDataDAO, CameraData}
import scala.collection.JavaConversions._

/**
 *
 * @author Brian Schlining
 * @since 2010-12-09
 */

class ImageReferenceFixer(toolBelt: ToolBelt, _rootUrl: String) {

    val log = LoggerFactory.getLogger(getClass())
    val rootUrl: String = if (_rootUrl.endsWith("/")) _rootUrl else _rootUrl + "/"


    /**
     *
     */
    def fileUrlToHttpUrl(_fileUrl: String): URL = {
        if (ImageReferenceFixer.isFileURL(_fileUrl)) {
            val fileUrl = _fileUrl.replaceAll(" ", "%20")
            val annotationPersistenceService = toolBelt.getAnnotationPersistenceService
            val platformNames = annotationPersistenceService.findAllPlatformNames()
            var httpString: String = null
            val itr = platformNames.iterator()
            var keepGoing = itr.hasNext()
            do {
                val p = itr.next()
                val platform = p.replaceAll(" ", "%20")
                val idx = fileUrl.indexOf(platform)
                if (idx > -1) {
                    httpString = rootUrl + fileUrl.substring(idx)
                    keepGoing = false
                }
                else {
                    keepGoing = itr.hasNext()
                }
            }
            while (keepGoing)
            new URL(httpString) // return
        }
        else {
            new URL(_fileUrl) // return
        }
    }

    def findCameraDataWithFileUrls() = {
        val dao = toolBelt.getAnnotationDAOFactory.newCameraDataDAO()
        val cameraData = dao.findByImageReferencePrefix(ImageReferenceFixer.FILE_PREFIX)
        dao.close()
        cameraData
    }

    def update[A <% Traversable[CameraData]](cameraDatas: A) {
        val dao = toolBelt.getAnnotationDAOFactory.newCameraDataDAO()
        dao.startTransaction()
        cameraDatas.foreach {
            cd =>
                val cameraData = dao.find(cd)
                update(cameraData)
        }
        dao.endTransaction()
        dao.close()
    }

    def update(cameraData: CameraData) {
        if (cameraData != null) {
            try {
                val url = fileUrlToHttpUrl(cameraData.getImageReference)
                log.debug("Attempting to update " + cameraData.getImageReference + " to " + url)

                if (ImageReferenceFixer.isImageOnWebServer(url)) {
                    cameraData.setImageReference(url.toExternalForm())
                }
            }
            catch {
                case e: Exception => log.warn("Failed to convert URL " + cameraData.getImageReference, e)
            }
        }
    }

}

object ImageReferenceFixer {
    /**
     * This is the key that is used to locate file URLS in the database.
     */
    val FILE_PREFIX: String = "file:"
    /***/
    val GIF_KEY: Array[Byte] = Array[Byte](0x47.asInstanceOf[Byte], 0x49.asInstanceOf[Byte], 0x46.asInstanceOf[Byte])
    /***/
    val JPG_KEY: Array[Byte] = Array[Byte](0x89.asInstanceOf[Byte], 0x50.asInstanceOf[Byte], 0x4E.asInstanceOf[Byte])
    /***/
    val PNG_KEY: Array[Byte] = Array[Byte](0xFF.asInstanceOf[Byte], 0xD8.asInstanceOf[Byte], 0xFF.asInstanceOf[Byte])

    lazy val toolBelt = {
        val injector = Lookup.getGuiceInjectorDispatcher.getValueObject.asInstanceOf[Injector]
        injector.getInstance(classOf[ToolBelt])
    }

    val log = LoggerFactory.getLogger(classOf[ImageReferenceFixer])

    /**
     * @return true if the url is a file url
     */
    def isFileURL(url: String) = {
        url != null && url.toLowerCase.startsWith(FILE_PREFIX)
    }

    def isImageOnWebServer(url: URL) = {
        if (url != null) {
            val b = new Array[Byte](3)
            try {
                val inputStream = url.openStream()
                inputStream.read(b)
                inputStream.close()
            }
            catch {
                case e: Exception => log.info("Unable to open the URL, " + url, e)
            }

            Arrays.equals(b, PNG_KEY) || Arrays.equals(b, JPG_KEY) || Arrays.equals(b, GIF_KEY) // RETURN
        }
        else {
            false // RETURN
        }
    }

    def main(args: Array[String]) {
        if (args.length != 1) {
            print("""
                | Usage: ImageReferenceFixer [rootUrl]
                |
                | Args: rootUrl = The root url that hosts the images. At MBARI
                |          it's http://search.mbari.org/ARCHIVE/frameGrabs/
                    """)
        }

        try {
            fix(args(0))
        }
        catch {
            case e: Exception => log.error("Unable to update image references", e)
        }
    }

    def fix(rootUrl: String) {
        val fixer = new ImageReferenceFixer(toolBelt, rootUrl)
        val cameraDatas = fixer.findCameraDataWithFileUrls()
        fixer.update(cameraDatas)
    }

}