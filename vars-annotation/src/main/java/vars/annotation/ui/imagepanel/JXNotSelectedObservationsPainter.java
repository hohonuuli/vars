/*
 * @(#)JXNotSelectedObservationsPainter.java   2012.08.07 at 02:22:20 PDT
 *
 * Copyright 2009 MBARI
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
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Observation;

import java.util.Collection;
import java.util.HashSet;

/**
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
        observations.removeAll(dataCoordinator.getSelectedObservations());
        setObservations(observations);
    }

}
