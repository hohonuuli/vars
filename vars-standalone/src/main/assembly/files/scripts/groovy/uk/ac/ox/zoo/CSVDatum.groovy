package uk.ac.ox.zoo

/**
 * 
 * @author Brian Schlining
 * @since 2012-09-06
 */
class CSVDatum {

    Date logDate
    Date recordedDate
    Double altitude
    Double depth
    Double latitude
    Double longitude
    Double salinity
    Double temperature
    Double xVelocity
    Double yVelocity
    Double zVelocity

    CSVDatum() {}

    @Override
    String toString() {
        return "CSVDatum{recordedDate=" + recordedDate + '}';
    }
}
