import vars.coolpix.CoolpixLoader
/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Jan 14, 2010
 * Time: 3:31:31 PM
 * To change this template use File | Settings | File Templates.
 */

def dir = new File(args[0])             // File URL to the directory containing the coolpix images
def platform = args[1]                  // ROV Name
def sequenceNumber = args[2] as Integer // Dive number
def webMapping = args[3]                // URL that maps 'dir'
webMapping = webMapping.endsWith("/") ? webMapping : webMapping + "/"

def images = dir.listFiles({d, name -> 
    name = name.toLowerCase();
    !name.endsWith("t.jpg") && name.endsWith(".jpg") } as FilenameFilter)


def urls = []
for (image in images) {
    urls << new URL("${webMapping}${image.name}")
}

println("Found the following images ...")
urls.each {println it}
def coolpixLoader = new CoolpixLoader()
coolpixLoader.load(urls, platform, sequenceNumber)

