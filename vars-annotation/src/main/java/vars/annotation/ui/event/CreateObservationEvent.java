package vars.annotation.ui.event;

import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.knowledgebase.Concept;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 * An event to create a new observation. This is an immutable class
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class CreateObservationEvent {

    private final Concept concept;
    private final VideoFrame videoFrame;
    private final Point2D point;
    private final Date date;

    /**
     *
     * @param concept The concept to use for the created observation
     * @param videoFrame The videoframe to attach the observation to
     * @param point A point on an image for the observation, this can be <b>null</b>
     * @param date The date the observation was created
     */
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
