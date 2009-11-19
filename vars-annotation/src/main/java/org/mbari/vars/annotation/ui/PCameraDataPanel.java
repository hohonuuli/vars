/*
 * @(#)PCameraDataPanel.java   2009.11.16 at 02:02:09 PST
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



package org.mbari.vars.annotation.ui;

import java.awt.Frame;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.CameraData;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>
 * A panel that displays the properties of a <code>CameraData</code> object.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PCameraDataPanel extends PropertiesPanel {

    private ActionAdapter directionAction;
    private final PersistenceController persistenceService;

    /**
     * Constructs ...
     */
    PCameraDataPanel(PersistenceController persistenceService) {
        super();
        this.persistenceService = persistenceService;
        setPropertyNames(new String[] {
            "Direction", "Name", "Zoom", "Focus", "Iris", "FieldWidth", "StillImage"
        });
        addListeners();
        addToolTip("StillImage");
    }

    private void addListeners() {
        final PropertyPanel p = getPropertyPanel("Direction");
        p.getEditButton();
        p.setEditAction(getDirectionAction());
    }

    private ActionAdapter getDirectionAction() {
        if (directionAction == null) {
            directionAction = new ActionAdapter() {

                public void doAction() {
                    final PropertyPanel p = getPropertyPanel("Direction");
                    final JTextField f1 = p.getValueField();
                    final String initialValue = f1.getText();
                    Frame frame = (Frame) Lookup.getApplicationFrameDispatcher().getValueObject();
                    final String selectedValue = (String) JOptionPane.showInputDialog(frame,
                        "Select a camera direction.", "VARS - Camera Direction", JOptionPane.QUESTION_MESSAGE, null,
                        CameraDirections.values(), initialValue);
                    if (selectedValue != null) {
                        final Collection<Observation> observations = (Collection<Observation>) Lookup
                            .getSelectedObservationsDispatcher();
                        if (observations.size() == 1) {
                            final Observation obs = observations.iterator().next();
                            final VideoFrame vf = obs.getVideoFrame();
                            final CameraData cd = vf.getCameraData();
                            cd.setDirection(selectedValue);

                            try {
                                persistenceService.updateObservations(new HashSet<Observation>() {

                                    {
                                        add(obs);
                                    }

                                });
                            }
                            catch (final Exception e1) {
                                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                            }

                            f1.setText(selectedValue);

                        }
                    }
                }
            };
        }

        return directionAction;
    }

    /**
     * Subscribes to the observationDispatcher.
     *
     * @param obj        Description of the Parameter
     * @param changeCode Description of the Parameter
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object obj, final Object changeCode) {
        final Observation obs = (Observation) obj;
        if (obs == null) {
            clearValues();

            return;
        }

        final VideoFrame vf = obs.getVideoFrame();
        if (vf == null) {
            clearValues();
        }
        else {
            final CameraData c = vf.getCameraData();
            if (c == null) {
                clearValues();
            }
            else {
                setProperties(c);
            }
        }
    }
}
