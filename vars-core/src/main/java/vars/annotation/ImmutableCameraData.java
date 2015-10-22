package vars.annotation;

import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2015-10-19T17:48:00
 */
public class ImmutableCameraData implements CameraData {


    private final String direction;
    private final Double fieldWidth;
    private final Integer focus;
    private final Float heading;
    private final String imageReference;
    private final Integer iris;
    private final Date logDate;
    private final String name;
    private final Float pitch;
    private final Float roll;
    private final Float viewHeight;
    private final Float viewWidth;
    private final String viewUnits;
    private final Float x;
    private final String xyUnits;
    private final Float y;
    private final Float z;
    private final String zUnits;
    private final Integer zoom;
    private final Object primaryKey;

    public ImmutableCameraData(Object primaryKey, String direction, Double fieldWidth, Integer focus,
            Float heading, String imageReference, Integer iris, Date logDate, String name, Float pitch,
            Float roll, Float viewHeight, Float viewWidth, String viewUnits, Float x, String xyUnits,
            Float y, Float z,String zUnits, Integer zoom) {
        this.direction = direction;
        this.fieldWidth = fieldWidth;
        this.focus = focus;
        this.heading = heading;
        this.imageReference = imageReference;
        this.iris = iris;
        this.logDate = logDate;
        this.name = name;
        this.pitch = pitch;
        this.roll = roll;
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        this.viewUnits = viewUnits;
        this.x = x;
        this.xyUnits = xyUnits;
        this.y = y;
        this.z = z;
        this.zUnits = zUnits;
        this.zoom = zoom;
        this.primaryKey = primaryKey;
    }

    public ImmutableCameraData(CameraData cameraData) {
        this(cameraData.getPrimaryKey(), cameraData.getDirection(), cameraData.getFieldWidth(),
                cameraData.getFocus(), cameraData.getHeading(), cameraData.getImageReference(),
                cameraData.getIris(), cameraData.getLogDate(), cameraData.getName(),
                cameraData.getPitch(), cameraData.getRoll(), cameraData.getViewHeight(),
                cameraData.getViewWidth(), cameraData.getViewUnits(), cameraData.getX(), cameraData.getXYUnits(),
                cameraData.getY(),cameraData.getZ(), cameraData.getZUnits(),
                cameraData.getZoom());
    }

    @Override
    public boolean containsData() {
        return true;
    }

    @Override
    public String getDirection() {
        return direction;
    }

    @Override
    public Double getFieldWidth() {
        return fieldWidth;
    }

    @Override
    public Integer getFocus() {
        return focus;
    }

    @Override
    public Float getHeading() {
        return heading;
    }

    @Override
    public String getImageReference() {
        return imageReference;
    }

    @Override
    public Integer getIris() {
        return iris;
    }

    @Override
    public Date getLogDate() {
        return logDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Float getPitch() {
        return pitch;
    }

    @Override
    public Float getRoll() {
        return roll;
    }

    public String getXYUnits() {
        return xyUnits;
    }

    public String getZUnits() {
        return zUnits;
    }

    @Override
    public VideoFrame getVideoFrame() {
        throw new UnsupportedOperationException("Immutable objects do not have references. Use DAO find methods instead");
    }

    @Override
    public Float getViewHeight() {
        return viewHeight;
    }

    @Override
    public Float getViewWidth() {
        return viewWidth;
    }

    @Override
    public String getViewUnits() {
        return viewUnits;
    }

    @Override
    public Float getX() {
        return x;
    }

    @Override
    public Float getY() {
        return y;
    }

    @Override
    public Float getZ() {
        return z;
    }

    @Override
    public Integer getZoom() {
        return zoom;
    }

    @Override
    public void setDirection(String direction) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setFieldWidth(Double fieldWidth) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setFocus(Integer focus) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setHeading(Float tilt) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setImageReference(String stillImage) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setIris(Integer iris) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setLogDate(Date logDate) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setPitch(Float pitch) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setRoll(Float roll) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setViewHeight(Float height) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setViewUnits(String units) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setViewWidth(Float width) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setX(Float x) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setXYUnits(String units) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setY(Float y) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setZ(Float z) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setZUnits(String units) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public void setZoom(Integer zoom) {
        throw new UnsupportedOperationException("Set methods are not supported on immutable objects");
    }

    @Override
    public Object getPrimaryKey() {
        return primaryKey;
    }
}
