package vars.annotation.ui.table.events;

import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

import java.awt.geom.Point2D;
import java.util.Date;

/**
 * The main implementation of a CreateObservationListener. This implementation
 * does the following:
 * <ol>
 *  <li>Creates a new Observation using the concept, observation and videoframe 
 *    provided. </li>
 *  <li>
 * </ol>
 * @author Brian Schlining
 * @since 2011-08-31
 */
public class CreateObservationListenerImpl implements CreateObservationListener {

    private final AnnotationFactory annotationFactory;
    private final PersistenceController persistenceController;

    public CreateObservationListenerImpl(AnnotationFactory annotationFactory, PersistenceController persistenceController) {
        this.annotationFactory = annotationFactory;
        this.persistenceController = persistenceController;
    }

    @Override
    public void doCreate(CreateObservationEvent event) {
        final VideoFrame videoFrame =  event.getVideoFrame();
        Point2D point = event.getPoint();

        UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
        if ((userAccount != null) && (videoFrame != null)) {
            Observation observation = annotationFactory.newObservation();
            observation.setConceptName(event.getConcept().getPrimaryConceptName().getName());
            observation.setObservationDate(new Date());
            observation.setObserver(userAccount.getUserName());
            if (point != null) {
                observation.setX(point.getX());
                observation.setY(point.getY());
            }

            // The persistence controller will trigger an update to setVideoFrame on this class
            persistenceController.insertObservation(videoFrame, observation);
        }
    }
}
