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

//~--- classes ----------------------------------------------------------------

/**
 * @author Brian Schlining
 * @version $Id: ConceptConstraintsPanelDemo.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class ConceptConstraintsPanelDemo extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 6929871871834413950L;
    /**
	 * @uml.property  name="jContentPane"
	 * @uml.associationEnd  
	 */
    private javax.swing.JPanel jContentPane = null;
    /**
	 * @uml.property  name="jPanel"
	 * @uml.associationEnd  
	 */
    private JPanel jPanel = null;

    private final Injector injector;

    //~--- constructors -------------------------------------------------------

    /**
     * This is the default constructor
     */
    public ConceptConstraintsPanelDemo() {
        super();
        injector = Guice.createInjector(new QueryModule());
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * This method initializes jContentPane
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jContentPane"
	 */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
        }

        return jContentPane;
    }

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jPanel"
	 */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new SearchPanel(injector);
        }

        return jPanel;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame f = new ConceptConstraintsPanelDemo();
        f.pack();
        f.setVisible(true);
    }
}
