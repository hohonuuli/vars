/*
 * @(#)AddSamplePropDialog.java   2009.11.18 at 04:32:38 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.vars.annotation.ui.dialogs;

import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import org.mbari.vars.annotation.ui.actions.AddPropertyAction;
import org.mbari.vars.annotation.ui.actions.AddSamplePropAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.HierachicalConceptNameComboBox;

/**
 * <p>A dialog that prompts the user for the sampler device and sample number.
 * When the OK button is pressed, 2 properties are added to the Observation
 * selected in the table. THese properties are 'sampled-by' and
 * 'sample-reference'</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @see org.mbari.vars.annotation.ui.actions.AddSamplePropAction
 * @see org.mbari.vars.annotation.ui.actions.AddPropertyAction
 */
public class AddSamplePropDialog extends JDialog {

    private javax.swing.JButton btnCancel = null;
    private javax.swing.JButton btnOk = null;
    private javax.swing.JComboBox cbSampler = null;
    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JLabel jLabel = null;
    private javax.swing.JLabel jLabel1 = null;
    private javax.swing.JPanel jPanel = null;
    private javax.swing.JPanel jPanel1 = null;
    private javax.swing.JPanel jPanel2 = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private javax.swing.JTextField tfSampleRefNum = null;
    private final ToolBelt toolBelt;

    /**
     * This is the default constructor
     *
     * @param toolBelt
     */
    public AddSamplePropDialog(ToolBelt toolBelt) {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject(), true);
        this.toolBelt = toolBelt;
        initialize();
    }

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
                AddPropertyAction action1 = new AddSamplePropAction(toolBelt);
                AddPropertyAction action2 = new AddPropertyAction(toolBelt, "sample-reference", "self", "0");

            });
            btnOk.setFocusable(true);
            btnOk.setRequestFocusEnabled(true);
        }

        return btnOk;
    }

    private javax.swing.JComboBox getCbSampler() {
        if (cbSampler == null) {
            Concept c;
            try {

                // TODO This is hard coded. It should be moved out to a properties file
                c = toolBelt.getAnnotationPersistenceService().findConceptByName("equipment");

                if (c == null) {
                    c = toolBelt.getAnnotationPersistenceService().findRootConcept();
                }
            }
            catch (final Exception e) {
                final ConceptName cn = new SimpleConceptNameBean(ConceptName.NAME_DEFAULT,
                    ConceptNameTypes.PRIMARY.getName());
                c = new SimpleConceptBean(cn);
            }

            cbSampler = new HierachicalConceptNameComboBox(c, toolBelt.getAnnotationPersistenceService());
            cbSampler.setFocusable(true);
            cbSampler.setRequestFocusEnabled(true);
        }

        return cbSampler;
    }

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

    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setText("  Sampled by:  ");
        }

        return jLabel;
    }

    private javax.swing.JLabel getJLabel1() {
        if (jLabel1 == null) {
            jLabel1 = new javax.swing.JLabel();
            jLabel1.setText("  Sample Reference Number:  ");
        }

        return jLabel1;
    }

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

    private javax.swing.JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new javax.swing.JPanel();
            jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));
            jPanel1.add(getJLabel1(), null);
            jPanel1.add(getTfSampleRefNum(), null);
        }

        return jPanel1;
    }

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

    private javax.swing.JTextField getTfSampleRefNum() {
        if (tfSampleRefNum == null) {
            tfSampleRefNum = new javax.swing.JTextField();
            tfSampleRefNum.setFocusable(true);
            tfSampleRefNum.setRequestFocusEnabled(true);
        }

        return tfSampleRefNum;
    }

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
