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

import java.util.Collection;
import java.util.Iterator;
import javax.swing.JProgressBar;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.ProgressDialog;
import org.mbari.vars.annotation.locale.UploadStillImageActionFactory;
import org.mbari.vars.annotation.ui.dispatchers.ObservationDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;
import vars.annotation.IObservation;
import vars.annotation.IVideoFrame;

/**
 * <p>This performs cleanup actions on the VideoArchive when it is closed.
 * These actions include, moving frame-grabs to a new location and updating
 * the StillImageURLs in the database.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: CloseVideoArchiveAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class CloseVideoArchiveAction extends ActionAdapter implements IVideoArchiveProperty {

    /** <!-- Field description --> */
    public static final String ACTION_NAME = "Close Video-archive";

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(CloseVideoArchiveAction.class);

    // private UpdateVideoArchiveAction action1 = new UpdateVideoArchiveAction();

    /**
     *     @uml.property  name="action2"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final org.mbari.vars.annotation.locale.UploadStillImageAction action2 =
        UploadStillImageActionFactory.getAction();

    /**
     *     @uml.property  name="videoArchive"
     *     @uml.associationEnd
     */
    private IVideoArchive videoArchive;

    /**
     * Constructor
     */
    public CloseVideoArchiveAction() {
        super(ACTION_NAME);
    }

    /**
     * Copies contents of CameraData.StillImageURL to image.archive.dir. This
     * also does a pattern match such that if any files exist with the same
     * name as the image but a different extension, they are also copied.
     */
    public void doAction() {
        if ((videoArchive == null) ||!isEnabled()) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Closing video archive, " + videoArchive);
        }

        final ProgressDialog progressDialog = AppFrameDispatcher.getProgressDialog();
        progressDialog.setLabel("Closing " + videoArchive.getVideoArchiveName());
        final JProgressBar progressBar = progressDialog.getProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(4);
        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressDialog.setVisible(true);

        /*
         * Save the last annotation
         */
        final IObservation observation = ObservationDispatcher.getInstance().getObservation();

        if ((observation != null) && (observation.getVideoFrame() != null)) {
            progressBar.setString("Saving " + observation.getVideoFrame().getTimeCode());
            progressBar.setValue(1);
            DAOEventQueue.updateVideoArchiveSet((IDataObject) observation);
        }

        /*
         * Flush all pending events from the UI.
         */
        //progressBar.setString("Commiting changes to the database");
        //progressBar.setValue(2);
        //DAOEventQueue.flush();

        /*
         * We want to scroll through all VideoFrames. If they do not have any
         * observations attached we want to delete them
         */
        if (log.isInfoEnabled()) {
            log.info("Removing empty VideoFrames from " + videoArchive);
        }
        progressBar.setString("Removing empty video-frames");
        progressBar.setValue(3);
        final Collection videoFrames = videoArchive.getVideoFrames();
        for (final Iterator i = videoFrames.iterator(); i.hasNext(); ) {
            final IVideoFrame videoFrame = (IVideoFrame) i.next();
            final Collection observations = videoFrame.getObservations();
            if (observations.size() == 0) {
                DAOEventQueue.delete((IDataObject) videoFrame);
            }
        }

        // Move images to server and update URL's
        action2.doAction();
        progressBar.setValue(4);
        progressDialog.setVisible(false);
        
        /*
         * Again, flush all pending events from the UI.
         */
        DAOEventQueue.flush();
        
    }

    /**
     *     @return  Returns the videoArchive.
     *     @uml.property  name="videoArchive"
     */
    public final IVideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *     @param videoArchive  The videoArchive to set.
     *     @uml.property  name="videoArchive"
     */
    public final void setVideoArchive(final IVideoArchive videoArchive) {
        this.videoArchive = videoArchive;

        // action1.setVideoArchive(videoArchive);
        action2.setVideoArchive(videoArchive);
    }
}
