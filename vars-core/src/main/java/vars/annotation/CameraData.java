/*
 * @(#)CameraData.java   2008.12.30 at 01:50:54 PST
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
public interface CameraData extends AnnotationObject {

    String PROP_DIRECTION = "direction";
    String PROP_FIELD_WIDTH = "fieldWidth";
    String PROP_FOCUS = "focus";
    String PROP_IRIS = "iris";
    String PROP_NAME = "name";
    String PROP_FRAME_GRAB_URL = "frameGrabURL";
    String PROP_VIDEO_FRAME = "videoFrame";
    String PROP_ZOOM = "zoom";


    /**
     * Check to verfiy that a cameradata object contains data.
     * @return <strong>false</strong> if the data values (name, direction, zoom,
     * iris, fieldWidth, and stillImage) are all null. <strong>true</strong> if
     * any of these values are not null
     */
    boolean containsData();

    /**
     * Get the <code>CameraData</code> direction.
     * @return  The String value of <code>CameraData</code> direction.
     */
    String getDirection();

    /**
     * Get the <code>CameraData</code> field width.
     * @return  The double value of <code>CameraData</code> field width.
     */
    Double getFieldWidth();

    /**
     * Get the <code>CameraData</code> focus.
     * @return  The integer value of <code>CameraData</code> focus.
     */
    Integer getFocus();

    /**
     * Get the <code>CameraData</code> iris.
     * @return  The integer value of <code>CameraData</code> iris.
     */
    Integer getIris();


    /**
     * Get the String name for this <code>CameraData</code> object.
     * @return  The String name for this <code>CameraData</code> object.
     */
    String getName();

    /**
     * Get the <code>String</code> of the Still Image captured by this <code>CameraData</code>.
     * @return  The <code>String</code> of the Still Image URL captured by  this <code>CameraData</code>.
     */
    String getFrameGrabURL();

    /**
     * @return
     */
    VideoFrame getVideoFrame();

    /**
     * Get the <code>CameraData</code> zoom.
     * @return  The integer value of <code>CameraData</code> zoom.
     */
    Integer getZoom();

    /**
     * <p>Set <code>CameraData</code> direction. This is typically a string such as 'ascending', 'descending', 'or cruise'. <font color="FFCCCC"> WARNING: Names longer than 50 characters will be truncated</font></p>
     * @param  direction
     */
    void setDirection(String direction);

    /**
     * Set <code>CameraData</code> field width.
     * @param fieldWidth  The double value of camera field width.
     */
    void setFieldWidth(Double fieldWidth);

    /**
     * Set <code>CameraData</code> focus.
     * @param focus  The integer value of camera focus.
     */
    void setFocus(Integer focus);


    /**
     * Set <code>CameraData</code> iris.
     * @param iris  The integer value of camera iris.
     */
    void setIris(Integer iris);

    /**
     * <p><!-- Method description --></p>
     * @param  logDate
     */
    void setLogDate(Date logDate);

    /**
     * <p>Set the name of the camera used to capture the related video tape. <font color="FFCCCC">WARNING: Names longer than 50 characters will be truncated</font></p>
     * @param name  The String name of the camera used to capture the related  video tape.
     */
    void setName(String name);

    /**
     * Set <code>String</code> of the Still Image URL captured by this <code>CameraData</code>.
     * @param  stillImage
     */
    void setFrameGrabURL(String stillImage);


    /**
     * Set <code>CameraData</code> zoom.
     * @param zoom  The integer value of camera zoom.
     */
    void setZoom(Integer zoom);
}
