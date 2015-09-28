/*
Load OCNMS navigation data into VARS. 

@author Brian Schlining
@since 2011-01-25

*/
import gov.noaa.olympiccoast.MergeNavigation

// Parse Arguments

if (args.size() < 3) {
    println("""\
    Load OCNMS navigation data into VARS. It expects the nav file to have the 
    following formats:

    Dive,Depth,ROV_Latitude,ROV_Longitude,Logdate,Pitch,Roll,Heading,Altimeter,Speed
    1157,297.777777,48.403987,-124.981079,\"2008-07-09 09:13:20\",-0.477777,3.700000,215.600000,0.600000,0

    Use as:
        load_ocnms_nav platform sequenceNumber navFile
    
    Args:
        platform: the name of the ROV, e.g. Ropos
        sequenceNumber: The dive number
        navFile: The path to the navigation file to load
        
    """.stripMargin())
    return
}

def platform = args[0]
def sequenceNumber = Integer.parseInt(args[1])
def navFile = new File(args[2])

// Create merge function
def mergeFunction = new MergeNavigation(platform, sequenceNumber)
mergeFunction.merge(navFile)


