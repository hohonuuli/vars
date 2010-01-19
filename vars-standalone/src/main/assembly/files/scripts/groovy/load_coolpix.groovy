import vars.coolpix.CoolpixLoader
/**
 * Example:
 * gsh ../scripts/groovy/load_coolpix.groovy \
 *   /Volumes/DigitalImages/DocRicketts/2009/docr84 \
 *   'Doc Ricketts' \
 *   84 \
 *   http://search.mbari.org/ARCHIVE/digitalImages/DocRicketts/2009/docr84/
 */

def dir = new File(args[0])             // Path to the directory containing the coolpix images
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

