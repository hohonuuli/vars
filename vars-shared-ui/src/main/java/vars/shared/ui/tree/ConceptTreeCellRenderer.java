/*
 * @(#)ConceptTreeCellRenderer.java   2009.11.04 at 02:32:08 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.shared.ui.tree;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.mbari.swing.SpinningDial;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;

/**
 *
 * @author brian
 */
public class ConceptTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final String DEFAULT = "/vars/images/16/nav_plain_blue.png";
    private static final String DEFAULT_WITH_IMAGE = "/vars/images/16/nav_plain_blue_square_glass_grey.png";
    private static final String PENDING = "/vars/images/16/nav_plain_red.png";
    private static final String PENDING_WITH_IMAGE = "/vars/images/16/nav_plain_red_square_glass_grey.png";
    private static final long serialVersionUID = -7382528013502852004L;
    private static final Color pendingTextColor = Color.RED.brighter().brighter();
    private static final Color loadingTextColor = Color.GRAY;

    // Buffer for text label
    private final StringBuffer textBuf = new StringBuffer();
    //private final List<ConceptName> secondaryNames = Collections.synchronizedList(new ArrayList<ConceptName>());
    private final Comparator<ConceptName> comparator = new Comparator<ConceptName>() {

        public int compare(ConceptName o1, ConceptName o2) {
            final String s1 = o1.getName();
            final String s2 = o2.getName();

            return s1.compareToIgnoreCase(s2);
        }
    };
    private final ImageIcon defaultIcon;
    private final ImageIcon defaultWithImageIcon;
    private final Icon loadingIcon;
    private final ImageIcon pendingIcon;
    private final ImageIcon pendingWithImageIcon;


    /**
     * Constructs ...
     *
     */
    public ConceptTreeCellRenderer() {
        super();
        defaultIcon = new ImageIcon(getClass().getResource(DEFAULT));
        defaultWithImageIcon = new ImageIcon(getClass().getResource(DEFAULT_WITH_IMAGE));
        pendingIcon = new ImageIcon(getClass().getResource(PENDING));
        pendingWithImageIcon = new ImageIcon(getClass().getResource(PENDING_WITH_IMAGE));
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
        //secondaryNames.clear();

        // Get the name from the Object contained in this node
        ConceptTreeNode node = (ConceptTreeNode) value;
        Concept concept = (Concept) node.getUserObject();

        /*
         * if the user object is a boolean value, then that means that
         * the concept nodes at this level are currently be retrieved.
         */
        if (concept instanceof ConceptTreeConcept) {
            setText("Retrieving Concepts...");
            setIcon(loadingIcon);
            setForeground(loadingTextColor);
        }
        else {

            ImageIcon imageIcon = defaultIcon;
            if (concept.getConceptMetadata().isPendingApproval()) {
                imageIcon = (concept.getConceptMetadata().getPrimaryImage() == null) ?
                        pendingIcon : pendingWithImageIcon;
                setForeground(pendingTextColor);
            }
            else {
                imageIcon = (concept.getConceptMetadata().getPrimaryImage() == null) ?
                        defaultIcon : defaultWithImageIcon;
            }

            setIcon(imageIcon);

            // Put the primary ConceptName in first
            textBuf.replace(0, textBuf.length(), concept.getPrimaryConceptName().getName());

            // now add the aliases
            final List<ConceptName> secondaryNames = new ArrayList<ConceptName>();

            secondaryNames.addAll(concept.getConceptNames());
            secondaryNames.remove(concept.getPrimaryConceptName());

            if (secondaryNames.size() > 0) {
                Collections.sort(secondaryNames, comparator);
                textBuf.append(" (");

                for (int i = 0; i < secondaryNames.size(); i++) {
                    textBuf.append(secondaryNames.get(i).getName());

                    if ((secondaryNames.size() - 1) != i) {
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
