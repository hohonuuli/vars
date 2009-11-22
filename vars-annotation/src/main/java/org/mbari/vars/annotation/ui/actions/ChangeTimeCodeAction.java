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


/*
Created on Jan 12, 2005
 */
package org.mbari.vars.annotation.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Icon;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.movie.Timecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.DAO;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.Observation;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 *
 * @author brian
 */
public class ChangeTimeCodeAction extends ActionAdapter {


    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This is the timecode we want to change to
     */
    private final Timecode timeCode = new Timecode();
    private final ToolBelt toolBelt;

    /**
     *
     */
    public ChangeTimeCodeAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
    }

    /**
     * @param name
     */
    public ChangeTimeCodeAction(final String name, ToolBelt toolBelt) {
        super(name);
        this.toolBelt = toolBelt;
    }

    /**
     * @param name
     * @param icon
     */
    public ChangeTimeCodeAction(final String name, final Icon icon, ToolBelt toolBelt) {
        super(name, icon);
        this.toolBelt = toolBelt;
    }

    /**
     *
     */
    public void doAction() {
        Collection<Observation> observations = (Collection<Observation>) Lookup.getSelectedObservationsDispatcher().getValueObject();
        observations = new ArrayList<Observation>(observations);
        // DAOTX
        DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
        dao.startTransaction();
        for (Observation observation : observations) {
            observation = dao.merge(observation);
            VideoFrame sourceVideoFrame = observation.getVideoFrame();
            VideoArchive videoArchive = sourceVideoFrame.getVideoArchive();
            VideoFrame targetVideoFrame = videoArchive.findVideoFrameByTimeCode(getTimeCode());
            if (targetVideoFrame == null) {
                sourceVideoFrame.setTimecode(getTimeCode());
            }
            else {
                // Move observations to target
                sourceVideoFrame.removeObservation(observation);
                targetVideoFrame.addObservation(observation);
            }
        }
        dao.endTransaction();
        
        toolBelt.getPersistenceController().updateUI(observations);
        
    }

    /**
     * @return Returns the timeCode.
     */
    public String getTimeCode() {
        return (timeCode == null) ? null : timeCode.toString();
    }

    /**
     *
     * @param timeCodeString
     */
    public void setTimeCode(final String timeCodeString) {
        timeCode.setTimecode(timeCodeString);
    }



 
}
