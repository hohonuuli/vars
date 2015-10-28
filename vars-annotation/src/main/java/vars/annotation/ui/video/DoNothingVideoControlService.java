/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.video;

import vars.avplayer.AbstractVideoControlService;
import vars.avplayer.VideoTime;

import java.util.Date;
import javax.swing.JDialog;

/**
 *
 * @author brian
 */
public class DoNothingVideoControlService extends AbstractVideoControlService {

    public void connect(Object... args) {
        // Do nothing
    }

    public JDialog getConnectionDialog() {
        return new JDialog();
    }

    public void seek(String timecode) {
        // Do nothing
    }

    public VideoTime requestVideoTime() {
        return new VideoTimeImpl("00:00:00:00", new Date());
    }

}
