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

import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.model.VideoArchiveSet;
import org.mbari.vars.annotation.model.dao.VideoArchiveSetDAO;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchive;

/**
 * <p>Changes the videoArchvieName property of  a VideoArchive. At MBARI,
 * the videoArchiveName is a composite key of platform, seqNumber and
 * tapeNumber, so we have to be sure that the renamed archvie gets associationed
 * with the correct properties.</p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: ChangeVideoArchiveNameAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ChangeVideoArchiveNameAction extends OpenVideoArchiveUsingParamsAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (!verifyParams() ||!verifyNameIsChanging()) {
            return;
        }

        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        IVideoArchiveSet newVas;
        try {
            newVas = resolveVideoArchiveSet(va);
        }
        catch (final Exception e) {
            AppFrameDispatcher.showErrorDialog("Failed to connect to the " + "database. Reason: " + e.getMessage());

            return;
        }

        final IVideoArchiveSet orgVas = va.getVideoArchiveSet();
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
    private IVideoArchiveSet resolveVideoArchiveSet(final IVideoArchive va) throws Exception {

        /*
         *  Check to see if the existing vas is a match. If so use it, otherwise
         *  we'll need to check to see if a match exists in the database. If
         *  no match is found then we'll need to create one and insert it.
         */
        IVideoArchiveSet vas = null;
        if (verifyVideoArchiveSetIsChanging()) {
            final String p = getPlatform();
            final int sn = getSeqNumber();
            final VideoArchiveSetDAO vasDao = VideoArchiveSetDAO.getInstance();

            /*
             *  Check the database for an existing match
             */
            vas = vasDao.findByPlatformAndSequenceNumber(p, sn);

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
     * @return  true if the new name is going to be different than the current
     *          name
     */
    private boolean verifyNameIsChanging() {
        boolean ok = true;

        /*
         *  Verify that the name is indeed being changed. If its the same then
         *  exit
         */
        final String newName = makeName();
        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        if (va != null) {
            final String orgName = va.getVideoArchiveName();
            final boolean sameName = newName.equals(orgName);
            if (sameName) {
                AppFrameDispatcher.showWarningDialog("The information you " +
                        "entered is the same as the Tape you are currently " +
                        "annotating. You're request is being ignored.");
                ok = false;
            }
        }

        return ok;
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
            AppFrameDispatcher.showWarningDialog("Some of the information " +
                    "required to carry out this action is missing. You're " + "request is being ignored.");
            ok = false;
        }

        return ok;
    }

    /**
     * @return  true if The platform and sequence number do not match those in
     *  the current videoArchiveSet. This means we'll need to do a database
     *  llokup to see if a match exists.
     */
    private boolean verifyVideoArchiveSetIsChanging() {
        boolean ok = true;
        final String p = getPlatform();
        final int sn = getSeqNumber();
        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        if (va != null) {
            final IVideoArchiveSet vas = va.getVideoArchiveSet();
            if (vas.hasSeqNumber(sn) && vas.getPlatformName().equals(p)) {
                ok = false;
            }
        }

        return ok;
    }

    /**
     *     @author  brian
     */
    private class ErrorHandler1 extends DAOExceptionHandler {

        final IVideoArchiveSet newVas;
        final String oldName;
        final IVideoArchiveSet oldVas;
        final IVideoArchive va;

        /**
         * Constructs ...
         *
         *
         *
         * @param newVas
         * @param oldVas
         * @param va
         * @param oldName
         */
        public ErrorHandler1(final IVideoArchiveSet newVas, final IVideoArchiveSet oldVas, final IVideoArchive va,
                             final String oldName) {
            this.newVas = newVas;
            this.va = va;
            this.oldVas = oldVas;
            this.oldName = oldName;
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param e
         */
        protected void doAction(final Exception e) {
            newVas.removeVideoArchive(va);
            oldVas.addVideoArchive(va);
            va.setVideoArchiveName(oldName);
        }
    }
}
