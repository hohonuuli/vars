/*
 * @(#)ICameraDeployment.java   2008.12.30 at 01:50:54 PST
 *
 * Copyright 2007 MBARI
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



package vars.annotation;

import java.util.Date;

/**
 *
 * @author brian
 */
public interface ICameraDeployment {

    String PROP_CHIEF_SCIENTIST_NAME = "chiefScientistName";
    String PROP_SEQUENCE_NUMBER = "sequenceNumber";
    String PROP_END_DATE = "endDate";
    String PROP_START_DATE = "startDate";
    String PROP_VIDEO_ARCHIVE_SET = "videoArchiveSet";

    /**
     * @return The String name of the chief scientist.
     */
    String getChiefScientistName();


    /**
     * Gets the sequence number (example: Dive Number).
     * @return  The sequence number for this <code>CameraPlatformDeployment</code>
     * @uml.property  name="seqNumber"
     */
    Integer getSequenceNumber();

    /**
     * @return  The ending date for this platformUsage.
     * @uml.property  name="usageEndDTG"
     */
    Date getEndDate();

    /**
     * @return  The starting date of this platform usage.
     * @uml.property  name="usageStartDTG"
     */
    Date getStartDate();

    /**
     * Returns the VideoArchiveSet associated with a CameraPlatformDeployment. Required by Castor
     * @return
     * @uml.property  name="videoArchiveSet"
     */
    IVideoArchiveSet getVideoArchiveSet();

    /**
     * Sets the String name of the chief scientist for this <code>CameraPlatformDeployment</code>.
     * This method is used by Castor to interface with a fake String name
     * instead of a person object.
     *
     *
     * @param chiefScientistName
     */
    void setChiefScientistName(String chiefScientistName);


    /**
     * Sets the sequence number, a.k.a. the dive number.
     * @param seqNumber  The sequential sequence number.
     * @uml.property  name="seqNumber"
     */
    void setSequenceNumber(Integer seqNumber);

    /**
     * Sets the <code>TimeBound</code> indicating the <code>CameraPlatformDeployment</code> start and end times. (Example: Dive Start and End Times)
     * @param  dtg
     * @uml.property  name="usageEndDTG"
     */
    void setEndDate(Date dtg);

    /**
     * Sets the <code>TimeBound</code> indicating the <code>CameraPlatformDeployment</code> start and end times. (Example: Dive Start and End Times)
     * @param  dtg
     * @uml.property  name="usageStartDTG"
     */
    void setStartDate(Date dtg);

}
