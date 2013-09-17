package gov.noaa.olympiccoast

class PumaDatum {

    double elapsedTime
    Date date
    double altitude
    double heading
    double latitude
    double longitude

    def PumaDatum() {}

    def PumaDatum(double elapsedTime, Date date, double altitude, double heading,
        double latitude, double longitude) {

        this.elapsedTime = elapsedTime
        this.date = date
        this.altitude = altitude
        this.heading = heading
        this.latitude = latitude
        this.longitude = longitude

    }

    @Override
    public String toString() {
        return "${getClass().simpleName}[date=${date},latitude=${latitude},longitude=${longitude}"
    }

}