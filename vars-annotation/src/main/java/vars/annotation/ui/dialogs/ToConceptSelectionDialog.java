/*
 * @(#)ToConceptSelectionDialog.java   2009.11.19 at 08:48:01 PST
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import org.bushe.swing.event.EventBus;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.mbari.swing.JFancyButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.knowledgebase.Concept;
import vars.shared.ui.ConceptNameComboBox;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.annotation.ui.Lookup;

/**
 *
 *
 * @version        Enter version here..., 2009.11.19 at 08:48:01 PST
 * @author         Brian Schlining [brian@mbari.org]    
 */
public class ToConceptSelectionDialog extends JDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AnnotationPersistenceService annotationPersistenceService;
    private JButton cancelButton;
    private ConceptNameComboBox comboBox;
    private JButton okButton;

    /**
     * Create the dialog
     *
     * @param annotationPersistenceService
     */
    public ToConceptSelectionDialog(AnnotationPersistenceService annotationPersistenceService) {
        super((Frame) Lookup.getApplicationFrameDispatcher().getValueObject());
        this.annotationPersistenceService = annotationPersistenceService;

        try {
            initialize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    protected JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JFancyButton();
            cancelButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/delete2.png")));
            cancelButton.setText("");
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ToConceptSelectionDialog.this.setVisible(false);
                }

            });
        }

        return cancelButton;
    }

    /**
     * @return
     */
    protected ConceptNameComboBox getComboBox() {
        if (comboBox == null) {
            Concept concept = null;
            try {
                concept = annotationPersistenceService.findConceptByName("physical-object");

                if (concept == null) {
                    concept = annotationPersistenceService.findRootConcept();
                }
            }
            catch (Exception ex) {
                log.error("Failed to lookup concepts from knowledgebase", ex);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, ex);
            }

            if (concept != null) {
                comboBox = new HierachicalConceptNameComboBox(concept, annotationPersistenceService);
            }
            else {
                comboBox = new HierachicalConceptNameComboBox(annotationPersistenceService);
            }
        }

        return comboBox;
    }

    /**
     * @return
     */
    public JButton getOkButton() {
        if (okButton == null) {
            okButton = new JFancyButton();
            okButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/knowledgebase/check2.png")));
            okButton.setText("");
            getRootPane().setDefaultButton(okButton);
            addHierarchyListener(new HierarchyListener() {

                public void hierarchyChanged(final HierarchyEvent e) {
                    if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                        ToConceptSelectionDialog.this.getRootPane().setDefaultButton(okButton);
                    }
                }

            });
        }

        return okButton;
    }

    /**
     * @return
     */
    public Concept getSelectedConcept() {
        String conceptName = (String) getComboBox().getSelectedItem();
        Concept concept = null;
        try {
            concept = annotationPersistenceService.findConceptByName(conceptName);
        }
        catch (Exception e) {
            log.error("Failed to lookup '" + conceptName + "' from the knowledgebase", e);
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }

        return concept;
    }

    private void initialize() throws Exception {
        final GroupLayout groupLayout = new GroupLayout((JComponent) getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(GroupLayout.LEADING)
                .add(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(groupLayout.createParallelGroup(GroupLayout.TRAILING)
                        .add(getComboBox(), 0, 476, Short.MAX_VALUE)
                        .add(groupLayout.createSequentialGroup()
                            .add(getOkButton())
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(getCancelButton())))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(GroupLayout.LEADING)
                .add(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(getComboBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(groupLayout.createParallelGroup(GroupLayout.BASELINE)
                        .add(getCancelButton())
                        .add(getOkButton()))
                    .addContainerGap(16, Short.MAX_VALUE))
        );
        getContentPane().setLayout(groupLayout);
                setModal(true);
        setTitle("VARS - Select Concept");
        pack();
    }

    /**
     *
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b) {
            getComboBox().requestFocusInWindow();
        }
    }
}
