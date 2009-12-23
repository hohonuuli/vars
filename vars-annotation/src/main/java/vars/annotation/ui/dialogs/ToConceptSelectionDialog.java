/*
 * @(#)ToConceptSelectionDialog.java   2009.12.23 at 08:26:51 PST
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

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.ui.Lookup;
import vars.knowledgebase.Concept;
import vars.shared.ui.ConceptNameComboBox;
import vars.shared.ui.HierachicalConceptNameComboBox;
import vars.shared.ui.dialogs.StandardDialog;

/**
 * A combo-box for choosing 'physical-object' and it's shild concepts.
 *
 * @author         Brian Schlining [brian@mbari.org]
 */
public class ToConceptSelectionDialog extends StandardDialog {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AnnotationPersistenceService annotationPersistenceService;
    private ConceptNameComboBox comboBox;

    /**
     * Create the dialog
     *
     * @param annotationPersistenceService
     */
    @Inject
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
    protected ConceptNameComboBox getComboBox() {
        if (comboBox == null) {
            Concept concept = null;
            try {
                // TODO "physical-object" is hard coded. should be in a properties file
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

            Dimension size = comboBox.getPreferredSize();
            Dimension preferredSize = new Dimension(350, size.height);
            comboBox.setPreferredSize(preferredSize);
            comboBox.setSelectedItem(concept.getPrimaryConceptName().getName());
            comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent ke) {
                    final char c = ke.getKeyChar();
                    if (c == KeyEvent.VK_ENTER) {
                        getOkayButton().doClick();
                    }
                }
            });

        }

        return comboBox;
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

        setModal(true);
        setTitle("VARS - Select Concept");
        getContentPane().add(getComboBox(), BorderLayout.CENTER);
        getCancelButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ToConceptSelectionDialog.this.dispose();
            }

        });
        getOkayButton().addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ToConceptSelectionDialog.this.dispose();
            }
        });

        getRootPane().setDefaultButton(getOkayButton());
        addHierarchyListener(new HierarchyListener() {

            public void hierarchyChanged(final HierarchyEvent e) {
                if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                    ToConceptSelectionDialog.this.getRootPane().setDefaultButton(getOkayButton());
                }
            }


        });
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
