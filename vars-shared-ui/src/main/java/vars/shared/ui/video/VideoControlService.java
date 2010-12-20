package vars.shared.ui.video;

import javax.swing.JDialog;

import org.mbari.vcr.IVCR;

public interface VideoControlService extends IVCR {
    
    /**
     * Connect to your video controller. This method accepts a varargs
     * as the argument so you can feed in whatever params you need.
     *
     * @param args The arguments need to connect to you video control service
     *
     */
    void connect(Object... args);

    /**
     * Connect to your video service with input from a User Interface. This
     * method should implement the UI needed to collect the parameters, then
     * call the connect method. The returned dialog may be requested once and then
     * subsequently reused by UI components so you should write it accordely.
     *
     * @return A JDialog that can be used to connect to your video service
     */
    JDialog getConnectionDialog();

    /**
     *
     * @return <b>true</b> if the VideoControlService is connected. <b>false</b> if
     *  it is not connected.
     */
    boolean isConnected();

    /**
     * Seek to the given timecode
     * @param timecode
     */
    void seek(String timecode);

    /**
     *
     * @return <b>true</b> If the video is playing. <b>false</b> if it is not playing (e.g.
     *  stopped, fast-forwarding, rewinding, etc.)
     */
    boolean isPlaying();

    /**
     *
     * @return <b>true</b> if the video is stopped
     */
    boolean isStopped();

    /**
     *
     * @return The current videotimeobject
     */
    VideoTime requestVideoTime();

    /**
     * Retrive information about the state of the connection
     * @return An object that encapsualtes the connection name and state.
     */
    VideoControlInformation getVideoControlInformation();


}
