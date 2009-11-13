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

import org.mbari.awt.event.ActionAdapter;

import org.mbari.framegrab.FakeGrabber;
import org.mbari.framegrab.GrabUtil;
import org.mbari.framegrab.IGrabber;
import org.mbari.framegrab.VideoChannelGrabber;

import org.mbari.movie.Timecode;

import org.mbari.util.Dispatcher;

import org.mbari.vars.annotation.ImageDirectory;
import org.mbari.vars.annotation.model.CameraData;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vars.annotation.ui.table.ObservationTable;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.util.VARSProperties;

import org.mbari.vcr.IVCR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Image;
import java.awt.Toolkit;

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
import java.util.Date;
import java.util.TimeZone;

import javax.swing.Action;
import javax.swing.KeyStroke;
import vars.annotation.ICameraData;
import vars.annotation.IVideoFrame;
import vars.annotation.IObservation;
import vars.annotation.IVideoArchive;


/**
 * <p>Action for capturing a frame an performing related activities on it.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: FrameCaptureAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class FrameCaptureAction2 extends ActionAdapter {
    //private final static NumberFormat format2i = new DecimalFormat("000");
    private static final NumberFormat format3i = new DecimalFormat("000");
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
    private static final DateFormat timezoneFormat = new SimpleDateFormat("ZZ");
    private static final Logger log = LoggerFactory.getLogger(FrameCaptureAction2.class);
    private static final String imageCopyrightOwner = VARSProperties.getImageCopyrightOwner();

    /** Field description */
    public static final Object DISPATCHER_KEY_FRAMEGRABBER = PredefinedDispatcher.GRABBER.getDispatcher().getKey();

    /**
     * This is the class that actually performs all Video IO operations
     */
    private IGrabber grabber = new FakeGrabber();

    /**
     * This class does all the heavy lifting.
     */
    private FrameCaptureHelper helper;

    /**
     * Provides information about where to save the images
     */
    private ImageDirectory imageDirectory;

    /**
     * Constructor for the FrameCaptureAction object
     */
    public FrameCaptureAction2() {
        super(); 

        // Make sure that QuickTime is installed
        boolean ok = ((Boolean) PredefinedDispatcher.QUICKTIME_STATUS.getDispatcher().getValueObject()).booleanValue();

        if (ok) {
            helper = new FrameCaptureHelper();

        } else {
            AppFrameDispatcher.showWarningDialog("QuickTime for Java is not installed. VARS functions requiring QuickTime will be disabled");

        }

        if (ok) {
            final Dispatcher dispatcher = PredefinedDispatcher.GRABBER.getDispatcher();
            dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        grabber = (IGrabber) evt.getNewValue();

                    }
                });

            // Listen for changes to the Grabber status
            final Dispatcher gDispatcher = PredefinedDispatcher.GRABBER_STATUS.getDispatcher();
            gDispatcher.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        updateStatus();

                    }
                });

            // Listen for changes to the videoArchive
            final Dispatcher vDispatcher = PredefinedDispatcher.VIDEOARCHIVE.getDispatcher();
            vDispatcher.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        IVideoArchive videoArchive = (IVideoArchive) evt.getNewValue();

                        if (videoArchive != null) {
                            try {
                                imageDirectory = new ImageDirectory(videoArchive);

                            } catch (IOException e) {
                                log.error("Failed to create a directory to save images into", e);
                                imageDirectory = null;

                            }
                        } else {
                            imageDirectory = null;

                        }

                        updateStatus();

                    }
                });
        }

        putValue(Action.NAME, "Frame Capture");
        putValue(Action.ACTION_COMMAND_KEY, "frame capture");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        setEnabled(ok);

    }

    /**
     *  Initiates the action.
     */
    public void doAction() {
        helper.capture();

    }

    private void updateStatus() {
        boolean ok = imageDirectory != null;

        if (ok) {
            Boolean status = (Boolean) PredefinedDispatcher.GRABBER_STATUS.getDispatcher().getValueObject();

            ok = (status == null) ? false : status.booleanValue();

        }

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

        } catch (final IOException e2) {
            if (log.isErrorEnabled()) {
                log.error("Failed to write " + comment.getAbsolutePath(), e2);

            }
        }
    }

    /**
     * Create .overlay file for the .png file name
     *
     * @param  png Description of the Parameter
     * @param  overlayText Description of the Parameter
     * @return  The File object representing the overlay file. null is returned if
     *          the fiel was not created.
     */
    private static File createOverlayFile(final File png, final String[] overlayText) {
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

        } catch (final IOException e2) {
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
        s[2] = snapTime.getFormattedGmtTime() + " GMT (local +" + snapTime.getGmtOffset().replaceFirst("-", "").replaceAll("0", "") + ")";

        /*
         *  TODO 20040921 brian: Will need to pass a String representation of a the annotation
         *  into s[3]
         */
        s[3] = "";

        return s;

    }

    /**
     * Method description
     *
     */
    public static void showSettingsDialog() {
        IGrabber grabber = (IGrabber) PredefinedDispatcher.GRABBER.getDispatcher().getValueObject();

        if (grabber instanceof VideoChannelGrabber) {
            ((VideoChannelGrabber) grabber).showSettingsDialog();

        } else {
            AppFrameDispatcher.showWarningDialog("No settings are currently available");

        }
    }

    /**
     * Performs the frame-capture related tasks, then updates the VideoArchive object.
     * @author   brian
     */
    private class FrameCaptureHelper {
        private final NewVideoFrameAction action = new NewVideoFrameAction();

        /**
         *  Capture an image
         *
         */
        public void capture() {
            final IVCR vcr = VcrDispatcher.getInstance().getVcr();

            if (vcr == null) {
                AppFrameDispatcher.showErrorDialog("You are not connected to the VCR. Unable to capture a frame.");

                return;

            }

            final IVideoArchive videoArchive = (IVideoArchive) PredefinedDispatcher.VIDEOARCHIVE.getDispatcher().getValueObject();

            if (videoArchive == null) {
                AppFrameDispatcher.showErrorDialog("No video-archive is open for annotating. Unable to capture an image.");

                return;

            }

            final String timecode = vcr.getVcrTimecode().toString();
            final SnapTime snapTime = new SnapTime(new Date(), timecode);
            final String timecode_ = snapTime.getTimeCodeAsName();
            final File png = new File(imageDirectory.getImageDirectory(), timecode_ + ".png");
            final File jpg = new File(png.getAbsolutePath().replaceFirst(".png", ".jpg"));

            Image image = null;

            try {
                image = GrabUtil.capture(grabber, png); // This call also writes to the the file png

            } catch (final Exception e) {
                e.printStackTrace();
                log.error("Frame-grab failed", e);
                AppFrameDispatcher.showErrorDialog("ERROR!! Failed to capture the frame. Reason given is '" + e.getMessage() + "'. See vars.log for more details.");

            }

            /*
             *  Create the comment file
             */
            createCommentFile(png, snapTime);

            /*
             *  Create the overlay file
             */
            final String[] overlayText = createOverlayText(png, snapTime);
            createOverlayFile(png, overlayText);

            if (image != null) {
                try {
                    GrabUtil.createJpgWithOverlay(image, jpg, overlayText);

                } catch (Exception e) {
                    log.error("Failed to create " + jpg.getAbsolutePath(), e);
                    AppFrameDispatcher.showErrorDialog("ERROR!! Failed to create preview image. Reason given is " + e.getMessage() + ". See vars.log for more details.");

                }
            }

            /*
             *  Update the information in the database
             */
            updateVideoArchive(jpg, snapTime);

        }

        /**
         * Populate a videoFrame with the correct information
         */
        private void updateVideoArchive(File jpg, SnapTime snapTime) {
            final IObservation observation = action.doAction("physical object", snapTime.getTimeCodeAsString());

            if (observation != null) {
                final IVideoFrame videoFrame = observation.getVideoFrame();
                ICameraData cameraData = videoFrame.getCameraData();

                if (cameraData == null) {
                    cameraData = new CameraData();
                    videoFrame.setCameraData(cameraData);

                }

                try {
                    cameraData.setStillImage(jpg.toURL().toExternalForm());

                } catch (final MalformedURLException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Problem creating a URL.", e);

                    }
                }
            }

            final ObservationTable table = ObservationTableDispatcher.getInstance().getObservationTable();
            table.setSelectedObservation(observation);

            /*
             * For some reason the above call is not redrawing the FrameGrabPanel
             * This call is a work around to address this.
             */
            PredefinedDispatcher.OBSERVATION.reset();
            PredefinedDispatcher.OBSERVATION.getDispatcher().setValueObject(observation);

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
        private final NumberFormat format4i = new DecimalFormat("0000");

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
