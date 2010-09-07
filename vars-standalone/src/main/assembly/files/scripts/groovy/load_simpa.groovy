import org.mbari.vars.simpa.SimpaLoader
/**
 * Load simpa data from a file
 *
 * Usage:
 * gsh ../scripts/groovy/load_simpa.groovy \
 *     datafile \
 *     rovname \
 *     divenumber \
 *     targetDir \
 *     targetUrl \
 *     commport \
 *     startTimecode\
 *     endTimecode \ 
 *     tapeNumber
 *
 * Example:
 * gsh ../scripts/groovy/load_simpa ./MosaicData.txt Ventana 3467 \
 *    /Volumes/framegrabs http://search.mbari.org/ARCHIVE/frameGrabs/ \
 *    "/dev/tty.RS422 Deck Control" 00:00:00:00 01:00:00:29 1
 *    
 */

def dataFile = new File(args[0])
def cameraId = args[1]
def sequenceId = Integer.parseInt(args[2])
def targetDir = args[3]
def targetUrl = args[4]
def commPort = args[5]
def startTimecode = args[6]
def endTimecode = args[7]
def tapeNumber = Integer.parseInt(args[8])

def simpaLoader = new SimpaLoader(targetDir, targetUrl, commPort)
def data = simpaLoader.read(dataFile.toURI().toURL())
simpaLoader.load(data, cameraId, sequenceId, startTimecode, endTimecode, tapeNumber)
simpaLoader.close()

