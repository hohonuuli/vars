/*
 * @(#)UploadStillImageAction.java   2009.11.20 at 04:15:37 PST
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.LabeledSpinningDialWaitIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.CameraData;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.VideoFrame;
import vars.annotation.ui.VARSProperties;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.PersistenceController;

/**
 * <p>
 * This action uploads frame-grabs captured from a local machine to a remote
 * directory. Once the images are moved it will change the file URL of the
 * image to a HTTP URL in the database.
 * </p>
 *
 * @author  <a href="http://www.mbari.org">MBARI </a>
 */
public class UploadStillImageAction extends org.mbari.vars.annotation.locale.UploadStillImageAction {

    /**  */
    public static final String ACTION_NAME = "Upload Still images";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PersistenceController persistenceController;
    private UploadLocation uploadLocation;

    /**
     * Constructor for the UploadStillImageAction object
     *
     * @param persistenceController
     */
    public UploadStillImageAction(PersistenceController persistenceController) {
        super();
        this.persistenceController = persistenceController;
    }

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
            copy(matches[i].toURI().toURL(), new File(dst, matches[i].getName()));
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
        if ((videoArchive == null) || !isEnabled() || (uploadLocation == null)) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to upload images.");
            }

            return;
        }

        // Get the path to copy files into
        final File dst = uploadLocation.getUploadDirectory();
        if (dst == null) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to create a directory to move the images into. No frame grabs have been moved.");
            }

            return;
        }

        // Create the path if needed.
        boolean success = true;
        if (!dst.exists()) {
            success = makeUploadDirectory(dst);

            if (!success) {
                if (log.isWarnEnabled()) {
                    log.warn("Failed to create the directory, " + dst.getAbsolutePath() + ", to move images into.");
                }

                return;
            }
        }

        // Move the images
        if (success) {
            moveImages(dst);
        }
        else {
            EventBus.publish(Lookup.TOPIC_WARNING, "Unable to copy framegrabs to " + dst.getAbsolutePath());
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

        VideoArchive va = persistenceController.loadVideoFramesFor(videoArchive);
        Collection<VideoFrame> vfs = new ArrayList<VideoFrame>(va.getVideoFrames());

        JComponent component = (JComponent) Lookup.getApplicationFrameDispatcher().getValueObject();
        LabeledSpinningDialWaitIndicator waitIndicator = new LabeledSpinningDialWaitIndicator(component);
        waitIndicator.setLabel("Uploading images");

        Collection<VideoFrame> videoFramesToUpdate = new ArrayList<VideoFrame>();


        int count = 0;
        for (Iterator<VideoFrame> i = vfs.iterator(); i.hasNext(); ) {
            final VideoFrame vf = i.next();
            CameraData cd = vf.getCameraData();

            /*
             *  src is a URL (as a String)
             */
            final String src = cd.getImageReference();
            if ((src != null) && src.startsWith("file")) {
                try {
                    waitIndicator.setLabel("Copying " + src);
                    final File filecopied = copy(src, dst);
                    updateDatabase(vf, filecopied);
                    videoFramesToUpdate.add(vf);
                }
                catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Failed to copy " + src + " to " + dst, e);
                    }
                }
            }

        }

        waitIndicator.setLabel("Updating database");
        persistenceController.updateVideoFrames(videoFramesToUpdate);

        waitIndicator.dispose();
    }

    /**
     * @param  videoArchive The videoArchive to set.
     */
    public void setVideoArchive(VideoArchive videoArchive) {
        this.videoArchive = videoArchive;

        if (videoArchive != null) {
            try {
                uploadLocation = new UploadLocation(videoArchive);
                setEnabled(true);
            }
            catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Trouble", e);
                }

                uploadLocation = null;
            }
        }
        else {
            uploadLocation = null;
        }
    }

    /**
     * @param  videoFrame The videoFrame to update
     * @param  dst The File where the image that this videoFrame refers to was
     *            copied.
     */
    private void updateDatabase(final VideoFrame videoFrame, final File dst) {
        URL dstUrl = null;
        try {
            if (uploadLocation != null) {
                dstUrl = uploadLocation.fileToUrl(dst);
            }
        }
        catch (Exception e) {
            dstUrl = null;

            if (log.isWarnEnabled()) {
                log.warn("Failed to convert a path to a url. The database " +
                         "sillImageURL reference will not be updated.", e);
            }

            return;
        }

        if (dstUrl != null) {
            final CameraData cameraData = videoFrame.getCameraData();
            final String oldStillImage = cameraData.getImageReference();
            cameraData.setImageReference(dstUrl.toExternalForm());
        }
    }

    class UploadLocation {

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
        private final VideoArchive videoArchive_;

        /**
         * Constructor for the UploadLocation object
         *
         * @param  videoArchive Description of the Parameter
         * @exception  MalformedURLException Description of the Exception
         * @exception  IllegalArgumentException Description of the Exception
         * @throws  MalformedURLException
         */
        UploadLocation(VideoArchive videoArchive) throws MalformedURLException, IllegalArgumentException {
            if (videoArchive == null) {
                throw new IllegalArgumentException("VideoArchive can not be null");
            }

            videoArchive_ = videoArchive;
            uploadUrl = new URL(VARSProperties.getImageArchiveURL());
            uploadRoot = new File(VARSProperties.getImageArchiveDirectory());

            if ((uploadRoot == null) || !uploadRoot.exists() || !uploadRoot.canWrite()) {
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
        URL fileToUrl(final File targetFile) throws IllegalArgumentException, MalformedURLException {

            // ---- Ensure that the file provided is located under the image archive directory
            String targetPath = targetFile.getAbsolutePath();
            final String rootPath = uploadRoot.getAbsolutePath();
            if (!targetPath.startsWith(rootPath)) {
                throw new IllegalArgumentException("The file, " + targetPath +
                                                   ", is not located in the expected location, " + rootPath);
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

            String dstUrlString = dstUrl.toString().replaceAll(" ", "%20");    // Space break things

            URL out = null;
            try {
                out = new URL(dstUrlString);
            }
            catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Strings in Java suck!!!", e);
                }
            }

            return out;
        }

        /**
         * Returns the directory where images should be uploaded into. The directory is  a
         * file of [imageArchive]\[platform]\images\[dive]
         * @return   The uploadDirectory value
         */
        File getUploadDirectory() {
            if (uploadDirectory == null) {

                /*
                 *  Construct a file of [imageArchive]\[platform]\images\[dive]
                 */
                final VideoArchiveSet vas = videoArchive_.getVideoArchiveSet();
                final String platform = vas.getPlatformName();
                Collection<CameraDeployment> cpds = vas.getCameraDeployments();
                if (cpds.size() == 0) {
                    return null;
                }

                CameraDeployment cpd = cpds.iterator().next();
                uploadDirectory = new File(new File(new File(uploadRoot, platform), "images"),
                                           format4i.format((long) cpd.getSequenceNumber()) + "");
            }

            return uploadDirectory;
        }
    }
}
