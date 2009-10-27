/*
 * @(#)SortedTreeNode.java   2009.10.26 at 11:14:51 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vars.shared.ui.kbtree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author brian
 */
public class SortedTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 9190234445771344521L;

    /**
     *
     */
    public SortedTreeNode() {
        super();
    }

    /**
     * @param userObject
     */
    public SortedTreeNode(Object userObject) {
        super(userObject);
    }

    /**
     * @param userObject
     * @param allowsChildren
     */
    public SortedTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /**
     * <p>Child nodes are always added in order based on the compareTo methods
     * of the nodes userObject. </p>
     *
     * @param node
     */
    @Override
    public void add(MutableTreeNode node) {
        if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
            Object obj = treeNode.getUserObject();
            boolean added = false;
            if (obj instanceof Comparable) {
                Comparable thisComp = (Comparable) obj;
                for (int i = 0; i < getChildCount(); i++) {
                    TreeNode thatNode = getChildAt(i);
                    if (thatNode instanceof DefaultMutableTreeNode) {
                        Object thatObj = ((DefaultMutableTreeNode) thatNode).getUserObject();
                        if (thatObj instanceof Comparable) {
                            int c = thisComp.compareTo((Comparable) thatObj);
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
                    }
                }
            }

            if (!added) {
                super.add(node);
            }
        }
    }
}
