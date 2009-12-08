/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.annotation.ui.video;

import gnu.io.CommPortIdentifier;

/**
 *
 * @author brian
 */
public class RS422VideoControlInformation implements VideoControlInformation {

    private final CommPortIdentifier commPortIdentifier;
    private final VideoControlStatus videoControlStatus;

    public RS422VideoControlInformation(final CommPortIdentifier commPortIdentifier, VideoControlStatus status) {
        this.commPortIdentifier = commPortIdentifier;
        this.videoControlStatus = status;
    }

    public String getVideoConnectionID() {
        return commPortIdentifier.getName();
    }

    public VideoControlStatus getVideoControlStatus() {
        return videoControlStatus;
    }

}
