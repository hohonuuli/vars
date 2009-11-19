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

import java.awt.Frame;
import java.util.Collection;
import javax.swing.JDialog;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetUsingParamsDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;
import vars.annotation.ui.ToolBelt;

/**
 * Prompts a user with a dialog for platform, seqNumber and tapeNumber. Finds the
 * matching VideoArchive and moves the VideoFrames to the at VideoArchive. Use as:
 *
 * <pre>
 * MoveVideoFrameWithDialogAction action = new MoveVideoFrameWithDialogAction();
 * action.setVideoFrames(collectionOfVideoFrames);
 * action.doAction();
 * </pre>
 *
 * @author brian
 * @version $Id: MoveVideoFrameWithDialogAction.java 418 2006-11-14 22:04:36Z hohonuuli $
 */
public class MoveVideoFrameWithDialogAction extends ActionAdapter {

    /** <!-- Field description --> */
    public static final String ACTION_NAME = "Move Frames";

    private final Logger log = LoggerFactory.getLogger(getClass());
 
    private final MoveVideoFrameAction moveAction = new MoveVideoFrameAction();

    private final Frame owner;
    
    private final ToolBelt toolBelt;

    /**
     *
     * @param owner
     */
    public MoveVideoFrameWithDialogAction(final Frame owner, ToolBelt toolBelt) {
        super(ACTION_NAME);
        this.toolBelt = toolBelt;
        this.owner = owner;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        final JDialog dialog = new MoveDialog(owner);
        dialog.setVisible(true);
    }

    /**
     *
     * @param videoFrames
     */
    public void setVideoFrames(final Collection<VideoFrame> videoFrames) {
        moveAction.setVideoFrames(videoFrames);
    }

    private class MoveDialog extends OpenVideoArchiveSetUsingParamsDialog {


        /**
         * Constructs ...
         *
         *
         * @param owner
         */
        MoveDialog(final Frame owner) {
            super(owner);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return
         */
        @Override
        public ActionAdapter getOkButtonAction() {
            if (okButtonAction == null) {
                okButtonAction = new ActionAdapter() {

                    public void doAction() {
                        final int seqNumber = Integer.parseInt(getTfDiveNumber().getText());
                        final String platform = (String) getCbCameraPlatform().getSelectedItem();
                        final int tapeNumber = Integer.parseInt(getTfTapeNumber().getText());

                        // TODO 20061114 brian: HD is mbari specific; may need to factor out.
                        final String postfix = getCbHD().isSelected() ? "HD" : null;
                        final String name = PersistenceController.makeVideoArchiveName(platform, seqNumber, tapeNumber, postfix);
                        VideoArchive va;
                        try {
                            // DAOTX 
                            VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
                            dao.startTransaction();
                            va = dao.findOrCreateByParameters(platform, seqNumber, name);
                            dao.persist(va);
                            dao.endTransaction();
                        }
                        catch (final Exception e) {
                            log.error("Failed to open a videoarchive", e);
                            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);

                            return;
                        }

                        moveAction.setVideoArchive(va);
                        moveAction.doAction();
                        dispose();
                        moveAction.setVideoArchive(null);
                        moveAction.setVideoFrames(null);
                    }
                };
            }

            return okButtonAction;
        }
    }
}
