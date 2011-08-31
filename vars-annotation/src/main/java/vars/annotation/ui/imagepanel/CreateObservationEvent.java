package vars.annotation.ui.imagepanel;

import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.knowledgebase.Concept;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class CreateObservationEvent {

    private final Concept concept;
    private final VideoFrame videoFrame;
    private final Point2D point;
    private final Date date;

    public CreateObservationEvent(Concept concept, VideoFrame videoFrame, Point2D point, Date date) {
        this.concept = concept;
        this.videoFrame = videoFrame;
        this.point = point;
        this.date = date;
    }

    public Concept getConcept() {
        return concept;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public Point2D getPoint() {
        return point;
    }

    public Date getDate() {
        return date;
    }
}
