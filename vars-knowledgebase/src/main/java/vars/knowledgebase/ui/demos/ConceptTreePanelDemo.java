/*
 * @(#)ConceptTreePanelDemo.java   2009.11.05 at 02:44:29 PST
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui.demos;

import com.google.inject.Injector;
import javax.swing.JFrame;
import org.jdesktop.swingx.JXTree;
import vars.knowledgebase.ui.StateLookup;
import vars.knowledgebase.ui.ToolBelt;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;
import vars.shared.ui.tree.ConceptTreePanel;

/**
 *
 * @author brian
 */
public class ConceptTreePanelDemo {

    public static void main(String[] args) {
        Injector injector = StateLookup.GUICE_INJECTOR;
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);
        JFrame frame = new JFrame();
        ConceptTreePanel panel = new ConceptTreePanel(toolBelt.getKnowledgebaseDAOFactory());
        final ConceptTreeModel treeModel = new ConceptTreeModel(toolBelt.getKnowledgebaseDAOFactory());
        JXTree tree = new JXTree(treeModel);
        tree.setCellRenderer(new ConceptTreeCellRenderer());
        panel.setJTree(tree);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
