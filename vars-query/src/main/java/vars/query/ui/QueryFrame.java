/*
 * @(#)QueryFrame.java   2009.11.16 at 08:57:23 PST
 *
 * Copyright 2009 MBARI
 *
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
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.mbari.awt.event.ActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryPersistenceService;
import vars.query.ui.db.QueryAction;
import vars.query.ui.db.QueryActionImpl;
import vars.query.ui.db.QueryActionUI;
import vars.query.ui.db.QueryExecutor;
import vars.query.ui.db.preparedstatement.EscapedQueryExecutorImpl;

/**
 * @author Brian Schlining
 */
public class QueryFrame extends JFrame {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private JMenuItem aboutMenuItem = null;
    private JMenu fileMenu = null;
    private JMenu helpMenu = null;
    private JMenuItem helpMenuItem = null;
    private javax.swing.JPanel jContentPane = null;
    private JMenuBar jJMenuBar = null;
    private JToolBar jToolBar = null;
    private RefineSearchPanel refineSearchPanel = null;
    private ActionAdapter resetAction = null;
    private JButton resetButton = null;
    private ActionAdapter searchAction = null;
    private JButton searchButton = null;
    private JMenuItem searchMenuItem = null;
    private SearchPanel searchPanel = null;
    private JTabbedPane tabbedPane = null;
    private ActionMap actionMap = new ActionMap();
    private final QueryPersistenceService queryPersistenceService;
    private final KnowledgebaseDAOFactory knowledgebaseDAOFactory;

    /**
     *
     *
     * @param knowledgebaseDAOFactory
     * @param queryPersistenceService
     * @throws HeadlessException
     */
    @Inject
    public QueryFrame(KnowledgebaseDAOFactory knowledgebaseDAOFactory, QueryPersistenceService queryPersistenceService)
            throws HeadlessException {
        super();
        this.knowledgebaseDAOFactory = knowledgebaseDAOFactory;
        this.queryPersistenceService = queryPersistenceService;
        initialize();
    }

    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText("About");
        }

        return aboutMenuItem;
    }

    private ActionMap getActionMap() {
        if (actionMap == null) {
            actionMap = new ActionMap();

            App queryApp = StateLookup.getApplication();

            if (queryApp != null) {
                actionMap.setParent(queryApp.getActionMap());
            }
        }

        return actionMap;
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getSearchMenuItem());
        }

        return fileMenu;
    }

    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.add(getHelpMenuItem());
            helpMenu.add(getAboutMenuItem());
        }

        return helpMenu;
    }

    private JMenuItem getHelpMenuItem() {
        if (helpMenuItem == null) {
            helpMenuItem = new JMenuItem();
            helpMenuItem.setText("Help");
        }

        return helpMenuItem;
    }

    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getTabbedPane(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJToolBar(), java.awt.BorderLayout.SOUTH);
        }

        return jContentPane;
    }

    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
            jJMenuBar.add(getHelpMenu());
        }

        return jJMenuBar;
    }

    private JToolBar getJToolBar() {
        if (jToolBar == null) {
            jToolBar = new JToolBar();
            jToolBar.setLayout(new BoxLayout(jToolBar, BoxLayout.X_AXIS));
            jToolBar.add(Box.createHorizontalGlue());
            jToolBar.add(getSearchButton(), BorderLayout.CENTER);
            jToolBar.add(Box.createHorizontalGlue());
        }

        return jToolBar;
    }

    private RefineSearchPanel getRefineSearchPanel() {
        if (refineSearchPanel == null) {
            refineSearchPanel = new RefineSearchPanel(queryPersistenceService);
        }

        return refineSearchPanel;
    }

    private ActionAdapter getResetAction() {
        if (resetAction == null) {
            resetAction = new ActionAdapter() {


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

    private JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setAction(getResetAction());
            resetButton.setText("Reset");
        }

        return resetButton;
    }

    private ActionAdapter getSearchAction() {
        if (searchAction == null) {
            searchAction = new ActionAdapter() {


                public void doAction() {


                    /*
                     * Create a QueryAction
                     */
                    QueryExecutor queryExecutor = new EscapedQueryExecutorImpl(getSearchPanel().getConceptConstraints(),
                            getRefineSearchPanel().getValuePanels(),
                            getSearchPanel().getCbAllInterpretations().isSelected(),
                            getSearchPanel().getCbAllAssociations().isSelected(),
                            queryPersistenceService.getAnnotationQueryable());
                    QueryAction queryAction = new QueryActionImpl(queryExecutor,
                            queryPersistenceService,
                            getSearchPanel().getCbHierarchy().isSelected(),
                            getSearchPanel().getCbPhylogeny().isSelected(),
                            getSearchPanel().getCbFullPhylogeny().isSelected(),
                            getSearchPanel().getCbAssociationPerColumn().isSelected());

                    /*
                     * This generates the UI components for a QueryAction such
                     * as a cancel dialog, a results frame and a dialog to
                     * display any errors that might occur.
                     */
                    new QueryActionUI(queryAction, queryPersistenceService.getURL());

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

    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton();
            searchButton.setAction(getSearchAction());
            searchButton.setText("Search");

            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/execute_query.png"));

            searchButton.setIcon(icon);
            searchButton.setEnabled(false);
            searchButton.setToolTipText("Search");
            getActionMap().put("Search", getSearchAction());
        }

        return searchButton;
    }

    private JMenuItem getSearchMenuItem() {
        if (searchMenuItem == null) {
            searchMenuItem = new JMenuItem();
            searchMenuItem.setText("Search");
        }

        return searchMenuItem;
    }

    private SearchPanel getSearchPanel() {
        if (searchPanel == null) {

            Injector injector = StateLookup.GUICE_INJECTOR;

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

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Basic", getSearchPanel());
            tabbedPane.addTab("Advanced", getRefineSearchPanel());
        }

        return tabbedPane;
    }

    private void initialize() {
        this.setJMenuBar(getJJMenuBar());
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());

        ResourceBundle bundle = ResourceBundle.getBundle(StateLookup.RESOURCE_BUNDLE, Locale.US);
        final String title = bundle.getString("frame.title");

        this.setTitle(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
