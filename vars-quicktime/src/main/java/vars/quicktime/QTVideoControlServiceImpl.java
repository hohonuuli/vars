/*
 * @(#)QTVideoControlServiceImpl.java   2013.02.15 at 10:45:46 PST
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

import java.awt.*;
import java.io.File;
import java.util.Date;
import javax.swing.*;
import org.mbari.framegrab.FakeGrabber;
import org.mbari.framegrab.IGrabber;
import org.mbari.movie.Timecode;
import org.mbari.vcr.IVCR;
import org.mbari.vcr.qt.TimeSource;
import org.mbari.vcr.qt.VCRWithDisplay;
import org.mbari.vcr.timer.AnnotationQueueVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.VARSException;
import vars.shared.ui.video.AbstractVideoControlService;
import vars.shared.ui.video.ImageCaptureException;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlStatus;
import vars.shared.ui.video.VideoTime;

/**
 * @author Brian Schlining
 * @since 2010-11-29
 */
public class QTVideoControlServiceImpl extends AbstractVideoControlService implements ImageCaptureService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private IGrabber grabber;

    /**
     * Constructs ...
     */
    public QTVideoControlServiceImpl() {

        // TODO make sure we've intialized everything correctly
    }

    /**
     *
     * @param timecode
     * @return
     *
     * @throws ImageCaptureException
     */
    public Image capture(String timecode) throws ImageCaptureException {
        return grabber.grab();
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
     * @param args The first argument is the string URL of the movie to open. The 2nd argument is a
     *  {@link TimeSource} object that indicates which timecode track to read from while annotating
     *  the movie file.
     */
    public void connect(Object... args) {

        disconnect();
        if (grabber != null) {
            grabber.dispose();
        }

        if ((args.length != 2) && !(args[0] instanceof String)) {
            throw new IllegalArgumentException("You didn't call this method correctly. The argument is the " +
                                               "string URL to the movie to open with QuickTime");
        }

        String movieName = (String) args[0];
        TimeSource timeSource = (TimeSource) args[1];

        try {
            VCRWithDisplay vcr0 = new VCRWithDisplay(movieName, timeSource);
            IVCR vcr = new AnnotationQueueVCR(vcr0);
            grabber = vcr0.getGrabber();
            setVcr(vcr);
            setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.CONNECTED));

        }
        catch (Exception e) {
            grabber = new FakeGrabber();
            setVcr(null);
            setVideoControlInformation(new VideoControlInformationImpl(movieName, VideoControlStatus.ERROR));
            throw new VARSException("Failed to open " + movieName + " with QuickTime for Java", e);
        }

    }

    /**
     */
    public void dispose() {
        getVcr().disconnect();
        grabber.dispose();
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
    @Override
    public boolean isPngAutosaved() {
        return false;
    }

    /**
     * @return
     */
    public VideoTime requestVideoTime() {
        IVCR vcr = getVcr();
        vcr.requestTimeCode();
        Timecode timecode = vcr.getVcrTimecode().getTimecode();
        final String tc = timecode.toString();

        return new VideoTime() {

            public Date getDate() {
                return null;
            }

            public String getTimecode() {
                return tc;
            }
        };
    }

    /**
     *
     * @param timecode
     */
    public void seek(String timecode) {
        getVcr().seekTimecode(new Timecode(timecode));
    }

    /**
     */
    public void showSettingsDialog() {

        // TODO Implement this
    }
}
