/*
 * @(#)JXNotSelectedAreaMeasurementPainter.java   2013.02.14 at 08:26:31 PST
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

import com.google.common.collect.Collections2;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.mbari.swing.JImageUrlCanvas;
import vars.annotation.Association;
import vars.annotation.Observation;

/**
 * @author Brian Schlining
 * @since 2013-01-17
 *
 * @param <A>
 */
public class JXNotSelectedAreaMeasurementPainter<A extends JImageUrlCanvas> extends JXAreaMeasurementPainter<A> {

    /**
     * Constructs ...
     */
    public JXNotSelectedAreaMeasurementPainter() {
        super(new Font("Sans Serif", Font.PLAIN, 8), Color.LIGHT_GRAY, new BasicStroke(1));
        AnnotationProcessor.process(this);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        UIDataCoordinator dataCoordinator = event.get();
        Set<Observation> observations = new HashSet<Observation>(dataCoordinator.getObservations());
        observations.removeAll(dataCoordinator.getSelectedObservations());

        // Search for all observations with area measurements
        List<Association> associations = new ArrayList<Association>();
        for (Observation obs : observations) {
            associations.addAll(obs.getAssociations());
        }

        Collection<Association> amAssociations = Collections2.filter(associations,
            AreaMeasurement.IS_AREA_MEASUREMENT_PREDICATE);
        setAssociations(amAssociations);
    }
}
