package vars.annotation.ui.imagepanel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.Observation;

import vars.annotation.VideoArchive;
import vars.annotation.ui.eventbus.ObservationsSelectedEvent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This painter draws all observations with the same identity reference as the selected one on
 * the image
 *
 * @author Brian Schlining
 * @since 2012-08-03
 */
public class JXIdentityReferencePainter<T extends JImageUrlCanvas>
        extends JXObservationsPainter<T> {

    /** The selected Observation */
    private Observation observation;
    private final Set<Observation> emptySet = new HashSet<Observation>();
    private final AnnotationPersistenceService annotationPersistenceService;

    @Inject
    public JXIdentityReferencePainter(AnnotationPersistenceService annotationPersistenceService) {
        super(MarkerStyle.FAINT, false, true);
        this.annotationPersistenceService = annotationPersistenceService;
        AnnotationProcessor.process(this);
    }

    public void setObservation(Observation observation) {
        if (this.observation != observation) {
            this.observation = observation;
            if (observation != null) {
                Collection<Association> associations = Collections2.filter(observation.getAssociations(), new Predicate<Association>() {
                    @Override
                    public boolean apply(@Nullable Association association) {
                        return association.getLinkName().equalsIgnoreCase("identity-reference");
                    }
                });

                if (!associations.isEmpty()) {
                    Association identityAss = associations.iterator().next();
                    int id = Integer.parseInt(identityAss.getLinkValue());
                    String conceptName = observation.getConceptName();
                    VideoArchive videoArchive = observation.getVideoFrame().getVideoArchive();
                    Collection<Observation> relatedObservations =
                            annotationPersistenceService.findAllObservationsByNameAndReferenceNumber(videoArchive, conceptName, id);
                    relatedObservations.remove(observation); // Don't draw the current observation
                    setObservations(relatedObservations);
                }
                else {
                    setObservations(emptySet);
                }

            }
            else {
                setObservations(emptySet);
            }
            setDirty(true);
        }
    }

    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        Collection<Observation> observations = new HashSet<Observation>(event.get().getSelectedObservations());
        if (observations.size() == 1) {
            setObservation(observations.iterator().next());
        }
        else {
            setObservation(null);
        }
    }


}
