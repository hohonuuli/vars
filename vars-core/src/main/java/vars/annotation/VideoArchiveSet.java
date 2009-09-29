/*
 * @(#)VideoArchiveSet.java   2008.12.30 at 01:50:53 PST
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
import java.util.List;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface VideoArchiveSet extends AnnotationObject {

    String PROP_END_DATE = "endDate";
    String PROP_FORMAT_CODE = "formatCode";
    String PROP_PLATFORM_NAME = "platformName";
    String PROP_SHIP_NAME = "shipName";
    String PROP_START_DATE = "startDate";
    String PROP_TRACKING_NUMBER = "trackingNumber";
 

    /**
     * Add to the <code>CameraPlatformDeployment</code> collection.
     *
     *
     * @param cameraDeployment
     * @return The passed <code>CameraPlatformDeployment</code> object if added, the matching
     * <code>CameraPlatformDeployment</code> object otherwise.
     * @see                   CameraDeployment
     */
    void addCameraDeployment(CameraDeployment cameraDeployment);

    /**
     * Add an <code>VideoArchive</code>.
     *
     *
     * @param videoArchive
     * @return The passed <code>VideoArchive</code> object if added, the matching
     * <code>VideoArchive</code> object otherwise.
     * @see VideoArchive
     */
    void addVideoArchive(VideoArchive videoArchive);


    /**
     * Gets the cameraPlatformDeploymentColl attribute of the VideoArchiveSet object
     * @return     The cameraPlatformDeploymentColl. This is a synchronized list of  <code>CameraPlatformDeployment</code> objects. Remember to synchronize  on it before using it's iterator.
     * @uml.property  name="cameraPlatformDeploymentColl"
     */
    Set<CameraDeployment> getCameraDeployments();

    /**
     * Gets the endDTG attribute of the VideoArchiveSet object
     * @return     The endDTG value
     * @uml.property  name="endDTG"
     */
    Date getEndDate();

    /**
     * Get the char Format Code for this <code>VideoArchiveSet</code>.
     * @return     The char Format Code for this <code>VideoArchiveSet</code>.
     * @uml.property  name="formatCode"
     */
    char getFormatCode();




    /**
     * Get the String platform name for this <code>VideoArchiveSet</code>.
     * @return     The String platform name for this <code>VideoArchiveSet</code>.
     * @uml.property  name="platformName"
     */
    String getPlatformName();

    /**
     * Get the String ship name for this <code>VideoArchiveSet</code>.
     * @return     The String ship name for this <code>VideoArchiveSet</code>.
     * @uml.property  name="shipName"
     */
    String getShipName();

    /**
     * Gets the startDTG attribute of the VideoArchiveSet object
     * @return     The startDTG value
     * @uml.property  name="startDTG"
     */
    Date getStartDate();

    /**
     * Gets the trackingNumber attribute of the VideoArchiveSet object
     * @return     The trackingNumber value
     * @uml.property  name="trackingNumber"
     */
    String getTrackingNumber();

    /**
     * Retrieve a child VideoArchive with the matching videoArchiveName (which
     * is a unique key)
     *
     * @param videoArchiveName The videoArchiveName to search for. If null is
     * supplied then the return will always be null.
     * @return The matching videoarchive if this VideoArchvieSet contains a VideoArchive with
     * the given videoArchiveName. <b>null</b> otherwise.
     */
    VideoArchive getVideoArchiveByName(final String videoArchiveName);

    /**
     * Gets the videoArchiveColl attribute of the VideoArchiveSet object
     * @return     The videoArchiveColl value
     * @uml.property  name="videoArchiveColl"
     */
    Set<VideoArchive> getVideoArchives();

    /**
     * This is a convience method to retrieve all <code>VideoFrames</code> that are
     * stored in all the child VideoArchives.
     *
     * @return A collection of <i>ALL</i> VideoFrames that are part of this VideoArchiveSet
     */
    List<VideoFrame> getVideoFrames();

    //void setVideoFrames(Collection<? extends VideoFrame> videoFrames);

    /**
     *
     * @param seqNumber The sequence number to search for
     * @return <b>true</b> if this VideoArchiveSet contains a
     * CameraPlatfomDeployment with the given seqNumber. <b>false</b>
     * otherwise.
     */
    boolean hasSequenceNumber(final int seqNumber);

    /**
     * @param videoArchiveName The videoArchiveName to search for. If null is
     * supplied then the return will always be false.
     * @return <b>true</b> If this VideoArchvieSet contains a VideoArchive with
     * the given videoArchiveName. <b>false</b> otherwise.
     */
    boolean hasVideoArchiveName(final String videoArchiveName);

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param cpd
     */
    void removeCameraDeployment(CameraDeployment cpd);

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param va
     */
    void removeVideoArchive(VideoArchive va);

    /**
     * Sets the cameraPlatformDeploymentColl attribute of the VideoArchiveSet object. <font color="FFCCCC">WARNING: This is required by Castor and is <b>NOT</b> to be used by you, the developer, so back off.</font>
     * @param platformUsageCollToSet   The new cameraPlatformDeploymentColl value
     * @uml.property  name="cameraPlatformDeploymentColl"
     */
    //void setCameraPlatformDeployments(Collection<? extends CameraDeployment> platformUsageCollToSet);

    /**
     * Sets the ending date of the time period that a VideoArchiveSet represents
     * @param endDTG   The new endDTG value
     * @uml.property  name="endDTG"
     */
    void setEndDate(Date endDTG);

    /**
     * Required by Castor.
     * @param formatCode   The new formatCode value
     * @uml.property  name="formatCode"
     */
    void setFormatCode(char formatCode);

    /**
     * Set the name of the platform that the Camera was mounted onto. Typically this will be the ROV name
     * @param platformName   The ROV name. It can be up to 4 characters long.
     * @uml.property  name="platformName"
     */
    void setPlatformName(String platformName);

    /**
     * Set the ship name that produces this VideoArchiveSet
     * @param shipName   Can only be up to 4 characcters long
     * @uml.property  name="shipName"
     */
    void setShipName(String shipName);

    /**
     * Sets the starting date of the time period that a VideoArchiveSet was recorded over attribute of the VideoArchiveSet object
     * @param startDTG   The new startDTG value
     * @uml.property  name="startDTG"
     */
    void setStartDate(Date startDTG);

    /**
     * Set the trackingNumber
     * @param trackingNumber   It can be up to 7 characters long
     * @uml.property  name="trackingNumber"
     */
    void setTrackingNumber(String trackingNumber);

}
