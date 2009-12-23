/*
 * @(#)SearchPanel.java   2009.11.21 at 08:14:57 PST
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

import com.google.inject.Injector;
import foxtrot.Job;
import foxtrot.Worker;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SpinningDialWaitIndicator;
import org.mbari.swing.WaitIndicator;
import org.mbari.util.Dispatcher;
import org.mbari.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.KnowledgebaseDAOFactory;
import vars.query.QueryPersistenceService;

/**
 * <p></p>
 *
 * @author Brian Schlining
 */
public class SearchPanel extends JPanel {

    private static final int RESPONSE_DELAY = 750;
    private JButton addButton = null;
    private JPanel allPanel = null;
    private AssociationSelectionPanel associationSelectionPanel = null;
    private JPanel bottomPanel = null;
    private JButton clearButton = null;
    private JList conceptConstraintsList = null;
    private ConceptNameSelectionPanel conceptNameSelectionPanel = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private JPanel mainPanel = null;
    private JPanel middlePanel = null;
    private JButton removeButton = null;
    private JScrollPane scrollPane = null;
    private JPanel topPanel = null;
    private JCheckBox cbAllAssociations;
    private JCheckBox cbAllInterpretations;
    private JCheckBox cbFullPhylogeny;
    private JCheckBox cbHierarchy;
    private JCheckBox cbPhylogeny;
    private ActionAdapter clearAction;
    private final Timer delayTimer;
    private final Injector injector;

    /**
     * Constructs ...
     *
     * @param injector
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

    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/add_conceptname.png"));
            addButton.setIcon(icon);
            addButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ConceptConstraints conceptConstraints = new ConceptConstraints();
                    try {
                        conceptConstraints.setConceptNamesAsStrings(
                            getConceptNameSelectionPanel().getSelectedConceptNamesAsStrings());
                    }
                    catch (Exception e1) {

                        // Fire eventbus message
                        EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e1);
                        log.error("Problem accessing the database", e1);
                        return;
                    }

                    conceptConstraints.setAssociationBean(getAssociationSelectionPanel().getAssociationBean());
                    ListListModel model = (ListListModel) getConceptConstraintsList().getModel();
                    model.add(conceptConstraints);
                }

            });

            /*
             * The clear button should only be enabled if there are items in
             * the list model.
             */
            getConceptConstraintsList().getModel().addListDataListener(new ListDataListener() {

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

    private AssociationSelectionPanel getAssociationSelectionPanel() {
        if (associationSelectionPanel == null) {
            associationSelectionPanel = injector.getInstance(AssociationSelectionPanel.class);
        }

        return associationSelectionPanel;
    }

    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            bottomPanel.add(getScrollPane(), java.awt.BorderLayout.CENTER);
            bottomPanel.add(getAllPanel(), BorderLayout.SOUTH);
        }

        return bottomPanel;
    }

    protected JCheckBox getCbAllAssociations() {
        if (cbAllAssociations == null) {
            cbAllAssociations = new JCheckBox();
            cbAllAssociations.setText("Return related associations");
            cbAllAssociations.setToolTipText("Return all associations for the returned annotations");
        }

        return cbAllAssociations;
    }

    protected JCheckBox getCbAllInterpretations() {
        if (cbAllInterpretations == null) {
            cbAllInterpretations = new JCheckBox();
            cbAllInterpretations.setText("Return concurrent observations");
            cbAllInterpretations.setToolTipText(
                "Return all observations that occur in the same video-frames as the returned annotations");
        }

        return cbAllInterpretations;
    }

    protected JCheckBox getCbFullPhylogeny() {
        if (cbFullPhylogeny == null) {
            cbFullPhylogeny = new JCheckBox();
            cbFullPhylogeny.setText("Return detailed organism phylogeny");
            cbFullPhylogeny.setToolTipText("Return the detailed phylogeny of the organisms");
        }

        return cbFullPhylogeny;
    }

    protected JCheckBox getCbHierarchy() {
        if (cbHierarchy == null) {
            cbHierarchy = new JCheckBox();
            cbHierarchy.setText("Return concept hierarchy");
            cbHierarchy.setToolTipText("Return the hierarchy of the concepts");
        }

        return cbHierarchy;
    }

    protected JCheckBox getCbPhylogeny() {
        if (cbPhylogeny == null) {
            cbPhylogeny = new JCheckBox();
            cbPhylogeny.setText("Return basic organism phylogeny");
            cbPhylogeny.setToolTipText("Return the phylogeny of the organisms");
        }

        return cbPhylogeny;
    }

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

    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/clear_conceptnames.png"));
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
            getConceptConstraintsList().getModel().addListDataListener(new ListDataListener() {

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
         */
    protected JList getConceptConstraintsList() {
        if (conceptConstraintsList == null) {
            conceptConstraintsList = new JList(new ListListModel(new ArrayList()));
        }

        return conceptConstraintsList;
    }

    private ConceptNameSelectionPanel getConceptNameSelectionPanel() {
        if (conceptNameSelectionPanel == null) {
            final QueryPersistenceService queryDAO = injector.getInstance(QueryPersistenceService.class);
            final KnowledgebaseDAOFactory daoFactory = injector.getInstance(KnowledgebaseDAOFactory.class);
            conceptNameSelectionPanel = new ConceptNameSelectionPanel(queryDAO, daoFactory);
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
            conceptNameSelectionPanel.getCParent().addChangeListener(changeListener);
            conceptNameSelectionPanel.getCSiblings().addChangeListener(changeListener);
            conceptNameSelectionPanel.getCChildren().addChangeListener(changeListener);
            conceptNameSelectionPanel.getCDescendant().addChangeListener(changeListener);

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

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(getTopPanel(), null);
            mainPanel.add(getMiddlePanel(), null);
        }

        return mainPanel;
    }

    private JPanel getMiddlePanel() {
        if (middlePanel == null) {
            middlePanel = new JPanel();
            middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
            middlePanel.add(getAddButton(), null);
            middlePanel.add(getRemoveButton(), null);
            middlePanel.add(getClearButton(), null);
        }

        return middlePanel;
    }

    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new JButton();
            removeButton.setText("Remove");
            final ImageIcon icon = new ImageIcon(getClass().getResource("/images/vars/query/remove_conceptname.png"));
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
            getConceptConstraintsList().addListSelectionListener(new ListSelectionListener() {

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

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();

            scrollPane.setViewportView(getConceptConstraintsList());
            scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        return scrollPane;
    }

    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.add(getConceptNameSelectionPanel(), null);
            topPanel.add(getAssociationSelectionPanel(), null);
        }

        return topPanel;
    }

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
        Dispatcher dispatcher = Dispatcher.getDispatcher(App.class);
        App queryApp = (App) dispatcher.getValueObject();
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
        getConceptNameSelectionPanel().getCbConceptName().setSelectedItem(ConceptConstraints.WILD_CARD_STRING);
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
                }
                catch (Exception e) {
                    EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                    log.error("Failed to retrieve concept names from the database.", e);
                }

                getAssociationSelectionPanel().setConceptNames(names);
                return null;
            }

        });

        waitIndicator.dispose();
        log.debug("Completed updating UI with information from the VARS database");
    }

}  

