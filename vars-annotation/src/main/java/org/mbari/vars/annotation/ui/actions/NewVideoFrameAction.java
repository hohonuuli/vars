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


/*
The Monterey Bay Aquarium Research Institute (MBARI) provides this
documentation and code 'as is', with no warranty, express or
implied, of its quality or consistency. It is provided without support and
without obligation on the part of MBARI to assist in its use, correction,
modification, or enhancement. This information should not be published or
distributed to third parties without specific written permission from MBARI
 */
package org.mbari.vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.movie.Timecode;
import org.mbari.util.NumberUtilities;
import org.mbari.vars.annotation.model.CameraData;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.PhysicalData;
import org.mbari.vars.annotation.model.VideoFrame;
import org.mbari.vars.annotation.model.dao.VideoFrameDAO;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.dao.DAOExceptionHandler;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedEvent;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedListener;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoFrame;
import vars.annotation.IVideoArchive;
import vars.annotation.IObservation;
import vars.annotation.ICameraData;
import vars.annotation.IPhysicalData;

/**
 * <p>Action to add a new VideoFrame and a 'nearly' empty Observation
 * to the ObservationTable. Persistence is NOT done in the action but
 * is handled by the table.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: NewVideoFrameAction.java 376 2006-10-26 18:21:43Z hohonuuli $
 */
public final class NewVideoFrameAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(NewVideoFrameAction.class);

    /**
     *     @uml.property  name="defaultConceptName"
     */
    private String defaultConceptName;

    /**
     *     @uml.property  name="timeCodeObj"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final Timecode timeCodeObj = new Timecode();

    /**
     * Constructor for the NewVideoFrameAction object
     */
    public NewVideoFrameAction() {
        super();

        try {
            defaultConceptName = KnowledgeBaseCache.getInstance().findRootConcept().getPrimaryConceptNameAsString();
        }
        catch (final DAOException e) {
            defaultConceptName = IConceptName.NAME_DEFAULT;

            if (log.isWarnEnabled()) {
                log.warn("Failed to lookup root concept from database", e);
            }
        }

        /*
         * If the cache is cleared the default concept name MAY change. We need to listen for that change
         */
        KnowledgeBaseCache.getInstance().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                try {
                    defaultConceptName =
                        KnowledgeBaseCache.getInstance().findRootConcept().getPrimaryConceptNameAsString();
                }
                catch (DAOException e) {
                    defaultConceptName = IConceptName.NAME_DEFAULT;

                    if (log.isWarnEnabled()) {
                        log.warn("Failed to lookup root concept from database", e);
                    }
                }
            }

            public void beforeClear(CacheClearedEvent evt) {
                defaultConceptName = IConceptName.NAME_DEFAULT;
            }

        });

        putValue(Action.NAME, "New video-frame");
        putValue(Action.ACTION_COMMAND_KEY, "new video-frame");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     * Intiates the action. using the DEFAULT_CONCEPTNAME
     * @see  org.mbari.awt.event.IAction
     */
    public void doAction() {
        doAction(defaultConceptName);
    }

    /**
     * Inserts a new Observation using the supplied conceptName. The new
     * observation will be attached to a new VideoFrame if no matching time
     * code was found. Otherwise a pre-existing videoFrame will be used.
     *
     * NOTE: The insertion and naming needs to be done in one step. Originally, the
     * code was inserting an observation o f'object' then renaming it.
     * However, during certain observations the row would not get moved to the
     * newly inserted observation and we would end up renaming the wrong object.
     *
     * @param  conceptName
     * @return The observation created. null if none was created.
     */
    public IObservation doAction(final String conceptName) {
        IObservation observation = null;

        // Need the VCR to get a current timecode
        final IVCR vcr = VcrDispatcher.getInstance().getVcr();
        if (vcr != null) {
            final String timecode = vcr.getVcrTimecode().toString();
            observation = doAction(conceptName, timecode);
        }

        return observation;
    }

    /**
     * Inserts a new Observation using the supplied conceptName and timecode. The new
     * observation will be attached to a new VideoFrame if no matching time
     * code was found. Otherwise a pre-existing videoFrame will be used.
     *
     * @param  conceptName
     * @param timecode A timecode in the format of HH:MM:SS:FF
     * @return The observation created. null if none was created.
     */
    public IObservation doAction(final String conceptName, final String timecode) {
        IObservation observation = null;

        // Need a videoArchive to add a VideoFrame too.
        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        if (va != null) {

            /*
             * Verfiy that the timecode is acceptable
             */
            boolean isTimeOK = false;
            try {
                timeCodeObj.setTimecode(timecode);
                isTimeOK = true;
            }
            catch (final Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Invalid timecode of " + timecode, e);
                }
            }

            final IVCR vcr = VcrDispatcher.getInstance().getVcr();
            if ((conceptName != null) && (timecode != null) && isTimeOK) {
                String person = PersonDispatcher.getInstance().getPerson();
                if (person == null) {
                    person = "default";
                }

                // See if a VideoFrame with the given time code already exists
                IVideoFrame vf = va.findVideoFrameByTimeCode(timecode);
                boolean vfIsOk = true;

                // If none are found create a new one.
                if (vf == null) {
                    vf = new VideoFrame();
                    Date utcDate = null;

                    /*
                     * If the VCR is recording we'll grab the time off of the
                     * computer clock. Otherwise we'll get it off of the
                     * userbits.
                     */
                    if (vcr.getVcrState().isRecording()) {
                        utcDate = new Date();
                    }
                    else {

                        /*
                         *  Try to grab the userbits off of the tape. The userbits
                         *  may have the time that the frame was recorded stored as a
                         *  little-endian 4-byte int.
                         */
                        vcr.requestVUserbits();
                        final int epicSeconds = NumberUtilities.toInt(vcr.getVcrUserbits().getUserbits(), true);
                        utcDate = new Date((long) epicSeconds * 1000L);
                    }

                    vf.setRecordedDate(utcDate);
                    vf.setTimeCode(timecode);
                    va.addVideoFrame(vf);
                    final String cameraDirection =
                        (String) PredefinedDispatcher.CAMERA_DIRECTION.getDispatcher().getValueObject();
                    if (cameraDirection != null) {
                        ICameraData cd = vf.getCameraData();
                        if (cd == null) {
                            cd = new CameraData();
                            vf.setCameraData(cd);
                        }

                        cd.setDirection(cameraDirection);
                    }

                    /*
                     * I added this so that it's easier to merge data in post-
                     * processing. Without this we have to use a stored
                     * procedure to create a physcal data record and increment
                     * the uniqueId table.
                     */
                    IPhysicalData physicalData = vf.getPhysicalData();
                    if (physicalData == null) {
                        physicalData = new PhysicalData();
                        vf.setPhysicalData(physicalData);
                    }


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
                            VideoFrameDAO.getInstance().insert((VideoFrame) vf);
                        }
                        catch (final Exception e) {

                            /*
                             * If an exception occurs, the insert was not successful, so we
                             * roll back the change.
                             */
                            log.error("Failed to insert " + vf, e);
                            va.removeVideoFrame(vf);
                            vfIsOk = false;
                            AppFrameDispatcher.showErrorDialog(
                                "Unable to insert new data in the database. You should restart VARS");
                        }
                    }
                }

                if (vfIsOk) {

                    // Create a new observation and add it to the videoFrame
                    observation = new Observation();
                    observation.setConceptName(conceptName);
                    observation.setObserver(person);
                    observation.setObservationDate(new Date());
                    vf.addObservation(observation);

                    // Insert the new Observation into the database.
                    DAOEventQueue.insert((Observation) observation, new FailedObservationInsertHandler(observation));

                    // Add the new observation to the table and set it as
                    // the item to be edited.
                    final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
                    table.addObservation(observation);
                    table.setSelectedObservation(observation);
                }


            }
            else {
                log.warn("No VCR is available to get a timecode from; unable to create a VideoFrame");
            }
        }
        else {
            log.warn("A VideoArchive has no been assigned; unable to create a VideoFrame");
        }

        return observation;
    }

    /**
     * If an observation fails to get inserted we need to notify the user
     * and delete it from the table.
     */
    private class FailedObservationInsertHandler extends DAOExceptionHandler {

        FailedObservationInsertHandler(final IObservation observation) {
            setObject(observation);
        }

        protected void doAction(final Exception e) {

            /*
             * Set the model to match the database
             */
            final IObservation observation = (IObservation) getObject();
            final IVideoFrame videoFrame = observation.getVideoFrame();
            videoFrame.removeObservation(observation);

            /*
             * Remvoe the observation from the UI
             */
            final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    table.removeObservation((IObservation) getObject());
                }

            });

            /*
             * Alert the user
             */
            AppFrameDispatcher.showErrorDialog("Failed to insert " + getObject() + ". Reason: " + e.getMessage());

        }
    }
}
