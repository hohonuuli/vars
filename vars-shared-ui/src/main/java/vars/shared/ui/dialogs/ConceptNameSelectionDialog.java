/*
 * @(#)ConceptNameSelectionDialog.java   2010.03.16 at 01:11:37 PDT
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



package vars.shared.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import org.mbari.swing.SortedComboBoxModel;
import vars.shared.ui.ConceptNameComboBox;

/**
 *
 * @author brian
 */
public class ConceptNameSelectionDialog extends StandardDialog {

    ConceptNameComboBox conceptNameComboBox;

    /**
     * Constructs ...
     */
    public ConceptNameSelectionDialog() {
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public ConceptNameSelectionDialog(Dialog owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public ConceptNameSelectionDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     */
    public ConceptNameSelectionDialog(Window owner) {
        super(owner);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modal
     */
    public ConceptNameSelectionDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public ConceptNameSelectionDialog(Dialog owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modal
     */
    public ConceptNameSelectionDialog(Frame owner, boolean modal) {
        super(owner, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public ConceptNameSelectionDialog(Frame owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param modalityType
     */
    public ConceptNameSelectionDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     */
    public ConceptNameSelectionDialog(Window owner, String title) {
        super(owner, title);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     */
    public ConceptNameSelectionDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     */
    public ConceptNameSelectionDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modalityType
     */
    public ConceptNameSelectionDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     */
    public ConceptNameSelectionDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modal
     * @param gc
     */
    public ConceptNameSelectionDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        initialize();
    }

    /**
     * Constructs ...
     *
     * @param owner
     * @param title
     * @param modalityType
     * @param gc
     */
    public ConceptNameSelectionDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        initialize();
    }

    /**
     * @return
     */
    public ConceptNameComboBox getConceptNameComboBox() {
        if (conceptNameComboBox == null) {
            conceptNameComboBox = new ConceptNameComboBox();

            // Select the whole word if focus is gained
            conceptNameComboBox.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    conceptNameComboBox.getEditor().selectAll();
                }
            });

            // Enter in combobox is the same as pressing the OK button
            conceptNameComboBox.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent ke) {
                    final char c = ke.getKeyChar();
                    if (c == KeyEvent.VK_ENTER) {
                        getOkayButton().doClick();
                    }
                }

            });
        }

        return conceptNameComboBox;
    }

    /**
     * @return
     */
    public String getSelectedItem() {
        return (String) getConceptNameComboBox().getSelectedItem();
    }

    private void initialize() {
        add(getConceptNameComboBox(), BorderLayout.CENTER);
        pack();
    }

    /**
     *
     * @param items
     */
    public void setItems(Collection<String> items) {
        SortedComboBoxModel<String> model = (SortedComboBoxModel<String>) getConceptNameComboBox().getModel();
        model.clear();
        model.addAll(items);
    }
}
