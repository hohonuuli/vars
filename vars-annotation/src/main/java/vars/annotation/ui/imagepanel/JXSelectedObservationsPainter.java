/*
 * @(#)JXSelectedObservationsPainter.java   2012.11.26 at 08:48:30 PST
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
import org.mbari.swing.JImageUrlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JXPainter that draws Observations in the current video frame that have been selected
 * @author Brian Schlining
 * @since 2012-08-03
 *
 * @param <T>
 */
public class JXSelectedObservationsPainter<T extends JImageUrlCanvas> extends JXObservationsPainter<T> {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs ...
     */
    public JXSelectedObservationsPainter() {
        this(MarkerStyle.SELECTED);
    }

    /**
     * Constructs ...
     *
     * @param markerStyle
     */
    public JXSelectedObservationsPainter(MarkerStyle markerStyle) {
        super(markerStyle, true, false);
        AnnotationProcessor.process(this);
    }

    /**
     *
     * @param event
     */
    @EventSubscriber(eventClass = IAFRepaintEvent.class)
    public void respondTo(IAFRepaintEvent event) {
        log.debug("Responding to Selection event with " + event.get().getSelectedObservations().size() +
                " observations");
        setObservations(event.get().getSelectedObservations());
    }
}
