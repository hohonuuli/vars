package vars.annotation.ui.commandqueue.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;
import vars.annotation.ui.eventbus.ObservationsChangedEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Command to add a given association to a collection of observations
 * @author Brian Schlining
 * @since 2011-09-25
 */
public class AddAssociationCmd implements Command {

    protected final ILink associationTemplate;
    protected final Collection<Observation> originalObservations;
    private Logger log = LoggerFactory.getLogger(getClass());

    public AddAssociationCmd(ILink associationTemplate, Collection<Observation> originalObservations) {
        this.associationTemplate = associationTemplate;
        this.originalObservations = new ArrayList<Observation>(originalObservations);
    }

    @Override
    public void apply(ToolBelt toolBelt) {
        doCommand(toolBelt, true);
    }

    @Override
    public void unapply(ToolBelt toolBelt) {
        doCommand(toolBelt, false);
    }

    protected void doCommand(ToolBelt toolBelt, boolean isApply) {
        final Collection<Observation> newObservations = new ArrayList<Observation>();
        final AnnotationDAOFactory annotationDAOFactory = toolBelt.getAnnotationDAOFactory();
        final AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();
        final AssociationDAO dao = annotationDAOFactory.newAssociationDAO();

        // DAOTX - Add Association to each observation
        dao.startTransaction();
        for (Observation observation : originalObservations) {

            try {
                //observation = dao.merge(observation);
                observation = dao.find(observation);
            }
            catch (Exception e) {
                log.warn("Failed to lookup {}", observation);
            }

            if (observation != null) {
                newObservations.add(observation);
                Association ass = annotationFactory.newAssociation(associationTemplate);
                String validatedToConcept = toolBelt.getPersistenceController().getValidatedConceptName(ass.getToConcept());
                ass.setToConcept(validatedToConcept);
                if (isApply) { // APPLY
                    observation.addAssociation(ass);
                    dao.persist(ass);
                }
                else { // UNAPPLY
                    Collection<Association> matchingAssociation = Collections2.filter(observation.getAssociations(), new IsMatchingAnnotation(ass));
                    if (matchingAssociation.size() == 1) {
                        Association match = matchingAssociation.iterator().next();
                        observation.removeAssociation(match);
                        dao.remove(match);
                    }
                }
            }
        }

        dao.endTransaction();
        dao.close();

        // --- Notify componenets of update
        ObservationsChangedEvent updateEvent = new ObservationsChangedEvent(null, newObservations);
        EventBus.publish(updateEvent);

    }

    @Override
    public String getDescription() {
        return "Add Association (" + associationTemplate + ") to " + originalObservations.size() + " observations";
    }

    private class IsMatchingAnnotation implements Predicate<Association> {

        private final ILink link;

        private IsMatchingAnnotation(ILink link) {
            this.link = link;
        }

        @Override
        public boolean apply(Association input) {
            return input.getLinkName().equals(link.getLinkName()) &&
                    input.getLinkValue().equals(link.getLinkValue()) &&
                    input.getToConcept().equals(link.getToConcept());
        }
    }
}
