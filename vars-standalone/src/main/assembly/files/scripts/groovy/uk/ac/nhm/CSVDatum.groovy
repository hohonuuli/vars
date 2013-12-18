package uk.ac.nhm

/**
 * Created by brian on 12/18/13.
 */
class CSVDatum {
    Date recordedDate
    Double depth
    Double latitude
    Double longitude

    CSVDatum() {}

    @Override
    String toString() {
        return "CSVDatum{recordedDate=" + recordedDate + '}';
    }
}

