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

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

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

 
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private String platform;
    private String postfix;

    private int seqNumber;

 
    private int tapeNumber;

    protected final AnnotationDAOFactory annotationDAOFactory;
    
    /**
     * Constructor
     */
    public OpenVideoArchiveUsingParamsAction(AnnotationDAOFactory annotationDAOFactory) {
        super("Open archive");
        this.annotationDAOFactory = annotationDAOFactory;
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (platform != null) {

            // Create a name like T0123-01 or V3210-10
            final String videoArchiveName = PersistenceController.makeVideoArchiveName(platform, seqNumber, tapeNumber, postfix);
            try {
                
                // DAOTX
                VideoArchiveDAO videoArchiveDAO = annotationDAOFactory.newVideoArchiveDAO();
                videoArchiveDAO.startTransaction();

                // Get a video archive to attach this too
                VideoArchive videoArchive = videoArchiveDAO.findOrCreateByParameters(platform, seqNumber, videoArchiveName);
                videoArchiveDAO.endTransaction();
                Lookup.getVideoArchiveDispatcher().setValueObject(videoArchive);

            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            }
        }
    }

    /**
     * @return
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
     */
    public int getSeqNumber() {
        return seqNumber;
    }

    /**
     * @return
     */
    public int getTapeNumber() {
        return tapeNumber;
    }



    /**
     * @param  string
     */
    public void setPlatform(final String string) {
        platform = string;
    }

    /**
     *
     * @param postfix
     */
    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    /**
     * @param  i
     */
    public void setSeqNumber(final int i) {
        seqNumber = i;
    }

    /**
     * @param  i
     */
    public void setTapeNumber(final int i) {
        tapeNumber = i;
    }
}
