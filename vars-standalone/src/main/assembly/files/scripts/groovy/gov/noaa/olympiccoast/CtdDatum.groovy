package gov.noaa.olympiccoast
/*
From the SeaBird file:

# name 0 = depSM: Depth [salt water, m]
# name 1 = t090C: Temperature [ITS-90, deg C]
# name 2 = sal00: Salinity, Practical [PSU]
# name 3 = sbeox0Mg/L: Oxygen, SBE 43 [mg/l]
# name 4 = sigma-é00: Density [sigma-theta, Kg/m^3]
# name 5 = timeJ: Julian Days
# name 6 = flag:  0.000e+00

Sample Row:
      0.854    13.2875     0.4067   -2.25749    -0.3412 193.385787  0.000e+00

*/

class CtdDatum {
    Float depth
    Float temperature
    Float salinity
    Float oxygen
    Float density
    Double julianDay
    Date date
    Float flag
    
    CtdDatum(float depth, float temperature, float salinity, oxygen, density, Double julianDay, flag, Date date = null) {
        this.depth = depth
        this.temperature = temperature
        this.salinity = salinity
        this.oxygen = oxygen
        this.density = density
        this.julianDay = julianDay
        this.flag = flag
        this.date = date
    }
    
    @Override
    public String toString() {
        return "${getClass().getSimpleName()}[date=${date},temperature=${temperature},salinity=${salinity}]"   
    }
}

