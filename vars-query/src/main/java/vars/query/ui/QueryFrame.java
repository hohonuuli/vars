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

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.ResourceBundle;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.util.Dispatcher;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryDAO;

//~--- classes ----------------------------------------------------------------

/**
 * @author Brian Schlining
 * @version $Id: QueryFrame.java 429 2006-11-20 22:51:32Z hohonuuli $
 */
public class QueryFrame extends JFrame {

    private static final long serialVersionUID = 1528090325157653848L;

    private static final Logger log = LoggerFactory.getLogger(QueryFrame.class);

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="jContentPane"
	 * @uml.associationEnd  
	 */
    private javax.swing.JPanel jContentPane = null;
    /**
	 * @uml.property  name="jJMenuBar"
	 * @uml.associationEnd  
	 */
    private JMenuBar jJMenuBar = null;
    /**
	 * @uml.property  name="fileMenu"
	 * @uml.associationEnd  
	 */
    private JMenu fileMenu = null;
    /**
	 * @uml.property  name="searchMenuItem"
	 * @uml.associationEnd  
	 */
    private JMenuItem searchMenuItem = null;
    /**
	 * @uml.property  name="helpMenuItem"
	 * @uml.associationEnd  
	 */
    private JMenuItem helpMenuItem = null;
    /**
	 * @uml.property  name="helpMenu"
	 * @uml.associationEnd  
	 */
    private JMenu helpMenu = null;
    /**
	 * @uml.property  name="aboutMenuItem"
	 * @uml.associationEnd  
	 */
    private JMenuItem aboutMenuItem = null;
    /**
	 * @uml.property  name="tabbedPane"
	 * @uml.associationEnd  
	 */
    private JTabbedPane tabbedPane = null;
    /**
	 * @uml.property  name="searchPanel"
	 * @uml.associationEnd  
	 */
    private SearchPanel searchPanel = null;
    /**
	 * @uml.property  name="searchButton"
	 * @uml.associationEnd  
	 */
    private JButton searchButton = null;
    /**
	 * @uml.property  name="searchAction"
	 * @uml.associationEnd  
	 */
    private ActionAdapter searchAction = null;
    /**
	 * @uml.property  name="resetButton"
	 * @uml.associationEnd  
	 */
    private JButton resetButton = null;
    /**
	 * @uml.property  name="resetAction"
	 * @uml.associationEnd  
	 */
    private ActionAdapter resetAction = null;
    /**
	 * @uml.property  name="refineSearchPanel"
	 * @uml.associationEnd  
	 */
    private RefineSearchPanel refineSearchPanel = null;
    /**
	 * @uml.property  name="jToolBar"
	 * @uml.associationEnd  
	 */
    private JToolBar jToolBar = null;
    /**
	 * @uml.property  name="actionMap"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private ActionMap actionMap = new ActionMap();

    private final QueryDAO queryDAO;
    private final ConceptDAO conceptDAO;

    //~--- constructors -------------------------------------------------------

    /**
     *
     * @throws HeadlessException
     */
    @Inject
    public QueryFrame(KnowledgebaseDAOFactory knowledgebaseDAOFactory, QueryDAO queryDAO) throws HeadlessException {
        super();
        this.conceptDAO = knowledgebaseDAOFactory.newConceptDAO();
        this.queryDAO = queryDAO;
        // TODO Auto-generated constructor stub
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * This method initializes jMenuItem
	 * @return  javax.swing.JMenuItem
	 * @uml.property  name="aboutMenuItem"
	 */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText("About");
        }

        return aboutMenuItem;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="actionMap"
	 */
    private ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = new ActionMap();
            Dispatcher dispatcher = Lookup.getApplicationDispatcher();
            QueryApp queryApp = (QueryApp) dispatcher.getValueObject();
            if (queryApp != null) {
                actionMap.setParent(queryApp.getActionMap());
            }
        }

        return actionMap;
    }

    /**
	 * This method initializes jMenu
	 * @return  javax.swing.JMenu
	 * @uml.property  name="fileMenu"
	 */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getSearchMenuItem());
        }

        return fileMenu;
    }

    /**
	 * This method initializes jMenu
	 * @return  javax.swing.JMenu
	 * @uml.property  name="helpMenu"
	 */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.add(getHelpMenuItem());
            helpMenu.add(getAboutMenuItem());
        }

        return helpMenu;
    }

    /**
	 * This method initializes jMenuItem
	 * @return  javax.swing.JMenuItem
	 * @uml.property  name="helpMenuItem"
	 */
    private JMenuItem getHelpMenuItem() {
        if (helpMenuItem == null) {
            helpMenuItem = new JMenuItem();
            helpMenuItem.setText("Help");
        }

        return helpMenuItem;
    }

    /**
	 * This method initializes jContentPane
	 * @return  javax.swing.JPanel
	 * @uml.property  name="jContentPane"
	 */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getTabbedPane(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJToolBar(), java.awt.BorderLayout.SOUTH);
        }

        return jContentPane;
    }

    /**
	 * This method initializes jJMenuBar
	 * @return  javax.swing.JMenuBar
	 * @uml.property  name="jJMenuBar"
	 */
    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
            jJMenuBar.add(getHelpMenu());
        }

        return jJMenuBar;
    }

    /**
	 * This method initializes jToolBar
	 * @return  javax.swing.JToolBar
	 * @uml.property  name="jToolBar"
	 */
    private JToolBar getJToolBar() {
        if (jToolBar == null) {
            jToolBar = new JToolBar();
            jToolBar.setLayout(new BoxLayout(jToolBar, BoxLayout.X_AXIS));
            jToolBar.add(Box.createHorizontalGlue());
            jToolBar.add(getSearchButton(), BorderLayout.CENTER);
            jToolBar.add(Box.createHorizontalGlue());
            // jToolBar.add(getResetButton());
            // jToolBar.add(getHelpButton());
        }

        return jToolBar;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="refineSearchPanel"
	 */
    private RefineSearchPanel getRefineSearchPanel() {
        if (refineSearchPanel == null) {
            refineSearchPanel = new RefineSearchPanel(queryDAO);
        }

        return refineSearchPanel;
    }

    /**
	 * The resetactio traverses the actionmaps looking for any action that starts with "RESET" and executes it.
	 * @return
	 * @uml.property  name="resetAction"
	 */
    private ActionAdapter getResetAction() {
        if (resetAction == null) {
            resetAction = new ActionAdapter() {

                private static final long serialVersionUID = -4071195135236367475L;

                public void doAction() {
                    final ActionMap actionMap = getActionMap();
                    Object[] keys = actionMap.allKeys();
                    for (int i = 0; i < keys.length; i++) {
                        Object key = keys[i];
                        System.out.println("Found Action with Key of " + key);

                        if (key.toString().startsWith("RESET")) {
                            actionMap.get(key).actionPerformed(null);
                        }
                    }
                }
            };
        }

        return resetAction;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="resetButton"
	 */
    private JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setAction(getResetAction());
            resetButton.setText("Reset");
        }

        return resetButton;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="searchAction"
	 */
    private ActionAdapter getSearchAction() {
        if (searchAction == null) {
            searchAction = new ActionAdapter() {

                private static final long serialVersionUID = 80316860610009593L;

                public void doAction() {

                    /*
                     * Create the query string. This is a bit hacky, poor separation
                     * of concerns, but it works.
                     */
                    final String query = SQLGenerator.getSQL(getSearchPanel().getConceptConstraints(),
                        getRefineSearchPanel().getValuePanels(),
                        getSearchPanel().getCbAllInterpretations().isSelected(),
                        getSearchPanel().getCbAllAssociations().isSelected());

                    /*
                     * Create a QueryAction
                     */
                    QueryAction queryAction = new QueryAction(query,
                        queryDAO,
                        conceptDAO,
                        getSearchPanel().getCbHierarchy().isSelected(),
                        getSearchPanel().getCbPhylogeny().isSelected(),
                        getSearchPanel().getCbFullPhylogeny().isSelected());

                    /*
                     * This generates the UI components for a QUeryaction such
                     * as a cancel dialog, a results frame and a dialog to
                     * display any errors that might occur.
                     */
                    new QueryActionUI(queryAction, queryDAO.getURL());

                    /*
                     * Execute the query
                     */
                    log.info("Starting query");
                    queryAction.doAction();
                }
            };
        }

        return searchAction;
    }

    /**
	 * This method initializes jButton
	 * @return  javax.swing.JButton
	 * @uml.property  name="searchButton"
	 */
    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton();
            searchButton.setAction(getSearchAction());
            searchButton.setText("Search");
            final ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/vars/query/execute_query.png"));
            searchButton.setIcon(icon);
            searchButton.setEnabled(false);
            searchButton.setToolTipText("Search");
            getActionMap().put("Search", getSearchAction());
        }

        return searchButton;
    }

    /**
	 * This method initializes jMenuItem
	 * @return  javax.swing.JMenuItem
	 * @uml.property  name="searchMenuItem"
	 */
    private JMenuItem getSearchMenuItem() {
        if (searchMenuItem == null) {
            searchMenuItem = new JMenuItem();
            searchMenuItem.setText("Search");
        }

        return searchMenuItem;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="searchPanel"
	 */
    private SearchPanel getSearchPanel() {
        if (searchPanel == null) {
            Dispatcher dispatcher = Lookup.getGuiceInjectorDispatcher();
            Injector injector = (Injector) dispatcher.getValueObject();
            searchPanel = new SearchPanel(injector);
            final ListModel listModel = searchPanel.getConceptConstraintsList().getModel();

            /*
             * This listener enables the search button only if there are constraints
             * available.
             */
            listModel.addListDataListener(new ListDataListener() {

                public void intervalAdded(ListDataEvent e) {
                    contentsChanged(e);
                }
                public void intervalRemoved(ListDataEvent e) {
                    contentsChanged(e);
                }
                public void contentsChanged(ListDataEvent e) {
                    getSearchButton().setEnabled(listModel.getSize() > 0);
                }

            });
        }

        return searchPanel;
    }

    /**
	 * This method initializes jTabbedPane
	 * @return  javax.swing.JTabbedPane
	 * @uml.property  name="tabbedPane"
	 */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Basic", getSearchPanel());
            tabbedPane.addTab("Advanced", getRefineSearchPanel());
        }

        return tabbedPane;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setJMenuBar(getJJMenuBar());
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        ResourceBundle bundle =  ResourceBundle.getBundle(Lookup.RESOURCE_BUNDLE);
        final String title = bundle.getString("frame.title");
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
