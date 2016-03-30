package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.annotation.AnnotationFactory;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsAddedEvent;
import vars.annotation.ui.eventbus.ObservationsRemovedEvent;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2011-10-17
 */
public class DuplicateObservationsCmd implements Command {

    private final String user;
    private final Collection<Observation> sourceObservations;
    private final Collection<Observation> duplicateObservations = Collections.synchronizedCollection(new ArrayList<Observation>());
    private final boolean selectObservations;

    public DuplicateObservationsCmd(String user, Collection<Observation> sourceObservations, boolean selectObservations) {
        if (user == null || sourceObservations == null) {
            throw new IllegalArgumentException("Command arguments can not be null");
        }
        this.user = user;
        this.sourceObservations = new ArrayList<Observation>(sourceObservations);
        this.selectObservations = selectObservations;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        Date observationDate = new Date();
        synchronized (duplicateObservations) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            for (Observation observation : sourceObservations) {
                observation = dao.find(observation);
                VideoFrame videoFrame = observation.getVideoFrame();
                Observation newObservation = annotationFactory.newObservation();
                newObservation.setConceptName(observation.getConceptName());
                newObservation.setObservationDate(observationDate);
                newObservation.setObserver(user);
                videoFrame.addObservation(newObservation);
                dao.persist(newObservation);
                duplicateObservations.add(newObservation);
            }
            dao.endTransaction();
        }
        EventBus.publish(new ObservationsAddedEvent(null, duplicateObservations));
        if (selectObservations) {
            EventBus.publish(new ObservationsSelectedEvent(null, duplicateObservations));
        }
    }
    

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> deletedObservations = new ArrayList<Observation>(duplicateObservations);
        synchronized (duplicateObservations) {
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            for (Observation observation : duplicateObservations) {
                deletedObservations.add(observation); // Add before bringing into transaction. Hashcode changes after delete.
                observation = dao.find(observation);

                VideoFrame videoFrame = observation.getVideoFrame();
                videoFrame.removeObservation(observation);
                dao.remove(observation);

                if (videoFrame.getObservations().size() == 0) {
                    dao.remove(videoFrame);
                }
            }
            dao.endTransaction();
            duplicateObservations.clear();
        }
        EventBus.publish(new ObservationsRemovedEvent(null, deletedObservations));

    }

    @Override
    public String getDescription() {
        return "Duplicate " + sourceObservations.size() + " observations";
    }
}
