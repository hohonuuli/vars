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
Created on Jan 12, 2005
 */
package org.mbari.vars.annotation.ui.actions;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.Icon;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.movie.Timecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.VideoArchive;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;

/**
 *
 * @author brian
 */
public class ChangeTimeCodeAction extends ActionAdapter {


    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is the timecode we want to change to
     */
    private final Timecode timeCode = new Timecode();

    /**
     * This is the videoframe whose timecode we want to change
     */
    private VideoFrame videoFrame;

    /**
     *
     */
    public ChangeTimeCodeAction() {
        super();
    }

    /**
     * @param name
     */
    public ChangeTimeCodeAction(final String name) {
        super(name);
    }

    /**
     * @param name
     * @param icon
     */
    public ChangeTimeCodeAction(final String name, final Icon icon) {
        super(name, icon);
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        final Observation obs = ObservationDispatcher.getInstance().getObservation();
        if (obs != null) {
            VideoFrame vf =  obs.getVideoFrame();
            synchronized (vf) {
                final String oldTimeCode = vf.getTimeCode();
                final IVideoArchive va = vf.getVideoArchive();
                final VideoFrame vfTarget = (VideoFrame) va.findVideoFrameByTimeCode(getTimeCode());
                if (vfTarget == null) {
                    vf.setTimeCode(getTimeCode());
                }
                else {

                    // Move observations to the target
                    final Collection siblingObs = vf.getObservations();
                    synchronized (siblingObs) {
                        for (final Iterator i = siblingObs.iterator(); i.hasNext(); ) {
                            final IObservation sibling = (IObservation) i.next();
                            vf.removeObservation(sibling);
                            vfTarget.addObservation(sibling);
                        }
                    }

                    final IVideoArchive parentVa = vf.getVideoArchive();
                    parentVa.removeVideoFrame(vf);

                    try {
                        VideoFrameDAO.getInstance().delete((IDataObject) vf);
                        vf = vfTarget;
                    }
                    catch (final Exception e) {

                        /*
                         * If the db transaction fails roll back the cahnges so that
                         * the model in memory matches the database.
                         */
                        synchronized (siblingObs) {
                            for (final Iterator i = siblingObs.iterator(); i.hasNext(); ) {
                                final IObservation sibling = (IObservation) i.next();
                                vfTarget.removeObservation(sibling);
                                vf.addObservation(sibling);
                            }
                        }

                        parentVa.addVideoFrame(vf);
                        AppFrameDispatcher.showErrorDialog("Unable to save the change to time-code! Reason: " +
                                                           e.getMessage() + ".");
                        log.error("Failed to move Observations to a different " + "pre-existing VideoFrame", e);
                    }
                }

                DAOEventQueue.update((IDataObject) vf, new VFUpdateErrorHandler((VideoFrame) vf, oldTimeCode));
            }
        }

        final ObservationDispatcher dispatcher = ObservationDispatcher.getInstance();
        dispatcher.setObservation(dispatcher.getObservation());
    }

    /**
     * @return Returns the timeCode.
     */
    public String getTimeCode() {
        return (timeCode == null) ? null : timeCode.toString();
    }

    /**
     *     @return  Returns the videoFrame.
     */
    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    /**
     *
     * @param timeCodeString
     */
    public void setTimeCode(final String timeCodeString) {
        timeCode.setTimecode(timeCodeString);
    }

    /**
     *     @param videoFrame  The videoFrame to set.
     */
    public void setVideoFrame(final VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    /**
     *     <p>ExceptionHandler that rolls back the change to the timecode in memory if the database transaction bombs.</p>
     *     @author  Brian Schlining
     *     @version  $Id: ChangeTimeCodeAction.java 332 2006-08-01 18:38:46Z hohonuuli $
     */
    private final class VFUpdateErrorHandler extends DAOExceptionHandler {

        private final String oldTimeCode;
        private final VideoFrame videoFrame;

        /**
         * Constructs ...
         *
         *
         * @param videoFrame
         * @param oldTimeCode
         */
        public VFUpdateErrorHandler(final VideoFrame videoFrame, final String oldTimeCode) {
            this.videoFrame = videoFrame;
            this.oldTimeCode = oldTimeCode;
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param e
         */
        protected void doAction(final Exception e) {
            videoFrame.setTimeCode(oldTimeCode);
        }
    }
}
