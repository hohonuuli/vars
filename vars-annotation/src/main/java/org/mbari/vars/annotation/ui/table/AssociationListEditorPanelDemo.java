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


package org.mbari.vars.annotation.ui.table;

import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.mbari.vars.annotation.model.Association;
import org.mbari.vars.annotation.model.Observation;

/**
 * <p>A demo class</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: AssociationListEditorPanelDemo.java 314 2006-07-10 02:38:46Z hohonuuli $
 */
public class AssociationListEditorPanelDemo extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 2597640857241765522L;

    /**
     * Constructor for the AssociationListEditorPanelDemo object
     */
    public AssociationListEditorPanelDemo() {
        try {
            final AssociationListEditorPanel p = new AssociationListEditorPanel();
            final Observation obs = new Observation();
            obs.setConceptName("nanomia");
            final Association a1 = new Association("color", "self", "blue");
            obs.addAssociation(a1);
            final Association a2 = new Association("eating", "krill", "1");
            obs.addAssociation(a2);
            p.setObservation(obs);
            getContentPane().add(p);
            this.pack();
            this.setVisible(true);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param event
     */
    protected void processWindowEvent(final WindowEvent event) {
        if (event.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }

        super.processWindowEvent(event);
    }

    /**
     *  The main program for the AssociationListEditorPanelDemo class
     *
     * @param  args The command line arguments
     */
    public static void main(final String[] args) {
        new AssociationListEditorPanelDemo();
    }
}
