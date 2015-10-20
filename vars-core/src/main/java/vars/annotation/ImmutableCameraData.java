package vars.annotation;

import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2015-10-19T17:48:00
 */
public class ImmutableCameraData implements CameraData {

    @Override
    public boolean containsData() {
        return false;
    }

    @Override
    public String getDirection() {
        return null;
    }

    @Override
    public Double getFieldWidth() {
        return null;
    }

    @Override
    public Integer getFocus() {
        return null;
    }

    @Override
    public Float getHeading() {
        return null;
    }

    @Override
    public String getImageReference() {
        return null;
    }

    @Override
    public Integer getIris() {
        return null;
    }

    @Override
    public Date getLogDate() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Float getPitch() {
        return null;
    }

    @Override
    public Float getRoll() {
        return null;
    }

    @Override
    public VideoFrame getVideoFrame() {
        return null;
    }

    @Override
    public Float getViewHeight() {
        return null;
    }

    @Override
    public String getViewUnits() {
        return null;
    }

    @Override
    public Float getViewWidth() {
        return null;
    }

    @Override
    public Float getX() {
        return null;
    }

    @Override
    public String getXYUnits() {
        return null;
    }

    @Override
    public Float getY() {
        return null;
    }

    @Override
    public Float getZ() {
        return null;
    }

    @Override
    public String getZUnits() {
        return null;
    }

    @Override
    public Integer getZoom() {
        return null;
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
        return null;
    }
}
