/*
 * @(#)CameraDeployment.java   2008.12.30 at 01:50:54 PST
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
public interface CameraDeployment extends AnnotationObject {

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
     */
    Integer getSequenceNumber();

    /**
     * @return  The ending date for this platformUsage.
     */
    Date getEndDate();

    /**
     * @return  The starting date of this platform usage.
     */
    Date getStartDate();

    /**
     * Returns the VideoArchiveSet associated with a CameraPlatformDeployment. Required by Castor
     * @return
     */
    VideoArchiveSet getVideoArchiveSet();

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
     */
    void setSequenceNumber(Integer seqNumber);

    /**
     * Sets the <code>TimeBound</code> indicating the <code>CameraPlatformDeployment</code> start and end times. (Example: Dive Start and End Times)
     * @param  dtg
     */
    void setEndDate(Date dtg);

    /**
     * Sets the <code>TimeBound</code> indicating the <code>CameraPlatformDeployment</code> start and end times. (Example: Dive Start and End Times)
     * @param  dtg
     */
    void setStartDate(Date dtg);

}
