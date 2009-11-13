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
 * Created on Dec 16, 2004
 */
package org.mbari.vars.annotation.locale.shore;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.swing.ProgressDialog;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOExceptionHandler;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.util.VARSProperties;
import org.mbari.vars.annotation.model.VideoFrame;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchive;
import vars.annotation.IVideoFrame;
import vars.annotation.ICameraData;
import vars.annotation.ICameraPlatformDeployment;

//~--- classes ----------------------------------------------------------------

/**
 * <p>
 * This action uploads frame-grabs captured from a local machine to a remote
 * directory. Once the images are moved it will change the file URL of the
 * image to a HTTP URL in the database.
 * </p>
 *
 * @author  <a href="http://www.mbari.org">MBARI </a>
 * @created  November 2, 2004
 * @version  $Id: UploadStillImageAction.java 453 2006-12-08 00:46:21Z hohonuuli $
 */
public class UploadStillImageAction
        extends org.mbari.vars.annotation.locale.UploadStillImageAction {

    /**
     *
     */
    private static final long serialVersionUID = -2827861342447323501L;
    private static final Logger log = LoggerFactory.getLogger(UploadStillImageAction.class);

    /** 
     * 
     */
    public static final String ACTION_NAME = "Upload Still images";

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="uploadLocation"
	 * @uml.associationEnd  inverse="this$0:org.mbari.vars.annotation.locale.shore.UploadStillImageAction$UploadLocation"
	 */
    private UploadLocation uploadLocation;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructor for the UploadStillImageAction object
     */
    public UploadStillImageAction() {
        super();
    }

    //~--- methods ------------------------------------------------------------

    /**
     * @param  src This will be a file URL like "file:/C:/Program Files/Documents
     *            and Settings/brian/VARS/images/Tiburon/0123/00_12_21_22.jpg"
     * @param  dst The destination directory to copy images into.
     * @return  Description of the Return Value
     * @throws  IOException
     */
    private File copy(String src, File dst) throws IOException {
        URL srcUrl = new URL(src);

        /*
         *  Chop the fullpath down to something like 00_12_21_22 so that we can
         *  search for files with similar names and copy them to the dst too.
         */
        final String[] parts = srcUrl.getFile().split("/");
        final String filename = parts[parts.length - 1];
        final File filepath = new File(dst, filename);
        int idx = filename.lastIndexOf(".");
        final String matchname = filename.substring(0, idx);

        // Create a filter for similar matches.
        final FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                final String name = pathname.getName();
                if (name.startsWith(matchname)) {
                    return true;
                }

                return false;
            }
        };

        /*
         *  We need to transform the src URL into a File path of the directory
         *  that contains the file.
         */
        File srcFile = new File(srcUrl.getFile());
        srcFile = srcFile.getParentFile();

        /*
         *  Pattern match for similar filenames. Then copy them to the dst
         *  directory.
         */
        File[] matches = srcFile.listFiles(filter);
        for (int i = 0; i < matches.length; i++) {
            copy(matches[i].toURL(), new File(dst, matches[i].getName()));
        }

        return filepath;
    }

    /**
     * Description of the Method
     *
     * @param  src Description of the Parameter
     * @param  dst Description of the Parameter
     * @exception  IOException Description of the Exception
     */
    private static void copy(URL src, File dst) throws IOException {
        InputStream in = src.openStream();
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Description of the Method
     */
    public void doAction() {
        if ((videoArchive == null) ||!isEnabled() ||
                (uploadLocation == null)) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to upload images.");
            }

            return;
        }

        // Get the path to copy files into
        final File dst = uploadLocation.getUploadDirectory();
        if (dst == null) {
            if (log.isWarnEnabled()) {
                log.warn(
                        "Unable to create a directory to move the images into. No frame grabs have been moved.");
            }

            return;
        }

        // Create the path if needed.
        boolean success = true;
        if (!dst.exists()) {
            success = makeUploadDirectory(dst);

            if (!success) {
                if (log.isWarnEnabled()) {
                    log.warn(
                            "Failed to create the directory, " +
                            dst.getAbsolutePath() + ", to move images into.");
                }

                return;
            }
        }

        // Move the images
        if (success) {
            moveImages(dst);
        } else {
            AppFrameDispatcher.showWarningDialog(
                    "Unable to copy framegrabs to " + dst.getAbsolutePath());
        }
    }

    /**
     * Creates the directories structure needed to copy images into
     *
     * @param  dst The destination to create
     * @return  true if the creation was a success. False if it failed.
     */
    private static boolean makeUploadDirectory(File dst) {
        boolean success = false;
        if (!dst.exists()) {
            success = dst.mkdirs();
        }

        return success;
    }

    /**
     * Description of the Method
     *
     * @param  dst Description of the Parameter
     */
    private void moveImages(File dst) {
        Collection vfs = videoArchive.getVideoFrames();

        ProgressDialog progressDialog = AppFrameDispatcher.getProgressDialog();
        progressDialog.setLabel("Uploading images from " + videoArchive.getVideoArchiveName());
        JProgressBar progressBar = progressDialog.getProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(vfs.size());
        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressDialog.setVisible(true);
        
        int count = 0;
        synchronized (vfs) {
            for (Iterator i = vfs.iterator(); i.hasNext(); ) {
                final IVideoFrame vf = (IVideoFrame) i.next();
                ICameraData cd = vf.getCameraData();
                if (cd != null) {
                    /*
                     *  src is a URL (as a String)
                     */
                    final String src = cd.getStillImage();
                    if ((src != null) && src.startsWith("file")) {
                        try {
                            progressBar.setString("Copying " + src);
                            final File filecopied = copy(src, dst);
                            updateDatabase(vf, filecopied);
                        } catch (IOException e) {
                            if (log.isErrorEnabled()) {
                                log.error(
                                        "Failed to copy " + src + " to " + dst,
                                        e);
                            }
                        }
                    }
                }
                progressBar.setValue(++count);
                progressBar.setString("");
            }
        }

        progressDialog.setVisible(false);
    }

    //~--- set methods --------------------------------------------------------

    /**
     * @param  videoArchive The videoArchive to set.
     */
    public void setVideoArchive(IVideoArchive videoArchive) {
        this.videoArchive = videoArchive;

        if (videoArchive != null) {
            try {
                uploadLocation = new UploadLocation(videoArchive);
                setEnabled(true);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Trouble", e);
                }

                uploadLocation = null;
            }
        } else {
            uploadLocation = null;
        }
    }

    //~--- methods ------------------------------------------------------------

    /**
     * @param  videoFrame The videoFrame to update
     * @param  dst The File where the image that this videoFrame refers to was
     *            copied.
     */
    private void updateDatabase(final IVideoFrame videoFrame, final File dst) {
        URL dstUrl = null;
        try {
            if (uploadLocation != null) {
                dstUrl = uploadLocation.fileToUrl(dst);
            }
        } catch (Exception e) {
            dstUrl = null;

            if (log.isWarnEnabled()) {
                log.warn(
                        "Failed to convert a path to a url. The database " +
                        "sillImageURL reference will not be updated.",
                        e);
            }

            return;
        }

        if (dstUrl != null) {
            final ICameraData cameraData = videoFrame.getCameraData();
            final String oldStillImage = cameraData.getStillImage();
            cameraData.setStillImage(dstUrl.toExternalForm());

            try {
                DAOEventQueue.update(
                        (VideoFrame) videoFrame,
                        new VFUpdateErrorHandler(cameraData, oldStillImage));
            } catch (Exception e) {
                cameraData.setStillImage(oldStillImage);

                if (log.isWarnEnabled()) {
                    log.warn(
                            "Failed to update stillImage, " + dstUrl +
                            ". Rolled back change",
                            e);
                }
            }
        }
    }

    //~--- inner classes ------------------------------------------------------

    /**
	 * Description of the Class
	 * @author   brian
	 * @created   November 2, 2004
	 */
    private class UploadLocation {

        private final NumberFormat format4i = new DecimalFormat("0000");

        /**
         * This is the directory that all images for a particular videoArchive will be
         * copied into.
         */
        private File uploadDirectory;

        /**
         * This is the root of the image archive
         */
        private final File uploadRoot;

        /**
         * THis is the URL that corresponds to the root of the image archive
         */
        private final URL uploadUrl;

        /**
         * The videoArchive of interest
         */
        private final IVideoArchive videoArchive_;

        /**
         * Constructor for the UploadLocation object
         *
         * @param  videoArchive Description of the Parameter
         * @exception  MalformedURLException Description of the Exception
         * @exception  IllegalArgumentException Description of the Exception
         * @throws  MalformedURLException
         */
        UploadLocation(IVideoArchive videoArchive)
                throws MalformedURLException, IllegalArgumentException {
            if (videoArchive == null) {
                throw new IllegalArgumentException(
                        "VideoArchive can not be null");
            }

            videoArchive_ = videoArchive;
            //final ResourceBundle rb = ResourceBundle.getBundle("vars");
            //uploadUrl = new URL(rb.getString("image.archive.url"));
            uploadUrl = new URL(VARSProperties.getImageArchiveURL());
            uploadRoot = new File(VARSProperties.getImageArchiveDirectory());
            //String s = rb.getString("image.archive.dir");
            //uploadRoot = new File(s);

            if ((uploadRoot == null) ||!uploadRoot.exists() ||
                    !uploadRoot.canWrite()) {
                setEnabled(false);
            }
        }

        /**
         * Creates a URL of [image.archive.url]/[platform]/images/[dive]/filename from
         * a file of [image.archive.dir]/[platform]/images/[dive]/filename
         *
         * @param  targetFile The File where the image that an image was copied to.
         * @return  The URL that corresponds to the File targetFile.
         * @exception  IllegalArgumentException Description of the Exception
         * @throws  MalformedURLException
         */
        URL fileToUrl(final File targetFile)
                throws IllegalArgumentException, MalformedURLException {

            //**** Ensure that the file provided is located under the image archive directory
            String targetPath = targetFile.getAbsolutePath();
            final String rootPath = uploadRoot.getAbsolutePath();
            if (!targetPath.startsWith(rootPath)) {
                throw new IllegalArgumentException("The file, " + targetPath +
                        ", is not located in the expected location, " +
                            rootPath);
            }


            String postfix = targetPath.substring(rootPath.length(), targetPath.length());
            final String[] parts = postfix.split("[\\\\\\\\,/]");
            StringBuffer dstUrl = new StringBuffer(uploadUrl.toExternalForm());
            boolean b = false;
            for (int i = 0; i < parts.length; i++) {
                if (!"".equals(parts[i])) {
                    if (b) {
                        dstUrl.append("/");
                    }

                    dstUrl.append(parts[i]);
                    b = true;
                }
            }
            String dstUrlString = dstUrl.toString().replaceAll(" ", "%20"); // Space break things

            URL out = null;
            try {
                out = new URL(dstUrlString);
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Strings in Java suck!!!", e);
                }
            }

            return out;
        }

        /**
		 * Returns the directory where images should be uploaded into. The directory is  a file of [imageArchive]\[platform]\images\[dive]
		 * @return   The uploadDirectory value
		 * @uml.property  name="uploadDirectory"
		 */
        File getUploadDirectory() {
            if (uploadDirectory == null) {
                /*
                 *  Construct a file of [imageArchive]\[platform]\images\[dive]
                 */
                final IVideoArchiveSet vas = videoArchive_.getVideoArchiveSet();
                final String platform = vas.getPlatformName();
                Collection cpds = vas.getCameraPlatformDeployments();
                if (cpds.size() == 0) {
                    return null;
                }

                ICameraPlatformDeployment cpd = (ICameraPlatformDeployment) cpds.iterator().next();
                uploadDirectory = new File(
                        new File(new File(uploadRoot, platform), "images"),
                            format4i.format((long) cpd.getSeqNumber()) + "");
            }

            return uploadDirectory;
        }
    }


    /**
	 * @author  brian
	 */
    private class VFUpdateErrorHandler extends DAOExceptionHandler {

        private final ICameraData cameraData;
        private final String oldStillImage;

        /**
         * Constructs ...
         *
         *
         * @param cameraData
         * @param oldStillImage
         */
        VFUpdateErrorHandler(ICameraData cameraData, String oldStillImage) {
            this.cameraData = cameraData;
            this.oldStillImage = oldStillImage;
        }

        protected void doAction(Exception e) {
            cameraData.setStillImage(oldStillImage);

            if (log.isWarnEnabled()) {
                log.warn(
                        "Failed to change a CameraDatas stillImage from" +
                        oldStillImage + " to " + cameraData.getStillImage() +
                            ". Rolling back change",
                        e);
            }
        }
    }
}
