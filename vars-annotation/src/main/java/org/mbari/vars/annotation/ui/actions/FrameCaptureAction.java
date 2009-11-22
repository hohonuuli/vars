/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
The Monterey Bay Aquarium Research Institute (MBARI) provides this
documentation and code 'as is', with no warranty, express or
implied, of its quality or consistency. It is provided without support and
without obligation on the part of MBARI to assist in its use, correction,
modification, or enhancement. This information should not be published or
distributed to third parties without specific written permission from MBARI
 */
package org.mbari.vars.annotation.ui.actions;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.movie.Timecode;
import org.mbari.util.Dispatcher;
import org.mbari.util.IObserver;

import vars.util.VARSProperties;
import org.mbari.vcr.IVCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import vars.annotation.CameraData;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoFrame;
import vars.annotation.VideoArchive;
import vars.annotation.Observation;
import vars.annotation.VideoArchiveSet;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.VideoService;

/**
 * <p>Action for capturing a frame an performing related activities on it.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: FrameCaptureAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class FrameCaptureAction extends ActionAdapter {

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
    private static final Logger log = LoggerFactory.getLogger(FrameCaptureAction.class);
    private static final String imageCopyrightOwner = VARSProperties.getImageCopyrightOwner();
    private final ToolBelt toolBelt;



    /**
     * This class does all the heavy lifting.
     */
    private FrameCaptureHelper helper;

    /**
     * Provides information about where to save the images
     */
    private final ImageDirectory imageDirectory = new ImageDirectory();

    /**
     * Constructor for the FrameCaptureAction object
     */
    public FrameCaptureAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        boolean ok = false;
        helper = new FrameCaptureHelper();
        putValue(Action.NAME, "Frame Capture");
        putValue(Action.ACTION_COMMAND_KEY, "frame capture");
        putValue(Action.ACCELERATOR_KEY,
                 KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        setEnabled(ok);
    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        if (isAvailable()) {
            helper.capture();
        }
    }

    /**
     *  Gets the available attribute of the FrameCaptureAction object
     *
     * @return  true if a frame-capture card is installed and accessable
     */
    public boolean isAvailable() {
        return Lookup.getVideoServiceDispatcher().getValueObject() != null;
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
     * Method description
     *
     */
//    public static void showSettingsDialog() {
//        final Framegrabber2 framegrabber2 =
//            (Framegrabber2) Dispatcher.getDispatcher(DISPATCHER_KEY_FRAMEGRABBER).getValueObject();
//        if (framegrabber2 != null) {
//            framegrabber2.showSettingsDialog();
//        }
//    }

    /**
     * Performs the frame-capture related tasks, then updates the VideoArchive object.
     * @author   brian
     */
    private class FrameCaptureHelper {

        private final NewVideoFrameAction action = new NewVideoFrameAction(toolBelt);
        private File jpg;
        private SnapTime snapTime;
        private IVCR vcr;
        private VideoArchive videoArchive;

        /**
         *  Capture an image
         *
         */
        public void capture() {
            
            VideoService videoService = (VideoService) Lookup.getVideoServiceDispatcher().getValueObject();
            if (videoService == null) {
                EventBus.publish(Lookup.TOPIC_WARNING, "No Video Service is available or frame capure");
                return;
            }

            /*
             *  Clear any previously set variables
             */
            jpg = null;
            snapTime = null;
            vcr = null;
            videoArchive = null;
            videoArchive = (VideoArchive) Lookup.getVideoArchiveDispatcher().getValueObject();

            if (videoArchive == null) {
                EventBus.publish(Lookup.TOPIC_WARNING,
                        "No video-archive is open for annotating. Unable to capture an image.");
                return;
            }

            /*
             *  This method grabs the timecode from the VCR.
             */
            vcr = videoService.getVCR();

            if (vcr == null) {
                EventBus.publish(Lookup.TOPIC_WARNING, "You are not connected to the VCR. Unable to capture a frame.");
                return;
            }

            final String timecode = vcr.getVcrTimecode().toString();
            snapTime = new SnapTime(new Date(), timecode);
            final String timecode_ = snapTime.getTimeCodeAsName();

            /*
             * Grab the image from the videoChannel.
             */
            File png = null;
            try {
                png = new File(imageDirectory.getImageDirectory(), timecode_ + ".png");
                videoService.grabImageAndSaveTo(png);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_WARNING, "ERROR!! Failed to capture the frame. Reason given is " +
                                                   e.getMessage() + ". See vars.log for more details.");
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
            BufferedImage image;
            try {
                image = ImageIO.read(png);
                jpg = new File(png.getAbsolutePath().replaceFirst(".png", ".jpg"));
                FrameCaptureUtilities.createJpgWithOverlay(image, jpg, overlayText);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_WARNING, "ERROR!! Failed to create preview image. Reason given is " +
                                                   e.getMessage() + ". See vars.log for more details.");
                log.error("Frame-grab failed", e);

                return;
            }


            /*
             *  Update the information in the database
             */
            updateVideoArchive();
        }

        /**
         * Populate a videoFrame with the correct information
         */
        private void updateVideoArchive() {
            final Observation observation = action.doAction("physical object", snapTime.getTimeCodeAsString());
            if (observation != null) {
                final VideoFrame videoFrame = observation.getVideoFrame();
                CameraData cameraData = videoFrame.getCameraData();

                try {
                    cameraData.setImageReference(jpg.toURI().toURL().toExternalForm());
                }
                catch (final MalformedURLException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Problem creating a URL.", e);
                    }
                }
            }
            
            final Collection<Observation> selectedObservations = ImmutableList.of(observation);
            toolBelt.getPersistenceController().updateUI(selectedObservations);
            Lookup.getSelectedObservationsDispatcher().setValueObject(selectedObservations);
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
    private class ImageDirectory implements PropertyChangeListener {

        private File imageDir;

        /**
         * Constructs ...
         *
         */
        ImageDirectory() {
            Lookup.getVideoArchiveDispatcher().addPropertyChangeListener(this);
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
            if (imageDir == null) {

                // Get users home directory
                final String userHome = System.getProperty("user.home");
                final File varsDir = new File(userHome, "VARS");
                final File iDir = new File(varsDir, "data");

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

                final File rovDir = new File(new File(iDir, platform), "images");

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
                imageDir = new File(rovDir, diveNumber);

                if (!imageDir.exists()) {
                    final boolean ok = imageDir.mkdirs();
                    if (!ok) {
                        final String msg = new StringBuffer().append("Unable to create the directory, ").append(
                                               imageDir.getAbsolutePath()).append(
                                               ", needed to store the images").toString();
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
            }

            return imageDir;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            /*
             *  Setting the imageDir to null causes the next call to getImageDirectory
             *  to regenerate a new path to the image directory. We do this to defer
             *  the creation of the imageDir object until it's needed.
             */
            imageDir = null;
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
         * <p><!-- Method description --></p>
         * @return  A date
         * @uml.property  name="date"
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
         * <p><!-- Method description --></p>
         *
         *
         * @return time formatted for the GMT timezone
         */
        String getFormattedGmtTime() {
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            return dateFormat.format(date);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return time formatted for the local timezone
         */
        String getFormattedLocalTime() {
            dateFormat.setTimeZone(TimeZone.getDefault());

            return dateFormat.format(date);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return  The timezone offset between local and GMT
         */
        String getGmtOffset() {
            return timezoneFormat.format(date);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return  Timecode formatted for names (':' is replaced with '_')
         */
        String getTimeCodeAsName() {
            return timeCode.toString().replace(':', '_');
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @return THe timecode as a string
         */
        String getTimeCodeAsString() {
            return timeCode.toString();
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
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
