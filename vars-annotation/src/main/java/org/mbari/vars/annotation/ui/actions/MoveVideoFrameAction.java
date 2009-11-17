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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.dao.VideoFrameDAO;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;
import vars.annotation.ICameraData;

/**
 * This action moves a Collection of VideoFrames to a single VideoArchive
 *
 * @author brian
 * @version $Id: MoveVideoFrameAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class MoveVideoFrameAction extends ActionAdapter implements IVideoArchiveProperty {

    /** <!-- Field description --> */
    public static final String ACTION_NAME = "Move Video Frames";

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MoveVideoFrameAction.class);

    /**
     *     This is where we want to move the videoframes to
     *     @uml.property  name="videoArchive"
     *     @uml.associationEnd
     */
    private IVideoArchive videoArchive;

    /**
     *     These are the videoFrames to be moved.
     *     @uml.property  name="videoFrames"
     *     @uml.associationEnd  multiplicity="(0 -1)" elementType="org.mbari.vars.annotation.model.VideoFrame"
     */
    private Collection videoFrames;

    /**
     *
     */
    public MoveVideoFrameAction() {
        super(ACTION_NAME);
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if ((videoArchive != null) && (videoFrames != null)) {

            /*
             * Flush all pending database transactions from the
             * queue so that we don't have to worry about multi-threading
             * issues for this transaction.
             */
            final DAOEventQueue queue = DAOEventQueue.getInstance();
            synchronized (queue) {
                DAOEventQueue.flush();

                final VideoFrameDAO vfDao = VideoFrameDAO.getInstance();
                for (final Iterator i = videoFrames.iterator(); i.hasNext(); ) {
                    final IVideoFrame sourceFrame = (IVideoFrame) i.next();
                    final IVideoArchive sourceArchive = sourceFrame.getVideoArchive();
                    if ((sourceArchive != null) &&!sourceArchive.equals(videoArchive)) {
                        if (log.isInfoEnabled()) {
                            log.info("Moving " + sourceFrame + " from " + sourceArchive + " to " + videoArchive);
                        }

                        /*
                         * Remove the VideoFrame from it's original VideoArchive
                         */
                        sourceArchive.removeVideoFrame(sourceFrame);

                        /*
                         * Check to see if the target VideoArchive already has a
                         * VideoFrame with that timecode.
                         */
                        final IVideoFrame targetFrame = videoArchive.findVideoFrameByTimeCode(sourceFrame.getTimeCode());
                        if (targetFrame != null) {
                            mergeFrames(sourceFrame, targetFrame);

                            try {
                                vfDao.delete((IDataObject) sourceFrame);
                                vfDao.updateVideoArchiveSet((IDataObject) targetFrame);
                            }
                            catch (final DAOException e) {
                                final String msg = "Unable to update " + sourceFrame + ". Aborting actions.";
                                log.error(msg, e);
                                AppFrameDispatcher.showErrorDialog(msg);

                                break;
                            }
                        }
                        else {
                            sourceArchive.removeVideoFrame(sourceFrame);
                            videoArchive.addVideoFrame(sourceFrame);

                            try {
                                VideoFrameDAO.getInstance().updateVideoArchiveSet((IDataObject) sourceFrame);
                            }
                            catch (final DAOException e) {
                                final String msg = "Unable to update " + sourceFrame + ". Aborting actions.";
                                log.error(msg, e);
                                AppFrameDispatcher.showErrorDialog(msg);

                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *     Returns the VideoArchive. This is the destination that the VideoFrames will/have been moved to.
     *     @see org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty#getVideoArchive()
     *     @return  The VideoArchive object that the frames will be moved to.
     *     @uml.property  name="videoArchive"
     */
    public IVideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *     @return  Returns the videoFrames.
     *     @uml.property  name="videoFrames"
     */
    public Collection getVideoFrames() {
        return videoFrames;
    }

    /**
     * Merges the information contained in 2 different videoFrames
     * @param sourceFrame The VideoFrame whose contents will be moved to the target
     *  VideoFrame
     * @param targetFrame The destination for the information from the source
     *  VideoFrame
     */
    private void mergeFrames(final IVideoFrame sourceFrame, final IVideoFrame targetFrame) {

        /*
         * If a VideoFrame with the same timecode was found in the
         * target(and if you reached this point, one was found) copy the
         * observations to the new videoFrame
         */
        final Collection observations = new ArrayList(sourceFrame.getObservations());
        for (final Iterator j = observations.iterator(); j.hasNext(); ) {
            final IObservation observation = (IObservation) j.next();
            sourceFrame.removeObservation(observation);
            targetFrame.addObservation(observation);
        }

        /*
         * If the target does not have an image, but the source does, copy the image
         * to the target.
         */
        final ICameraData srcCameraData = sourceFrame.getCameraData();
        if (srcCameraData != null) {
            final String sourceImage = srcCameraData.getStillImage();
            final ICameraData targetCamData = targetFrame.getCameraData();
            if (targetCamData != null) {
                final String targetImage = targetCamData.getStillImage();
                if ((sourceImage != null) && (targetImage == null)) {
                    targetCamData.setStillImage(sourceImage);
                }
            }
        }
    }

    /**
     *     Sets the target VideoArchive. This is the destination that the VideoFrames will be moved to.
     *     @param  videoArchive
     *     @uml.property  name="videoArchive"
     */
    public void setVideoArchive(final IVideoArchive videoArchive) {
        if (log.isInfoEnabled()) {
            log.info("setting VideoArchive property with " + videoArchive);
        }

        this.videoArchive = videoArchive;
    }

    /**
     *     @param videoFrames  The videoFrames to set.
     *     @uml.property  name="videoFrames"
     */
    public void setVideoFrames(final Collection videoFrames) {
        if (log.isInfoEnabled()) {
            log.info("setting VideoFrame property with " + videoFrames);
        }

        this.videoFrames = videoFrames;
    }
}
