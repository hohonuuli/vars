/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import vars.knowledgebase.ui.dialogs.AddConceptDialog;
import vars.shared.ui.ILockableEditor;
import vars.shared.ui.kbtree.ConceptTree;
import vars.shared.ui.kbtree.ConceptTreePopupMenu;

/**
 *
 * @author brian
 */
public class EditableConceptTreePopupMenu extends ConceptTreePopupMenu implements ILockableEditor {

    private final JMenuItem addConceptMenuItem;
        private final JMenuItem removeConceptMenuItem;
        private final JMenuItem moveConceptItem;
        private JDialog dialog;
        private boolean locked;

        private final ToolBelt toolBelt;

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

    public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            addConceptMenuItem.setEnabled(!locked);
            removeConceptMenuItem.setEnabled(!locked);
            moveConceptItem.setEnabled(!locked);
        }

        /**
		 * @return  the removeConceptMenuItem
		 * @uml.property  name="removeConceptMenuItem"
		 */
        public JMenuItem getRemoveConceptMenuItem() {
            return removeConceptMenuItem;
        }

        public JMenuItem getEditConceptMenuItem() {
            return moveConceptItem;
        }

        /**
		 * @return  the dialog
		 * @uml.property  name="dialog"
		 */
        public JDialog getDialog() {
            if (dialog == null) {
                dialog = new AddConceptDialog(null, null, null, null);
            }
            return dialog;
        }

        public void triggerEditAction() {
        	if (!isLocked()) {
        		((org.mbari.vars.knowledgebase.ui.dialogs.AddConceptDialog) getDialog()).setConcept(getSelectedConcept());
            	getDialog().setVisible(true);
        	}
        }

        public void triggerRemoveAction() {
        	if (!isLocked()) {
        		conceptTree.removeConcept();
        	}
        }

        public void triggerAddAction() {
        	if (!isLocked()) {
        		((org.mbari.vars.knowledgebase.ui.dialogs.AddConceptDialog) getDialog()).setConcept(null);
            	getDialog().setVisible(true);
        	}
        }
    }



}
