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

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.dispatchers.VideoArchiveDispatcher;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.IDataObject;
import vars.annotation.IVideoArchiveSet;
import vars.annotation.IVideoArchive;

/**
 * <p>Action that sets the format code in the <code>VideoArchiveSet</code></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: ChangeAnnotationModeAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ChangeAnnotationModeAction extends ActionAdapter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *     @uml.property  name="formatCode"
     */
    private char formatCode;

    /**
     *
     */
    public ChangeAnnotationModeAction() {
        super();
    }

    /**
     * Sets the formatcode of the VideoArchive retrieved from the
     * VideoArchiveDispatcher
     *
     * @see org.mbari.vars.annotation.ui.actions.IAction#doAction()
     */
    public void doAction() {
        final IVideoArchive va = VideoArchiveDispatcher.getInstance().getVideoArchive();
        if (va != null) {
            final IVideoArchiveSet vas = va.getVideoArchiveSet();
            if (vas != null) {
                vas.setFormatCode(formatCode);
                DAOEventQueue.updateVideoArchiveSet((IDataObject) vas);
            }
        }
    }

    /**
     *     @return  The format code that is currently set
     *     @uml.property  name="formatCode"
     */
    public char getFormatCode() {
        return formatCode;
    }

    /**
     *     @param c  The format code to be set
     *     @uml.property  name="formatCode"
     */
    public void setFormatCode(final char c) {
        formatCode = c;
    }
}
