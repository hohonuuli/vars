/*
 * @(#)UpdateNewRefNumAction.java   2009.11.16 at 09:42:47 PST
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.mbari.awt.event.ActionAdapter;
import vars.annotation.AnnotationDAOFactory;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.annotation.VideoArchiveSet;

/**
 * <p>
 * This action sets the initial value of a NewRefNumPropAction. When a new VideoArchive is
 * set in the VideoArchiveDispatcher it queries for the highest ref number found
 * and sets the NewRefNumPropAction to use that value.
 * </p>
 *
 * <p>Once instantiated this class will register itself with the videoArchiveDispatcher and
 * automatically change the NewRefNumber as appropriate. </p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class UpdateNewRefNumAction extends ActionAdapter implements IVideoArchiveProperty {

    private boolean needsUpdating = true;
    private final AnnotationDAOFactory annotationDAOFactory;
    private VideoArchive videoArchive;

    /**
     * Constructs ...
     *
     *
     * @param annotationDAOFactory
     */
    public UpdateNewRefNumAction(AnnotationDAOFactory annotationDAOFactory) {
        super();
        this.annotationDAOFactory = annotationDAOFactory;
        doAction();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        if (needsUpdating) {

            /*
             * Get all the existing reference numbers in a VideoArchive
             */
            int refNum = 1;

            /*
             * If A VideoArchive is available set the refNum to the maximum value
             * found + 1.
             */
            if (videoArchive != null) {
                VideoArchiveDAO dao = annotationDAOFactory.newVideoArchiveDAO();
                dao.startTransaction();
                // TODO identity-reference is hard coded. It needs to be put in a property file
                Set<String> refNums = dao.findAllLinkValues(videoArchive, "identity-reference");
                dao.endTransaction();

                if (refNums.size() != 0) {
                    final Set<Integer> intValues = new HashSet<Integer>();

                    for (final Iterator<String> i = refNums.iterator(); i.hasNext(); ) {
                        final String s = (String) i.next();

                        try {
                            intValues.add(Integer.valueOf(s));
                        }
                        catch (final Exception e) {
                            // Do nothing. It's not an int value
                        }
                    }

                    // Get the largest ref number and add 1
                    if (!intValues.isEmpty()) {
                       refNum = ((Integer) Collections.max(intValues)).intValue();
                       refNum++;
                    }

                }
            }

            AddNewRefNumPropAction.setRefNumber(refNum);
            needsUpdating = false;
        }
    }

    /**
     * @return
     */
    public VideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *
     * @param newVideoArchive
     */
    public void setVideoArchive(final VideoArchive newVideoArchive) {
        needsUpdating = true;

        if ((videoArchive != null) && (newVideoArchive != null)) {
            final VideoArchiveSet oldVas = videoArchive.getVideoArchiveSet();
            final VideoArchiveSet newVas = newVideoArchive.getVideoArchiveSet();

            if (oldVas.equals(newVas)) {
                needsUpdating = false;
            }
        }

        this.videoArchive = newVideoArchive;
    }
}
