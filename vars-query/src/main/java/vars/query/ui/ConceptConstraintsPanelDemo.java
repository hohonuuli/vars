/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.query.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import vars.query.QueryModule;


/**
 * @author Brian Schlining
 */
public class ConceptConstraintsPanelDemo extends JFrame {


    private javax.swing.JPanel jContentPane = null;

    private JPanel jPanel = null;

    private final Injector injector;


    /**
     * This is the default constructor
     */
    public ConceptConstraintsPanelDemo() {
        super();
        injector = Guice.createInjector(new QueryModule());
        initialize();
    }



    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
        }

        return jContentPane;
    }


    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new SearchPanel(injector);
        }

        return jPanel;
    }


    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame f = new ConceptConstraintsPanelDemo();
        f.pack();
        f.setVisible(true);
    }
}
