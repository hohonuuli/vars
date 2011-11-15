import org.mbari.vars.tripod.TripodLoader
/**
 * 
 * @author Brian Schlining
 * @since 2011-11-14
 */


if (args.size() < 6) {
    println("""
    | Usage:
    |   gsh load_tripod <url> <sequence number> <longitude> <latitude> <depth> \
    |       <lens to seafloor distance> <angle of inclination of camera>
    |
    | Args:
    |   url = The url that gives a directory listing of the images to load
    |         (e.g. http://tripod.shore.mbari.org/ImageArchive/TripodM/Pulse%2055/101NC_D3/)
    |   sequence number = the Deployment number (e.g. 55)
    |   longitude = The longitude of the deployed camera
    |   latitude = The latitude of the deployed camera
    |   depth = The tripods depth in meters
    |   lens to seafloor distance = Distance in meters from the lens to the seafloor.
    |   angle of inclination = Angle of the camera in degrees, from level horizontal, towards seafloor.
    |                          downwards is positive.
    |
    """.stripMargin('|'))
    return
}


def remoteDirectoryUrl = new URL(args[0])
def platform = 'Tripod M'
def sequenceNumber = Integer.parseInt(args[1])
def longitude = Double.parseDouble(args[2])
def latitude = Double.parseDouble(args[3])
def depth = Double.parseDouble(args[4])
def lensToSeaFloorDistance = Double.parseDouble(args[5])
def angleOfInclination = Double.parseDouble(args[6])

def loader = new TripodLoader()
loader.load(remoteDirectoryUrl, platform, sequenceNumber, longitude, latitude, depth, lensToSeaFloorDistance, angleOfInclination)


