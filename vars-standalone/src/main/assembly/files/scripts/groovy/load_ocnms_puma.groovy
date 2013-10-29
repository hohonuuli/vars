import gov.noaa.olympiccoast.PumaLoader

if (args.size() < 3) {
    println("""\
    Merge PUMA aerial vehicle data with VARS annotations.

    Use as:
        load_ocnms_puma platform sequenceNumber pumadatafile

    Args:
        platform: the name of the ROV, e.g. Ropos
        sequenceNumber: The dive number
        pumadatafile: The puma data file to load
    """.stripMargin())
    return
}

def platform = args[0]
def sequenceNumber = Integer.parseInt(args[1])
def file = new File(args[2])
def loader = new PumaLoader(platform, sequenceNumber)
loader.merge(file)