package uk.ac.ox.zoo

/**
 * 
 * @author Brian Schlining
 * @since 2012-09-06
 */
class CSVDatum {

    Date recordedDate
    Double depth
    Double temperature
    Double salinity
    Date logDate
    Double latitude
    Double longitude

    CSVDatum(Date recordedDate, Double depth, Double temperature, Double salinity, Date logDate,
             Double latitude, Double longitude) {
        this.recordedDate = recordedDate
        this.depth = depth
        this.temperature = temperature
        this.salinity = salinity
        this.logDate = logDate
        this.latitude = latitude
        this.longitude = longitude
    }

    CSVDatum() {}

    @Override
    String toString() {
        return "CSVDatum{recordedDate=" + recordedDate + '}';
    }
}
