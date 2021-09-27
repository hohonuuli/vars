/*
 * @(#)JXNotSelectedObservationsPainter.java   2012.11.26 at 08:48:31 PST
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

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import mbarix4j.swing.JImageUrlCanvas;
import vars.annotation.Observation;

import java.util.Collection;
import java.util.HashSet;

/**
 * JXPainter that draws the Observations in a VideoFrame that have not been selected
 *
 * @author Brian Schlining
 * @since 2012-08-03
 *
 * @param <T>
 */
public class JXNotSelectedObservationsPainter<T extends JImageUrlCanvas> extends JXObservationsPainter<T> {

    /**
     * Constructs ...
     */
    public JXNotSelectedObservationsPainter() {
        this(MarkerStyle.NOTSELECTED);
    }

    /**
     * Constructs ...
     *
     * @param markerStyle
     */
    public JXNotSelectedObservationsPainter(MarkerStyle markerStyle) {
        super(markerStyle, true, false);
        AnnotationProcessor.process(this);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Collection<Observation> observations = new HashSet<Observation>(dataCoordinator.getObservations());
        // TODO Add filtering to show/hide annotations from other users
        observations.removeAll(dataCoordinator.getSelectedObservations());
        setObservations(observations);
    }
}
