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
Created on Dec 1, 2003
 */
package org.mbari.vars.annotation.ui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.mbari.vars.util.AppFrameDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p><!--Insert summary here--></p>
 *
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusPanel.java 398 2006-10-31 00:55:10Z hohonuuli $
 */
public class StatusPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(StatusPanel.class);
    private static final long serialVersionUID = 6386783372977837098L;

    /**
     *     @uml.property  name="personLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel personLabel = null;

    /**
     *     @uml.property  name="varsLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel varsLabel = null;

    /**
     *     @uml.property  name="vcrLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel vcrLabel = null;

    /**
     *     @uml.property  name="videoArchiveLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel videoArchiveLabel = null;

    /**
     * @directed
     */

    /*
     *  # StatusLabel lnkStatusLabel;
     */

    /**
     * This is the default constructor
     */
    public StatusPanel() {
        super();
        initialize();
    }

    /**
     *     This method initializes personLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="personLabel"
     */
    private javax.swing.JLabel getPersonLabel() {
        if (personLabel == null) {
            personLabel = new StatusLabelForPerson();
        }

        return personLabel;
    }

    /**
     *     This method initializes varsLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="varsLabel"
     */
    private javax.swing.JLabel getVarsLabel() {
        if (varsLabel == null) {
            varsLabel = new StatusLabelForVarsDb();
        }

        return varsLabel;
    }

    /**
     *     This method initializes vcrLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="vcrLabel"
     */
    private javax.swing.JLabel getVcrLabel() {
        if (vcrLabel == null) {
            vcrLabel = new StatusLabelForVcr();
        }

        return vcrLabel;
    }

    /**
     *     This method initializes videoArchiveLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="videoArchiveLabel"
     */
    private javax.swing.JLabel getVideoArchiveLabel() {
        if (videoArchiveLabel == null) {
            videoArchiveLabel = new StatusLabelForVideoArchive();
        }

        return videoArchiveLabel;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        this.add(getVarsLabel(), null);
        this.add(getPersonLabel(), null);

        try {
            this.add(getVcrLabel(), null);
        }
        catch (Error e) {
            log.warn("Failed to load RXTX libraries.", e);
            AppFrameDispatcher.showWarningDialog("RXTX, the software libraries used " +
                    "to support serial port I/O, are missing or are not properly installed." +
                    " VARS will not be able to communicate with your VCR.");
        }

        this.add(getVideoArchiveLabel(), null);
        this.setSize(400, 26);
        this.setMinimumSize(new Dimension(400, 26));
        this.setAlignmentX(Component.RIGHT_ALIGNMENT);
    }
}
