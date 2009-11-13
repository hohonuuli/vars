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
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.annotation.model.VideoFrame;
import org.mbari.vars.annotation.model.dao.ObservationDAO;
import org.mbari.vars.annotation.model.dao.VideoFrameDAO;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;

/**
 *  <p>Deletes an observation from the database. Also deletes the associated video
 *  frame, if appropriate.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  February 3, 2004
 * @version  $Id: DeleteObservationAction.java 408 2006-10-31 23:53:27Z hohonuuli $
 */
public class DeleteObservationAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *  Description of the Field
     */
    private static final Logger log = LoggerFactory.getLogger(DeleteObservationAction.class);

    /**
     *     Description of the Field
     *     @uml.property  name="observation"
     *     @uml.associationEnd
     */
    private IObservation observation;

    /**
     * @see  org.mbari.awt.event.IAction
     */
    public void doAction() {
        if (observation != null) {
            final IVideoFrame vf = observation.getVideoFrame();
            if (vf != null) {

                synchronized (DAOEventQueue.getInstance()) {
                    DAOEventQueue.flush();

                    try {
                        ObservationDAO.getInstance().delete((Observation) observation);
                    }
                    catch (DAOException e) {
                        AppFrameDispatcher.showErrorDialog("An error occured while deleting " + observation +
                                                           ". Reason:" + e.getLocalizedMessage());
                    }

                    vf.removeObservation(observation);

                    final Collection obs = vf.getObservations();

                    // Check to see that the VideoFrame has other observations. If
                    // not then delete it.
                    if (obs.size() == 0) {
                        final IVideoArchive va = vf.getVideoArchive();

                        // Delete the VideoFrame from the database.
                        try {
                            VideoFrameDAO.getInstance().delete((VideoFrame) vf);
                        }
                        catch (DAOException e) {
                            AppFrameDispatcher.showErrorDialog("An error occured while deleting " + vf + ". Reason:" +
                                                               e.getLocalizedMessage());
                        }

                        va.removeVideoFrame(vf);

                    }
                }

//              //Delete the Observation from the database.
//              vf.removeObservation(observation);
//              DAOEventQueue.delete(observation);
//              
//              
//              final Collection obs = vf.getObservations();
//
//              //Check to see that the VideoFrame has other observations. If
//              //not then delete it.
//              if(obs.size() == 0) {
//               final VideoArchive va = vf.getVideoArchive();
//
//               // Delete the VideoFrame from the database.
//               va.removeVideoFrame(vf);
//               DAOEventQueue.delete(vf);
//              }
            }
            else {
                log.warn("Attempted to delete an observation without a parent" +
                         " video frame. How did you even create an observation " + " without a parent VideoFrame?");
            }
        }
        else {
            log.info("Attempted to delete an Observation without selecting" + "the observation to be deleted");
        }
    }

    /**
     *     @return
     *     @uml.property  name="observation"
     */
    public IObservation getObservation() {
        return observation;
    }

    /**
     *     @param  observation
     *     @uml.property  name="observation"
     */
    public void setObservation(final IObservation observation) {
        this.observation = observation;
    }
}
