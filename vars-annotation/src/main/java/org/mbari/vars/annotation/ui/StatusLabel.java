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

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.mbari.util.IObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>A widget that live at the bottom of the annotation app. Indicates that
 * status of a particular connection or setting. Green = good, red = bad</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: StatusLabel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public abstract class StatusLabel extends JLabel implements IObserver {

    private static final Logger log = LoggerFactory.getLogger(StatusLabel.class);

    /**
     *     @uml.property  name="offIcon"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private ImageIcon offIcon;

    /**
     *     @uml.property  name="ok"
     */
    private boolean ok;

    /**
     *     @uml.property  name="onIcon"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private ImageIcon onIcon;

    /**
     * Constructor for the StatusLabel object
     */
    public StatusLabel() {
        offIcon = getImageIcon("/images/vars/annotation/error.png");
        onIcon = getImageIcon("/images/vars/annotation/check.png");
        setOk(false);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(1, 2, 1, 2)));
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param relativePath
     *
     * @return
     */
    private ImageIcon getImageIcon(final String relativePath) {
        return new ImageIcon(getClass().getResource(relativePath));
    }

    /**
     * @return returns the state of the Label.
     */
    public boolean isOk() {
        return ok;
    }

    /**
     *     @param  b
     *     @uml.property  name="ok"
     */
    public void setOk(final boolean b) {
        ok = b;

        if (ok) {
            setIcon(onIcon);
        }
        else {
            setIcon(offIcon);
        }
    }
}
