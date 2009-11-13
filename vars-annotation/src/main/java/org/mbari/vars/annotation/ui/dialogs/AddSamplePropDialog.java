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
Created on Apr 19, 2004
 */
package org.mbari.vars.annotation.ui.dialogs;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import org.mbari.vars.annotation.ui.actions.AddPropertyAction;
import org.mbari.vars.annotation.ui.actions.AddSamplePropAction;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.ui.HierachicalConceptNameComboBox;
import org.mbari.vars.util.AppFrameDispatcher;

/**
 * <p>A dialog that prompts the user for the sampler device and sample number.
 * When the OK button is pressed, 2 properties are added to the Observation
 * selected in the table. THese properties are 'sampled-by' and
 * 'sample-reference'</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: AddSamplePropDialog.java 332 2006-08-01 18:38:46Z hohonuuli $
 * @see org.mbari.vars.annotation.ui.actions.AddSamplePropAction
 * @see org.mbari.vars.annotation.ui.actions.AddPropertyAction
 */
public class AddSamplePropDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 5646565565894821951L;

    /**
     *     @uml.property  name="btnCancel"
     *     @uml.associationEnd
     */
    private javax.swing.JButton btnCancel = null;

    /**
     *     @uml.property  name="btnOk"
     *     @uml.associationEnd
     */
    private javax.swing.JButton btnOk = null;

    /**
     *     @uml.property  name="cbSampler"
     *     @uml.associationEnd
     */
    private javax.swing.JComboBox cbSampler = null;

    /**
     *     @uml.property  name="jContentPane"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jContentPane = null;

    /**
     *     @uml.property  name="jLabel"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel jLabel = null;

    /**
     *     @uml.property  name="jLabel1"
     *     @uml.associationEnd
     */
    private javax.swing.JLabel jLabel1 = null;

    /**
     *     @uml.property  name="jPanel"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel = null;

    /**
     *     @uml.property  name="jPanel1"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel1 = null;

    /**
     *     @uml.property  name="jPanel2"
     *     @uml.associationEnd
     */
    private javax.swing.JPanel jPanel2 = null;

    /**
     *     @uml.property  name="tfSampleRefNum"
     *     @uml.associationEnd
     */
    private javax.swing.JTextField tfSampleRefNum = null;

    /**
     * This is the default constructor
     */
    public AddSamplePropDialog() {
        super(AppFrameDispatcher.getFrame(), true);
        initialize();
    }

    /**
     *     This method initializes btnCancel
     *     @return   javax.swing.JButton
     *     @uml.property  name="btnCancel"
     */
    private javax.swing.JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new javax.swing.JButton();
            btnCancel.setText("Cancel");
            btnCancel.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
            btnCancel.setFocusable(true);
            btnCancel.setRequestFocusEnabled(true);
        }

        return btnCancel;
    }

    /**
     *     This method initializes btnOk
     *     @return   javax.swing.JButton
     *     @uml.property  name="btnOk"
     */
    private javax.swing.JButton getBtnOk() {
        if (btnOk == null) {
            btnOk = new javax.swing.JButton();
            btnOk.setText("OK");
            btnOk.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {

                    // Add the sampled-by association
                    action1.setToConcept((String) getCbSampler().getSelectedItem());
                    action1.doAction();

                    // Add the sample reference association.
                    final String text = getTfSampleRefNum().getText();
                    if (!text.equals("")) {
                        action2.setLinkValue(getTfSampleRefNum().getText());
                        action2.doAction();
                    }

                    // reset the state of the ui for the next use.
                    getTfSampleRefNum().setText("");
                    getCbSampler().getEditor().selectAll();
                    dispose();
                }
                AddPropertyAction action1 = new AddSamplePropAction();
                AddPropertyAction action2 = new AddPropertyAction("sample-reference", "self", "0");

            });
            btnOk.setFocusable(true);
            btnOk.setRequestFocusEnabled(true);
        }

        return btnOk;
    }

    /**
     *     This method initializes cbSampler
     *     @return   javax.swing.JComboBox
     *     @uml.property  name="cbSampler"
     */
    private javax.swing.JComboBox getCbSampler() {
        if (cbSampler == null) {
            Concept c;
            try {
                c = KnowledgeBaseCache.getInstance().findConceptByName("equipment");

                if (c == null) {
                    c = KnowledgeBaseCache.getInstance().findRootConcept();

                }
            }
            catch (final DAOException e) {
                final ConceptName cn = new ConceptName(IConceptName.NAME_DEFAULT, IConceptName.NAMETYPE_PRIMARY);
                c = new Concept();
                c.addConceptName(cn);
            }

            cbSampler = new HierachicalConceptNameComboBox(c);
            cbSampler.setFocusable(true);
            cbSampler.setRequestFocusEnabled(true);
        }

        return cbSampler;
    }

    /**
     *     This method initializes jContentPane
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jContentPane"
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new javax.swing.BoxLayout(jContentPane, javax.swing.BoxLayout.Y_AXIS));
            jContentPane.add(getJPanel(), null);
            jContentPane.add(getJPanel1(), null);
            jContentPane.add(getJPanel2(), null);
        }

        return jContentPane;
    }

    /**
     *     This method initializes jLabel
     *     @return   javax.swing.JLabel
     *     @uml.property  name="jLabel"
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("  Sampled by:  ");
        }

        return jLabel;
    }

    /**
     *     This method initializes jLabel1
     *     @return   javax.swing.JLabel
     *     @uml.property  name="jLabel1"
     */
    private javax.swing.JLabel getJLabel1() {
        if (jLabel1 == null) {
            jLabel1 = new javax.swing.JLabel();
            jLabel1.setText("  Sample Reference Number:  ");
        }

        return jLabel1;
    }

    /**
     *     This method initializes jPanel
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel"
     */
    private javax.swing.JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new javax.swing.JPanel();
            jPanel.setLayout(new javax.swing.BoxLayout(jPanel, javax.swing.BoxLayout.X_AXIS));
            jPanel.add(getJLabel(), null);
            jPanel.add(getCbSampler(), null);
            jPanel.setPreferredSize(new java.awt.Dimension(107, 25));
        }

        return jPanel;
    }

    /**
     *     This method initializes jPanel1
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel1"
     */
    private javax.swing.JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new javax.swing.JPanel();
            jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));
            jPanel1.add(getJLabel1(), null);
            jPanel1.add(getTfSampleRefNum(), null);
        }

        return jPanel1;
    }

    /**
     *     This method initializes jPanel2
     *     @return   javax.swing.JPanel
     *     @uml.property  name="jPanel2"
     */
    private javax.swing.JPanel getJPanel2() {
        if (jPanel2 == null) {
            jPanel2 = new javax.swing.JPanel();
            jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));
            jPanel2.add(getBtnOk(), null);
            jPanel2.add(getBtnCancel(), null);
            jPanel2.setPreferredSize(new java.awt.Dimension(124, 30));
        }

        return jPanel2;
    }

    /**
     *     This method initializes tfSampleRefNum
     *     @return   javax.swing.JTextField
     *     @uml.property  name="tfSampleRefNum"
     */
    private javax.swing.JTextField getTfSampleRefNum() {
        if (tfSampleRefNum == null) {
            tfSampleRefNum = new javax.swing.JTextField();
            tfSampleRefNum.setFocusable(true);
            tfSampleRefNum.setRequestFocusEnabled(true);
        }

        return tfSampleRefNum;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        this.setSize(350, 103);
        this.setContentPane(getJContentPane());
        this.setTitle("VARS - Add Sample Reference");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

    /**
     *  Overridden method. Transfers focus to the cbSampler component when set
     * to true.
     *
     * @param  b The new visible value
     */
    public void setVisible(final boolean b) {
        if (b) {
            final JComboBox cb = getCbSampler();
            cb.requestFocus();
            cb.getEditor().selectAll();
        }

        super.setVisible(b);
    }
}
