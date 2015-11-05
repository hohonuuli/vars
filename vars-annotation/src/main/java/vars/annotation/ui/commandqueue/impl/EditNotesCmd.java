package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.EventBus;
import vars.DAO;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoFrame;
import vars.annotation.jpa.ObservationImpl;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Brian Schlining
 * @since 2015-11-05T13:01:00
 */
public class EditNotesCmd implements Command {

    private final String newNote;
    private final String oldNote;
    private final Long observationId;

    public EditNotesCmd(String newNote, String oldNote, Long observationId) {
        this.newNote = newNote;
        this.oldNote = oldNote;
        this.observationId = observationId;
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);

    }

    private void doCommand(ToolBelt toolBelt, boolean isApply) {
        DAO dao = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        dao.startTransaction();
        Observation observation = dao.findByPrimaryKey(ObservationImpl.class, observationId);
        ObservationsChangedEvent updateEvent = null;
        if (observation != null) {
            String note = isApply ? newNote : oldNote;
            observation.setNotes(note);
            Collection<Observation> changedObservations = Collections.singletonList(observation);
            updateEvent = new ObservationsChangedEvent(null, changedObservations);
        }
        dao.endTransaction();
        dao.close();
        if (updateEvent != null) {
            EventBus.publish(updateEvent);
        }
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    @Override
    public String getDescription() {
        return "Change notes for Observation[id = " + observationId + "] from " + oldNote + " to " + newNote;
    }

}
