/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vars.knowledgebase.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.TreeModel;
import org.jdesktop.swingx.JXTree;
import vars.knowledgebase.KnowledgebaseModule;
import vars.shared.ui.tree.ConceptTreeCellRenderer;
import vars.shared.ui.tree.ConceptTreeModel;
import vars.shared.ui.tree.ConceptTreePanel;

/**
 *
 * @author brian
 */
public class ConceptTreePanelDemo {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new KnowledgebaseModule());
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);
        JFrame frame = new JFrame();
        ConceptTreePanel panel = new ConceptTreePanel(toolBelt.getKnowledgebaseDAOFactory());
        TreeModel treeModel = new ConceptTreeModel(toolBelt.getKnowledgebaseDAOFactory());
        JXTree tree = new JXTree(treeModel);
        tree.setCellRenderer(new ConceptTreeCellRenderer());
        panel.setJTree(tree);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
