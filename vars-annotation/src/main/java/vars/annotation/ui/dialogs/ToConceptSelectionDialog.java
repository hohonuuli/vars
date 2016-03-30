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
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.ui.StateLookup;
import vars.knowledgebase.Concept;
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
    private HierachicalConceptNameComboBox comboBox;

    /**
     * Create the dialog
     *
     * @param annotationPersistenceService
     */
    @Inject
    public ToConceptSelectionDialog(AnnotationPersistenceService annotationPersistenceService) {
        this(annotationPersistenceService, "physical-object");
    }

    public ToConceptSelectionDialog(AnnotationPersistenceService annotationPersistenceService, String conceptName) {
        super(StateLookup.getAnnotationFrame());
        this.annotationPersistenceService = annotationPersistenceService;

        try {
            initialize();
            setBaseConceptName(conceptName);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setBaseConceptName(String conceptName) {

        try {
            Concept concept = annotationPersistenceService.findConceptByName(conceptName);

            if (concept == null) {
                concept = annotationPersistenceService.findRootConcept();
            }

            getComboBox().setConcept(concept);
            getComboBox().setSelectedItem(concept.getPrimaryConceptName().getName());
        }
        catch (Exception ex) {
            log.error("Failed to lookup concepts from knowledgebase", ex);
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, ex);
        }

    }


    /**
     * @return
     */
    protected HierachicalConceptNameComboBox getComboBox() {
        if (comboBox == null) {

            comboBox = new HierachicalConceptNameComboBox(annotationPersistenceService);

            Dimension size = comboBox.getPreferredSize();
            Dimension preferredSize = new Dimension(350, size.height);
            comboBox.setPreferredSize(preferredSize);
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
            EventBus.publish(StateLookup.TOPIC_NONFATAL_ERROR, e);
        }

        return concept;
    }

    private void initialize() throws Exception {

        setModal(true);
        setTitle("VARS - Select Concept");
        getContentPane().add(getComboBox(), BorderLayout.CENTER);
        getCancelButton().addActionListener(e -> ToConceptSelectionDialog.this.dispose());
        getOkayButton().addActionListener(e -> ToConceptSelectionDialog.this.dispose());

        getRootPane().setDefaultButton(getOkayButton());
        addHierarchyListener(e -> {
            if (HierarchyEvent.SHOWING_CHANGED == (HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags())) {
                ToConceptSelectionDialog.this.getRootPane().setDefaultButton(getOkayButton());
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
