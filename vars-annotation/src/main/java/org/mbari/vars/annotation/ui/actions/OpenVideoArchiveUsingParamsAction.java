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

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.model.dao.VideoArchiveDAO;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Opens a <code>VideoArchive</code> for editing. You must first set the
 * fields of this object before calling doAction. For example:</p>
 *
 * <pre>
 * OpenVideoArchiveUsingParamsAction a = new OpenVideoArchiveUsingParamsAction();
 * a.setPlatform(VideoArchiveSet.VENTANA);
 * a.setSeqNumber(1359);
 * a.setTapeNumber(3);
 * a.doAction();
 * </pre>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: OpenVideoArchiveUsingParamsAction.java 418 2006-11-14 22:04:36Z hohonuuli $
 */
public class OpenVideoArchiveUsingParamsAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger("vars.annotation");

    /**
     * @uml.property  name="platform"
     */
    private String platform;
    private String postfix;

    /**
     * @uml.property  name="seqNumber"
     */
    private int seqNumber;

    /**
     * @uml.property  name="tapeNumber"
     */
    private int tapeNumber;

    /**
     * Constructor
     */
    public OpenVideoArchiveUsingParamsAction() {
        super("Open archive");
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (platform != null) {

            // Create a name like T0123-01 or V3210-10
            final String videoArchiveName = makeName();
            try {

                // See if a videoarchive with this id already exists
                VideoArchive videoArchive = VideoArchiveDAO.getInstance().findByVideoArchiveName(videoArchiveName);

                // if no matching videoarchive is found create it.
                if (videoArchive == null) {
                    videoArchive = VideoArchiveDAO.getInstance().openByParameters(platform, seqNumber, tapeNumber,
                            postfix);
                }
                else {
                    AppFrameDispatcher.showWarningDialog(
                        "You are opening a dive that already exists in the database. Please check your dive number.");
                }

                if (videoArchive != null) {
                    PredefinedDispatcher.VIDEOARCHIVE.getDispatcher().setValueObject(videoArchive);
                }
                else {
                    if (log.isErrorEnabled()) {
                        log.error("Unable to find or create a VideoArchive.");
                    }
                }
            }
            catch (final DAOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Database exception", e);
                }
            }
        }
    }

    /**
     * @return
     * @uml.property  name="platform"
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * @return
     * @uml.property  name="seqNumber"
     */
    public int getSeqNumber() {
        return seqNumber;
    }

    /**
     * @return
     * @uml.property  name="tapeNumber"
     */
    public int getTapeNumber() {
        return tapeNumber;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    protected String makeName() {
        return VideoArchive.makeVideoArchiveName(platform, seqNumber, tapeNumber, postfix);
    }

    /**
     * @param  string
     * @uml.property  name="platform"
     */
    public void setPlatform(final String string) {
        platform = string;
    }

    /**
     * Method description
     *
     *
     * @param postfix
     */
    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    /**
     * @param  i
     * @uml.property  name="seqNumber"
     */
    public void setSeqNumber(final int i) {
        seqNumber = i;
    }

    /**
     * @param  i
     * @uml.property  name="tapeNumber"
     */
    public void setTapeNumber(final int i) {
        tapeNumber = i;
    }
}
