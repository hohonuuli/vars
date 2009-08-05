/*
 * @(#)IVideoFrame.java   2008.12.30 at 01:50:53 PST
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

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author brian
 */
public interface IVideoFrame {

    String PROP_CAMERA_DATA = "cameraData";
    String PROP_ALTERNATE_TIMECODE = "alternateTimecode";
    String PROP_IN_SEQUENCE = "inSequence";
    String PROP_RECORDED_DATE = "recordedDate";
    String PROP_TIMECODE  = "timecode";
    String PROP_PHYSICAL_DATA = "physicalData";

    /**
     * Add to the <code>Observation</code>  collection of this <code>VideoFrame</code>.
     * @param obs The <code>Observation</code> to add.
     * @see IObservation
     * @return the Observation with updated ID.
     */
    IObservation addObservation(IObservation obs);

    /**
     * Get the <code>CameraData</code> associated with this <code>VideoFrame</code>.
     * @return  A handle to the <code>CameraData</code> object associated with  this <code>VideoFrame</code>. <b>null</b> will never be returned.
     * @see  ICameraData
     */
    ICameraData getCameraData();


    /**
     * Get the HD <code>Timecode</code> associated with this <code>VideoFrame</code>.
     * @return  The HD <code>TTimecode/code> associated with this <code>VideoFrame</code>.
     */
    String getAlternateTimecode();


    /**
     * @return  The observations associated with this annotated frame. This  collection is synchronized.
     */
    Collection<? extends IObservation> getObservations();

    /**
     * Get the <code>PhysicalData</code> associated with this <code>VideoFrame</code>.
     * @return  A handle to the <code>PhysicalData</code> object associated with  this <code>VideoFrame</code>. <b>null</b> should never be returned.
     * @see  IPhysicalData
     */
    IPhysicalData getPhysicalData();

    /**
     * @return  The date that annotated frame was recorded.
     */
    Date getRecordedDate();


    /**
     * @return
     */
    String getTimecode();

    /**
     * @return
     */
    IVideoArchive getVideoArchive();

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
     * <p><!-- Method description --></p>
     *
     *
     * @param obs
     *
     * @return
     */
    boolean removeObservation(IObservation obs);

    /**
     * Set the <code>CameraData</code> for this <code>VideoFrame</code>. If the passed
     * <code>CameraData</code> object is equal to the current <code>CameraData</code> object, the
     * current object reference not changed; otherwise, the reference is set to the
     * passed parameter. In either case, the <code>CameraData</code> reference for this
     * <code>VideoFrame</code> is returned.
     *
     *
     * @param camera
     * @return The <code>CameraData</code> for this <code>VideoFrame</code>.
     * @see ICameraData
     */
    ICameraData setCameraData(ICameraData camera);


    /**
     * Set the HD <code>Timecode</code> for this <code>VideoFrame</code>.
     *
     * @param altTimecode  The HD <code>Timecode</code> object to associate with this  <code>VideoFrame</code>.
     */
    void setAlternateTimecode(String altTimecode);



    /**
     * Set the flag indicating whether this <code>VideoFrame</code> is part of a larger sequence of <code>VideoFrame</code> objects. a.k.a This annotation is part of larger group of annotations describing a sequence of events.
     * @param state  boolean indicator of sequence state
     * @uml.property  name="inSequence"
     */
    void setInSequence(boolean state);

    /**
     * Use by Castor, developers should not call this.
     * @param  observationCollToSet
     * @uml.property  name="observationColl"
     */
    //void setObservations(Collection observationCollToSet);

    /**
     * Set the <code>PhysicalData</code> for this <code>VideoFrame</code>. The <code>PhysicalData</code> object holds related information which pertains to all <code>Observation</code> objects of all <code>Annotator</code> objects of this <code>VideoFrame</code>.
     * @see IPhysicalData
     * @see IObservation
     * @param  ancillaryData
     * @uml.property  name="physicalData"
     */
    void setPhysicalData(IPhysicalData ancillaryData);

    /**
     * Sets the time of the frame representing the annotation
     * @param dtg  The Date that the annotated frame was recorded in UTC.
     * @uml.property  name="recordedDTG"
     */
    void setRecordedDate(Date dtg);

    /**
     * @param  long1
     * @uml.property  name="sampleId"
     */
    //void setSampleId(Long long1);

    /**
     * <p><!-- Method description --></p>
     * @param  timeCode
     * @uml.property  name="timeCode"
     */
    void setTimecode(String timecode);




}
