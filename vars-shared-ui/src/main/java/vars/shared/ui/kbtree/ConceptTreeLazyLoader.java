/*
 * @(#)ConceptTreeLazyLoader.java   2009.10.26 at 11:30:29 PDT
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

import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 */
public class ConceptTreeLazyLoader implements TreeExpansionListener {

    private final DefaultTreeModel treeModel;

    /**
     * Constructs ...
     *
     *
     * @param model
     */
    public ConceptTreeLazyLoader(DefaultTreeModel model) {
        treeModel = model;
    }

    /**
     * @param event
     */
    public void treeCollapsed(TreeExpansionEvent event) {

        // TODO Auto-generated method stub
    }

    /**
     * @param event
     */
    public void treeExpanded(final TreeExpansionEvent event) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        final TreeConcept treeConcept = (TreeConcept) node.getUserObject();
        Thread lazyLoader = new Thread() {

            @Override
            public void run() {
                if ((treeConcept != null) && treeConcept.lazyExpand(node)) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            treeModel.reload(node);
                        }
                    });
                }
            }
        };
        lazyLoader.start();
    }
}
