import gov.noaa.olympiccoast.HabitatReader
import gov.noaa.olympiccoast.HabitatLoader

if (args.size() < 2) {
    println("""\
    Load OCNMS habitat video annotations into VARS. It expects the files to
    have the following formats:
    
    Year	Dive #	Site	Date	Clip_Name	Transect	Hab code	Survey activity	Start time	End time	Comments
    2008	1162	10	7/12/2008	0001HT	1	XX	tcx	15:44:00	15:44:41	start tran 1								

    Use as: 
        load_ocnms_hab platform habFile
    
    Args:
        platform: the name of the ROV, e.g. Ropos
        habFile: The path to the habitat file to load
    
    """.stripMargin())
    return
}   

def platform = args[0]
def file = new File(args[1])

def data = HabitatReader.read(file)
new HabitatLoader().load(data, platform)
