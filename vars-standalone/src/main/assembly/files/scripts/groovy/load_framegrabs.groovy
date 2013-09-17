import vars.annotation.AutoFramegrabLoader
/**
 * @author Brian Schlining
 * @since 2012-09-04
 */

if (args.size() != 7) {
    println("""\
          Grabs framegrabs from a VCR at a specified interval and loads them into VARS

          Usage:
            gsh ../scripts/groovy/load_framegrabs.groovy \\
              rovname divenumber targetDir targetUrl commport tapeNumber intervalInSeconds

          Example:
            gsh ../scripts/groovy/load_framegrabs Ventana 3467 \\
              /Volumes/framegrabs http://search.mbari.org/ARCHIVE/frameGrabs/ |
              "/dev/tty.RS422 Deck Control" 1 3

    """.stripIndent())
    return
}


def cameraId = args[0]
def sequenceId = Integer.parseInt(args[1])
def targetDir = args[2]
def targetUrl = args[3]
def commPort = args[4]
def tapeNumber = Integer.parseInt(args[5])
def intervalInSeconds = Integer.parseInt(args[6])

def autoLoader = new AutoFramegrabLoader(targetDir, targetUrl, commPort, intervalInSeconds)
autoLoader.load(cameraId, sequenceId, intervalInSeconds, tapeNumber)
autoLoader.close()
