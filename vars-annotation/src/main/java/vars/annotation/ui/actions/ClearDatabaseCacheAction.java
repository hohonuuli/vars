/*
 * @(#)ClearDatabaseCacheAction.java   2009.12.11 at 06:25:51 PST
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



package vars.annotation.ui.actions;

import java.util.Collection;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.util.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.PersistenceCache;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoFrame;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;

/**
 * This action refreshes the knowledgebase used by the Annotation Application.
 *
 * @author brian
 * @version $Id: $
 */
public class ClearDatabaseCacheAction extends ActionAdapter {

    /** Field description */
    public static final String ACTION_NAME = "Refresh Data";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     *
     * @param toolBelt
     */
    public ClearDatabaseCacheAction(ToolBelt toolBelt) {
        super(ACTION_NAME);
        this.toolBelt = toolBelt;
    }

    /**
     * Method description
     *
     */
    @Override
    public void doAction() {

        final Dispatcher dispatcher = Lookup.getVideoArchiveDispatcher();
        final VideoArchive videoArchive = (VideoArchive) dispatcher.getValueObject();

        // We need this to reopen the VideoArchive after refresh
        final String name = (videoArchive == null) ? null : videoArchive.getName();
        dispatcher.setValueObject(null);

        final PersistenceCache persistenceCache = toolBelt.getPersistenceCache();
        final CacheClearedListener listener = new CacheClearedListener() {

            public void beforeClear(CacheClearedEvent evt) {

                // Do Nothing
            }

            public void afterClear(CacheClearedEvent evt) {
                try {

                    /*
                     * Reopen the VideoArchive
                     */
                    if (name != null) {
                        Thread thread = new Thread(new Runnable() {

                            public void run() {
                                VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
                                dao.startTransaction();
                                VideoArchive refreshedVideoArchive = dao.findByName(name);
                                @SuppressWarnings("unused") Collection<VideoFrame> videoFrames = videoArchive
                                    .getVideoFrames();
                                dao.endTransaction();

                                if (refreshedVideoArchive == null) {
                                    EventBus.publish(Lookup.TOPIC_WARNING,
                                                     "Unable to find " + name + " in the database");
                                }
                                else {
                                    dispatcher.setValueObject(videoArchive);
                                }
                            }
                        }, "Clear cache thread");

                        thread.start();

                    }

                    // Cleanup
                    persistenceCache.removeCacheClearedListener(this);
                }
                catch (Exception e) {
                    log.error("Failed to refresh the knowledgbase", e);
                    EventBus.publish(Lookup.TOPIC_FATAL_ERROR, e);
                }

            }
        };


        persistenceCache.addCacheClearedListener(listener);
        persistenceCache.clear();


    }
}
