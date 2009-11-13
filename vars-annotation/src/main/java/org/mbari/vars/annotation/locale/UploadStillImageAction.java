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


/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mbari.vars.annotation.locale;

import org.mbari.awt.event.ActionAdapter;
import org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty;
import vars.annotation.IVideoArchive;

//~--- classes ----------------------------------------------------------------

/**
 * @author brian
 */
public class UploadStillImageAction extends ActionAdapter
        implements IVideoArchiveProperty {

    private static final long serialVersionUID = 582501358568206018L;
    /**
	 * @uml.property  name="videoArchive"
	 * @uml.associationEnd  
	 */
    protected IVideoArchive videoArchive;

    //~--- constructors -------------------------------------------------------

    /**
     *
     */
    public UploadStillImageAction() {
        super("Copy Still Images to Archive");
    }

    //~--- methods ------------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void doAction() {
        /*
         *  Do nothing by default. For shore operations this should be overridden.
         * TO copy images to an archival location and update the URLS in the database.
         */
    }

    //~--- get methods --------------------------------------------------------

    /*
     *  (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty#getVideoArchive()
     */

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="videoArchive"
	 */
    public IVideoArchive getVideoArchive() {
        return videoArchive;
    }

    //~--- set methods --------------------------------------------------------

    /*
     *  (non-Javadoc)
     * @see org.mbari.vars.annotation.ui.actions.IVideoArchiveProperty#setVideoArchive(org.mbari.vars.annotation.model.VideoArchive)
     */

    /**
	 * <p><!-- Method description --></p>
	 * @param  videoArchive
	 * @uml.property  name="videoArchive"
	 */
    public void setVideoArchive(IVideoArchive videoArchive) {
        this.videoArchive = videoArchive;
    }
}
