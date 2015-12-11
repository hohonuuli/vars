/*
 * @(#)VideoArchive.java   2008.12.30 at 01:50:53 PST
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

import vars.ILazy;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author brian
 */
public interface VideoArchive extends AnnotationObject, ILazy {

    String PROP_START_TIME_CODE = "startTimecode";
    String PROP_NAME = "name";
    String PROP_VIDEO_ARCHIVE_SET = "videoArchiveSet";

    void addVideoFrame(final VideoFrame videoFrame);

    /**
     * Searches the VideoArchive and returns the first video frame with the
     * given time code;
     * @param timecode A String in the format of HH:MM:SS:FF
     * @return The VideoFrame with a timecode that matches the argument.
     * If no matches are found <b>null</b> is returned. If multiple
     * matches exist the first one found is returned.
     */
    VideoFrame findVideoFrameByTimeCode(final String timecode);

    /**
     * Empty VideoFrames are  those that do not contain any observations. In general,
     * this method isn't really needed. However, when reading vif files, occasionally
     * an empty one escapes through. This finds thes errant beasts and returns
     * references to them.
     *
     * @return A collection of VideoFrames that do not contain any observations. The
     * collection will be empty if no empty VideoFrames were found.
     *
     */
    Collection<VideoFrame> getEmptyVideoFrames();


    /**
     * Stored in DB as a String instead of Timecode object
     *
     * @return
     */
    String getStartTimecode();

    /**
     * Get the tape number of this <code>VideoArchive</code>.
     * @return  The integer tape number.
     * @uml.property  name="videoArchiveName"
     */
    String getName();

    /**
     * @return
     */
    VideoArchiveSet getVideoArchiveSet();

    /**
     * Retrieve the videoframe collection. Note: This can result in a database access if the VideoArchive has been persisted and retrieved from the database. Do not add or remove items directly from this collections.
     * @return  A synchronized collection.
     */
    List<VideoFrame> getVideoFrames();


    void removeVideoFrame(final VideoFrame videoFrame);


    /**
     * The timecode is stored in DB as a String instead of Timecode object -
     * Castor uses this method for O/R. Developers should use setTimeCode instead
     * of this method.
     *
     * @param timecode
     */
    void setStartTimecode(final String timecode);

    /**
     * Set the tape number of this <code>VideoArchive</code> using the specified Integer.
     * @param videoArchiveName  An Integer tape number for this <code>VideoArchive</code>.
     * @uml.property  name="videoArchiveName"
     */
    void setName(final String videoArchiveName);


}
