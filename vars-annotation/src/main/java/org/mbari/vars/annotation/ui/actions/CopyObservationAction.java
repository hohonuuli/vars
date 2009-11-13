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


package org.mbari.vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.CameraData;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.VideoFrame;
import org.mbari.vars.annotation.model.dao.VideoFrameDAO;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoFrame;
import vars.annotation.IVideoArchive;
import vars.annotation.IObservation;

/**
 * <p>
 * Copies a selected Observation but adds it to a new VidoeFrame.
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: CopyObservationAction.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public final class CopyObservationAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CopyObservationAction.class);

    /**
     * Constructs ...
     *
     */
    public CopyObservationAction() {
        putValue(Action.NAME, "Copy observation to a new timecode");
        putValue(Action.ACTION_COMMAND_KEY, "copy observation");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     *  Initiate the action
     *
     */
    public void doAction() {

        // Need a videoRachive to add a VideoFrame too.
        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        if (va != null) {

            // Need the VCR to get a current timecode
            final IVCR vcr = VcrDispatcher.getInstance().getVcr();
            if (vcr != null) {

                // Get the current observation
                final IObservation src = ObservationDispatcher.getInstance().getObservation();
                if (src != null) {
                    final String timecode = vcr.getVcrTimecode().toString();
                    String person = PersonDispatcher.getInstance().getPerson();
                    if (person == null) {
                        person = PersonDispatcher.DEFAULT_USER;
                    }

                    /*
                     * See if a VideoFrame with the given time code already
                     * exists
                     */
                    boolean vfIsOk = true;
                    IVideoFrame vf = va.findVideoFrameByTimeCode(timecode);

                    // If none are found create a new one.
                    if (vf == null) {
                        vf = new VideoFrame();
                        vf.setTimeCode(timecode);

                        // Set the direction of the camera
                        if (vf.getCameraData() == null) {
                            vf.setCameraData(new CameraData());
                        }

                        vf.getCameraData().setDirection(
                            (String) PredefinedDispatcher.CAMERA_DIRECTION.getDispatcher().getValueObject());
                        va.addVideoFrame(vf);

                        /*
                         * We don't want any other transactions to occur while
                         * we are inserting the new videoframe so we synchronize
                         * on the DAOEventQueue.
                         */
                        synchronized (DAOEventQueue.getInstance()) {

                            /*
                             * Flush the DAOQueue so that we don't need to worry about
                             * multi-threading issues. This makes exception handling
                             * easier to deal with here so that we can roll back
                             * changes on an error.
                             */
                            DAOEventQueue.flush();

                            // Insert the new VideoFrame into the database
                            try {
                                VideoFrameDAO.getInstance().insert((IDataObject) vf);
                            }
                            catch (final Exception e) {

                                /*
                                 * If an exception occurs, the insert was not successful, so we
                                 * roll back the change.
                                 */
                                log.error("Failed to insert " + vf, e);
                                va.removeVideoFrame(vf);
                                vfIsOk = false;
                                AppFrameDispatcher.showErrorDialog("A database error occurred " + vf + ". Error: " +
                                                                   e + ". You should restart VARS");
                            }
                        }
                    }

                    if (vfIsOk) {

                        // Create a new observation and add it to the videoFrame
                        final Observation obs = new Observation(src);
                        obs.setObserver(person);
                        obs.setObservationDate(new Date());
                        vf.addObservation(obs);

                        // Insert the new Observation into the database.
                        DAOEventQueue.insert(obs, new InsertErrorHandler(obs));

                        // Add the new observation to the table and set it as
                        // the item to be edited.
                        final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
                        table.addObservation(obs);
                        table.setSelectedObservation(obs);
                    }
                    else {
                        log.warn(
                            "Unable to insert a VideoFrame into the database. Aborting the copy observation action.");
                    }
                }
                else {
                    log.warn("Unable to copy the observation. No observation is currently selected. ");
                }
            }
            else {
                log.warn("No VCR is available to get a timecode from; unable to create a VideoFrame");
            }
        }
        else {
            log.warn("A VideoArchive has no been assigned; Unable to create a VideoFrame");
        }
    }

    /**
     * Handles an error that might occur during an insert into the database.
     * If an error occurs the UI is changed so that the view is consistent with
     * the database state.
     */
    private class InsertErrorHandler extends DAOExceptionHandler {

        InsertErrorHandler(final Observation observation) {

            setObject(observation);
        }

        protected void doAction(final Exception e) {

            /*
             * Remove a reference from the model
             */
            final Observation observation = (Observation) getObject();
            final IVideoFrame videoFrame = observation.getVideoFrame();
            videoFrame.removeObservation(observation);

            /*
             * Redraw the UI
             */
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
                    table.removeObservation(observation);
                }

            });

            /*
             * Notify the user
             */
            AppFrameDispatcher.showErrorDialog("Failed to insert the " + "observation, '" +
                                               observation.getConceptName() + "'. You" + " may need to restart VARS.");
        }
    }
}
