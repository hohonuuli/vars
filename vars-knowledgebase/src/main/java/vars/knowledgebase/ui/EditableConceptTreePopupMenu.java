/*
 * @(#)EditableConceptTreePopupMenu.java   2009.10.27 at 10:27:49 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import vars.knowledgebase.ui.dialogs.AddConceptDialog;
import vars.shared.ui.ILockableEditor;
import vars.shared.ui.kbtree.ConceptTreePopupMenu;

/**
 *
 * @author brian
 */
public class EditableConceptTreePopupMenu extends ConceptTreePopupMenu implements ILockableEditor {

    private final JMenuItem addConceptMenuItem;
    private JDialog dialog;
    private boolean locked;
    private final JMenuItem moveConceptItem;
    private final JMenuItem removeConceptMenuItem;
    private final ToolBelt toolBelt;

    /**
     * Constructs ...
     *
     * @param conceptTree
     * @param toolBelt
     */
    public EditableConceptTreePopupMenu(final EditableConceptTree conceptTree, ToolBelt toolBelt) {
        super(conceptTree);
        this.toolBelt = toolBelt;

        addConceptMenuItem = new JMenuItem(EditableConceptTree.ADD_CONCEPT, 'A');
        removeConceptMenuItem = new JMenuItem(EditableConceptTree.REMOVE_CONCEPT, 'R');
        moveConceptItem = new JMenuItem(EditableConceptTree.EDIT_CONCEPT, 'M');


        addConceptMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                triggerAddAction();
            }

        });

        removeConceptMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                triggerRemoveAction();
            }

        });

        moveConceptItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                triggerEditAction();
            }

        });

        addSeparator();
        add(addConceptMenuItem);
        add(removeConceptMenuItem);
        add(moveConceptItem);
    }

    public JDialog getDialog() {
        if (dialog == null) {
            dialog = new AddConceptDialog(toolBelt);
        }
        return dialog;
    }

    public JMenuItem getEditConceptMenuItem() {
        return moveConceptItem;
    }

    public JMenuItem getRemoveConceptMenuItem() {
        return removeConceptMenuItem;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        addConceptMenuItem.setEnabled(!locked);
        removeConceptMenuItem.setEnabled(!locked);
        moveConceptItem.setEnabled(!locked);
    }

    public void triggerAddAction() {
        if (!isLocked()) {
            ((AddConceptDialog) getDialog()).setConcept(null);
            getDialog().setVisible(true);
        }
    }

    public void triggerEditAction() {
        if (!isLocked()) {
            ((AddConceptDialog) getDialog()).setConcept(getConceptTree().getSelectedConcept());
            getDialog().setVisible(true);
        }
    }

    public void triggerRemoveAction() {
        if (!isLocked()) {
            getConceptTree().removedConcept(null);
        }
    }
}
