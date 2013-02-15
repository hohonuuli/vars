/*
 * @(#)QTImageCaptureServiceImpl.java   2013.02.15 at 10:46:13 PST
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



package vars.quicktime;

import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import org.mbari.framegrab.FakeGrabber;
import org.mbari.framegrab.GrabberException;
import org.mbari.framegrab.IGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;

/**
 * Adapter class to map QTX4J IGrabber to a VARS-REDUX ImageGrabber.
 *
 * @author brian
 */
public class QTImageCaptureServiceImpl implements ImageCaptureService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final IGrabber grabber;

    /**
     * Constructs ...
     */
    public QTImageCaptureServiceImpl() {
        IGrabber myGrabber = null;
        try {
            myGrabber = (IGrabber) Class.forName("org.mbari.framegrab.VideoChannelGrabber").newInstance();
        }
        catch (Exception e) {
            log.warn("Failed to initialize QuickTime for Java components.", e);
            myGrabber = new FakeGrabber();
        }
        grabber = myGrabber;
    }

    /**
     *
     * @param timecode
     * @return
     *
     * @throws ImageCaptureException
     */
    public Image capture(String timecode) throws ImageCaptureException {
        Image image = null;
        try {
            image = grabber.grab();
        }
        catch (GrabberException e) {
            throw new ImageCaptureException(e);
        }

        return image;
    }

    /**
     *
     * @param file
     * @return
     *
     * @throws ImageCaptureException
     */
    @Override
    public Image capture(File file) throws ImageCaptureException {
        throw new UnsupportedOperationException("This is not implemented for " + getClass().getSimpleName());
    }

    /**
     */
    public void dispose() {
        log.info("Disposing of " + getClass());
        grabber.dispose();
    }

    /**
     * @return
     */
    public IGrabber getGrabber() {
        return grabber;
    }

    /**
     * @return
     */
    @Override
    public boolean isPngAutosaved() {
        return false;
    }

    /**
     */
    public void showSettingsDialog() {
        Class clazz = grabber.getClass();
        try {

            // Use reflection since not all IGrabbers will have a
            // showSettingsDialog method.
            Method method = clazz.getMethod("showSettingsDialog", new Class[0]);
            method.invoke(grabber, new Object[0]);
        }
        catch (Throwable ex) {

            // Do nothing
            log.info("Unable to show dialog for " + clazz, ex);
            Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();
            JOptionPane.showMessageDialog(frame, "No settings are available", "VARS - Video Settings",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

    }
}
