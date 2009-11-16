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
Created on October 31, 2003, 1:39 PM
 */
package org.mbari.vars.annotation.ui;

import java.awt.BorderLayout;
import org.mbari.vars.annotation.ui.table.ObservationTablePanel;

/**
 * <p>A JPanel with an ObservationTable embedded in it.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: EditorPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class EditorPanel extends javax.swing.JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -8056377391049168527L;

    /**
     *     @uml.property  name="observationTablePanel"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    private ObservationTablePanel observationTablePanel;

    /**
     * Creates new form EditorPanel
     */
    public EditorPanel() {
        this.setLayout(new BorderLayout());
        jbInit();
    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void jbInit() {

        // GEN-BEGIN:initComponents
        observationTablePanel = new ObservationTablePanel();
        add(observationTablePanel, BorderLayout.CENTER);
    }

    // GEN-END:initComponents
}