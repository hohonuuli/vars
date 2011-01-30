import gov.noaa.olympiccoast.CtdReader
import gov.noaa.olympiccoast.MergeCtd

if (args.size() < 3) {
    println("""\
    Merge OCNMS ctd data with VARS annotations. It expects the files to
    have the following formats:\n
    
    From the SeaBird file:\n

    # name 0 = depSM: Depth [salt water, m]
    # name 1 = t090C: Temperature [ITS-90, deg C]
    # name 2 = sal00: Salinity, Practical [PSU]
    # name 3 = sbeox0Mg/L: Oxygen, SBE 43 [mg/l]
    # name 4 = sigma-é00: Density [sigma-theta, Kg/m^3]
    # name 5 = timeJ: Julian Days
    # name 6 = flag:  0.000e+00\n
    
    Sample Row:
          0.854    13.2875     0.4067   -2.25749    -0.3412 193.385787  0.000e+00\n
    
    Use as: 
        load_ocnms_obs platform sequenceNumber year ctdFile\n
    
    Args:
        platform: the name of the ROV, e.g. Ropos
        sequenceNumber: The dive number
        year: The year that the CTD data was collected (4-digit e.g. 2011)
        ctdFile: The path to the observation file to load
    
    """.stripMargin())
    return
}   

def platform = args[0]
def sequenceNumber = Integer.parseInt(args[1])
def year = Integer.parseInt(args[2])
def file = new File(args[3])
new MergeCtd(platform, sequenceNumber, file, year).apply()
