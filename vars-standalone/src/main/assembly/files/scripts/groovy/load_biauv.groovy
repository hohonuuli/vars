
/**
 * 
 * @author Brian Schlining
 * @since Sep 7, 2010
 */

/**
 * Example:
 * gsh ../scripts/groovy/load_benthicrover.groovy \
 *   /Volumes/DigitalImages/DocRicketts/2009/docr84 \
 *   84 \
 *   http://search.mbari.org/ARCHIVE/digitalImages/DocRicketts/2009/docr84/
 */

 if (args.size() < 3) {
     println("""\
            Usage: load_biauv missionDirectory webMapping

            Arguments:
                imageDirectory = The name of the directory containing the images to load
                    (this script does NOT recurse through subdirectories)
                sequenceNumber = The rovers 'dive number'
                webMapping = The directory on a web server that maps to imageDirectory

            Example:
                gsh ../scripts/groovy/load_benthicrover.groovy \
                '/Volumes/ProjectLibrary/900502.BenthicRover/Rover.Deployment/0910-MARS-Lobos/TransitImages/VARS JPEG Images' \
                1 \
                http://seaspray.shore.mbari.org/vars/data/framegrabs/Benthic%20Rover/images/0001/

            """.stripIndent())
     return
 }

//def dir = new File(args[0])             // Path to the directory containing the benthic rover images
//def platform = "Benthic Rover"          // ROV Name
//def sequenceNumber = args[1] as Integer // Dive number
//def webMapping = args[2]                // URL that maps 'dir'
//webMapping = webMapping.endsWith("/") ? webMapping : webMapping + "/"
//
//def images = dir.listFiles({d, name ->
//    name = name.toLowerCase();
//    name.endsWith(".jpeg") || name.endsWith(".jpg") } as FilenameFilter)
//
//def urls = []
//for (image in images) {
//    urls << new URL("${webMapping}${image.name}")
//}
//
//println("Found the following images ...")
//urls.each {println it}
//def loader = new RoverLoader()
//loader.load(urls, platform, sequenceNumber)

