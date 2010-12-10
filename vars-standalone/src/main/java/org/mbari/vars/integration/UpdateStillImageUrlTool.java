/*
 * @(#)UpdateStillImageUrlTool.java   2010.10.15 at 08:30:26 PDT
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



package org.mbari.vars.integration;

import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ToolBelt;
import vars.annotation.CameraData;
import vars.annotation.CameraDataDAO;
import vars.knowledgebase.ui.Lookup;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 *
 *
 * @version        Enter version here..., 2010.09.02 at 11:59:47 PDT
 * @author         Brian Schlining [brian@mbari.org]
 */
public class UpdateStillImageUrlTool {

    /**
     * This is the key that is used to locate file URLS in the database.
     */
    public static final String FILE_PREFIX = "file:";

    /**  */
    public static final byte[] GIF_KEY = { (byte) 0x47, (byte) 0x49, (byte) 0x46 };

    /**  */
    public static final byte[] JPG_KEY = { (byte) 0x89, (byte) 0x50, (byte) 0x4E };

    /**  */
    public static final byte[] PNG_KEY = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };

    /**
     * VARS stores images in a directory that contains this as part of it's path. The
     * tail end of the local URL and the remote URL are the same, but the starting
     * portions are different. This key is used to locate the parts of the path that
     * are the same.
     */
    public static final String SEARCH_KEY = "VARS/data";
    private static final Logger log = LoggerFactory.getLogger(UpdateStillImageUrlTool.class);

    /**
     * THis is the string that gets prepended onto file urls to create web URLs
     */
    public static String HTTP_PREFIX = "http://search.mbari.org/ARCHIVE/frameGrabs/";
    private static ToolBelt toolBelt;

    static {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();

        toolBelt = injector.getInstance(ToolBelt.class);
    }

    /**
     *
     */
    private UpdateStillImageUrlTool() {
        super();

        // NO instantiation allowed
    }

    /**
     * Converts a file URL stored in a database to the coresponding http url.
     *
     * @param fileUrl
     * @return A http URL. null if the String provided should not be converted to a
     *      URL.
     *
     * @throws MalformedURLException
     */
    public static URL fileUrlToHttpUrl(final String fileUrl) throws MalformedURLException {
        URL httpUrl = null;

        if ((fileUrl != null) && fileUrl.toLowerCase().startsWith(FILE_PREFIX)) {
            int idx = fileUrl.indexOf(SEARCH_KEY);

            if (idx > -1) {
                idx = idx + SEARCH_KEY.length() + 1;

                String httpString = HTTP_PREFIX + fileUrl.substring(idx);

                httpString = httpString.replaceAll(" ", "%20");

                httpUrl = new URL(httpString);
            }
        }

        return httpUrl;
    }

    /**
     * Searchs the VARS database for all file URLs
     *
     * @return A collection of CameraData objects whose stillImageUrl field is a
     *      file URL.
     *
     */
    public static Collection findFileUrls() {
        CameraDataDAO dao = toolBelt.getAnnotationDAOFactory().newCameraDataDAO();
        List<CameraData> cameraData = dao.findByImageReferencePrefix(FILE_PREFIX);
        dao.close();

        return cameraData;
    }

    /**
     * Checks the web server to see if the image exists. IT does this by opening a
     * stream and reading the first 3 bytes. It checks these bytes to see if its a
     * jpg, gif or png.
     *
     * @param url
     * @return true if the image exists.
     */
    public static boolean isImageOnWebServer(final URL url) {
        boolean onServer = false;

        if (url != null) {
            byte[] b = new byte[3];

            try {
                InputStream in = url.openStream();

                in.read(b);
                in.close();
            }
            catch (Exception e) {
                if (log.isInfoEnabled()) {
                    log.info("Unable to open the URL, " + url, e);
                }
            }

            if (Arrays.equals(b, PNG_KEY) || Arrays.equals(b, GIF_KEY) || Arrays.equals(b, JPG_KEY)) {
                onServer = true;
            }
        }

        return onServer;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        // Parse args
//        String prefix = args[0];
//        prefix = prefix.endsWith("/") ? prefix : prefix + "/";
//        HTTP_PREFIX = prefix;

        try {
            updateStillImageUrls();
        }
        catch (Exception e) {
            log.error("Unable to update the still image URLS.", e);
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     */
    public static void updateStillImageUrls() {
        Collection cameraDatums = findFileUrls();

        for (Iterator i = cameraDatums.iterator(); i.hasNext(); ) {
            CameraData cd = (CameraData) i.next();

            try {
                updateUrl(cd);
            }
            catch (MalformedURLException e) {
                log.warn("Failed to update " + cd, e);
            }
        }
    }

    /**
     *
     * @param cameraData
     * @throws MalformedURLException
     */
    public static void updateUrl(CameraData cameraData) throws MalformedURLException {
        if (cameraData != null) {
            URL newUrl = fileUrlToHttpUrl(cameraData.getImageReference());

            if (log.isDebugEnabled()) {
                log.debug("Attempting to update " + cameraData.getImageReference() + " to " + newUrl);
            }

            if (isImageOnWebServer(newUrl)) {
                CameraDataDAO dao = toolBelt.getAnnotationDAOFactory().newCameraDataDAO();
                cameraData = dao.find(cameraData);
                dao.startTransaction();
                cameraData.setImageReference(newUrl.toExternalForm());
                dao.endTransaction();
                dao.close();
            }
        }
    }
}
