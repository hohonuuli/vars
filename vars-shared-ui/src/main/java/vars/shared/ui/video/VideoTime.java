/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.video;

import java.util.Date;

/**
 * Represents both an index into a tape and it's corresponding moment in time
 *
 * @author brian
 */
public interface VideoTime {

    Date getDate();

    String getTimecode();

}