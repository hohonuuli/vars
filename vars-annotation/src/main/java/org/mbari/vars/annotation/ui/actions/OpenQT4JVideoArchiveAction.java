/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
OpenQT4JVideoArchiveAction.java
 *
Created on March 20, 2007, 1:25 PM
 *
To change this template, choose Tools | Template Manager
and open the template in the editor.
 */

package org.mbari.vars.annotation.ui.actions;

import java.net.URL;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.model.dao.VideoArchiveDAO;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.util.AppFrameDispatcher;
import org.mbari.vcr.qt.TimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brian
 */
public class OpenQT4JVideoArchiveAction extends ActionAdapter {

    private static final Logger log = LoggerFactory.getLogger(OpenQT4JVideoArchiveAction.class);
    private String cameraPlatfrom;
    private Integer seqNumber;
    private TimeSource timeSource;
    private URL url;

    /** Creates a new instance of OpenQT4JVideoArchiveAction */
    public OpenQT4JVideoArchiveAction() {
        super("Open QT Archive");
    }

    /**
     * Method description
     *
     */
    public void doAction() {

        if (url != null) {
            String videoArchiveName = url.toExternalForm();
            
            PredefinedDispatcher.TIMESOURCE.getDispatcher().setValueObject(timeSource);
            try {
                
                // See if a videoarchive with this id already exists
                VideoArchive videoArchive = VideoArchiveDAO.getInstance().findByVideoArchiveName(videoArchiveName);

                // if no matching videoarchive is found create it.
                // TODO Check for the existance of the video. If not found show error dialog
                if (videoArchive == null) {
                    videoArchive = VideoArchiveDAO.getInstance().openByParameters(cameraPlatfrom, seqNumber,
                            videoArchiveName);
                }
                else {
                    AppFrameDispatcher.showWarningDialog(
                        "You are opening a movie that already exists in the database.");
                }

                if (videoArchive != null) {
                    PredefinedDispatcher.VIDEOARCHIVE.getDispatcher().setValueObject(videoArchive);
                }
                else {
                    if (log.isErrorEnabled()) {
                        log.error("Unable to find or create a VideoArchive.");
                    }
                }
            }
            catch (final DAOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Database exception", e);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public String getCameraPlatfrom() {
        return cameraPlatfrom;
    }

    /**
     *
     * @return
     */
    public Integer getSeqNumber() {
        return seqNumber;
    }

    /**
     *
     * @return
     */
    public TimeSource getTimeSource() {
        return timeSource;
    }

    /**
     *
     * @return
     */
    public URL getUrl() {
        return url;
    }

    /**
     *
     * @param cameraPlatfrom
     */
    public void setCameraPlatfrom(String cameraPlatfrom) {
        this.cameraPlatfrom = cameraPlatfrom;
    }

    /**
     *
     * @param seqNumber
     */
    public void setSeqNumber(Integer seqNumber) {
        this.seqNumber = seqNumber;
    }

    /**
     *
     * @param timeSource
     */
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    /**
     *
     * @param url
     */
    public void setUrl(URL url) {
        this.url = url;
    }
}
