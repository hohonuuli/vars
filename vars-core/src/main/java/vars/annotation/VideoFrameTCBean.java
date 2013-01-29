package vars.annotation;

import java.util.Date;
import java.util.List;

/**
 * Mockup bean that holds a timecode. Used for timecode lookup.
 * @author Brian Schlining
 * @since 2013-01-28
 */
public class VideoFrameTCBean implements VideoFrame {

    private final String timecode;

    public VideoFrameTCBean(String timecode) {
        this.timecode = timecode;
    }

    @Override
    public void addObservation(Observation obs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CameraData getCameraData() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Observation> getObservations() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public PhysicalData getPhysicalData() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public VideoArchive getVideoArchive() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean hasImageReference() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isInSequence() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeObservation(Observation obs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setAlternateTimecode(String altTimecode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setInSequence(boolean state) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setRecordedDate(Date dtg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setTimecode(String timecode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object getPrimaryKey() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getTimecode() {
        return timecode;
    }

    @Override
    public String getAlternateTimecode() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Date getRecordedDate() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
