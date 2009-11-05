/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.swing.JFrame;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import org.jdesktop.swingx.JXTree;
import vars.knowledgebase.KnowledgebaseModule;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;

/**
 *
 * @author brian
 */
public class ConceptTreeDemo {

    public ConceptTreeDemo() {
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new KnowledgebaseModule());
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);
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
