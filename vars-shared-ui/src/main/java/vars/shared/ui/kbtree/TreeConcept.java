/*
 * @(#)TreeConcept.java   2009.10.26 at 11:24:24 PDT
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.History;

/**
 *
 * @version    $Id: TreeConcept.java 294 2006-07-06 23:29:45Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class TreeConcept implements Comparable {

    private final Concept concept;
    private final String[] secondaryNames;

    /**
     * Constructs ...
     *
     *
     * @param concept
     */
    public TreeConcept(Concept concept) {
        if (concept == null) {
            throw new IllegalArgumentException("Can not use null as argument to TreeConcepts constructor");
        }

        this.concept = concept;

        /*
         * Maintain an array of the secondary names
         */
        final Collection<ConceptName> conceptNames = new ArrayList<ConceptName>(concept.getConceptNames());
        conceptNames.remove(concept.getPrimaryConceptName());

        secondaryNames = new String[conceptNames.size()];
        int i = 0;
        for (ConceptName conceptName : conceptNames) {
            secondaryNames[i] = conceptName.getName();
            i++;
        }

    }

    /*
     *  (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param o
     *
     * @return
     */
    public int compareTo(Object o) {
        return getName().compareTo(((TreeConcept) o).getName());
    }

    /**
         * <p><!-- Method description --></p>
         * @return
         * @uml.property  name="concept"
         */
    public Concept getConcept() {
        return concept;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    public String getName() {
        return concept.getPrimaryConceptName().toString();
    }

    /**
     * @return
     */
    String[] getSecondaryNames() {

        // RxNOTE wcpr No defensive copy since package protected method
        return secondaryNames;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    boolean hasDetails() {
        return concept.hasDetails();
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    boolean hasPrimaryImage() {
        return concept.getConceptMetadata().hasPrimaryImage();
    }

    public boolean isPendingApproval() {
        boolean pendingApproval = false;
        Set<History> histories = new HashSet<History>(concept.getConceptMetadata().getHistories());
        for (History history : histories) {
            pendingApproval = !(history.isApproved() || history.isRejected());

            if (pendingApproval) {
                break;
            }
        }

        return pendingApproval;
    }

    /**
     * Add this concepts children to the node passed to the method. For example,
     * <pre>
     * TreeConcept treeConcept = new TreeConcept(rootConcept);
     * DefaultMutableTreeNode rootNode =
     *     new DefaultMutableTreeNode(treeConcept);
     * treeConcept.lazyExpand(rootNode);
     *
     * </pre>
     *
     * @param parent
     * @return
     *
     */
    public synchronized boolean lazyExpand(DefaultMutableTreeNode parent) {

        // Return false if attempt made to expand a node with no children.
        if (parent.isLeaf()) {
            return false;
        }

        // check if this node needs to be updated
        DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent.getFirstChild();
        if (flag == null) {
            return false;    // no flag set, this node doesn't need any action performed
        }

        Object obj = flag.getUserObject();
        if (!(obj instanceof Boolean)) {
            return false;    // the first object is not a flag, no need to expand
        }

        // remove the flag
        parent.removeAllChildren();
        Collection<Concept> concepts = new ArrayList<Concept>(getConcept().getChildConcepts());
        for (Iterator iter = concepts.iterator(); iter.hasNext(); ) {
            Concept childConcept = (Concept) iter.next();
            DefaultMutableTreeNode node = new SortedTreeNode(new TreeConcept(childConcept));
            parent.add(node);

            if (childConcept.hasChildConcepts()) {
                node.add(new SortedTreeNode(Boolean.TRUE));
            }
        }

        return true;
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("TreeConcept( ");
        buf.append(getName());

        if (0 < secondaryNames.length) {
            buf.append(": ");

            for (int i = 0; i < secondaryNames.length - 1; ++i) {
                buf.append(secondaryNames[i]);
                buf.append(", ");
            }

            buf.append(secondaryNames[secondaryNames.length - 1]);
        }

        buf.append(" )");

        return buf.toString();
    }
}
