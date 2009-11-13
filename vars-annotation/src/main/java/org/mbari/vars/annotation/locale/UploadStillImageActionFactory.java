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
 * Created on Dec 16, 2004 by Brian Schlining
 */
package org.mbari.vars.annotation.locale;

//~--- classes ----------------------------------------------------------------

/**
 * <p></p>
 *
 * @author <a href="http://www.mbari.org">MBARI</a>
 * @version $Id: UploadStillImageActionFactory.java 3 2005-10-27 16:20:12Z hohonuuli $
 */
public class UploadStillImageActionFactory {

    /**
     *
     */
    private UploadStillImageActionFactory() {
        // No instantiation
    }

    //~--- get methods --------------------------------------------------------

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public static UploadStillImageAction getAction() {
        final String platform = LocaleFactory.getCameraPlatform();
        UploadStillImageAction action = null;
        if ((platform == null) || platform.toLowerCase().equals("shore")) {
            action = new org.mbari.vars.annotation.locale.shore.UploadStillImageAction();
        } else {
            action = new org.mbari.vars.annotation.locale.UploadStillImageAction();
        }

        return action;
    }
}
