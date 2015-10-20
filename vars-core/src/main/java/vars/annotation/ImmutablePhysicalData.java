package vars.annotation;

import vars.annotation.PhysicalData;
import vars.annotation.VideoFrame;

import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2015-10-19T11:21:00
 */
public class ImmutablePhysicalData implements PhysicalData {

    private final Float altitude;
    private final Float depth;
    private final Double latitude;
    private final Float light;
    private final Date logDate;
    private final Double longitude;
    private final Float oxygen;
    private final Float salinity;
    private final Float temperature;
    private final Object primaryKey;

    public ImmutablePhysicalData(Object primaryKey, Float altitude, Float depth, Double latitude, Float light,
            Date logDate, Double longitude, Float oxygen, Float salinity, Float temperature) {
        this.primaryKey = primaryKey;
        this.altitude = altitude;
        this.depth = depth;
        this.latitude = latitude;
        this.light = light;
        this.logDate = logDate;
        this.longitude = longitude;
        this.oxygen = oxygen;
        this.salinity = salinity;
        this.temperature = temperature;
    }

    public ImmutablePhysicalData(PhysicalData physicalData) {
        this.primaryKey = physicalData.getPrimaryKey();
        this.altitude = physicalData.getAltitude();
        this.depth = physicalData.getDepth();
        this.latitude = physicalData.getLatitude();
        this.light = physicalData.getLight();
        this.logDate = physicalData.getLogDate();
        this.longitude = physicalData.getLongitude();
        this.oxygen = physicalData.getOxygen();
        this.salinity = physicalData.getSalinity();
        this.temperature = physicalData.getTemperature();
    }

    @Override
    public boolean containsData() {
        return true;
    }

    @Override
    public Float getAltitude() {
        return altitude;
    }

    @Override
    public Float getDepth() {
        return depth;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Float getLight() {
        return light;
    }

    @Override
    public Date getLogDate() {
        return logDate;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public Float getOxygen() {
        return oxygen;
    }

    @Override
    public Float getSalinity() {
        return salinity;
    }

    @Override
    public Float getTemperature() {
        return temperature;
    }

    @Override
    public VideoFrame getVideoFrame() {
        throw new UnsupportedOperationException("Immutable objects do not have references. Use DAO find methods instead");
    }

    @Override
    public void setAltitude(Float altitude) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setDepth(Float depth) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setLatitude(Double latitude) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setLight(Float light) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setLogDate(Date logDate) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setLongitude(Double longitude) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setOxygen(Float oxygen) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setSalinity(Float salinity) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setTemperature(Float temperature) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public Object getPrimaryKey() {
        return primaryKey;
    }
}
