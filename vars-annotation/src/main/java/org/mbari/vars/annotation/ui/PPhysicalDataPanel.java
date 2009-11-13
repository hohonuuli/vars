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
Created on October 31, 2003, 9:16 AM
 */
package org.mbari.vars.annotation.ui;

import org.mbari.util.IObserver;
import vars.annotation.IObservation;
import vars.annotation.IPhysicalData;
import vars.annotation.IVideoFrame;

/**
 * <p>Displays the properties of a PhysicalData object.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: PPhysicalDataPanel.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class PPhysicalDataPanel extends PropertiesPanel implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = 4679317552284109641L;

    /**
     * Creates new form PPhysicalDataPanel
     */
    public PPhysicalDataPanel() {
        super();
        setPropertyNames(new String[] {
            "Latitude", "Longitude", "Depth", "Temperature", "Salinity", "Oxygen", "Light"
        });
    }

    /**
     * @param  obj Description of the Parameter
     * @param  changeCode Description of the Parameter
     * @see  org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object obj, final Object changeCode) {
        final IObservation obs = (IObservation) obj;
        if (obs == null) {
            clearValues();

            return;
        }

        final IVideoFrame vf = obs.getVideoFrame();
        if (vf == null) {
            clearValues();
        }
        else {
            final IPhysicalData ad = vf.getPhysicalData();
            if (ad == null) {
                clearValues();
            }
            else {
                setProperties(ad);
            }
        }
    }
}
