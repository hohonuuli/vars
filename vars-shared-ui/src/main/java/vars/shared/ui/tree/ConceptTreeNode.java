/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.shared.ui.tree;

import java.util.Comparator;
import javax.swing.tree.MutableTreeNode;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptPrimaryNameComparator;
import vars.shared.ui.kbtree.SortedTreeNode;

/**
 *
 * @author brian
 */
public class ConceptTreeNode extends SortedTreeNode {

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
