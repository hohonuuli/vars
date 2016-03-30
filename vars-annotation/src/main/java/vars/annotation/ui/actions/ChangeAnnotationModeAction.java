/*
 * @(#)ChangeAnnotationModeAction.java   2009.11.19 at 09:38:13 PST
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

import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import vars.DAO;
import vars.annotation.VideoArchive;
import vars.annotation.ui.StateLookup;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.eventbus.VideoArchiveChangedEvent;

/**
 * <p>Action that sets the format code in the <code>VideoArchiveSet</code></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 */
public class ChangeAnnotationModeAction extends ActionAdapter {

    private char formatCode;
    private final ToolBelt toolBelt;

    /**
     *
     *
     */
    public ChangeAnnotationModeAction(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
    }

    /**
     * Sets the formatcode of the VideoArchive retrieved from the
     * VideoArchiveDispatcher
     *
     */
    public void doAction() {
        VideoArchive videoArchive = StateLookup.getVideoArchive();
        if (videoArchive != null) {
            // DAOTX
            DAO dao = toolBelt.getAnnotationDAOFactory().newDAO();
            dao.startTransaction();
            videoArchive = dao.find(videoArchive);
            videoArchive.getVideoArchiveSet().setFormatCode(formatCode);
            dao.endTransaction();
            EventBus.publish(new VideoArchiveChangedEvent(null, videoArchive));
        }
    }

    /**
     *     @return  The format code that is currently set
     */
    public char getFormatCode() {
        return formatCode;
    }

    /**
     *     @param c  The format code to be set
     */
    public void setFormatCode(final char c) {
        formatCode = c;
    }
}
