/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars;

import java.util.Comparator;

/**
 *
 * @author brian
 */
public class VideoMomentByTimecodeComparator implements Comparator<IVideoMoment> {

    public int compare(IVideoMoment o1, IVideoMoment o2) {
        return o1.getTimecode().compareTo(o2.getTimecode());
    }

}
