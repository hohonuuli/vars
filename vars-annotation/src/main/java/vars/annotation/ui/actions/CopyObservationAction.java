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


package vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.DAO;
import vars.UserAccount;
import vars.annotation.AnnotationFactory;
import vars.annotation.Association;
import vars.annotation.CameraDirections;
import vars.annotation.VideoFrame;
import vars.annotation.VideoArchive;
import vars.annotation.Observation;
import vars.annotation.ui.ToolBelt;
import vars.old.annotation.ui.VideoService;
import vars.annotation.ui.Lookup;

/**
 * <p>
 * Copies a selected Observation but adds it to a new VidoeFrame.
 * </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public final class CopyObservationAction extends ActionAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;
    

    /**
     * Constructs ...
     *
     */
    public CopyObservationAction(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        putValue(Action.NAME, "Copy observation to a new timecode");
        putValue(Action.ACTION_COMMAND_KEY, "copy observation");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    /**
     *  Initiate the action
     *
     */
    public void doAction() {

        // Need a videoArchive to add a VideoFrame too.
        VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
        if (va != null) {
            
            
            
            final AnnotationFactory annotationFactory = toolBelt.getAnnotationFactory();

            // Need the VCR to get a current timecode
            final VideoService videoService = (VideoService) Lookup.getVideoServiceDispatcher().getValueObject();
            final IVCR vcr = videoService == null ? null : videoService.getVCR();
            if (vcr != null) {
                
                Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
                if (observations.size() == 0) {
                    return;
                }
                
                observations = new ArrayList<Observation>(observations); // Copy collection to avoid threading issues
                
                /*
                 * DAOTX See if a VideoFrame with the given time code already exists
                 */
                DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
                dao.startTransaction();
                va = dao.merge(va);
                
                final String timecode = vcr.getVcrTimecode().toString();
                VideoFrame videoFrame = va.findVideoFrameByTimeCode(timecode);
                if (videoFrame == null) {
                    videoFrame = annotationFactory.newVideoFrame();
                    videoFrame.setTimecode(timecode);
                    
                    CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
                    videoFrame.getCameraData().setDirection(cameraDirections.getDirection());
                    va.addVideoFrame(videoFrame);
                    dao.persist(videoFrame);
                    
                }
                
                UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                Date date = new Date();
                
                Collection<Observation> newObservations = new ArrayList<Observation>(); // List used to notify UI
                for (Observation observation : observations) {
                    Observation copyObservation = annotationFactory.newObservation();
                    copyObservation.setObserver(userAccount.getUserName());
                    copyObservation.setObservationDate(date);
                    copyObservation.setConceptName(observation.getConceptName());
                    videoFrame.addObservation(copyObservation);
                    dao.persist(copyObservation);
                    // Deep copy
                    for (Association association : new ArrayList<Association>(observation.getAssociations())) {
                        Association copyAssociation = annotationFactory.newAssociation();
                        copyAssociation.setLinkName(association.getLinkName());
                        copyAssociation.setLinkValue(association.getLinkValue());
                        copyAssociation.setToConcept(association.getToConcept());
                        copyObservation.addAssociation(copyAssociation);
                        dao.persist(copyAssociation);
                    }
                    newObservations.add(copyObservation);
                }
                dao.endTransaction();
                
                toolBelt.getPersistenceController().updateUI(newObservations);


            }
            else {
                log.warn("No VCR is available to get a timecode from; unable to create a VideoFrame");
            }
        }
        else {
            log.warn("A VideoArchive has no been assigned; Unable to create a VideoFrame");
        }
    }


}
