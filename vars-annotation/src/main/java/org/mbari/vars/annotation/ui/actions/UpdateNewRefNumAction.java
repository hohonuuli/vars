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


package org.mbari.vars.annotation.ui.actions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.model.VideoArchive;
import org.mbari.vars.annotation.model.VideoArchiveSet;
import org.mbari.vars.annotation.model.dao.VideoArchiveSetDAO;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchive;

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
 * @version  $Id: UpdateNewRefNumAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class UpdateNewRefNumAction extends ActionAdapter implements IVideoArchiveProperty {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="needsUpdating"
     */
    private boolean needsUpdating = true;

    /**
     *     @uml.property  name="videoArchive"
     *     @uml.associationEnd
     */
    private IVideoArchive videoArchive;

    /**
     * Constructs ...
     *
     */
    public UpdateNewRefNumAction() {
        super();
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
                final IVideoArchiveSet vas = videoArchive.getVideoArchiveSet();
                final Set refNums = VideoArchiveSetDAO.getInstance().findAllReferenceNumbers((VideoArchiveSet) vas);
                if (refNums.size() != 0) {
                    final Set intValues = new HashSet();
                    for (final Iterator i = refNums.iterator(); i.hasNext(); ) {
                        final String s = (String) i.next();
                        try {
                            intValues.add(Integer.valueOf(s));
                        }
                        catch (final Exception e) {

                            // Do nothing. It's not an int value
                        }
                    }

                    refNum = ((Integer) Collections.max(intValues)).intValue();
                    refNum++;
                }
            }

            AddNewRefNumPropAction.setRefNumber(refNum);
            needsUpdating = false;
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty#getVideoArchive()
     */

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="videoArchive"
     */
    public IVideoArchive getVideoArchive() {
        return videoArchive;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @param  newVideoArchive
     *     @uml.property  name="videoArchive"
     */
    public void setVideoArchive(final IVideoArchive newVideoArchive) {
        needsUpdating = true;

        if ((videoArchive != null) && (newVideoArchive != null)) {
            final IVideoArchiveSet oldVas = videoArchive.getVideoArchiveSet();
            final IVideoArchiveSet newVas = newVideoArchive.getVideoArchiveSet();
            if (oldVas.equals(newVas)) {
                needsUpdating = false;
            }
        }

        this.videoArchive = newVideoArchive;
    }
}
