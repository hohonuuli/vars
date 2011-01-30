package gov.noaa.olympiccoast

/**
 * 
 * @author Brian Schlining
 * @since 2011-01-24
 */
class NavigationDatum {
    Integer dive
    Float depth
    Double latitude
    Double longitude
    Date date
    Float pitch
    Float roll
    Float heading
    Float altitude
    Float speed


    NavigationDatum(int dive, float depth, double latitude, double longitude, Date date,
                    pitch, roll, heading, altitude, speed) {
        this.dive = dive
        this.depth = depth
        this.latitude = latitude
        this.longitude = longitude
        this.date = date
        this.pitch = pitch
        this.roll = roll
        this.heading = heading
        this.altitude = altitude
        this.speed = speed
    }
    
    @Override
    public String toString() {
        return "${getClass().simpleName}[dive=$dive,latitude=$latitude,longitude=$longitude]"
    }
}
