/*
 * @(#)UDPVideoControlService.java   2009.12.09 at 08:58:33 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.annotation.ui.video;

import java.util.Date;
import javax.swing.JDialog;
import org.mbari.movie.Timecode;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import org.mbari.vcr.udp01.VCR;
import vars.VARSException;
import vars.shared.ui.video.AbstractVideoControlService;
import vars.shared.ui.video.VideoControlStatus;
import vars.shared.ui.video.VideoTime;

/**
 *
 * @author brian
 */
public class UDPVideoControlService extends AbstractVideoControlService {

    private volatile double frameRate;

    /**
     *
     *
     * @param args [String host, Integer port, Double frameRate]
     */
    public void connect(Object... args) {
        disconnect();


        if ((args.length == 2) && !(args[0] instanceof String)) {
            throw new IllegalArgumentException(
                "You didn't call this method correctly. Read the JavaDocs and check your arguments.");
        }

        String host = (String) args[0];
        Integer port = (Integer) args[1];
        frameRate = ((Double) args[2]).doubleValue();

        try {
            final IVCR vcrUdp = new VCR(host, port);
            setVcr(new AnnotationMonitoringVCR(vcrUdp));
            setVideoControlInformation(new VideoControlInformationImpl(host + ":" + port, VideoControlStatus.CONNECTED));
        }
        catch (Exception ex) {
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(host + ":" + port, VideoControlStatus.ERROR));
            throw new VARSException("Failed to connect to " + port, ex);
        }
        
    }

    /**
     * @return
     */
    public JDialog getConnectionDialog() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return
     */
    public VideoTime requestVideoTime() {

        IVCR vcr = getVcr();

        /*
         * The vcr.requestXXX methods block until the VCR responds so we don't
         * have to do anything fancy to get the correct timecode. We need to
         * request both the timecode and userbits.
         */
        if (vcr.getVcrState().isPlaying()) {
            vcr.requestVTimeCode();
        }
        else {
            vcr.requestLTimeCode();
        }

        vcr.requestUserbits();

        // Read the timecode
        IVCRTimecode vcrTimecode = vcr.getVcrTimecode();
        IVCRUserbits vcrUserbits = vcr.getVcrUserbits();

        final Timecode timecode = vcrTimecode.getTimecode();
        timecode.setFrameRate(frameRate);

        // Convert userbits from byte[]->long->Date
        final int epicSeconds = NumberUtilities.toInt(vcrUserbits.getUserbits(), true);
        final Date date = new Date((long) epicSeconds * 1000L);

        return new VideoTimeImpl(timecode.toString(), date);
    }

    /**
     *
     * @param timecode
     */
    public void seek(String timecode) {

        // TODO Start a timer and listen for valid checksums. If a valid one
        // occurs reset the time. If timer ends throw a VideoControlException
        getVcr().seekTimecode(new Timecode(timecode, frameRate));
    }
}
