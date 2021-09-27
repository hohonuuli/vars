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
 

package vars.query.ui.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import mbarix4j.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//~--- classes ----------------------------------------------------------------

/**
 * Use as:
 * <pre>
 * // Create an array of URLS representing images that you want to save
 * URLS[] urls = new URL[]{new URL"http://someserver/somepath/image1.jpg"),
 *      new URL("http://someserver/somepath/image2.png")};
 *
 * // Initialize the action and set the parameters (URLs, and directory to save images to)
 * SaveFramegrabsAction action = new SaveFramegrabsAction();
 * action.setUrls(urls);
 * action.setSaveLocation("/Users/bob/someimages")
 * action.doAction();
 *
 * </pre>
 * @author brian
 * @version $Id: SaveFramegrabsAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class SaveFramegrabsAction extends ActionAdapter {


    private static final long serialVersionUID = -2264478482293981201L;
    public static final String ACTION_NAME = "Download Images";
    private static final Logger log = LoggerFactory.getLogger(SaveFramegrabsAction.class);


    private ProgressMonitor progressMonitor;

    private File saveLocation;

    private URL[] urls;


    public SaveFramegrabsAction() {
        super(ACTION_NAME);
    }


    /**
     * Copies the contents of a URL to a local file.
     *
     * @param src The URL that we want to retrieve
     * @param dst The destination to where we want to save the url. All intermediate
     *  directories will be created as needed.
     * @throws IOException
     */
    public static void copy(URL src, File dst) throws IOException {
        if ((src != null) && (dst != null)) {
            boolean success = true;
            File parent = dst.getParentFile();
            if (!parent.exists()) {
                success = parent.mkdirs();
            }

            if (success) {
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
        }
    }

    /**
     *
     */
    public void doAction() {
        if ((urls != null) && (saveLocation != null)) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                if (progressMonitor != null) {
                    if (progressMonitor.isCanceled()) {
                        break;
                    }

                    SwingUtilities.invokeLater(
                            new UpdateProgressMonitor(i,
                            url.toExternalForm()));
                }

                if (doesImageExist(url)) {
                    File file = urlToLocalPath(url);

                    /*
                     * Don't copy the file if it already exists!!
                     */
                    if (!file.exists()) {
                        try {
                            copy(url, file);
                        } catch (IOException e) {
                            log.error(
                                    "Unable to copy " + url + " to " +
                                    file.getAbsolutePath(),
                                    e);
                        }
                    }
                }
            }

            if (progressMonitor != null) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        progressMonitor.close();
                    }
                });
            }
        }
    }

    /**
     * Checks to see if the images is available at the url
     * @param url The url of the image
     * @return true if the image can be downloaded. false otherwise.
     */
    public static boolean doesImageExist(URL url) {
        boolean exists = false;
        try {
            InputStream in = url.openStream();
            in.read();
            in.close();
            exists = true;
        } catch (Exception e) {
            // Do nothing. This file is not available.
        }

        return exists;
    }


    public void setProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }


    public void setSaveLocation(File saveLocation) {
        this.saveLocation = saveLocation;
    }

    /**
	 * Set the URL of to be downloaded.
	 * @param urls  A collection of URL objects. Each URL should correspond to a  framegrab that is to be downloaded.
	 */
    public void setUrls(URL[] urls) {
        this.urls = urls;
    }


    /**
     * Takes a URL of the framegrab and turns it into a file name. This is nescessary
     * since the 'filename' part of the URL may not be unique by iteself. By
     * writing out a full path we preserve some unique reference. As a shortcut
     * for MBARI internal use we remove parts of the pathname up to the word
     * 'framegrab' For example,
     * <code>
     *  // External code
     * SaveFramegrabAction action = new SaveFramegrabAction();
     * action.setSaveLocations('/Temp')
     *
     *  // Internal to SaveFramegrabAction
     *  // Here we use a url with the string 'framegrabs' in it
     * File f = urlToLocalPath(new URL("http://search.mbari.org/ARCHIVE/frameGrabs/Ventana/images/2627/00_50_41_04.jpg"));
     * f.toString(); // = "/Temp/Ventana/images/2677/00_05_41_04.jpg
     *
     *  // Here we use a url without 'framegrabs'
     * File g = urlToLocalPath(new URL("http://search.mbari.org/ARCHIVE/foo/Ventana/images/2627/00_50_41_04.jpg"));
     * g.toString(); // = "/Temp/search.mbari.org/ARCHIVE/foo/Ventana/images/2627/00_50_41_04.jpg"
     * </code>
     *
     * @param url
     * @return
     */
    private File urlToLocalPath(URL url) {
        String[] parts = url.toExternalForm().replace("%20", " ").split("/");
        int idx = 0;
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i];
            if (s.equalsIgnoreCase("framegrabs")) {
                idx = i + 1;
                break;
            }
        }

        File f = saveLocation;
        for (int i = idx; i < parts.length; i++) {
            String s = parts[i];
            if (!s.equalsIgnoreCase("http:") &&!s.equals("")) {
                f = new File(f, parts[i]);
            }
        }

        return f;
    }


    private class UpdateProgressMonitor implements Runnable {

        private final int i;
        private final String note;

        /**
         * Constructs ...
         *
         *
         * @param i
         * @param note
         */
        public UpdateProgressMonitor(int i, String note) {
            this.i = i;
            this.note = note;
        }

        /**
         *
         */
        public void run() {
            progressMonitor.setNote(note);
            progressMonitor.setProgress(i);
        }
    }
}
