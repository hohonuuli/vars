/*
 * @(#)VideoFrame.java   2008.12.30 at 01:50:53 PST
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
import java.util.Set;
import vars.VideoMoment;

/**
 *
 * @author brian
 */
public interface VideoFrame extends AnnotationObject, VideoMoment {

    String PROP_CAMERA_DATA = "cameraData";
    String PROP_ALTERNATE_TIMECODE = "alternateTimecode";
    String PROP_IN_SEQUENCE = "inSequence";
    String PROP_RECORDED_DATE = "recordedDate";
    String PROP_TIMECODE  = "timecode";
    String PROP_PHYSICAL_DATA = "physicalData";

    /**
     * Add to the <code>Observation</code>  collection of this <code>VideoFrame</code>.
     * @param obs The <code>Observation</code> to add.
     * @see Observation
     * @return the Observation with updated ID.
     */
    void addObservation(Observation obs);

    /**
     * Get the <code>CameraData</code> associated with this <code>VideoFrame</code>.
     * @return  A handle to the <code>CameraData</code> object associated with  this <code>VideoFrame</code>. <b>null</b> will never be returned.
     * @see  CameraData
     */
    CameraData getCameraData();



    /**
     * @return  The observations associated with this annotated frame. This  collection is synchronized.
     */
    Set<Observation> getObservations();

    /**
     * Get the <code>PhysicalData</code> associated with this <code>VideoFrame</code>.
     * @return  A handle to the <code>PhysicalData</code> object associated with  this <code>VideoFrame</code>. <b>null</b> should never be returned.
     * @see  PhysicalData
     */
    PhysicalData getPhysicalData();


    /**
     * @return
     */
    VideoArchive getVideoArchive();

    /**
     * Indeicates if an annotation has an associated framegrab.
     * @return
     */
    boolean hasFrameGrab();


    /**
     * Check whether this <code>VideoFrame</code> is part of a larger sequence of <code>VideoFrame</code> objects.
     * @return <code><code>true</code></code> if this <code>VideoFrame</code> is part of a larger
     * sequence of <code>VideoFrame</code> objects; <code><code>false</code></code> otherwise.
     */
    boolean isInSequence();

    /**
     * @param obs
     *
     * @return
     */
    void removeObservation(Observation obs);


    /**
     * Set the HD <code>Timecode</code> for this <code>VideoFrame</code>.
     *
     * @param altTimecode  The HD <code>Timecode</code> object to associate with this  <code>VideoFrame</code>.
     */
    void setAlternateTimecode(String altTimecode);



    /**
     * Set the flag indicating whether this <code>VideoFrame</code> is part of a larger sequence of <code>VideoFrame</code> objects. a.k.a This annotation is part of larger group of annotations describing a sequence of events.
     * @param state  boolean indicator of sequence state
     */
    void setInSequence(boolean state);



    /**
     * Sets the time of the frame representing the annotation
     * @param dtg  The Date that the annotated frame was recorded in UTC.
     */
    void setRecordedDate(Date dtg);



    /**
     * @param  timecode
     */
    void setTimecode(String timecode);




}
