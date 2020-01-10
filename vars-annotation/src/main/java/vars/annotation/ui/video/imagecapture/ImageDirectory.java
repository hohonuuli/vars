/*
 * @(#)ImageDirectory.java   2013.02.15 at 09:26:21 PST
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



package vars.annotation.ui.video.imagecapture;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import vars.UserAccount;
import vars.annotation.CameraDeployment;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveSet;
import vars.annotation.ui.StateLookup;
import vars.shared.preferences.PreferencesService;

/**
 * @author Brian Schlining
 * @since 2013-02-15
 */
public class ImageDirectory {

    /**
         *  Value used if no platform information is available
         */
    public final static String UNKNOWN_PLATFORM = "unknown";

    /**
     *  Value used if no dive information is available
     */
    public final static String UNKNOWN_SEQNUMBER = "0000";
    private final static NumberFormat format4i = new DecimalFormat("0000");
    private final PreferencesService preferencesService;

    /**
     * Constructs ...
     *
     */
    public ImageDirectory() {
        preferencesService = new PreferencesService(StateLookup.PREFERENCES_FACTORY);
    }

    /**
     * The current implementation creates a directory
     * $HOME/VARS/data/[platform]/images/[divenumber]" in the users
     * home directory and stores the images there.
     *
     * @return  The location of the image directory
     * @throws java.io.IOException If unable to create or write to the image directory
     */
    public File getImageDirectory() throws IOException {
        UserAccount userAccount = StateLookup.getUserAccount();
        final String hostname = preferencesService.getHostname();
        File imageTarget = preferencesService.findImageTarget(userAccount.getUserName(), hostname);


        // Get the platform name. Defaults to unknown
        final VideoArchive va = StateLookup.getVideoArchive();
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

                throw new IOException(msg);
            }
        }
        else if (!imageDir.canWrite()) {
            final String msg = new StringBuffer().append("Unable to write to the directory, ").append(
                imageDir.getAbsolutePath()).toString();

            throw new IOException(msg);
        }


        return imageDir;
    }
}
