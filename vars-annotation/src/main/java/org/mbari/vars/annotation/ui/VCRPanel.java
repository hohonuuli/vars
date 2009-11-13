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


package org.mbari.vars.annotation.ui;

import org.mbari.util.IObserver;
import org.mbari.vars.annotation.ui.dispatchers.VcrDispatcher;
import org.mbari.vcr.IVCR;

/**
 * <p>A VCR panel that monitors for changes of VCRs</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: VCRPanel.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class VCRPanel extends org.mbari.vcr.ui.VCRPanel implements IObserver {

    /**
     *
     */
    private static final long serialVersionUID = 4917967545974139337L;

    /**
     * Constructor
     */
    public VCRPanel() {
        super();
        final VcrDispatcher dispatcher = VcrDispatcher.getInstance();
        dispatcher.addObserver(this);
        setVcr(dispatcher.getVcr());
    }

    /**
     *  Recieves updates from the VcrDispatcher. This method should not be called
     * by a developer.
     *
     * @param  newVcr The new IVCR object
     * @param  changeCode This parameter is not used.
     * @see org.mbari.util.IObserver#update(java.lang.Object, java.lang.Object)
     */
    public void update(final Object newVcr, final Object changeCode) {
        setVcr((IVCR) newVcr);
    }
}
