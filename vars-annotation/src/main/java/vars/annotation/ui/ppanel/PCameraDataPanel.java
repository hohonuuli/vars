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



package vars.annotation.ui.ppanel;

import com.google.common.collect.ImmutableList;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bushe.swing.event.EventBus;
import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.swing.PropertyPanel;
import vars.DAO;
import vars.annotation.CameraData;
import vars.annotation.CameraDataValueEq;
import vars.annotation.CameraDirections;
import vars.annotation.ImmutableCameraData;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.ChangeCameraDataCmd;
import vars.annotation.ui.commandqueue.impl.ChangeCameraDirectionsCmd;


/**
 * <p>
 * A panel that displays the properties of a <code>CameraData</code> object.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class PCameraDataPanel extends PropertiesPanel {

    private ActionAdapter directionAction;
    private final ToolBelt toolBelt;
    private volatile CameraData cameraData;
    private CameraDataValueEq eq = new CameraDataValueEq();
    private static final String[] propertyNames = {"Direction", "Name", "Zoom", "Focus", "Iris", "FieldWidth", "ImageReference", "X", "Y"};

    private final ActionAdapter updateAction = new ActionAdapter() {
        @Override
        public void doAction() {
            if (cameraData != null) {
                CameraData newCameraData = readDataPanels();
                if (newCameraData != null) {
                    Command command = new ChangeCameraDataCmd(cameraData, newCameraData);
                    CommandEvent commandEvent = new CommandEvent(command);
                    EventBus.publish(commandEvent);
                }
            }
        }
    };

    /**
     * Constructs ...
     */
    public PCameraDataPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        setPropertyNames(propertyNames);
        Arrays.stream(propertyNames)
                .filter(name -> !name.equals("Direction"))
                .forEach(name -> {
                    PropertyPanel panel = getPropertyPanel(name);
                    JTextField valueField = panel.getValueField();
                    valueField.addActionListener(e -> updateAction.doAction());
                    valueField.getDocument().addDocumentListener(new MyDocListener(valueField));
                    panel.setEditable(true);
                });

        addListeners();
        addToolTip("ImageReference");
    }

    private void addListeners() {
        final PropertyPanel p = getPropertyPanel("Direction");
        p.getEditButton();
        p.setEditAction(getDirectionAction());
    }

    private boolean isEdited() {
        CameraData a = cameraData;
        CameraData b = readDataPanels();
        if (a == null || b == null) {
            return false;
        }
        else {
            return !eq.equal(a, b);
        }
    }

    private ActionAdapter getDirectionAction() {
        if (directionAction == null) {
            directionAction = new ActionAdapter() {

                public void doAction() {
                    final PropertyPanel p = getPropertyPanel("Direction");
                    final JTextField f1 = p.getValueField();
                    final String initialValue = f1.getText();
                    Frame frame = StateLookup.getAnnotationFrame();
                    final CameraDirections selectedValue = (CameraDirections) JOptionPane.showInputDialog(frame,
                        "Select a camera direction.", "VARS - Camera Direction", JOptionPane.QUESTION_MESSAGE, null,
                        CameraDirections.values(), CameraDirections.findValue(initialValue));
                    if (selectedValue != null) {
                        final Collection<Observation> observations = StateLookup.getSelectedObservations();
                        if (observations.size() == 1) {

                            try {
                                DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                                dao.startTransaction();
                                Observation obs = observations.iterator().next();
                                obs = dao.find(obs);
                                final VideoFrame vf = obs.getVideoFrame();
                                dao.endTransaction();
                                dao.close();
                                Command command = new ChangeCameraDirectionsCmd(selectedValue.getDirection(),
                                        ImmutableList.of(vf));
                                CommandEvent commandEvent = new CommandEvent(command);
                                EventBus.publish(commandEvent);
                            }
                            catch (final Exception e1) {
                                EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e1);
                            }

                            f1.setText(selectedValue.toString());
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
     * @see mbarix4j.util.IObserver#update(java.lang.Object, java.lang.Object)
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
            cameraData = vf.getCameraData();
            if (cameraData == null) {
                clearValues();
            }
            else {
                setProperties(cameraData);
            }
            for (String name : propertyNames) {
                getPropertyPanel(name).getValueField().setForeground(Color.BLACK);
            }
        }
    }

    private CameraData readDataPanels() {
        if (cameraData == null) {
            return null;
        }
        else {
            return new ImmutableCameraData(cameraData.getPrimaryKey(),
                    readString("Direction", cameraData.getDirection()),
                    readDouble("FieldWidth", cameraData.getFieldWidth()),
                    readInteger("Focus", cameraData.getFocus()),
                    cameraData.getHeading(),
                    readString("ImageReference", cameraData.getImageReference()),
                    readInteger("Iris", cameraData.getIris()),
                    cameraData.getLogDate(),
                    readString("Name", cameraData.getName()),
                    cameraData.getPitch(),
                    cameraData.getRoll(),
                    cameraData.getViewHeight(),
                    cameraData.getViewWidth(),
                    cameraData.getViewUnits(),
                    readFloat("X", cameraData.getX()),
                    cameraData.getXYUnits(),
                    readFloat("Y", cameraData.getY()),
                    cameraData.getZ(),
                    cameraData.getZUnits(),
                    readInteger("Zoom", cameraData.getZoom()));
        }
    }

    class MyDocListener implements DocumentListener {

        private final JTextField textField;
        public final Color defaultColor;

        public MyDocListener(JTextField textField) {
            this.textField = textField;
            this.defaultColor = textField.getForeground();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateUI();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateUI();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateUI();
        }

        private void updateUI() {
            Color color = isEdited() ? Color.RED : defaultColor;
            textField.setForeground(color);
        }

    }
}
