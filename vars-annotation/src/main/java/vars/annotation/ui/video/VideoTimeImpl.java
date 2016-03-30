/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.video;

import java.util.Date;
import vars.annotation.ui.StateLookup;

/**
 *
 * @author brian
 * @deprecated Use VideoIndex instead
 */
public class VideoTimeImpl implements VideoTime {

    private final String timecode;
    private final Date date;


    public VideoTimeImpl(String timecode, Date date) {
        this.timecode = timecode;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getTimecode() {
        return timecode;
    }

    @Override
    public String toString() {
        return "<VideoTime date=\"" + StateLookup.getUTCDateFormat().format(date) + "\" timecode=\"" + timecode + "\" />";
    }

}
