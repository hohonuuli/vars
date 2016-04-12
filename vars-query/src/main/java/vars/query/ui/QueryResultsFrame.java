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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import vars.query.results.QueryResults;


/**
 * @author Brian Schlining
 */
public class QueryResultsFrame extends JFrame {


    private javax.swing.JPanel jContentPane = null;

    private JMenuItem helpMenuItem = null;

    private JMenu helpMenu = null;

    private JMenu fileMenu = null;

    private JMenuItem closeMenuItem = null;

    private JMenuItem aboutMenuItem = null;

    private JMenuBar aMenuBar = null;

    private final String query;
 
    private final QueryResults queryResults;

    private JMenuItem saveMenuItem = null;

    private JMenuItem saveImagesMenuItem = null;

    private JToolBar toolBar = null;

    private JTabbedPane tabbedPane = null;

    private JButton saveImagesButton = null;
 
    private ActionAdapter saveImagesAction = null;
  
    private JButton saveButton = null;

    private ActionAdapter saveAction = null;
    private JButton saveAsKMLButton = null;
    private ActionAdapter saveAsKMLAction = null;

    private JPanel queryPanel = null;
  
    private JLabel queryLabel = null;

    private JTable dataTable = null;
 
    private JScrollPane dataScrollPane = null;
 
    private JPanel dataPanel = null; 

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private String databaseUrl;

    /**
     * This is the default constructor
     *
     * @param queryResults
     */
    public QueryResultsFrame(QueryResults queryResults) {
        this(queryResults, null, null);
    }

    /**
     * Constructs ...
     *
     *
     * @param queryResults
     * @param query
     */
    public QueryResultsFrame(QueryResults queryResults, String query, String databaseUrl) {
        super();
        this.queryResults = queryResults;
        this.query = query;
        this.databaseUrl = databaseUrl == null ? "unknown" : databaseUrl;
        initialize();
    }
    //~--- get methods --------------------------------------------------------

    /**
     * This method initializes jJMenuBar
     * @return  javax.swing.JMenuBar
     * @uml.property  name="aMenuBar"
     */
    private JMenuBar getAMenuBar() {
        if (aMenuBar == null) {
            aMenuBar = new JMenuBar();
            aMenuBar.add(getFileMenu());
            aMenuBar.add(getHelpMenu());
        }

        return aMenuBar;
    }

    /**
     * This method initializes jMenuItem
     * @return  javax.swing.JMenuItem
     * @uml.property  name="aboutMenuItem"
     */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
        }

        return aboutMenuItem;
    }

    /**
     * This method initializes jMenuItem
     * @return  javax.swing.JMenuItem
     * @uml.property  name="closeMenuItem"
     */
    private JMenuItem getCloseMenuItem() {
        if (closeMenuItem == null) {
            closeMenuItem = new JMenuItem();
            closeMenuItem.setText("Close");
            closeMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    QueryResultsFrame.this.dispose();
                }
            });
        }

        return closeMenuItem;
    }

    /**
     * This method initializes jPanel
     * @return  javax.swing.JPanel
     * @uml.property  name="dataPanel"
     */
    private JPanel getDataPanel() {
        if (dataPanel == null) {
            dataPanel = new JPanel();
            dataPanel.setLayout(new BorderLayout());
            dataPanel.add(getDataScrollPane(), java.awt.BorderLayout.CENTER);
        }

        return dataPanel;
    }

    /**
     * This method initializes jScrollPane
     * @return  javax.swing.JScrollPane
     * @uml.property  name="dataScrollPane"
     */
    private JScrollPane getDataScrollPane() {
        if (dataScrollPane == null) {
            dataScrollPane = new JScrollPane();
            dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            dataScrollPane.setViewportView(getDataTable());
        }

        return dataScrollPane;
    }

    /**
     * This method initializes jTable
     * @return  javax.swing.JTable
     * @uml.property  name="dataTable"
     */
    private JTable getDataTable() {
        if (dataTable == null) {
            dataTable = new QueryResultsTable(queryResults);
        }

        return dataTable;
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
            fileMenu.add(getSaveMenuItem());
            fileMenu.add(getCloseMenuItem());
            fileMenu.add(getSaveImagesMenuItem());
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
            helpMenu.add(getAboutMenuItem());
            helpMenu.add(getHelpMenuItem());
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
            jContentPane.add(getToolBar(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getTabbedPane(), java.awt.BorderLayout.CENTER);
        }

        return jContentPane;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="query"
     */
    public String getQuery() {
        return query;
    }

    /**
     * Create an HTML formatted string that displays the query and the database information
     * @return
     * @uml.property  name="queryLabel"
     */
    private JLabel getQueryLabel() {
        if (queryLabel == null) {
            queryLabel = new JLabel();

            /*
             * We wrap everything as HTML so the display wraps nicely.
             */
            StringBuffer queryText = new StringBuffer();
            queryText.append("<html><head></head><body>");

            // Append the date
            queryText.append("<p>").append(new Date()).append("</p><br>");

            // Append the URL we used

            queryText.append("<p>DATABASE<br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<strong>");
            queryText.append(databaseUrl).append("</strong></p><br>");

            /*
             * Append the query we use. SInce we're wrapping in HTML we'll need to
             * convert all < or > signs
             */
            String temp = query;
            temp = temp.replaceAll("<=", "&lt;=");
            temp = temp.replaceAll(">=", "&gt;=");
            queryText.append("<p>QUERY<br>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<strong>");
            queryText.append(temp).append("</strong></p><br>");

            // Append the record count
            queryText.append("<p>TOTAL RECORDS: ").append(queryResults.getRows()).append("</p><br>");
            queryText.append("</body></html>");
            queryLabel.setText(queryText.toString());
            queryLabel.setVerticalAlignment(SwingConstants.TOP);
            queryLabel.setPreferredSize(new Dimension(200, 200));
        }

        return queryLabel;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="queryPanel"
     */
    private JPanel getQueryPanel() {
        if (queryPanel == null) {
            queryPanel = new JPanel();
            queryPanel.setLayout(new BorderLayout());
            queryPanel.add(getQueryLabel(), BorderLayout.CENTER);
        }

        return queryPanel;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="queryResults"
     */
    public QueryResults getQueryResults() {
        return queryResults;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="saveAction"
     */
    private ActionAdapter getSaveAction() {
        if (saveAction == null) {

            /*
             * Prompt the user with a dialog and save the results to that location
             */
            saveAction = new ActionAdapter() {

                private static final long serialVersionUID = 1L;

                public void doAction() {

                    /*
                     * Show dialog for selecting a directory
                     */
                    int option = chooser.showSaveDialog(QueryResultsFrame.this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        SaveQueryResultsAction action = new SaveQueryResultsAction(chooser.getSelectedFile(), queryResults, query, databaseUrl);
                        action.doAction();
                    }
                }
                private final JFileChooser chooser = new JFileChooser();
            };
        }

        return saveAction;
    }

    private ActionAdapter getSaveAsKmlAction() {
        if (saveAsKMLAction == null) {
            saveAsKMLAction = new ActionAdapter() {

                public void doAction() {
                    /*
                     * Show dialog for selecting a directory
                     */
                    int option = chooser.showSaveDialog(QueryResultsFrame.this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        SaveQueryResultsAsKMLAction action = new SaveQueryResultsAsKMLAction(chooser.getSelectedFile(), 
                                queryResults, query, databaseUrl);
                        action.doAction();
                    }
                }
                private final JFileChooser chooser = new JFileChooser();
            };
        }

        return saveAsKMLAction;
    }
    
    private JButton getSaveAsKMLButton() {
        if (saveAsKMLButton == null) {
            saveAsKMLButton = new JButton();
            saveAsKMLButton.setText("Save as KML");
            saveAsKMLButton.addActionListener(getSaveAsKmlAction());
        }
        return saveAsKMLButton;
    }

    /**
     * This method initializes jButton
     * @return  javax.swing.JButton
     * @uml.property  name="saveButton"
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save");
            saveButton.addActionListener(getSaveAction());
        }

        return saveButton;
    }

    /**
     * <p><!-- Method description --></p>
     * @return
     * @uml.property  name="saveImagesAction"
     */
    private ActionAdapter getSaveImagesAction() {
        if (saveImagesAction == null) {
            saveImagesAction = new SaveImagesFromQueryResultsAction();
            ((SaveImagesFromQueryResultsAction) saveImagesAction).setQueryResultsFrame(this);
        }

        return saveImagesAction;
    }

    /**
     * This method initializes jButton
     * @return  javax.swing.JButton
     * @uml.property  name="saveImagesButton"
     */
    private JButton getSaveImagesButton() {
        if (saveImagesButton == null) {
            saveImagesButton = new JButton();
            saveImagesButton.setText("Save Images");
            saveImagesButton.addActionListener(getSaveImagesAction());
        }

        return saveImagesButton;
    }

    /**
     * This method initializes jMenuItem
     * @return  javax.swing.JMenuItem
     * @uml.property  name="saveImagesMenuItem"
     */
    private JMenuItem getSaveImagesMenuItem() {
        if (saveImagesMenuItem == null) {
            saveImagesMenuItem = new JMenuItem();
        }

        return saveImagesMenuItem;
    }

    /**
     * This method initializes jMenuItem
     * @return  javax.swing.JMenuItem
     * @uml.property  name="saveMenuItem"
     */
    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem();
            saveMenuItem.addActionListener(getSaveAction());
        }

        return saveMenuItem;
    }

    /**
     * This method initializes jTabbedPane
     * @return  javax.swing.JTabbedPane
     * @uml.property  name="tabbedPane"
     */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Data", getDataPanel());

            /*
             * Only display the query if one is available.
             */
            if (query != null) {
                tabbedPane.addTab("Query", getQueryPanel());
            }
        }

        return tabbedPane;
    }

    /**
     * This method initializes jToolBar
     * @return  javax.swing.JToolBar
     * @uml.property  name="toolBar"
     */
    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getSaveButton());
            toolBar.add(getSaveAsKMLButton());
            toolBar.add(getSaveImagesButton());
        }

        return toolBar;
    }
    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setJMenuBar(getAMenuBar());
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("VARS Query Results (" + queryResults.getRows() + " rows)");
    }
}