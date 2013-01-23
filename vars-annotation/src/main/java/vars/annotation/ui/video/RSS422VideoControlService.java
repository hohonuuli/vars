/*
 * @(#)RSS422VideoControlService.java   2009.12.09 at 08:58:47 PST
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

import gnu.io.CommPortIdentifier;
import java.io.File;
import java.util.Date;
import java.util.Set;
import javax.swing.JDialog;
import org.mbari.comm.CommUtil;
import org.mbari.movie.Timecode;
import org.mbari.nativelib.Native;
import org.mbari.util.NumberUtilities;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
//import org.mbari.vcr.purejavacomm.VCR;
import org.mbari.vcr.rs422.VCR;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import org.mbari.vcr.timer.AnnotationQueueVCR;
import org.mbari.vcr.timer.priority.AnnotationPQVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.annotation.ui.Lookup;
import vars.shared.ui.video.AbstractVideoControlService;
import vars.shared.ui.video.VideoControlStatus;
import vars.shared.ui.video.VideoTime;

/**
 *
 * @author brian
 */
public class RSS422VideoControlService extends AbstractVideoControlService {

    private static final Logger log = LoggerFactory.getLogger(RSS422VideoControlService.class);
    private double frameRate;
    private static final String LIBRARY_NAME = "rxtxSerial";


    /**
     * Constructs ...
     */
    public RSS422VideoControlService() {
        try {
            System.loadLibrary(LIBRARY_NAME);
            log.info(LIBRARY_NAME + " was found on the java.library.path and loaded");
        }
        catch (UnsatisfiedLinkError e) {
            extractAndLoadNativeLibraries();
        }
    }

    /**
   *
   * @param args String portName, Double frameRate
     */
    public void connect(Object... args) {

        disconnect();

        IVCR vcr = getVcr();

        if ((args.length != 2) && !(args[0] instanceof String)) {
            throw new IllegalArgumentException(
                "You didn't call this method correctly. Read the JavaDocs and check your arguments.");
        }

        String port = (String) args[0];
        Double fr = (Double) args[1];
        frameRate = fr.doubleValue();

        try {
            //vcr = new AnnotationPQVCR(new VCR(port));
            vcr = new AnnotationQueueVCR(new VCR(port));
            //vcr = new AnnotationMonitoringVCR(new VCR(port));
        }
        catch (Exception ex) {
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(port, VideoControlStatus.ERROR));
            throw new VARSException("Failed to connect to " + port, ex);
        }

        setVcr(vcr);
        setVideoControlInformation(new VideoControlInformationImpl(port, VideoControlStatus.CONNECTED));
    }

    /**
     */
    @Override
    public void disconnect() {
        IVCR vcr = getVcr();
        log.info("Disconnecting from " + vcr);
        if (vcr != null) {
            vcr.disconnect();
            setVcr(null);
        }
    }

    private void extractAndLoadNativeLibraries() {

       String libraryName = System.mapLibraryName("rxtxSerial");
       String os = System.getProperty("os.name");

        if (libraryName != null) {

            // Location to extract the native libraries into $HOME/.vars/native/Mac
            File libraryHome = new File(new File(Lookup.getSettingsDirectory(), "native"), os.substring(0, 3));
            if (!libraryHome.exists()) {
                libraryHome.mkdirs();
            }

            if (!libraryHome.canWrite()) {
                throw new VARSException("Unable to extract native libary to " + libraryHome +
                                        ". Verify that you have write access to that directory");
            }

            // This finds the native library, extracts it and hacks the java.library.path if needed
            new Native(LIBRARY_NAME, "native", libraryHome, getClass().getClassLoader());


        }
        else {
            log.error( "A native RXTX library for your platform is not available. " +
                    "You will not be able to control your VCR");
        }

    }

    /**
     * @return
     */
    public synchronized Set<CommPortIdentifier> findAvailableCommPorts() {
        return CommUtil.getAvailableSerialPorts();
    }

    /**
     * @return
     */
    public JDialog getConnectionDialog() {

//        if (connectionDialog == null) {
//            connectionDialog = new RS422VideoControlServiceDialog(this);
//            connectionDialog.setModal(true);
//        }
//        return connectionDialog;
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
        log.debug("Timecode: " + timecode.toString() + '\n' +
                "Userbits: " + vcrUserbits.getUserbits() + '\n' +
                "Date: " + date);

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

    String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

}
