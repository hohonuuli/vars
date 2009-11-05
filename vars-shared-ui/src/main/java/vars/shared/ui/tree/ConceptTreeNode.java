/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptPrimaryNameComparator;

/**
 * {@link  TreeNode} used to construct the {@link ConceptTreeModel}
 * 
 * @author brian
 */
public class ConceptTreeNode extends DefaultMutableTreeNode {

    private boolean loaded;
    private static final Comparator<Concept> COMPARATOR = new ConceptPrimaryNameComparator();


    public ConceptTreeNode(Concept userObject) {
        super(userObject);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * <p>Child nodes are added in order by primary name </p>
     *
     * @param node
     */
    @Override
    public void add(MutableTreeNode node) {
        if (node instanceof ConceptTreeNode) {
            ConceptTreeNode treeNode = (ConceptTreeNode) node;
            Concept thisObj = (Concept) treeNode.getUserObject();
            boolean added = false;

            for (int i = 0; i < getChildCount(); i++) {
                ConceptTreeNode thatNode = (ConceptTreeNode) getChildAt(i);

                Concept thatObj = (Concept) thatNode.getUserObject();

                int c = COMPARATOR.compare(thisObj, thatObj);
                if (c == 0) {
                    super.add(node);
                    added = true;
                    break;
                }
                else if (c < 0) {
                    super.insert(node, i);
                    added = true;
                    break;
                }
                
            }

            if (!added) {
                super.add(node);
            }
        }
    }

    

    
}
