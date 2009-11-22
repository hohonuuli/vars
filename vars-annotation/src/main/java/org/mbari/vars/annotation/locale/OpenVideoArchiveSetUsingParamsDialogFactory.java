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
Created on Dec 16, 2004
 */
package org.mbari.vars.annotation.locale;

//import java.util.Collection;
import javax.swing.JDialog;

import vars.annotation.AnnotationDAOFactory;

//import org.mbari.vars.annotation.model.VideoArchiveSet;

/**
 * Factory that creates a locale specific dialog for opening video archives for
 * editing. The local is specified in <code>vars.properties</code> in the property
 * <code>deployment.locale</code>. The deployment.locale property shoudl match one
 * of the values retrived by <code>VideoArchiveSet.getCameraPlatforms()</code>.
 *
 * @author brian
 * @version $Id: $
 */
public class OpenVideoArchiveSetUsingParamsDialogFactory {
    

    /**
     *
     */
    public OpenVideoArchiveSetUsingParamsDialogFactory() {
        super();

    }

    /**
     * <p>Fetch the locale specfic dialog used for opening a video archive set</p>
     *
     *
     * @return
     */
    public static JDialog getDialog(AnnotationDAOFactory annotationDAOFactory) {

        return new OpenVideoArchiveSetDialog3(annotationDAOFactory);
    }
}
