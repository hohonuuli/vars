/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.Date;

/**
 * Bean class for holding Dates and timecodes. This is analagous to a
 * videoframe's information
 * @author brian
 */
public class VideoMoment implements IVideoMoment {

    private final String timecode;
    private final String alternateTimecode;
    private final Date recordedDate;

    public VideoMoment(Date recordedDate, String timecode, String alternateTimecode) {
        this.timecode = timecode;
        this.alternateTimecode = alternateTimecode;
        this.recordedDate = recordedDate;
    }

    public String getAlternateTimecode() {
        return alternateTimecode;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public String getTimecode() {
        return timecode;
    }

    @Override
    public String toString() {
        return "VideoMoment[recordedDate=" + recordedDate + ", timecode=" + timecode +
                ", alternateTimecode=" + alternateTimecode + "]";
    }



}
