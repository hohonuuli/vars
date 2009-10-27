/*
 * @(#)ConceptTreeCellRenderer.java   2009.10.26 at 11:25:46 PDT
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

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mbari.swing.SpinningDial;

/**
 *
 * @version    $Id: ConceptTreeCellRenderer.java 265 2006-06-20 05:30:09Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class ConceptTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final String DEFAULT = "/images/vars/knowledgebase/nav_plain_blue.png";
    private static final String PENDING = "/images/vars/knowledgebase/nav_plain_red.png";
    private static final long serialVersionUID = -7382528013502852004L;
    private static final Color pendingTextColor = Color.RED.brighter().brighter();
    private static final Color loadingTextColor = Color.GRAY;

    // Buffer for text label
    private final StringBuffer textBuf = new StringBuffer();
    private final ImageIcon defaultIcon;
    private final Icon loadingIcon;
    private final ImageIcon pendingIcon;

    /**
     * Constructs ...
     *
     */
    public ConceptTreeCellRenderer() {
        super();
        defaultIcon = new ImageIcon(getClass().getResource(DEFAULT));
        pendingIcon = new ImageIcon(getClass().getResource(PENDING));
        loadingIcon = new SpinningDial(18, 18, 8);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param tree
     * @param value
     * @param isSelected
     * @param isExpanded
     * @param isLeaf
     * @param row
     * @param hasFocus
     *
     * @return
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded,
            boolean isLeaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);

        // Get the name from the Object contained in this node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        /*
         * if the user object is a boolean value, then that means that
         * the concept nodes at this level are currently be retrieved.
         */
        if (userObject instanceof Boolean) {
            setText("Retrieving Concepts...");
            setIcon(loadingIcon);
            setForeground(loadingTextColor);
        }
        else {
            TreeConcept treeConcept = (TreeConcept) userObject;

            if (treeConcept.isPendingApproval()) {
                setForeground(pendingTextColor);
                setIcon(pendingIcon);
            }
            else {
                setIcon(defaultIcon);
            }


            // Put the primary ConceptName in first
            textBuf.replace(0, textBuf.length(), treeConcept.getName());

            // now add the aliases
            String[] secondaryNames = treeConcept.getSecondaryNames();
            Arrays.sort(secondaryNames);

            if (0 < secondaryNames.length) {
                textBuf.append(" (");

                for (int i = 0; i < secondaryNames.length; i++) {
                    textBuf.append(secondaryNames[i]);

                    if (i != secondaryNames.length - 1) {
                        textBuf.append(", ");
                    }
                }

                textBuf.append(")");
            }

            setText(textBuf.toString());

        }

        return this;
    }
}
