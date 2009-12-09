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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JDialog;
import org.mbari.comm.CommUtil;
import org.mbari.movie.Timecode;
import org.mbari.util.LibPathHacker;
import org.mbari.util.NumberUtilities;
import org.mbari.util.IOUtilities;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.IVCRTimecode;
import org.mbari.vcr.IVCRUserbits;
import org.mbari.vcr.rs422.VCR;
import org.mbari.vcr.timer.AnnotationMonitoringVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.annotation.ui.Lookup;

/**
 *
 * @author brian
 */
public class RSS422VideoControlService extends AbstractVideoControlService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private double frameRate;

    //private RS422VideoControlServiceDialog connectionDialog;

    /**
     * Constructs ...
     */
    public RSS422VideoControlService() {
            extractAndLoadNativeLibraries();
    }

    /**
   *
   * @param args String portName, Double frameRate
     */
    public void connect(Object... args) {

        disconnect();

        IVCR vcr = (IVCR) getVcr();

        if ((args.length != 2) && !(args[0] instanceof String)) {
            throw new IllegalArgumentException(
                "You didn't call this method correctly. Read the JavaDocs and check your arguments.");
        }

        String port = (String) args[0];
        Double fr = (Double) args[1];
        frameRate = fr.doubleValue();

        try {
            vcr = new AnnotationMonitoringVCR(new VCR(port));
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
        if (vcr != null) {
            vcr.disconnect();
            setVcr(null);
        }
    }

    private void extractAndLoadNativeLibraries() {


        // Map the operating system to the correct library
        Map<String, String> libraryNames = new HashMap<String, String>() {

            {
                put("Win", "rxtxSerial.dll");
                put("Lin", "librxtxSerial.so");
                put("Mac", "librxtxSerial.jnilib");
            }
        };


        String os = System.getProperty("os.name");

        String libraryName = libraryNames.get(os.substring(0, 3));

        if (libraryName != null) {

            // Location to extract the native libraries into $HOME/.simpa/native/Mac
            File libraryHome = new File(new File(Lookup.getSettingsDirectory(), "native"), os.substring(0, 3));
            if (!libraryHome.exists()) {
                libraryHome.mkdirs();
            }

            if (!libraryHome.canWrite()) {
                throw new VARSException("Unable to extract native libary to " + libraryHome +
                                        ". Verify that you have write access to that directory");
            }

            // Full path to where we want the native library to be
            File libraryFile = new File(libraryHome, libraryName);
            libraryFile.deleteOnExit();

            /*
             * Copy the library to a location where we can use it.
             *
             * TODO: What's the best way to update the existing library? I only
             * want to do that if needed.
             */
            if (libraryFile.exists()) {
                log.info("The native RXTX library " + libraryFile.getAbsolutePath() +
                         " already exists. Skipping extraction");
            }
            else {
                try {

                    URL url = getClass().getResource("/native/" + libraryName);
                    log.info("Copying RXTX native library from " + url.toExternalForm() + " to " + libraryFile);
                    FileOutputStream out = new FileOutputStream(libraryFile);
                    InputStream in = getClass().getResourceAsStream("/native/" + libraryName);
                    IOUtilities.copy(in, out);
                    in.close();
                    out.close();
                }
                catch (Exception e) {
                    throw new VARSException("Failed to extract RXTX native library to " +
                                            libraryFile.getAbsolutePath(), e);
                }
            }

            /*
             * HACK ALERT!! This will only work on Sun's JVM. We're hacking the
             * java.library.path variable so that we can modify it at runtime.
             */
            try {
                LibPathHacker.addDir(libraryHome.getAbsolutePath());
            }
            catch (Exception e) {
                log.warn("Unable to hack the java.library.path property. This " +
                         "may have occured if you aren't running on Sun's JVM. " +
                         "VARS may be unable to access the RXTX native libraries");
            }

            try {

                /*
                 * We'll run into problems if we try to load the library more
                 * than once into the JVM. I think the catch statement will trap
                 * that issue though.
                 */
                System.load(libraryFile.getAbsolutePath());
            }
            catch (Exception e) {
                log.warn("Failed to load the native library '" + libraryFile.getAbsolutePath());
            }

        }
        else {
            log.error(
                "A native RXTX library for your platform is not available. You will not be able to control your VCR");
        }

    }

    /**
     * @return
     */
    public Set<CommPortIdentifier> findAvailableCommPorts() {
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
