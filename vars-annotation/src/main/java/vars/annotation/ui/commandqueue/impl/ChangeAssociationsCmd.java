package vars.annotation.ui.commandqueue.impl;

import org.bushe.swing.event.EventBus;
import vars.ILink;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Brian Schlining
 * @since 2011-10-11
 */
public class ChangeAssociationsCmd implements Command {

    private final ILink newLink;
    private final Collection<DataBean> originalData = new ArrayList<DataBean>();

    public ChangeAssociationsCmd(ILink newLink, Collection<Association> associations) {
        if (newLink == null || associations == null) {
            throw new IllegalArgumentException("null parameters are not allowed in the constructor");
        }
        this.newLink = newLink;
        for(Association association : associations) {
            originalData.add(new DataBean(association.getPrimaryKey(), association));
        }
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        String toConcept = toolBelt.getPersistenceController().getValidatedConceptName(newLink.getToConcept());
        Collection<Observation> modifiedObservations = new HashSet<Observation>();
        AssociationDAO associationDAO = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
        associationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Association association = associationDAO.findByPrimaryKey(bean.primaryKey);
            if (association != null) {
                association.setLinkName(newLink.getLinkName());
                association.setToConcept(toConcept);
                association.setLinkValue(newLink.getLinkValue());
                modifiedObservations.add(association.getObservation());
            }
        }
        associationDAO.endTransaction();
        associationDAO.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        Collection<Observation> modifiedObservations = new HashSet<Observation>();
        AssociationDAO associationDAO = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
        associationDAO.startTransaction();
        for (DataBean bean : originalData) {
            Association originalAssociation = bean.originalAssociation;
            Association association = associationDAO.findByPrimaryKey(bean.primaryKey);
            if (association != null) {
                association.setLinkName(originalAssociation.getLinkName());
                association.setToConcept(originalAssociation.getToConcept());
                association.setLinkValue(originalAssociation.getLinkValue());
                modifiedObservations.add(association.getObservation());
            }
        }
        associationDAO.endTransaction();
        associationDAO.close();
        EventBus.publish(new ObservationsChangedEvent(null, modifiedObservations));
    }

    @Override
    public String getDescription() {
        return "Change " + originalData.size() + " Associations to " + newLink;
    }

    private class DataBean {
        final Object primaryKey;
        final Association originalAssociation;

        private DataBean(Object primaryKey, Association originalAssociation) {
            this.primaryKey = primaryKey;
            this.originalAssociation = originalAssociation;
        }
    }
}
