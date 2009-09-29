/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation;

import java.util.Comparator;

/**
 *
 * @author brian
 */
public class VideoArchiveNameComparator implements Comparator<VideoArchive> {

    public int compare(VideoArchive o1, VideoArchive o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
