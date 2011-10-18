package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author Brian Schlining
 * @since 2011-10-18
 */
public class ChangeObservationNameCmd implements Command {

    private final String newName;
    private final Collection<DataBean> originalData = new ArrayList<DataBean>();
    private final String newUser;
    private final Date newDate;

    public ChangeObservationNameCmd(Collection<Observation> observations, String newName, String newUser, Date newDate) {
        this.newName = newName;
        this.newUser = newUser;
        this.newDate = newDate;

        for (Observation observation : observations) {
            originalData.add(new DataBean(observation.getConceptName(), observation.getObserver(),
                    observation.getObservationDate(), observation.getPrimaryKey()));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {

        // Get the validated concept name
        ConceptDAO conceptDAO = toolBelt.getKnowledgebaseDAOFactory().newConceptDAO();
        conceptDAO.startTransaction();
        Concept concept = conceptDAO.findByName(newName);
        String conceptName = concept == null ? newName : concept.getPrimaryConceptName().getName();
        conceptDAO.endTransaction();
        conceptDAO.close();

        // change name
        Collection<Observation> changedObservations = new ArrayList<Observation>();
        ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        observationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Observation observation = observationDAO.findByPrimaryKey(bean.primaryKey);
            if (observation != null) {
                observation.setConceptName(conceptName);
                observation.setObserver(newUser);
                observation.setObservationDate(newDate);
                changedObservations.add(observation);
            }
        }
        observationDAO.endTransaction();
        observationDAO.close();

        // Notify of changes
        EventBus.publish(new ObservationsChangedEvent(null, changedObservations));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        // change name
        Collection<Observation> changedObservations = new ArrayList<Observation>();
        ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        observationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Observation observation = observationDAO.findByPrimaryKey(bean.primaryKey);
            if (observation != null) {
                observation.setConceptName(bean.conceptName);
                observation.setObserver(bean.observer);
                observation.setObservationDate(bean.observationDate);
                changedObservations.add(observation);
            }
        }
        observationDAO.endTransaction();
        observationDAO.close();

        // Notify of changes
        EventBus.publish(new ObservationsChangedEvent(null, changedObservations));
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private class DataBean {
        final String conceptName;
        final String observer;
        final Date observationDate;
        final Object primaryKey;

        private DataBean(String conceptName, String observer, Date observationDate, Object primaryKey) {
            this.conceptName = conceptName;
            this.observer = observer;
            this.observationDate = observationDate;
            this.primaryKey = primaryKey;
        }
    }
}
