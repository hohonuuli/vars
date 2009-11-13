/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.PropertyPanel;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ICameraData;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;

/**
 * <p>
 * A panel that displays the properties of a <code>CameraData</code> object.
 * </p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: PCameraDataPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class PCameraDataPanel extends PropertiesPanel {

    /**
     *
     */
    private static final long serialVersionUID = -3537457109453647935L;
    private static final Logger log = LoggerFactory.getLogger(PCameraDataPanel.class);

    /**
     * @uml.property name="directionAction"
     * @uml.associationEnd
     */
    private ActionAdapter directionAction;

    /**
     * Constructs ...
     */
    PCameraDataPanel() {
        super();
        setPropertyNames(new String[]{
                "Direction", "Name", "Zoom", "Focus", "Iris", "FieldWidth", "StillImage"
        });
        addListeners();
        addToolTip("StillImage");
    }

    /**
     * <p><!-- Method description --></p>
     */
    private void addListeners() {
        final PropertyPanel p = getPropertyPanel("Direction");
        p.getEditButton();
        p.setEditAction(getDirectionAction());
    }

    /**
     * <p><!-- Method description --></p>
     *
     * @return
     * @uml.property name="directionAction"
     */
    private ActionAdapter getDirectionAction() {
        if (directionAction == null) {
            directionAction = new ActionAdapter() {

                /**
                 *
                 */
                private static final long serialVersionUID = -6355620271863236350L;

                public void doAction() {
                    final PropertyPanel p = getPropertyPanel("Direction");
                    final JTextField f1 = p.getValueField();
                    final String initialValue = f1.getText();
                    final String selectedValue = (String) JOptionPane.showInputDialog(AppFrameDispatcher.getFrame(),
                            "Select a camera direction.", "VARS - Camera Direction",
                            JOptionPane.QUESTION_MESSAGE, null, ICameraData.DIRECTIONS,
                            initialValue);
                    if (selectedValue != null) {
                        final IObservation obs = ObservationDispatcher.getInstance().getObservation();
                        final IVideoFrame vf = obs.getVideoFrame();
                        final ICameraData cd = vf.getCameraData();
                        cd.setDirection(selectedValue);

                        try {
                            DAOEventQueue.update((IDataObject) vf);
                        }
                        catch (final Exception e1) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update a videoframe", e1);
                            }
                        }

                        f1.setText(selectedValue);

                        // Redraw the table
                        ObservationTableDispatcher.getInstance().getObservationTable().redrawAll();

                        // Update everything that's listening to the videoarchive.
                        final VideoArchiveDispatcher vad = VideoArchiveDispatcher.getInstance();
                        vad.setVideoArchive(vad.getVideoArchive());
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
        final IObservation obs = (IObservation) obj;
        if (obs == null) {
            clearValues();

            return;
        }

        final IVideoFrame vf = obs.getVideoFrame();
        if (vf == null) {
            clearValues();
        }
        else {
            final ICameraData c = vf.getCameraData();
            if (c == null) {
                clearValues();
            }
            else {
                setProperties(c);
            }
        }
    }
}
