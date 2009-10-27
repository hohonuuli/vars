/*
 * @(#)ConceptTreePopupMenu.java   2009.10.26 at 01:52:16 PDT
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author brian
 */

/**
      * Popup menu for tasks related to the <code>Concept</code> objects in the <code>MaintGui</code> concept tree.
      */
public class ConceptTreePopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 3149773522835773548L;
    private final ConceptTree conceptTree;

    /**
     * Constructs a <code>ConceptMenu</code> to perform tasks related to the
     * specified <code>ConceptTree</code>.
     *
     * @param conceptTree
     */
    public ConceptTreePopupMenu(final ConceptTree conceptTree) {
        this.conceptTree = conceptTree;
        JMenuItem expandDescendentsMenuItem = new JMenuItem(ConceptTree.EXPAND_DESCENDENTS, 'E');
        JMenuItem collapseChildrenMenuItem = new JMenuItem(ConceptTree.COLLAPSE_CHILDREN, 'C');
        JMenuItem expandAllMenuItem = new JMenuItem(ConceptTree.EXPAND_ALL, 'X');
        JMenuItem collapseAllMenuItem = new JMenuItem(ConceptTree.COLLAPSE_ALL, 'O');

        expandDescendentsMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                conceptTree.expandDescendents();
            }

        });

        collapseChildrenMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                conceptTree.collapseChildren();
            }

        });

        expandAllMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                conceptTree.expandAll();
            }

        });

        collapseAllMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                conceptTree.collapseAll();
            }

        });


        add(expandDescendentsMenuItem);
        add(collapseChildrenMenuItem);
        addSeparator();
        add(expandAllMenuItem);
        add(collapseAllMenuItem);

    }
}
