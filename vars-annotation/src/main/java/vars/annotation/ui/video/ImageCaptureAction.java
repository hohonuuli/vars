/*
 * @(#)ImageCaptureAction.java   2010.05.03 at 11:37:21 PDT
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

import com.google.common.collect.ImmutableList;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.awt.image.ImageUtilities;
import org.mbari.movie.Timecode;
import org.mbari.util.NumberUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.annotation.CameraData;
import vars.annotation.CameraDeployment;
import vars.annotation.CameraDirections;
import vars.annotation.Observation;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.VARSProperties;
import vars.annotation.ui.actions.NewObservationAction;
import vars.annotation.ui.commandqueue.Command;
import vars.annotation.ui.commandqueue.CommandEvent;
import vars.annotation.ui.commandqueue.impl.AddObservationCmd;
import vars.shared.preferences.PreferencesService;
import vars.shared.ui.video.ImageCaptureService;
import vars.shared.ui.video.VideoControlService;

/**
 * <p>Action for capturing a frame an performing related activities on it.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: FrameCaptureAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ImageCaptureAction extends ActionAdapter {

    /**
     *  Value used if no platform information is available
     */
    public final static String UNKNOWN_PLATFORM = "unknown";

    /**
     *  Value used if no dive information is available
     */
    public final static String UNKNOWN_SEQNUMBER = "0000";
    private final static NumberFormat format4i = new DecimalFormat("0000");
    private final static NumberFormat format3i = new DecimalFormat("000");
    private final static DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    private final static DateFormat timezoneFormat = new SimpleDateFormat("ZZ");
    private static final Logger log = LoggerFactory.getLogger(ImageCaptureAction.class);
    private static final String imageCopyrightOwner = VARSProperties.getImageCopyrightOwner();

    /**
     * Provides information about where to save the images
     */
    private final ImageDirectory imageDirectory;
    private final FrameCaptureFunction frameCaptureFunction;

    /**
     * This class does all the heavy lifting.
     */
    private final ToolBelt toolBelt;

    /**
     * Constructor for the FrameCaptureAction object
     *
     * @param toolBelt
     */
    public ImageCaptureAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        this.imageDirectory = new ImageDirectory();
        this.frameCaptureFunction = new FrameCaptureFunction();
        boolean ok = false;
        putValue(Action.NAME, "Frame Capture");
        putValue(Action.ACTION_COMMAND_KEY, "frame capture");
        putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        setEnabled(ok);
    }

    /**
     * Create .comment file for the .png file name
     *
     * @param  png Description of the Parameter
     * @param  snapTime Description of the Parameter
     */
    private static void createCommentFile(final File png, final SnapTime snapTime) {
        final String strAnnoText = "";

        // File comments
        final StringBuffer s = new StringBuffer("Copyright ").append(snapTime.getYear());
        s.append(imageCopyrightOwner).append("\n");
        s.append("File      = ").append(png.getAbsolutePath()).append(" (MAIN)\n");
        s.append("EPOCHsecs = ").append(snapTime.getTimeInSecs()).append("\n");
        s.append("LocalTime = ").append(snapTime.getFormattedLocalTime()).append("\n");
        s.append("UTCTime   = ").append(snapTime.getFormattedGmtTime()).append("\n");
        s.append("YYYYJJJ   = ").append(snapTime.getTrackingNumber()).append("\n");
        s.append("AnnTC     = ").append(snapTime.getTimeCodeAsString()).append("\n");
        s.append("SnapTC    = ").append(snapTime.getTimeCodeAsName()).append("\n");
        s.append("Annotation= ").append(strAnnoText).append("\n");

        if (log.isInfoEnabled()) {
            log.info("strComment = \n" + s.toString());
        }

        /*
         *  Write the comment to a file.
         */
        final File comment = new File(png.getAbsolutePath().replaceFirst(".png", ".comment"));
        try {
            final BufferedWriter bwCmt = new BufferedWriter(new FileWriter(comment));
            bwCmt.write(s.toString());
            bwCmt.close();
        }
        catch (final IOException e2) {
            if (log.isErrorEnabled()) {
                log.error("Failed to write " + comment.getAbsolutePath(), e2);
            }
        }
    }

    /**
     * Create .overlay file for the .png file name
     *
     * @param  png Description of the Parameter
     * @param  snapTime Description of the Parameter
     * @param  overlayText Description of the Parameter
     * @return  The File object representing the overlay file. null is returned if
     *          the fiel was not created.
     */
    private static File createOverlayFile(final File png, final SnapTime snapTime, final String[] overlayText) {
        final StringBuffer text = new StringBuffer("");
        for (int i = 0; i < overlayText.length; i++) {
            text.append(overlayText[i]).append("\n");
        }

        if (log.isInfoEnabled()) {
            log.info("strOverlay = \n" + text.toString());
        }

        /*
         *  Write the overlay file
         */
        File overlay = new File(png.getAbsolutePath().replaceFirst(".png", ".overlay"));
        try {
            final BufferedWriter bwOvly = new BufferedWriter(new FileWriter(overlay));
            bwOvly.write(text.toString());
            bwOvly.close();
        }
        catch (final IOException e2) {
            if (log.isErrorEnabled()) {
                log.error("Failed to create overlay file", e2);
            }

            overlay = null;
        }

        return overlay;
    }

    /**
     * Creates the textual overlay for the preview image
     *
     * @param  png Description of the Parameter
     * @param  snapTime Description of the Parameter
     * @return  A string array of ext to be overlaid onto an image.
     */
    private static String[] createOverlayText(final File png, final SnapTime snapTime) {
        final String[] s = new String[4];
        s[0] = "Copyright " + snapTime.getYear() + " " + imageCopyrightOwner;
        s[1] = png.getAbsolutePath() + " (MAIN)";
        s[2] = snapTime.getFormattedGmtTime() + " GMT (local +" +
                snapTime.getGmtOffset().replaceFirst("-", "").replaceAll("0", "") + ")";
        s[3] = "";

        return s;
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (isAvailable()) {
            frameCaptureFunction.capture();
        }
    }

    /**
     *  Gets the available attribute of the FrameCaptureAction object
     *
     * @return  true if a frame-capture card is installed and accessable
     */
    public boolean isAvailable() {
        return Lookup.getVideoControlServiceDispatcher().getValueObject() != null;
    }

    /**
     * Performs the frame-capture related tasks, then updates the VideoArchive object.
     * @author   brian
     */
    private class FrameCaptureFunction {

        private final NewObservationAction action = new NewObservationAction(toolBelt);

        /**
         * Constructs ...
         */
        public FrameCaptureFunction() {}

        /**
         *  Capture an image
         *
         */
        public void capture() {

            // Verify that we have the services needed to capture an image
            VideoControlService videoControlService = (VideoControlService) Lookup.getVideoControlServiceDispatcher()
                    .getValueObject();
            if (videoControlService == null) {
                EventBus.publish(Lookup.TOPIC_WARNING, "You are not connected to the VCR. Unable to capture a frame.");
                return;
            }

            ImageCaptureService imageCaptureService = (ImageCaptureService) Lookup.getImageCaptureServiceDispatcher()
                    .getValueObject();
            if (imageCaptureService == null) {
                EventBus.publish(Lookup.TOPIC_WARNING, "No image capture service is available for frame capture");
                return;
            }

            // Get the image archive we're adding to.
            VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
            if (videoArchive == null) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                        "No video-archive is open for annotating. Unable to capture an image.");
                return;
            }

            final String timecode = videoControlService.getVcrTimecode().toString();
            final SnapTime snapTime = new SnapTime(new Date(), timecode);
            final String timecode_ = snapTime.getTimeCodeAsName();

            /*
             * Grab the image from the videoChannel.
             */
            File png = null;
            BufferedImage bufferedImage = null;
            try {
                png = new File(imageDirectory.getImageDirectory(), timecode_ + ".png");

                // Captures image and writes a copy to disk in a seperate thread
                Image image = ImageCaptureUtilities.capture(imageCaptureService, timecode, png);
                bufferedImage = ImageUtilities.toBufferedImage(image);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                        "ERROR!! Failed to capture the frame. Reason given is " +
                                e.getMessage() + ". ");
                log.error("Frame-grab failed", e);
                return;
            }

            /*
             *  Create the comment file
             */
            createCommentFile(png, snapTime);

            /*
             *  Create the overlay file
             */
            final String[] overlayText = createOverlayText(png, snapTime);
            createOverlayFile(png, snapTime, overlayText);

            /*
             *  Create preview image with overlay
             */
            try {
                File jpg = new File(png.getAbsolutePath().replaceFirst(".png", ".jpg"));
                ImageCaptureUtilities.createJpgWithOverlay(bufferedImage, jpg, overlayText);

                /*
                 *  Update the information in the database
                 */
                updateVideoArchive(snapTime, jpg);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                        "ERROR!! Failed to create preview image. Reason given is " +
                                e.getMessage() + ".");
                log.error("Frame-grab failed", e);

                return;
            }

        }

        /**
         * Populate a videoFrame with the correct information
         */
        private void updateVideoArchive(SnapTime snapTime, File jpg) {

            VideoArchive videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
            if (videoArchive != null) {

                /*
                 * If the VCR is recording we'll grab the time off of the
                 * computer clock. Otherwise we'll get it off of the
                 * userbits.
                 */
                final VideoControlService videoService = (VideoControlService) Lookup.getVideoControlServiceDispatcher().getValueObject();
                Date utcDate;
                if (videoService.getVcrState().isRecording()) {
                    utcDate = new Date();
                }
                else {

                    /*
                    *  Try to grab the userbits off of the tape. The userbits
                    *  may have the time that the frame was recorded stored as a
                    *  little-endian 4-byte int.
                    */
                    videoService.requestVUserbits();
                    final int epicSeconds = NumberUtilities.toInt(videoService.getVcrUserbits().getUserbits(), true);
                    utcDate = new Date((long) epicSeconds * 1000L);
                }

                CameraDirections cameraDirections = (CameraDirections) Lookup.getCameraDirectionDispatcher().getValueObject();
                final String cameraDirection = cameraDirections.getDirection();

                UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
                String user = userAccount == null ? UserAccount.USERNAME_DEFAULT : userAccount.getUserName();
                String imageReference = null;

                try {
                    imageReference = jpg.toURI().toURL().toExternalForm();
                }
                catch (final MalformedURLException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Problem creating a URL.", e);
                    }
                }
                
                // FIXME physical object is hardcoded. Should be set in preferences
                Command command = new AddObservationCmd("physical object", snapTime.getTimeCodeAsString(), utcDate,
                        videoArchive.getName(), user, cameraDirection, null, imageReference);
                CommandEvent commandEvent = new CommandEvent(command);
                EventBus.publish(commandEvent);
            }

        }
    }


    /**
     * A convenience class that represents the location to write the images into. This
     * class updates automatically based on information from the VideoArchive. If
     * the VideoArchive is changed in the annotation app then this class will update
     * to reflect this change.
     *
     * @author  brian
     * @version
     */
    private class ImageDirectory {

        private final PreferencesService preferencesService;

        /**
         * Constructs ...
         *
         */
        ImageDirectory() {
            preferencesService = new PreferencesService(Lookup.getPreferencesFactory());
        }

        /**
         * The current imlementation creates a directory
         * $HOME/VARS/data/[platform]/images/[divenumber]" in the users
         * home directory and stores the images there.
         *
         * @return  The location of the image directory
         * @throws  IOException If unable to create or write to the image directory
         */
        File getImageDirectory() throws IOException {
            final UserAccount userAccount = (UserAccount) Lookup.getUserAccountDispatcher().getValueObject();
            final String hostname = preferencesService.getHostname();
            File imageTarget = preferencesService.findImageTarget(userAccount.getUserName(), hostname);


            // Get the platform name. Defaults to unknown
            final VideoArchive va = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();
            final VideoArchiveSet vas = va.getVideoArchiveSet();
            String platform = UNKNOWN_PLATFORM;
            if (vas != null) {
                platform = vas.getPlatformName();

                if (platform == null) {
                    platform = UNKNOWN_PLATFORM;
                }
            }

            final File rovDir = new File(new File(imageTarget, platform), "images");

            // Get the dive number. Defaults to 0000
            final Collection<CameraDeployment> cpds = vas.getCameraDeployments();
            String diveNumber = UNKNOWN_SEQNUMBER;
            if (cpds.size() != 0) {
                final CameraDeployment cd = cpds.iterator().next();
                diveNumber = format4i.format(cd.getSequenceNumber());
            }

            /*
             *  Create the directory. Throw exceptions if there is a problem
             */
            File imageDir = new File(rovDir, diveNumber);

            if (!imageDir.exists()) {
                final boolean ok = imageDir.mkdirs();
                if (!ok) {
                    final String msg = new StringBuffer().append("Unable to create the directory, ").append(
                            imageDir.getAbsolutePath()).append(", needed to store the images").toString();
                    imageDir = null;

                    throw new IOException(msg);
                }
            }
            else if (!imageDir.canWrite()) {
                final String msg = new StringBuffer().append("Unable to write to the directory, ").append(
                        imageDir.getAbsolutePath()).toString();
                imageDir = null;

                throw new IOException(msg);
            }


            return imageDir;
        }
    }


    /**
     * Represents an instant of time related to a Video tape. This object combines 'real' time, represented by a date object, with VCR time, represented by a tape time-code.
     * @author   brian
     * @version
     */
    private class SnapTime {

        private final Date date;
        private final Calendar gmtCalendar;
        private final Timecode timeCode;

        /**
         * Constructs ...
         *
         *
         * @param date
         * @param timeCode
         */
        SnapTime(final Date date, final String timeCode) {
            this.date = date;
            this.timeCode = new Timecode(timeCode);
            gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        }

        /**
         * @return  A date
         */
        Date getDate() {
            return date;
        }

        /**
         * @return  DDD
         */
        String getDayOfYear() {
            return format3i.format(gmtCalendar.get(Calendar.DAY_OF_YEAR));
        }

        /**
         * @return time formatted for the GMT timezone
         */
        String getFormattedGmtTime() {
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            return dateFormat.format(date);
        }

        /**
         * @return time formatted for the local timezone
         */
        String getFormattedLocalTime() {
            dateFormat.setTimeZone(TimeZone.getDefault());

            return dateFormat.format(date);
        }

        /**
         * @return  The timezone offset between local and GMT
         */
        String getGmtOffset() {
            return timezoneFormat.format(date);
        }

        /**
         * @return  Timecode formatted for names (':' is replaced with '_')
         */
        String getTimeCodeAsName() {
            return timeCode.toString().replace(':', '_');
        }

        /**
         * @return THe timecode as a string
         */
        String getTimeCodeAsString() {
            return timeCode.toString();
        }

        /**
         * @return  The current time in seconds
         */
        long getTimeInSecs() {
            return date.getTime() / 1000L;
        }

        /**
         * @return  YYYYDDD
         */
        String getTrackingNumber() {
            return getYear() + getDayOfYear();
        }

        /**
         * @return  YYYY
         */
        String getYear() {
            return format4i.format(gmtCalendar.get(Calendar.YEAR));
        }
    }
}
