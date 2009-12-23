/*
 * @(#)AddSamplePropDialog.java   2009.12.17 at 04:51:53 PST
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



package vars.annotation.ui.dialogs;

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import vars.annotation.ui.ToolBelt;
import vars.annotation.ui.actions.AddPropertyAction;
import vars.annotation.ui.actions.AddSamplePropAction;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.SimpleConceptBean;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.shared.ui.dialogs.StandardDialog;

/**
 *
 *
 * @version        Enter version here..., 2009.12.17 at 04:51:53 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class AddSamplePropDialog extends StandardDialog {

    private JComboBox comboBox;
    private JLabel lblSampleId;
    private JLabel lblSampledBy;
    private JPanel panel;
    private JTextField textField;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param toolBelt
     */
    public AddSamplePropDialog(ToolBelt toolBelt) {
        this.toolBelt = toolBelt;
        initialize();
    }

    private JComboBox getComboBox() {
        if (comboBox == null) {
            Concept concept;

            // Do lookup
            try {
                concept = (Concept) Worker.post(new Task() {

                    @Override
                    public Object run() throws Exception {

                        // TODO This is hard coded. It should be moved out to a properties file
                        Concept c = toolBelt.getAnnotationPersistenceService().findConceptByName("equipment");

                        if (c == null) {
                            c = toolBelt.getAnnotationPersistenceService().findRootConcept();
                        }

                        return c;
                    }
                });

            }
            catch (Exception e) {
                final ConceptName cn = new SimpleConceptNameBean(ConceptName.NAME_DEFAULT,
                    ConceptNameTypes.PRIMARY.getName());
                concept = new SimpleConceptBean(cn);
            }

            comboBox = new HierachicalConceptNameComboBox(concept, toolBelt.getAnnotationPersistenceService());
            comboBox.setFocusable(true);
            comboBox.setRequestFocusEnabled(true);
            comboBox.setSelectedItem("equipment");
        }

        return comboBox;
    }

    private JLabel getLblSampleId() {
        if (lblSampleId == null) {
            lblSampleId = new JLabel("Sample ID");
        }

        return lblSampleId;
    }

    private JLabel getLblSampledBy() {
        if (lblSampledBy == null) {
            lblSampledBy = new JLabel("Sampled By");
        }

        return lblSampledBy;
    }

    private JPanel getPanel() {
            if (panel == null) {
                    panel = new JPanel();
                    GroupLayout groupLayout = new GroupLayout(panel);
                    groupLayout.setHorizontalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                    .addComponent(getLblSampledBy())
                                                    .addComponent(getLblSampleId()))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                    .addComponent(getTextField(), GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                                                    .addComponent(getComboBox(), 0, 361, Short.MAX_VALUE))
                                            .addContainerGap())
                    );
                    groupLayout.setVerticalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblSampledBy())
                                                    .addComponent(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                    .addComponent(getLblSampleId())
                                                    .addComponent(getTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addContainerGap(0, Short.MAX_VALUE))
                    );
                    panel.setLayout(groupLayout);
            }
            return panel;
    }

    private JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setColumns(10);
            textField.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                        getOkayButton().doClick();
                    }
                }

            });
        }

        return textField;
    }

    private void initialize() {

        setTitle("VARS - Add Sample Reference");
        this.setResizable(false);
        getContentPane().add(getPanel(), BorderLayout.CENTER);
        setLocationRelativeTo(null);
        getOkayButton().addActionListener(new ActionListener() {

            AddPropertyAction action1 = new AddSamplePropAction(toolBelt);
            AddPropertyAction action2 = new AddPropertyAction(toolBelt, "sample-reference", "self", "0");

            public void actionPerformed(ActionEvent e) {

                // Add the sampled-by association
                action1.setToConcept((String) getComboBox().getSelectedItem());
                action1.doAction();

                // Add the sample reference association.
                final String text = getTextField().getText();
                if (!text.equals("")) {
                    action2.setLinkValue(text);
                    action2.doAction();
                }

                // reset the state of the ui for the next use.
                getTextField().setText("");
                getComboBox().getEditor().selectAll();
                dispose();
            }


        });

        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        pack();
    }

    /**
 *  Overridden method. Transfers focus to the cbSampler component when set
 * to true.
 *
 * @param  b The new visible value
 */
    @Override
    public void setVisible(final boolean b) {
        if (b) {
            final JComboBox cb = getComboBox();
            cb.requestFocus();
            cb.getEditor().selectAll();
        }

        super.setVisible(b);
    }
}
