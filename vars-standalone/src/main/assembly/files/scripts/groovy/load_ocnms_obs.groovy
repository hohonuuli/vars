import gov.noaa.olympiccoast.ObsReader
import gov.noaa.olympiccoast.ObsLoader

if (args.size() < 3) {
    println("""\
    Load OCNMS observational video annotations into VARS. It expects the files to
    have the following formats:
    
    \"Logdate\",\"dive\",\"Speciesrpt\",\"TimeCode\",\"Camera_view\"
    \"2008-07-12 17:41:11\",\"1162\",\"Anemone\",\"17 41 11 10\",\"vertical\"
    
    Use as: 
        load_ocnms_obs platform sequenceNumber obsFile
    
    Args:
        platform: the name of the ROV, e.g. Ropos
        sequenceNumber: The dive number
        obsFile: The path to the observation file to load
    
    """.stripMargin())
    return
}   

def platform = args[0]
def sequenceNumber = Integer.parseInt(args[1])
def file = new File(args[2])

def data = ObsReader.read(file)
new ObsLoader().load(data, platform, sequenceNumber)
