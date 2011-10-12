package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ObservationDAO;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Command to remove a given association from a collection of observations.
 *
 * <p>Implementation Note: THis is a subclass of {@link AddAssociationCmd}  that just flips
 * the apply/unapply methods</p>
 * @author Brian Schlining
 * @since 2011-10-10
 */
public class RemoveAssociationsCmd implements Command {

    private final Collection<DataBean> originalData = new ArrayList<DataBean>();

    public RemoveAssociationsCmd(Collection<Association> originalAssociations) {
        for(Association association : originalAssociations) {
            originalData.add(new DataBean(association, association.getPrimaryKey(), association.getObservation().getPrimaryKey()));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        final AssociationDAO dao = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
        dao.startTransaction();
        for (DataBean bean : originalData) {
            Association association = dao.findByPrimaryKey(bean.associationPrimaryKey);
            if (association != null) {
                Observation observation = association.getObservation();
                observation.removeAssociation(association);
                dao.remove(association);
                modifiedObservations.add(observation);
            }
        }
        dao.endTransaction();
        dao.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> modifiedObservations = new ArrayList<Observation>();
        AnnotationFactory factory = toolBelt.getAnnotationFactory();
        final ObservationDAO observationDAO = toolBelt.getAnnotationDAOFactory().newObservationDAO();
        final AssociationDAO associationDAO = toolBelt.getAnnotationDAOFactory().newAssociationDAO(observationDAO.getEntityManager());
        for (DataBean bean : originalData) {
            Observation observation = observationDAO.findByPrimaryKey(bean.originalObservationPrimaryKey);
            if (observation != null) {
                Association association = factory.newAssociation(bean.originalAssociation);
                observation.addAssociation(association);
                associationDAO.persist(association);
                modifiedObservations.add(observation);
            }
        }
        observationDAO.endTransaction();
        observationDAO.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }



    @Override
    public String getDescription() {
        return "Delete " + originalData.size() + " associations";
    }

    private class DataBean {
        final Object associationPrimaryKey;
        final Object originalObservationPrimaryKey;
        final Association originalAssociation;

        private DataBean(Association originalAssociation, Object associationPrimaryKey, Object originalObservationPrimaryKey) {
            this.originalAssociation = originalAssociation;
            this.associationPrimaryKey = associationPrimaryKey;
            this.originalObservationPrimaryKey = originalObservationPrimaryKey;
        }
    }
}
