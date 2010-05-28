package vars.shared.ui.tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author brian
 */
public class JTreePopupMenu extends JPopupMenu {

    private final JTree tree;


    public JTreePopupMenu(JTree tree) {
        this.tree = tree;
    }

    private void expandNode() {
        TreePath path = tree.getSelectionPath();
        Object node =  path.getLastPathComponent();

        if (node == null) {
            return;
        }

        TreeModel model = tree.getModel();
        while (true) {
            int count = model.getChildCount(node);
            if (count == 0) {
                break;
            }
            node = model.getChild(node, count - 1);
            path = path.pathByAddingChild(node);
        }
        tree.scrollPathToVisible(path);

    }

    private void collapseNode() {
        tree.collapsePath(tree.getSelectionPath());
    }
}
