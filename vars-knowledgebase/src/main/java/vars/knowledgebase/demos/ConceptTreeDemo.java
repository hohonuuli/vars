/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.demos;

import com.google.inject.Injector;
import javax.swing.JFrame;
import javax.swing.tree.TreeModel;
import org.jdesktop.swingx.JXTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.ui.Lookup;
import vars.knowledgebase.ui.Toolbelt;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;

/**
 *
 * @author brian
 */
public class ConceptTreeDemo {

    private static final Logger log = LoggerFactory.getLogger(ConceptTreeDemo.class);

    public ConceptTreeDemo() {
    }

    public static void main(String[] args) {
        Injector injector = (Injector) Lookup.getGuiceInjectorDispatcher().getValueObject();
        Toolbelt toolBelt = injector.getInstance(Toolbelt.class);
        JFrame frame = new JFrame();
        TreeModel treeModel = new ConceptTreeModel(toolBelt.getKnowledgebaseDAOFactory());
        JXTree tree = new JXTree(treeModel);
        tree.setCellRenderer(new ConceptTreeCellRenderer());
        frame.add(tree);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



}
