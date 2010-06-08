import vars.simpa.SimpaLoader
/**
 * ./gsh ../scripts/groovy/load_simpa.groovy \
 *     ./MosaicData.txt \
 *     Ventana \
 *     3467 \
 *     /Volumes/framegrabs \
 *     http://search.mbari.org/ARCHIVE/frameGrabs/ \
 *     "/dev/tty.RS422 Deck Control" \
 *     01:59:00:00 \
 *     03:00:00:00 \
 *     3
 * @author brian
 */

def datafile = new File(args[0])
def platform = args[1]
def sequenceNumber = args[2]
def targetDir = args[3]
def targetUrl = args[4]
def commport = args[5]
def startTimecode = args[6]
def endTimecode = args[7]
def tapeNumber = args[8]

def simpaLoader = new SimpaLoader(targetDir, targetUrl, commport);
def data = simpaLoader.read(datafile.toURI().toURL())
simpaLoader.load(data, platform, sequenceNumber, startTimecode, endTimecode, tapeNumber)
