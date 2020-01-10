/*
 * @(#)JXIdentityReferencePainter.java   2012.11.26 at 08:48:32 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
 *
 * @param <T>
 */
public class JXIdentityReferencePainter<T extends JImageUrlCanvas> extends JXObservationsPainter<T> {

    private final Set<Observation> emptySet = new HashSet<Observation>();
    private final AnnotationPersistenceService annotationPersistenceService;

    /** The selected Observation */
    private Observation observation;

    /**
     * Constructs ...
     *
     * @param annotationPersistenceService
     */
    @Inject
    public JXIdentityReferencePainter(AnnotationPersistenceService annotationPersistenceService) {
        super(MarkerStyle.FAINT, false, true);
        this.annotationPersistenceService = annotationPersistenceService;
        AnnotationProcessor.process(this);
    }

    /**
     *
     * @param event
     */
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

    /**
     *
     * @param observation
     */
    public void setObservation(Observation observation) {
        if (this.observation != observation) {
            this.observation = observation;
            if (observation != null) {
                Collection<Association> associations = Collections2.filter(observation.getAssociations(),
                        association -> association.getLinkName().equalsIgnoreCase("identity-reference"));

                if (!associations.isEmpty()) {
                    Association identityAss = associations.iterator().next();
                    int id = Integer.parseInt(identityAss.getLinkValue());
                    String conceptName = observation.getConceptName();
                    VideoArchive videoArchive = observation.getVideoFrame().getVideoArchive();
                    Collection<Observation> relatedObservations = annotationPersistenceService
                        .findAllObservationsByNameAndReferenceNumber(videoArchive, conceptName, id);
                    relatedObservations.remove(observation);    // Don't draw the current observation
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
}
