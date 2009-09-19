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
public class VideoMomentByDateComparator implements Comparator<IVideoMoment> {

    public int compare(IVideoMoment o1, IVideoMoment o2) {
        return o1.getRecordedDate().compareTo(o2.getRecordedDate());
    }

}
