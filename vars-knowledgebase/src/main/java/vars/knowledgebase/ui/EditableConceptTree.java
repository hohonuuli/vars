/*
 * @(#)EditableConceptTree.java   2009.10.26 at 09:04:53 PDT
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

import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.DAO;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.History;
import vars.shared.ui.GlobalLookup;
import vars.shared.ui.ILockableEditor;
import vars.shared.ui.kbtree.ConceptTree;
import vars.shared.ui.kbtree.TreeConcept;

/**
 *
 * @author brian
 */
public class EditableConceptTree extends ConceptTree implements ILockableEditor {

    public final static String ADD_CONCEPT = "Add Child";
    public final static String EDIT_CONCEPT = "Edit";
    public final static String REMOVE_CONCEPT = "Delete";
    private boolean locked = true;
    private final ToolBelt toolBelt;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructs ...
     *
     * @param rootConcept
     * @param toolBelt
     */
    public EditableConceptTree(Concept rootConcept, ToolBelt toolBelt) {
        super(rootConcept, toolBelt.getKnowledgebaseDAOFactory());
        this.toolBelt = toolBelt;
        this.popupMenu = new EditableConceptTreePopupMenu(this, toolBelt);

        /*
         * Don't let foks delete the root node!
         */
        addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                if (!isLocked()) {
                    TreePath treePath = e.getNewLeadSelectionPath();
                    if (treePath != null) {
                        int i = treePath.getPathCount();
                        ((EditableConceptTreePopupMenu) getPopupMenu()).getRemoveConceptMenuItem().setEnabled(i > 1);
                        ((EditableConceptTreePopupMenu) getPopupMenu()).getEditConceptMenuItem().setEnabled(i > 1);
                    }
                }
            }

        });
    }

 
    public boolean isLocked() {
        return locked;
    }

    /**
     * Removes the <code>Concept</code> represented by the currently selected
     * node from the <code>KnowledgeBase</code>.
     */
    public void removeConcept() {
        Frame frame = (Frame) GlobalLookup.getSelectedFrameDispatcher().getValueObject();

        if (isLocked()) {
            JOptionPane.showMessageDialog(frame, "You must log in first", "VARS - Message", JOptionPane.PLAIN_MESSAGE);
        }
        else {
            final DefaultMutableTreeNode node = getSelectedNode();
            final String conceptName = ((TreeConcept) node.getUserObject()).getName();
            final UserAccount userAccount = (UserAccount) GlobalLookup.getUserAccountDispatcher().getValueObject();

            if ((userAccount != null) && (!userAccount.isReadOnly())) {

                String message = "Mark '" + conceptName +
                                 "' for removal?If approved, this \nwill also remove all of its descendents?";
                Object[] options = { "Remove", "Cancel" };
                int optionNum = JOptionPane.showOptionDialog(frame, message, "VARS - Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

                if (optionNum == 0) {

                    final Concept concept = ((TreeConcept) node.getUserObject()).getConcept();
                    Concept parentConcept = concept.getParentConcept();
                    if (parentConcept != null) {
                        History history = toolBelt.getHistoryFactory().delete(userAccount, concept);

                        DAO dao = toolBelt.getKnowledgebaseDAOFactory().newDAO();
                        dao.startTransaction();
                        parentConcept = dao.merge(parentConcept);
                        parentConcept.getConceptMetadata().addHistory(history);
                        history = dao.persist(history);
                        dao.endTransaction();

                        EventBus.publish(Lookup.TOPIC_APPROVE_HISTORY, history);
                        EventBus.publish(Lookup.TOPIC_REFRESH_KNOWLEGEBASE, parentConcept.getPrimaryConceptName().getName());

                    }
                }
            }
        }
    }


    /**
     * Method description
     * @param  locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
        ((EditableConceptTreePopupMenu) getPopupMenu()).setLocked(locked);
    }
}
