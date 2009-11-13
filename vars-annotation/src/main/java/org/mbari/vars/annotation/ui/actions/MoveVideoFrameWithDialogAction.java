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
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.locale.OpenVideoArchiveSetUsingParamsDialog;
import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.model.dao.VideoArchiveDAO;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(MoveVideoFrameWithDialogAction.class);

    /**
     *     @uml.property  name="moveAction"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private final MoveVideoFrameAction moveAction = new MoveVideoFrameAction();

    /**
     *     The owner of the dialog. For modal dialogs, if we don't set this it could put up the wrong frame, confusing users.
     *     @uml.property  name="owner"
     */
    private final Frame owner;

    /**
     *
     *
     * @param owner
     */
    public MoveVideoFrameWithDialogAction(final Frame owner) {
        super(ACTION_NAME);
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
     * <p><!-- Method description --></p>
     *
     *
     * @param videoFrames
     */
    public void setVideoFrames(final Collection videoFrames) {
        moveAction.setVideoFrames(videoFrames);
    }

    private class MoveDialog extends OpenVideoArchiveSetUsingParamsDialog {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

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

                    /**
                     *
                     */
                    private static final long serialVersionUID = 2616673187662138550L;

                    public void doAction() {
                        final int seqNumber = Integer.parseInt(getTfDiveNumber().getText());
                        final String platform = (String) getCbCameraPlatform().getSelectedItem();
                        final int tapeNumber = Integer.parseInt(getTfTapeNumber().getText());

                        // TODO 20061114 brian: HD is mbari specific; may need to facor out.
                        final String postfix = getCbHD().isSelected() ? "HD" : null;
                        VideoArchive va;
                        try {
                            va = VideoArchiveDAO.getInstance().openByParameters(platform, seqNumber, tapeNumber,
                                    postfix);
                        }
                        catch (final DAOException e) {
                            log.error("Failed to open a videoarchive", e);
                            AppFrameDispatcher.showErrorDialog("Unable to talk to database!");

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
