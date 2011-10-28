package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class RenameObservationsCmd implements Command {

    private final String newConceptName;
    private final String user;
    private final Collection<DataBean> originalData = new ArrayList<DataBean>();

    public RenameObservationsCmd(String newConceptName, String user, Collection<Observation> observations) {
        this.newConceptName = newConceptName;
        this.user = user;
        for(Observation observation : observations) {
            originalData.add(new DataBean(observation.getPrimaryKey(), observation));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        String conceptName = toolBelt.getPersistenceController().getValidatedConceptName(newConceptName);
        ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        observationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Observation observation = observationDAO.findByPrimaryKey(bean.primaryKey);
            if (observation != null) {
                observation.setConceptName(conceptName);
                observation.setObserver(user);
                modifiedObservations.add(observation);
            }
        }
        observationDAO.endTransaction();
        observationDAO.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        observationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Observation observation = observationDAO.findByPrimaryKey(bean.primaryKey);
            if (observation != null) {
                observation.setConceptName(bean.originalObservation.getConceptName());
                observation.setObserver(bean.originalObservation.getObserver());
                modifiedObservations.add(observation);
            }
        }
        observationDAO.endTransaction();
        observationDAO.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }

    @Override
    public String getDescription() {
        return "Rename " + originalData.size() + " Observations to " + newConceptName;
    }

    private class DataBean {
        final Object primaryKey;
        final Observation originalObservation;

        private DataBean(Object primaryKey, Observation originalObservation) {
            this.primaryKey = primaryKey;
            this.originalObservation = originalObservation;
        }
    }
}
