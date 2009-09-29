/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.Date;

/**
 * Represents a moment of time on a videotape
 * @author brian
 */
public interface VideoMoment {

    String TIMECODE_INVALID = "--:--:--:--";

    /**
     *
     * @return The timecode for this moment
     */
    String getTimecode();

    /**
     * @return The alternate timecode for this moment
     */
    String getAlternateTimecode();

    /**
     * The date that this videoframe was actually recorded
     * @return
     */
    Date getRecordedDate();

}
