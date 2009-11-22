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

import org.bushe.swing.event.EventBus;

import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSetDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * <p>Changes the videoArchvieName property of  a VideoArchive. At MBARI,
 * the videoArchiveName is a composite key of platform, seqNumber and
 * tapeNumber, so we have to be sure that the renamed archive gets associated
 * with the correct properties.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeVideoArchiveNameAction extends OpenVideoArchiveUsingParamsAction {
    
    private final ToolBelt toolBelt;



    public ChangeVideoArchiveNameAction(ToolBelt toolBelt) {
        super(toolBelt.getAnnotationDAOFactory());
        this.toolBelt = toolBelt;
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (!verifyParams() ||!verifyNameIsChanging()) {
            return;
        }

        final VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        VideoArchiveSet newVas;
        try {
            newVas = resolveVideoArchiveSet(va);
        }
        catch (final Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            return;
        }

        final VideoArchiveSet orgVas = va.getVideoArchiveSet();
        orgVas.removeVideoArchive(va);

        try {

            /*
             * We're flushing all pending Database transactions first before we
             * change the name of the archive. This makes error handling much
             * simpler since now we only have to worry about the current thread.
             */
            final DAOEventQueue eventQueue = DAOEventQueue.getInstance();
            synchronized (eventQueue) {
                DAOEventQueue.flush();
                VideoArchiveSetDAO.getInstance().updateVideoArchiveSet((VideoArchiveSet) orgVas);
            }
        }
        catch (final Exception e1) {
            AppFrameDispatcher.showErrorDialog("Failed to change the name in the database. Reason: " + e1.getMessage());
            orgVas.addVideoArchive(va);

            return;
        }

        newVas.addVideoArchive(va);
        final String oldName = va.getVideoArchiveName();
        va.setVideoArchiveName(makeName());
        DAOEventQueue.update((VideoArchiveSet) newVas, new ErrorHandler1((VideoArchiveSet) newVas, (VideoArchiveSet) orgVas, (VideoArchive) va, oldName));
        VideoArchiveDispatcher.getInstance().setVideoArchive(va);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param va
     *
     * @return
     *
     * @throws Exception
     */
    private VideoArchiveSet resolveVideoArchiveSet(final VideoArchive va) throws Exception {

        /*
         *  Check to see if the existing vas is a match. If so use it, otherwise
         *  we'll need to check to see if a match exists in the database. If
         *  no match is found then we'll need to create one and insert it.
         */
        VideoArchiveSet vas = null;
        if (verifyVideoArchiveSetIsChanging()) {
            final String p = getPlatform();
            final int sn = getSeqNumber();
            final VideoArchiveSetDAO vasDAO = toolBelt.getAnnotationDAOFactory().newVideoArchiveSetDAO();
            

            /*
             *  Check the database for an existing match
             */
            Collection<VideoArchiveSet> videoArchiveSets = vasDAO.findAllByPlatformAndSequenceNumber(p, sn);
            if (videoArchiveSets.size() > 0) {
                vas
            }
            

            /*
             *  If no match was found create one and insert it.
             */
            if (vas == null) {
                vas = VideoArchiveSet.makeVideoArchiveSet(p, sn);
                DAOEventQueue.insert((VideoArchiveSet) vas);
            }
        }
        else {
            vas = va.getVideoArchiveSet();
        }

        return vas;
    }


    /**
     * @return  true if all the params are valid
     */
    private boolean verifyParams() {
        boolean ok = true;

        /*
         *  Verify that all the need parameters are present
         */
        final String p = getPlatform();
        final int sn = getSeqNumber();
        final int tn = getTapeNumber();

        // Check that all required info is entered
        if ((p == null) || (sn == 0) || (tn == 0)) {
            EventBus.publish(Lookup.TOPIC_WARNING, "Some of the information " +
                    "required to carry out this action is missing. You're request is being ignored.");
            ok = false;
        }

        return ok;
    }

    /**
     * @return  true if The platform and sequence number do not match those in
     *  the current videoArchiveSet. This means we'll need to do a database
     *  lookup to see if a match exists.
     */
    private boolean verifyVideoArchiveSetIsChanging() {
        boolean ok = true;
        final String p = getPlatform();
        final int sn = getSeqNumber();
        final VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (videoArchive != null) {
            final VideoArchiveSet vas = videoArchive.getVideoArchiveSet();
            if (vas.hasSequenceNumber(sn) && vas.getPlatformName().equals(p)) {
                ok = false;
            }
        }

        return ok;
    }

}
