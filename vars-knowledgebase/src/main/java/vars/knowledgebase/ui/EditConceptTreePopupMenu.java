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
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ui.dialogs.AddConceptDialog;
import vars.shared.ui.ILockableEditor;
import vars.shared.ui.tree.ConceptTreeModel;
import vars.shared.ui.tree.ConceptTreeNode;

/**
 *
 * @author brian
 */
public class EditConceptTreePopupMenu extends JPopupMenu implements ILockableEditor {

    private final JMenuItem addConceptMenuItem;
    private JDialog dialog;
    private boolean locked;
    private final JMenuItem moveConceptItem;
    private final JMenuItem removeConceptMenuItem;
    private final ToolBelt toolBelt;
    private final JTree tree;

    /**
     * Constructs ...
     *
     * @param conceptTree
     * @param toolBelt
     */
    public EditConceptTreePopupMenu(JTree tree, ToolBelt toolBelt) {
        if (toolBelt == null) {
            throw new IllegalArgumentException("ToolBelt argument can not be null");
        }
        this.tree = tree;
        this.toolBelt = toolBelt;

        addConceptMenuItem = new JMenuItem("Add Concept", 'A');
        removeConceptMenuItem = new JMenuItem("Remove Concept", 'R');
        moveConceptItem = new JMenuItem("Edit Concept", 'M');


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
            int row = tree.getSelectionRows()[0];
            TreePath path = tree.getPathForRow(row);
            ConceptTreeNode node = (ConceptTreeNode) path.getLastPathComponent();

            ((AddConceptDialog) getDialog()).setConcept((Concept) node.getUserObject());
            getDialog().setVisible(true);
        }
    }

    public void triggerRemoveAction() {
        if (!isLocked()) {
            ConceptTreeModel model = (ConceptTreeModel) tree.getModel();
            int row = tree.getSelectionRows()[0];
            TreePath path = tree.getPathForRow(row);
            ConceptTreeNode node = (ConceptTreeNode) path.getLastPathComponent();
            node.setLoaded(false);
            model.reload(node);
        }
    }
}
