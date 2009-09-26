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


import com.google.inject.Injector;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.ListListModel;
import org.mbari.swing.WaitIndicator;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.util.Dispatcher;
import org.mbari.util.ImmutableList;
import foxtrot.Worker;
import foxtrot.Job;
import org.bushe.swing.event.EventBus;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryDAO;

//~--- classes ----------------------------------------------------------------

/**
 * <p></p>
 *
 * @author Brian Schlining
 * @version $Id: SearchPanel.java 431 2006-11-21 00:07:43Z hohonuuli $
 */
public class SearchPanel extends JPanel {

    private static final long serialVersionUID = 4506941047207763908L;
    private static final Logger log = LoggerFactory.getLogger(SearchPanel.class);
    private static final int RESPONSE_DELAY = 750;

    //~--- fields -------------------------------------------------------------

    /**
	 * @uml.property  name="cbAllAssociations"
	 * @uml.associationEnd  
	 */
    private JCheckBox cbAllAssociations;
    /**
	 * @uml.property  name="cbAllInterpretations"
	 * @uml.associationEnd  
	 */
    private JCheckBox cbAllInterpretations;
    
    private JCheckBox cbHierarchy;
    
    private JCheckBox cbPhylogeny;
    
    private JCheckBox cbFullPhylogeny;
    /**
	 * @uml.property  name="clearAction"
	 * @uml.associationEnd  
	 */
    private ActionAdapter clearAction;
    /**
	 * @uml.property  name="delayTimer"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private final Timer delayTimer;
    /**
	 * @uml.property  name="topPanel"
	 * @uml.associationEnd  
	 */
    private JPanel topPanel = null;
    /**
	 * @uml.property  name="scrollPane"
	 * @uml.associationEnd  
	 */
    private JScrollPane scrollPane = null;
    /**
	 * @uml.property  name="removeButton"
	 * @uml.associationEnd  
	 */
    private JButton removeButton = null;
    /**
	 * @uml.property  name="middlePanel"
	 * @uml.associationEnd  
	 */
    private JPanel middlePanel = null;
    /**
	 * @uml.property  name="mainPanel"
	 * @uml.associationEnd  
	 */
    private JPanel mainPanel = null;    // @jve:decl-index=0:visual-constraint="637,128"
    /**
	 * @uml.property  name="conceptNameSelectionPanel"
	 * @uml.associationEnd  
	 */
    private ConceptNameSelectionPanel conceptNameSelectionPanel = null;
    /**
	 * @uml.property  name="conceptConstraintsList"
	 * @uml.associationEnd  
	 */
    private JList conceptConstraintsList = null;
    /**
	 * @uml.property  name="clearButton"
	 * @uml.associationEnd  
	 */
    private JButton clearButton = null;
    /**
	 * @uml.property  name="bottomPanel"
	 * @uml.associationEnd  
	 */
    private JPanel bottomPanel = null;
    /**
	 * @uml.property  name="associationSelectionPanel"
	 * @uml.associationEnd  
	 */
    private AssociationSelectionPanel associationSelectionPanel = null;
    /**
	 * @uml.property  name="allPanel"
	 * @uml.associationEnd  
	 */
    private JPanel allPanel = null;
    /**
	 * @uml.property  name="addButton"
	 * @uml.associationEnd  
	 */
    private JButton addButton = null;

    private final Injector injector;

    //~--- constructors -------------------------------------------------------

    /**
     *
     */
    public SearchPanel(Injector injector) {
        super();
        this.injector = injector;
        /*
         * We're adding a slight delay here so that db lookups don't try to
         * occur as a person types.
         */

        // This action occurs when the timer fires.
        ActionListener changeItemAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                updateAssociationSelectionPanel();
            }
        };

        // The timer with a delay and bound to above action.
        delayTimer = new Timer(RESPONSE_DELAY, changeItemAction);
        delayTimer.setRepeats(false);
        initialize();
    }

    //~--- get methods --------------------------------------------------------

    /**
	 * This method initializes jButton1
	 * @return  javax.swing.JButton
	 * @uml.property  name="addButton"
	 */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            final ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/vars/query/add_conceptname.png"));
            addButton.setIcon(icon);
            addButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ConceptConstraints conceptConstraints = new ConceptConstraints();
                    try {
                        conceptConstraints.setConceptNamesAsStrings(
                                getConceptNameSelectionPanel().getSelectedConceptNamesAsStrings());
                    } catch (Exception e1) {
                        // Fire eventbus message
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                        log.error("Problem accessing the database", e1);
                        return;
                    }

                    conceptConstraints.setAssociationBean(
                            getAssociationSelectionPanel().getAssociationBean());
                    ListListModel model = (ListListModel) getConceptConstraintsList().getModel();
                    model.add(conceptConstraints);
                }

            });
            
            /*
             * The clear button should only be enabled if there are items in
             * the list model.
             */
            getConceptConstraintsList().getModel().addListDataListener(
                    new ListDataListener() {

                public void intervalAdded(ListDataEvent e) {
                    update();
                }
                public void intervalRemoved(ListDataEvent e) {
                    update();
                }
                public void contentsChanged(ListDataEvent e) {
                    update();
                }
                private void update() {
                    boolean enableThrobber = getConceptConstraintsList().getModel().getSize() < 1;

                    //addButton.setEnabled(enableClearButton);
                }

            });
        }

        return addButton;
    }

    /**
	 * The all panel contains checkboxes for 'all interpretations' and 'all associations
	 * @return
	 * @uml.property  name="allPanel"
	 */
    private JPanel getAllPanel() {
        if (allPanel == null) {
            allPanel = new JPanel();
            allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));
            allPanel.add(getCbAllAssociations());
            allPanel.add(getCbAllInterpretations());
            allPanel.add(getCbHierarchy());
            allPanel.add(getCbPhylogeny());
            allPanel.add(getCbFullPhylogeny());
        }

        return allPanel;
    }

    /**
	 * This method initializes jPanel2
	 * @return  javax.swing.JPanel
	 * @uml.property  name="associationSelectionPanel"
	 */
    private AssociationSelectionPanel getAssociationSelectionPanel() {
        if (associationSelectionPanel == null) {
            associationSelectionPanel = injector.getInstance(AssociationSelectionPanel.class);
        }

        return associationSelectionPanel;
    }

    /**
	 * This method initializes jPanel3
	 * @return  javax.swing.JPanel
	 * @uml.property  name="bottomPanel"
	 */
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            bottomPanel.add(getScrollPane(), java.awt.BorderLayout.CENTER);
            bottomPanel.add(getAllPanel(), BorderLayout.SOUTH);
        }

        return bottomPanel;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="cbAllAssociations"
	 */
    protected JCheckBox getCbAllAssociations() {
        if (cbAllAssociations == null) {
            cbAllAssociations = new JCheckBox();
            cbAllAssociations.setText("Return related associations");
            cbAllAssociations.setToolTipText(
                    "Return all associations for the returned annotations");
        }

        return cbAllAssociations;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="cbAllInterpretations"
	 */
    protected JCheckBox getCbAllInterpretations() {
        if (cbAllInterpretations == null) {
            cbAllInterpretations = new JCheckBox();
            cbAllInterpretations.setText("Return concurrent observations");
            cbAllInterpretations.setToolTipText(
                    "Return all observations that occur in the same video-frames as the returned annotations");
        }

        return cbAllInterpretations;
    }
    
    protected JCheckBox getCbHierarchy() {
    	if (cbHierarchy == null) {
    		cbHierarchy = new JCheckBox();
    		cbHierarchy.setText("Return concept hierarchy");
            cbHierarchy.setToolTipText(
                    "Return the hierarchy of the concepts");
    	}
    	return cbHierarchy;
    }
    
    protected JCheckBox getCbPhylogeny() {
    	if (cbPhylogeny == null) {
    	    cbPhylogeny = new JCheckBox();
    	    cbPhylogeny.setText("Return basic organism phylogeny");
            cbPhylogeny.setToolTipText(
                    "Return the phylogeny of the organisms");
    	}
    	return cbPhylogeny;
    }
    
    protected JCheckBox getCbFullPhylogeny() {
    	if (cbFullPhylogeny == null) {
    	    cbFullPhylogeny = new JCheckBox();
    	    cbFullPhylogeny.setText("Return detailed organism phylogeny");
            cbFullPhylogeny.setToolTipText(
                    "Return the detailed phylogeny of the organisms");
    	}
    	return cbFullPhylogeny;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="clearAction"
	 */
    private ActionAdapter getClearAction() {
        if (clearAction == null) {
            clearAction = new ActionAdapter() {

                private static final long serialVersionUID = 7394517305352726379L;

                public void doAction() {
                    ListListModel model = (ListListModel) getConceptConstraintsList().getModel();
                    model.clear();
                }
            };
        }

        return clearAction;
    }

    /**
	 * <p><!-- Method description --></p>
	 * @return
	 * @uml.property  name="clearButton"
	 */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            final ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/vars/query/clear_conceptnames.png"));
            clearButton.setIcon(icon);
            clearButton.addActionListener(getClearAction());
            /*
             * The clear button is only enabled when there are actually
             * items in the list
             */
            clearButton.setEnabled(false);

            /*
             * The clear button should only be enabled if there are items in
             * the list model.
             */
            getConceptConstraintsList().getModel().addListDataListener(
                    new ListDataListener() {

                public void intervalAdded(ListDataEvent e) {
                    update();
                }
                public void intervalRemoved(ListDataEvent e) {
                    update();
                }
                public void contentsChanged(ListDataEvent e) {
                    update();
                }
                private void update() {
                    boolean enableClearButton = true;
                    if (getConceptConstraintsList().getModel().getSize() < 1) {
                        enableClearButton = false;
                    }

                    clearButton.setEnabled(enableClearButton);
                }

            });
        }

        return clearButton;
    }

    /**
     * Retrieve the conceptConstraints
     * @return An immutableCollection which contains the ConceptConstraints that the
     *  user has specified.
     */
    public Collection getConceptConstraints() {
        ListListModel model = (ListListModel) getConceptConstraintsList().getModel();
        return new ImmutableList(model.getList());
    }

    /**
	 * This method initializes a JList that contains the ConceptConstraints that are to be used for querying. The Model used is a ListListModel
	 * @return  javax.swing.JList
	 * @uml.property  name="conceptConstraintsList"
	 */
    protected JList getConceptConstraintsList() {
        if (conceptConstraintsList == null) {
            conceptConstraintsList = new JList(
                    new ListListModel(new ArrayList()));
        }

        return conceptConstraintsList;
    }

    /**
	 * This method initializes jPanel1
	 * @return  javax.swing.JPanel
	 * @uml.property  name="conceptNameSelectionPanel"
	 */
    private ConceptNameSelectionPanel getConceptNameSelectionPanel() {
        if (conceptNameSelectionPanel == null) {
            final QueryDAO queryDAO = injector.getInstance(QueryDAO.class);
            final KnowledgebaseDAOFactory daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
            conceptNameSelectionPanel = new ConceptNameSelectionPanel(queryDAO, daoFactory.newConceptDAO());
            final FancyComboBox cbConceptName = (FancyComboBox) conceptNameSelectionPanel.getCbConceptName();

            /*
             * When an item is changed we give a slight delay before looking up
             * info from teh database. This is VERY important other wise it tries
             * to look stuff up as a user types, very annoying for the user.
             */
            cbConceptName.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        delayTimer.restart();
                    }
                }

            });

            /*
             * Same here, when a check box is toggled give a brief delay before
             * updating with info from the database.
             */
            ChangeListener changeListener = new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    delayTimer.restart();
                }
            };
            conceptNameSelectionPanel.getCParent().addChangeListener(
                    changeListener);
            conceptNameSelectionPanel.getCSiblings().addChangeListener(
                    changeListener);
            conceptNameSelectionPanel.getCChildren().addChangeListener(
                    changeListener);
            conceptNameSelectionPanel.getCDescendant().addChangeListener(
                    changeListener);

            /*
             * If the user presses ENTER, update the associationSelectionPanel
             */
            cbConceptName.addEditorActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    updateAssociationSelectionPanel();
                }
            });

            /*
             * If the ComboBox loses focus update the associatioSelectionPanel
             */
            cbConceptName.addEditorFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent event) {
                    updateAssociationSelectionPanel();
                }
            });
        }

        return conceptNameSelectionPanel;
    }

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="mainPanel"
	 */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(getTopPanel(), null);
            mainPanel.add(getMiddlePanel(), null);
        }

        return mainPanel;
    }

    /**
	 * This method initializes jPanel4
	 * @return  javax.swing.JPanel
	 * @uml.property  name="middlePanel"
	 */
    private JPanel getMiddlePanel() {
        if (middlePanel == null) {
            middlePanel = new JPanel();
            middlePanel.setLayout(
                    new BoxLayout(middlePanel, BoxLayout.X_AXIS));
            middlePanel.add(getAddButton(), null);
            middlePanel.add(getRemoveButton(), null);
            middlePanel.add(getClearButton(), null);
        }

        return middlePanel;
    }

    /**
	 * This method initializes jButton
	 * @return  javax.swing.JButton
	 * @uml.property  name="removeButton"
	 */
    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new JButton();
            removeButton.setText("Remove");
            final ImageIcon icon = new ImageIcon(
                getClass().getResource("/images/vars/query/remove_conceptname.png"));
            removeButton.setIcon(icon);
            removeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ListListModel model = (ListListModel) getConceptConstraintsList().getModel();
                    int[] idx = getConceptConstraintsList().getSelectedIndices();
                    for (int i = 0; i < idx.length; i++) {
                        model.remove(idx[i]);
                    }
                }

            });
            /*
             * The remove button is only enabled if something is selected in
             * the conceptConstraintsList
             */
            removeButton.setEnabled(false);

            /*
             * If no values are selected in the list then the remove button
             * should be disabled.
             */
            getConceptConstraintsList().addListSelectionListener(
                    new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    boolean enableRemoveButton = false;
                    if (getConceptConstraintsList().getSelectedIndex() > -1) {
                        enableRemoveButton = true;
                    }

                    removeButton.setEnabled(enableRemoveButton);
                }

            });
        }

        return removeButton;
    }

    /**
	 * This method initializes jScrollPane
	 * @return  javax.swing.JScrollPane
	 * @uml.property  name="scrollPane"
	 */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();

            // scrollPane.setBounds(65, 5, 3, 3);
            scrollPane.setViewportView(getConceptConstraintsList());
            scrollPane.setHorizontalScrollBarPolicy(
                    javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        return scrollPane;
    }

    /**
	 * This method initializes jPanel
	 * @return  javax.swing.JPanel
	 * @uml.property  name="topPanel"
	 */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.add(getConceptNameSelectionPanel(), null);
            topPanel.add(getAssociationSelectionPanel(), null);
        }

        return topPanel;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(587, 357);
        this.add(getMainPanel(), java.awt.BorderLayout.NORTH);
        this.add(getBottomPanel(), java.awt.BorderLayout.CENTER);
        updateAssociationSelectionPanel();

        /*
         * Share the action Maps
         */
        ActionMap actionMap = getActionMap();
        Dispatcher dispatcher = Dispatcher.getDispatcher(QueryApp.class);
        QueryApp queryApp = (QueryApp) dispatcher.getValueObject();
        if (queryApp != null) {
            actionMap.setParent(queryApp.getActionMap());
        }

        /*
         * We add this to the ActionMap. It can be called by the resetAction in
         * the QueryFrame.
         */
        actionMap.put("RESET_SearchPanel", new ActionAdapter() {
            
            private static final long serialVersionUID = -3079307381079657219L;

            public void doAction() {
                reset();
            }
        });
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void reset() {
        getClearAction().doAction();
        getConceptNameSelectionPanel().getCbConceptName().setSelectedItem(
                ConceptConstraints.WILD_CARD_STRING);
    }


    /**
     * <p><!-- Method description --></p>
     *
     */
    private void updateAssociationSelectionPanel() {
        WaitIndicator waitIndicator = new SpinningDialWaitIndicator(this);
        log.debug("Begin updating UI with information from the VARS database");

        /*
         * Execute the db tasks off of the EventDispacthThread so that we can
         * draw the WaitIndicator.
         */
        Worker.post(new Job() {
            public Object run() {
                Collection names = null;
                try {
                    names = getConceptNameSelectionPanel().getSelectedConceptNamesAsStrings();
                } catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    log.error( "Failed to retrieve concept names from the database.", e);
                }

                getAssociationSelectionPanel().setConceptNames(names);
                return null;  
            }
        });

        waitIndicator.dispose();
        log.debug("Completed updating UI with information from the VARS database");
    }
}    // @jve:decl-index=0:visual-constraint="10,10"

