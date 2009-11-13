/*
 * Copyright 2007 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.mbari.vars.annotation.ui.actions;

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.dao.VideoArchiveDAO;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IVideoArchive;

/**
 * This action refreshes the knowledgebase used by the Annotation Application. As a side-effect, the
 * open {@link IVideoArchive} must be closed since we need to expire the Castor cache.
 *
 * @author brian
 * @version $Id: $
 */
public class ClearDatabaseCacheAction extends ActionAdapter {

    /** Field description */
    public static final String ACTION_NAME = "Refresh Data";
    private static final Logger log = LoggerFactory.getLogger(ClearDatabaseCacheAction.class);

    /**
     * Constructs ...
     *
     */
    public ClearDatabaseCacheAction() {
        super(ACTION_NAME);
    }

    /**
     * Method description
     *
     */
    @Override public void doAction() {
        try {

            IVideoArchive videoArchive = VideoArchiveDispatcher.getInstance().getVideoArchive();
            String videoArchiveName = null;

            /*
             * Close any open archives before the cache is expired by the clear call
             */
            if (videoArchive != null) {

                // Get the current name
                videoArchiveName = videoArchive.getVideoArchiveName();

                // Close the current VideoArchive
                CloseVideoArchiveAction closeAction = new CloseVideoArchiveAction();
                closeAction.setVideoArchive(videoArchive);
                closeAction.doAction();
                VideoArchiveDispatcher.getInstance().setVideoArchive(null);
            }

            /*
             * This call also exprires the cache so you need to reopne the current VideoArchiveSet
             */
            KnowledgeBaseCache.getInstance().clear();

            /*
             * Reopen the VideoArchive
             */
            if (videoArchiveName != null) {
                videoArchive = VideoArchiveDAO.getInstance().findByVideoArchiveName(videoArchiveName);

                if (videoArchive == null) {
                    AppFrameDispatcher.showWarningDialog("Unable to find " + videoArchiveName + " in the database");
                }
                else {
                    VideoArchiveDispatcher.getInstance().setVideoArchive(videoArchive);
                }
            }


        }
        catch (DAOException e) {
            log.error("Failed to refresh the knowledgbase", e);
            AppFrameDispatcher.showErrorDialog("Unable to refresh the knowledgebase! You should restart VARS");
        }

    }
}
