
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



package vars.knowledgebase.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.SearchableTreePanel;
import org.mbari.util.Dispatcher;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedEvent;
import org.mbari.vars.knowledgebase.model.dao.CacheClearedListener;
import org.mbari.vars.knowledgebase.model.dao.IKnowledgeBaseCache;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.knowledgebase.ui.actions.LoginAction;
import org.mbari.vars.model.UserAccount;
import org.mbari.vars.ui.ConceptTree;
import org.mbari.vars.ui.ConceptTree.ConceptPopupMenu;
import org.mbari.vars.ui.ModifyUserDialog;
import org.mbari.vars.ui.SearchableConceptTreePanel;
import org.mbari.vars.ui.TreeConcept;
import org.mbari.vars.util.AppFrameDispatcher;
import vars.knowledgebase.IConcept;

/**
 * <p><!-- Class description --></p>
 *
 * @version    $Id: KnowledgebaseFrame.java 295 2006-07-06 23:47:31Z hohonuuli $
 * @author     <a href="http://www.mbari.org">Monterey Bay Aquarium Research Institute</a>
 */
public class KnowledgebaseFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = -6933314608809904289L;
    private static final Logger log = LoggerFactory.getLogger(KnowledgebaseFrame.class);
    private static final int MAX_SEARCH_LOOP_COUNT = 1000;
    /**
	 * @uml.property  name="splitPane"
	 * @uml.associationEnd  
	 */
    private JSplitPane splitPane = null;
    /**
	 * @uml.property  name="treePanel"
	 * @uml.associationEnd  
	 */
    private SearchableTreePanel treePanel = null;
    /**
	 * @uml.property  name="tabbedPane"
	 * @uml.associationEnd  
	 */
    private JTabbedPane tabbedPane = null;
    /**
	 * @uml.property  name="namesEditorPanel"
	 * @uml.associationEnd  
	 */
    private NamesEditorPanel namesEditorPanel = null;
    /**
	 * @uml.property  name="mediaEditorPanel"
	 * @uml.associationEnd  
	 */
    private MediaEditorPanel mediaEditorPanel;
    /**
	 * @uml.property  name="linkTemplateEditorPanel"
	 * @uml.associationEnd  
	 */
    private LinkTemplateEditorPanel linkTemplateEditorPanel = null;
    private LinkRealizationEditorPanel linkRealizationEditorPanel = null;
    /**
	 * @uml.property  name="lockAction"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="this$0:org.mbari.vars.knowledgebase.ui.KnowledgebaseFrame$LockAction"
	 */
    private final LockAction lockAction = new LockAction();
    /**
	 * @uml.property  name="historyEditorPanel"
	 * @uml.associationEnd  
	 */
    private HistoryEditorPanel historyEditorPanel = null;
    /**
	 * @uml.property  name="loginAction"
	 * @uml.associationEnd  
	 */
    private LoginAction loginAction;
    /**
	 * @uml.property  name="treeSelectionListener"
	 * @uml.associationEnd  
	 */
    private TreeSelectionListener treeSelectionListener;
	/**
	 * @uml.property  name="myMenuBar"
	 * @uml.associationEnd  
	 */
	private JMenuBar myMenuBar = null;
	/**
	 * @uml.property  name="rightPanel"
	 * @uml.associationEnd  
	 */
	private JPanel rightPanel = null;
	/**
	 * @uml.property  name="conceptPanel"
	 * @uml.associationEnd  
	 */
	private ConceptPanel conceptPanel = null;


    /**
     * Constructs ...
     *
     */
    public KnowledgebaseFrame() {
        initialize();
        initUserAccountDispatcher();
        KnowledgeBaseCache.getInstance().addCacheClearedListener(new ACacheClearedListener());
    }

    /**
     * Initializes the UserAccount Dispatcher. This watches for changes in
     * the user logged in and toggles the editors states appropriately. It uses
     * the LockAction to toggle the editors.
     */
    private void initUserAccountDispatcher() {
        Dispatcher dispatcher = Dispatcher.getDispatcher(UserAccount.class);

        dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                UserAccount userAccount = (UserAccount) evt.getNewValue();
                boolean locked = ((userAccount == null) || (userAccount.isReadOnly()));

                if (log.isDebugEnabled()) {
                    log.debug("Using UserAccount '" + userAccount + "'; setting locked to " + locked);
                }

                lockAction.setLocked(locked);
            }

        });
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    void initialize() {
        this.setSize(new Dimension(459, 367));
        this.setJMenuBar(getMyMenuBar());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getSplitPane());
        
        ResourceBundle bundle =  ResourceBundle.getBundle("vars-knowledgebase");
        final String title = bundle.getString("frame.title");
        this.setTitle(title);
    }

    /**
     * This call clears the Knowledgebase cache, refreshes the Concept tree
     * and opens the tree to the given node.
     * @param name Representing the node that we want to open to.
     */
    public void refreshTreeAndOpenNode(String name) {

        /**
         * Refresh node
         */
        Concept concept = null;
        try {
            KnowledgeBaseCache.getInstance().clear();
            concept = KnowledgeBaseCache.getInstance().findConceptByName(name);
        }
        catch (DAOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to clear cache", e);
            }

            AppFrameDispatcher.showErrorDialog("Failed to clear" + " knowledgebase cache. Please close this " +
                                               "application");
        }

        

        final SearchableTreePanel tp = getTreePanel();
        if (tp != null) {
            final Dispatcher dispatcher = KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT;
            int count = 0;

            /*
             * We check in this loop that we are indeed at the node we wanted.
             */
            while (count < MAX_SEARCH_LOOP_COUNT) {
                tp.goToMatchingNode(name, false);

                final Concept selectedConcept = (Concept) dispatcher.getValueObject();

                if ((selectedConcept != null) && (selectedConcept.getPrimaryConceptNameAsString().equals(concept.getPrimaryConceptNameAsString()))) {
                    break;
                }

                count++;
            }

            if (count >= MAX_SEARCH_LOOP_COUNT) {
                AppFrameDispatcher.showErrorDialog("Failed to reopen '" + name + "'");
            }
        }
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="historyEditorPanel"
	 */
    private HistoryEditorPanel getHistoryEditorPanel() {
        if (historyEditorPanel == null) {
            historyEditorPanel = new HistoryEditorPanel();
            setupEditorPanel(historyEditorPanel);

            /*
             * The history panel will remain locked if the user credentials
             * are not verified. We register with the UserAccount Dispatcher
             * to keep track of credentials
             */
            Dispatcher.getDispatcher(UserAccount.class).addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    historyEditorPanel.setUserAccount((UserAccount) evt.getNewValue());
                }

            });
        }

        return historyEditorPanel;
    }
    
    private LinkRealizationEditorPanel getLinkRealizationEditorPanel() {
        if (linkRealizationEditorPanel == null) {
            linkRealizationEditorPanel = new LinkRealizationEditorPanel();
            setupEditorPanel(linkRealizationEditorPanel);
        }

        return linkRealizationEditorPanel;
    }

    /**
	 * The default LoginAction jsut logins in. For this application we want it to toggle the state of the lock. i.e If the user is already logged in we want it to log out.
	 * @return  A subclass of LoginAction
	 * @uml.property  name="loginAction"
	 */
    private LoginAction getLoginAction() {
        if (loginAction == null) {
            loginAction = new LoginAction() {

                private static final long serialVersionUID = -471621133459092015L;

                public void doAction() {
                    if (lockAction.isLocked()) {
                        super.doAction();
                    }
                    else {
                        Dispatcher.getDispatcher(UserAccount.class).setValueObject(null);
                    }
                }
            };
        }

        return loginAction;
    }

    /**
	 * This method initializes namesEditorPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="namesEditorPanel"
	 */
    private NamesEditorPanel getNamesEditorPanel() {
        if (namesEditorPanel == null) {
            namesEditorPanel = new NamesEditorPanel();
            setupEditorPanel(namesEditorPanel);
        }

        return namesEditorPanel;
    }
    
    /**
	 * @return  the mediaEditorPanel
	 * @uml.property  name="mediaEditorPanel"
	 */
    private MediaEditorPanel getMediaEditorPanel() {
        if (mediaEditorPanel == null) {
            mediaEditorPanel = new MediaEditorPanel();
            setupEditorPanel(mediaEditorPanel);
        }
        return mediaEditorPanel;
    }
    
    /**
	 * @return  the linkTemplateEditorPanel
	 * @uml.property  name="linkTemplateEditorPanel"
	 */
    private LinkTemplateEditorPanel getLinkTemplateEditorPanel() {
        if (linkTemplateEditorPanel == null) {
            linkTemplateEditorPanel = new LinkTemplateEditorPanel();
            setupEditorPanel(linkTemplateEditorPanel);
        }
        return linkTemplateEditorPanel;
    }
    
    private void setupEditorPanel(final EditorPanel editorPanel) {
    	
    	final Dispatcher conceptDispatcher = KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT;
    	
        /*
         * Add a listener so that this panel is updated whenever a new
         * concept is selected in the Knowledgebasetree.
         */
        conceptDispatcher.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                final Concept concept = (Concept) evt.getNewValue();
                editorPanel.setConcept(concept);
            }

        });
        
        /*
         * Register the panel with the lockAction. This will signal the
         * editor that the user is logged in.
         */
        lockAction.addEditor(editorPanel);

        
    }

    /**
	 * This method initializes splitPane
	 * @return  javax.swing.JSplitPane
	 * @uml.property  name="splitPane"
	 */
    private JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane();

            splitPane.setLeftComponent(getTreePanel());
            splitPane.setRightComponent(getRightPanel());
        }

        return splitPane;
    }
    
    /**
	 * @return  the rightPanel
	 * @uml.property  name="rightPanel"
	 */
    public JPanel getRightPanel() {
    	if (rightPanel == null) {
    		rightPanel = new JPanel();
    		rightPanel.setLayout(new BorderLayout());
    		rightPanel.add(getConceptPanel(), BorderLayout.NORTH);
    		rightPanel.add(getTabbedPane(), BorderLayout.CENTER);
    	}
    	return rightPanel;
    	
    }
    
    /**
	 * @return  the conceptPanel
	 * @uml.property  name="conceptPanel"
	 */
    private ConceptPanel getConceptPanel() {
    	if (conceptPanel == null) {
    		conceptPanel = new ConceptPanel();
    		final Dispatcher dispatcher = KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT;
    		dispatcher.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					final Concept concept = (Concept) dispatcher.getValueObject();
					conceptPanel.setConcept(concept);
				}
    		});
            /*
             * Allow the user to login when they click on the lock button
             */
            final JButton lockButton = conceptPanel.getLockButton();
            lockButton.addActionListener(getLoginAction());
            
            /*
             * When an account is set the tooltip text shoudl tell us what user
             */
            final Dispatcher userAccountDispatcher = KnowledgebaseApp.DISPATCHER_USERACCOUNT;
            userAccountDispatcher.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    final UserAccount userAccount = (UserAccount) evt.getNewValue();
                    conceptPanel.setUserAccount(userAccount);
                }
                
            });
    	}
    	return conceptPanel;
    }

    /**
	 * This method initializes tabbedPane
	 * @return  javax.swing.JTabbedPane
	 * @uml.property  name="tabbedPane"
	 */
    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();

            tabbedPane.addTab("Names", null, getNamesEditorPanel(), null);
            tabbedPane.addTab("Templates", null, getLinkTemplateEditorPanel(), null);
            tabbedPane.addTab("Properties", null, getLinkRealizationEditorPanel(), null);
            tabbedPane.addTab("Media", null, getMediaEditorPanel(), null);
            tabbedPane.addTab("History", null, getHistoryEditorPanel(), null);
            
        }

        return tabbedPane;
    }

    /**
	 * This method initializes treePanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="treePanel"
	 */
    protected SearchableTreePanel getTreePanel() {
        if (treePanel == null) {
            treePanel = new SearchableConceptTreePanel();

            /*
             * Fetch the root concept. We need this to intialize the ConceptTree
             */
            IKnowledgeBaseCache cache = KnowledgeBaseCache.getInstance();
            IConcept concept = null;

            try {
                concept = cache.findRootConcept();
            }
            catch (DAOException e) {
                concept = new Concept(new ConceptName("ERROR!!", ConceptName.NAMETYPE_PRIMARY), null);

                AppFrameDispatcher.showErrorDialog("Failed to load knowledgebase.");
                log.error("Failed to load knowledgebase", e);
            }

            /*
             * Build the conceptTree and add a listener. This listener should
             * pass the concept to a Dispatcher so other objects will get
             * notified when the concept changes.
             */
            final ConceptTree conceptTree = new ConceptTree(concept);
            conceptTree.addTreeSelectionListener(getTreeSelectionListener());
            lockAction.addEditor(conceptTree);
            treePanel.setJTree(conceptTree);
            JPopupMenu popupMenu = conceptTree.getPopupMenu();
            popupMenu.addSeparator();
            JMenuItem menuIteum = new JMenuItem("Refresh", 'r');
            menuIteum.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTreeAndOpenNode(conceptTree.getSelectedConcept().getPrimaryConceptNameAsString());
                }
            });
            popupMenu.add(menuIteum);

            /*
             * Register the tree with a dispatcher so that other components can
             * attempt to fetch it.
             */
            KnowledgebaseApp.DISPATCHER_TREE.setValueObject(treePanel);
        }

        return treePanel;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="treeSelectionListener"
	 */
    private TreeSelectionListener getTreeSelectionListener() {
        if (treeSelectionListener == null) {
            treeSelectionListener = new ATreeSelectionListener();
        }

        return treeSelectionListener;
    }


    /**
     * Listens for when the knowledgebase cache is cleared. When it's cleared
     * this rebuilds the treepanel.
     * @author brian
     *
     */
    private class ACacheClearedListener implements CacheClearedListener {
        

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param evt
         */
        public void afterClear(CacheClearedEvent evt) {
            // Do nothing
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param evt
         */
        public void beforeClear(CacheClearedEvent evt) {
            KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT.setValueObject(null);
        }
    }

    /**
     * Listens to the ConceptTree. When a concept is selected int the tree this
     * listener sets the concept in a Dispatcher so all components can listen
     * for the currently selected concept.
     */
    private class ATreeSelectionListener implements TreeSelectionListener {

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param e
         */
        public void valueChanged(TreeSelectionEvent e) {
            TreePath selectionPath = e.getNewLeadSelectionPath();
            IConcept concept = null;

            if (selectionPath != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof TreeConcept) {
                    TreeConcept treeConcept = (TreeConcept) userObject;
                    concept = treeConcept.getConcept();
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Selected " + concept + " in the knowledgebase tree");
            }

            Dispatcher dispatcher = KnowledgebaseApp.DISPATCHER_SELECTED_CONCEPT;
            dispatcher.setValueObject(concept);
        }
    }

    /**
	 * This action deals with toggling the locks on the editors. TO unlock, the users credentials will need to be validated.
	 * @author  brian
	 */
    private class LockAction {

        private boolean locked = true;
        private final Set editors = new HashSet();

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param lockableEditor
         */
        void addEditor(ILockableEditor lockableEditor) {
            lockableEditor.setLocked(isLocked());
            editors.add(lockableEditor);
        }

        /**
         * <p><!-- Method description --></p>
         *
         *
         * @param lockableEditor
         */
        void removeEditor(ILockableEditor lockableEditor) {
            editors.remove(lockableEditor);
        }

        /**
		 * @return  Returns the locked.
		 * @uml.property  name="locked"
		 */
        boolean isLocked() {
            return locked;
        }

        /**
		 * Lock all the editors
		 * @param locked  The locked to set.
		 * @uml.property  name="locked"
		 */
        void setLocked(boolean locked) {
            if (this.locked != locked) {
                this.locked = locked;

                for (Iterator i = editors.iterator(); i.hasNext(); ) {
                    ILockableEditor editor = (ILockableEditor) i.next();

                    editor.setLocked(locked);
                }
            }
        }
    }

	/**
	 * This method initializes myMenuBar	
	 * @return  javax.swing.JMenuBar
	 * @uml.property  name="myMenuBar"
	 */
	private JMenuBar getMyMenuBar() {
		if (myMenuBar == null) {
			myMenuBar = new JMenuBar();
			
			final JMenu menuEdit = new JMenu("Edit");
            menuEdit.setMnemonic('E');
            myMenuBar.add(menuEdit);

            // create EditUser menu item
            final JMenuItem editUser = new JMenuItem(new EditUserAccountAction());
            menuEdit.add(editUser);
            editUser.setMnemonic('U');
            editUser.setText("Edit User Accounts");
            editUser.setEnabled(false);
            
            // create EditConcept menu item
            final JMenuItem editConcept = new JMenuItem(new ActionAdapter() {
				private static final long serialVersionUID = 1L;

				public void doAction() {
					final ConceptPopupMenu popupMenu = ((ConceptTree) getTreePanel().getJTree()).getPopupMenu();
					popupMenu.triggerEditAction();
				}
            });
            menuEdit.add(editConcept);
            editConcept.setMnemonic('C');
            editConcept.setText("Edit Selected Concept");
            editConcept.setEnabled(false);
            
            final Dispatcher dispatcher = KnowledgebaseApp.DISPATCHER_USERACCOUNT;
            dispatcher.addPropertyChangeListener(new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					final UserAccount userAccount = (UserAccount) evt.getNewValue();
					final boolean enable = userAccount != null && !userAccount.isReadOnly();
					editUser.setEnabled(enable);
					editConcept.setEnabled(enable);
				}
            	
            });
		}
		return myMenuBar;
	}
	
	/**
	 * @author  brian
	 */
	private class EditUserAccountAction extends ActionAdapter {

		private static final long serialVersionUID = 1L;
		private final ModifyUserDialog dialog = new ModifyUserDialog(AppFrameDispatcher.getFrame());
		
		public void doAction() {
			
			dialog.setUserAccount((UserAccount) KnowledgebaseApp.DISPATCHER_USERACCOUNT.getValueObject());
			dialog.setVisible(true);
		}
		
	}
	
}    // @jve:decl-index=0:visual-constraint="10,10"

