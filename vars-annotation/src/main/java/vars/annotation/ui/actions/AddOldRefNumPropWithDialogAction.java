/*
 * @(#)AddOldRefNumPropWithDialogAction.java   2009.11.19 at 09:20:39 PST
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



package vars.annotation.ui.actions;

import java.awt.Frame;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.event.IAction;
import org.mbari.text.ReverseSortComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.knowledgebase.Concept;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.Lookup;

/**
 * <p>Adds 'identity-reference | self | [some integer]' property to the Observation set in
 * the ObservationDispatcher</p>
 *
 * @author <a href="http://www.mbari.org">MBARI </a>
 */
public class AddOldRefNumPropWithDialogAction extends ActionAdapter {

    private static final Comparator COMPARATOR = new ReverseSortComparator();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final IAction action;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public AddOldRefNumPropWithDialogAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        action = new AddOldRefNumPropAction(toolBelt);
    }

    /**
     */
    @SuppressWarnings("unchecked")
    public void doAction() {

        /*
         * Get all the existing reference numbers in a VideoArchive
         */
        final VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive == null) {
            return;
        }

        final Collection<Observation> observations = (Collection<Observation>)
                Lookup.getSelectedObservationsDispatcher().getValueObject();
        if (observations.size() == 0) {
            return;
        }

        String conceptName = observations.iterator().next().getConceptName();
        for (Observation observation : observations) {
            if (!observation.getConceptName().equals(conceptName)) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                                 "The selected observations must all contain the same conceptname");
                return;
            }
        }

        final VideoArchiveSet vas = videoArchive.getVideoArchiveSet();
        Concept concept = null;
        Collection<Integer> refNums = new TreeSet<Integer>();
        try {
            AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();
            concept = service.findConceptByName(conceptName);
            refNums = service.findAllReferenceNumbers(vas, concept);
        }
        catch (final Exception e) {
            log.error("Failed to lookup a concept in the knowledebase", e);
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            return;
        }


        /*
         * If non were found let the user know. Otherwise show a dialog
         * allowing the user to select.
         */
        if (refNums.size() == 0) {
            EventBus.publish(Lookup.TOPIC_WARNING,
                             "<html><body>No reference numbers for " + conceptName +
                             "were found. Use 'New #' instead.</body></html>");
        }
        else {

            final Object[] choices = refNums.toArray(new Integer[refNums.size()]);
            Arrays.sort(choices, COMPARATOR);
            final String i = (String) JOptionPane.showInputDialog(
                (Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), "Select a reference number",
                "VARS - Select Reference Number", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

            // If a string was returned, say so.
            if (i != null) {
                AddOldRefNumPropAction.setRefNumber(i);
                action.doAction();
            }
        }
    }
}
