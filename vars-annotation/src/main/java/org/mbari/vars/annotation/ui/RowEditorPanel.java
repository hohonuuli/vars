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


/*
Created on Mar 11, 2004
 */
package org.mbari.vars.annotation.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mbari.vars.annotation.ui.dispatchers.ObservationTableDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PersonDispatcher;
import org.mbari.vars.annotation.ui.dispatchers.PredefinedDispatcher;
import org.mbari.vars.annotation.ui.table.AssociationListEditorPanel;
import vars.knowledgebase.ConceptName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.knowledgebase.Concept;

/**
 * <p>THis panel is explcitly desinged for editing Observations in the
 * ObservationTable.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @created  May 3, 2004
 */
public class RowEditorPanel extends JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final Set shifttab = new HashSet(1);

    // The keys which will be listened for in the JTextArea for a change in
    // focus
    private static final Set tab = new HashSet(1);

    static {
        tab.add(KeyStroke.getKeyStroke("TAB"));
    }
    static {
        shifttab.add(KeyStroke.getKeyStroke("shift TAB"));
    }

    /**
     *     The actions for changing the focus behavior of a JTextArea
     *     @uml.property  name="nextFocusAction"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    protected Action nextFocusAction = new AbstractAction("Move Focus Forwards") {

        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocus();
        }
    };

    /**
     *     @uml.property  name="prevFocusAction"
     *     @uml.associationEnd  multiplicity="(1 1)"
     */
    protected Action prevFocusAction = new AbstractAction("Move Focus Backwards") {


        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocusBackward();
        }
    };

    private JComboBox conceptComboBox;

    private JPanel jPanel;


    private AssociationListEditorPanel listPanel;

    /**
     *     A collection of names that we should not allow notes to be added to.
     */
    private Collection notableConceptNames;


    private JTextArea notesArea;

    private final ObservationTable observationTable;

    private Observation selectedObservation;

    /**
     * Constructor for the RowEditorPanel object
     */
    public RowEditorPanel() {
        this(ObservationTableDispatcher.getInstance().getObservationTable());
    }

    /**
     * Constructor for the RowEditorPanel object
     *
     * @param  observationTable Description of the Parameter
     */
    public RowEditorPanel(final ObservationTable observationTable) {
        super();
        this.observationTable = observationTable;
        observationTable.setFocusable(false);
        initialize();

        /**
         * Listen for changes in the observation
         */
        PredefinedDispatcher.OBSERVATION.getDispatcher().addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                final Observation newObs = (Observation) evt.getNewValue();
                setObservation(newObs);
            }

        });

        /*
         * This allows the notable concept names to be refreshed is the
         * KnowledgebaseCache is cleared
         */
        KnowledgeBaseCache.getInstance().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(final CacheClearedEvent evt) {

                // DO nothing
            }

            public void beforeClear(final CacheClearedEvent evt) {
                notableConceptNames = null;
            }

        });


    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="conceptComboBox"
     */
    private JComboBox getConceptComboBox() {
        if (conceptComboBox == null) {
            conceptComboBox = new AllConceptNamesComboBox();
            conceptComboBox.setPreferredSize(new Dimension(250, 23));

            conceptComboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {

                    /*
                     *  If a new item is selected this updates the conceptname in the observation and
                     *  changes the observers name.
                     */
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final String conceptName = (String) conceptComboBox.getSelectedItem();
                        getNotesArea().setEditable(!getNotableConceptNames().contains(conceptName));

                        if ((selectedObservation != null) && !selectedObservation.getConceptName().equals(conceptName)) {
                            selectedObservation.setConceptName(conceptName);
                            final String person = PersonDispatcher.getInstance().getPerson();
                            selectedObservation.setObserver(person);
                            selectedObservation.setObservationDate(new Date());

                            if (log.isDebugEnabled()) {
                                log.debug("Observation changed to " + conceptName + " by " + person);
                            }
                        }
                    }

                    /*
                     *  When the conceptname is changed in the combobox. We want to
                     *  close the AssociationEditorPanel. If we don't the contents
                     *  of the AssociationEditorPanel do not match the selected
                     *  Concept.
                     */
                    final AssociationListEditorPanel p = getListPanel();
                    if (p.isEditingAssociation()) {
                        p.getAssociationEditorPanel().resetDisplay();
                        p.setEditingAssociation(false);
                    }
                }

            });

            /*
             *  Updates the conceptName to the primary name when [ENTER] is typed
             */
            final JTextField editor = (JTextField) conceptComboBox.getEditor().getEditorComponent();
            editor.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(final KeyEvent ke) {
                    final char c = ke.getKeyChar();
                    if (c == KeyEvent.VK_ENTER) {

                        /*
                         * When [ENTER] is pressed in the combobox, we want to:
                         *  - close the popup window
                         *  - Find the primary name of the concept selected in the combobox
                         *  - Set the combo box to the primary name if it's not already done.
                         *
                         * When a new item is selected in the comboBox, for example, when it's validated,
                         * it triggers an ItemEvent which in turn updates the Observation's concept name.
                         */
                        log.debug("ENTER Pressed in Editor");
                        IConcept concept;
                        String primaryName;
                        final String selectedName = (String) conceptComboBox.getSelectedItem();
                        conceptComboBox.setPopupVisible(false);
                        conceptComboBox.setEnabled(false);

                        try {
                            concept = KnowledgeBaseCache.getInstance().findConceptByName(selectedName);
                            primaryName = concept.getPrimaryConceptNameAsString();
                        }
                        catch (final DAOException e1) {
                            log.error("Failed to lookup '" + conceptComboBox.getSelectedItem() + "' from database", e1);
                            AppFrameDispatcher.showErrorDialog(
                                "Failed to validate the concept-name. There may be a problem with the database connection");
                            primaryName = IConceptName.NAME_DEFAULT;
                        }

                        if (!selectedName.equals(primaryName)) {
                            conceptComboBox.setSelectedItem(primaryName);
                        }

                        conceptComboBox.setEnabled(true);
                        conceptComboBox.requestFocus();
                    }
                }

            });
        }

        return conceptComboBox;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="jPanel"
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout(4, 4));
            jPanel.add(getConceptComboBox(), BorderLayout.WEST);
            final JScrollPane scrollPane = new JScrollPane(getNotesArea());

            // scrollPane.setPreferredSize(new Dimension(5, 20));
            jPanel.add(scrollPane, BorderLayout.CENTER);
        }

        return jPanel;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="listPanel"
     */
    private AssociationListEditorPanel getListPanel() {
        if (listPanel == null) {
            listPanel = new AssociationListEditorPanel();
            listPanel.setMinimumSize(new Dimension(200, 100));
        }

        return listPanel;
    }

    /**
     *     <p>The video lab has requested that top level concepts should not be able to have notes added to them. This is because annotators on the ship frequently use 'object' or 'physical-object' as the concept name but then but the detail of what the object is in the notes. They would like to stop this practice and force users to choose a concept.</p> <p>This method gets the root concept name and the names of its child concepts (and only the root concepts child concepts) and adds them to a collection. </p>
     *     @return   A collection of strings representing the root conceptname and it's  child names.
     *     @uml.property  name="notableConceptNames"
     */
    private Collection getNotableConceptNames() {
        if (notableConceptNames == null) {


            notableConceptNames = new HashSet();

            try {
                final IConcept rootConcept = KnowledgeBaseCache.getInstance().findRootConcept();
                notableConceptNames.add(rootConcept.getPrimaryConceptNameAsString());
                final Collection chillin = rootConcept.getChildConceptColl();
                for (final Iterator i = chillin.iterator(); i.hasNext(); ) {
                    final IConcept child = (IConcept) i.next();
                    notableConceptNames.add(child.getPrimaryConceptNameAsString());
                }
            }
            catch (final DAOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Failed to get root concept from database", e);
                }
            }
        }

        return notableConceptNames;
    }

    /**
     *     <p><!-- Method description --></p>
     *     @return
     *     @uml.property  name="notesArea"
     */
    private JTextArea getNotesArea() {
        if (notesArea == null) {
            notesArea = new JTextArea(1, 1);
            notesArea.setToolTipText("Notes");
            notesArea.getInputMap().put(KeyStroke.getKeyStroke("tab"), nextFocusAction.getValue(Action.NAME));
            notesArea.getInputMap().put(KeyStroke.getKeyStroke("shifttab"), prevFocusAction.getValue(Action.NAME));
            notesArea.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
            notesArea.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
            notesArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
            notesArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shifttab);
            notesArea.setWrapStyleWord(true);
            notesArea.setLineWrap(true);

            notesArea.getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(final DocumentEvent e) {
                    update();
                }

                public void insertUpdate(final DocumentEvent e) {
                    update();
                }

                public void removeUpdate(final DocumentEvent e) {
                    update();
                }

                void update() {
                    if (selectedObservation != null) {
                        selectedObservation.setNotes(getNotesArea().getText());
                    }
                }

            });

        }

        return notesArea;
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void initialize() {
        setLayout(new BorderLayout());
        add(getJPanel(), BorderLayout.NORTH);
        add(getListPanel(), BorderLayout.CENTER);
        setEnabled(false);

        //      Add actions
        mapKeys();
        setFocusCycleRoot(true);

        //FocusTraversalPolicy policy = getFocusTraversalPolicy();
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {

            /**
             *
             */
            private static final long serialVersionUID = -3923850138071244157L;

            // TODO Warning, when a component should not be focused, I
            // recursively
            // call getComponentAfter, if all components should not be focused,
            // this
            // could lead to an infinite loop.
            @Override
            public Component getComponentAfter(final Container focusCycleRoot, final Component aComponent) {
                Component retval = super.getComponentAfter(focusCycleRoot, aComponent);
                if (retval == notesArea) {
                    retval = this.getComponentAfter(focusCycleRoot, retval);
                }

                if (retval instanceof JList) {
                    if (listPanel.getObservation().getAssociationList().size() == 0) {
                        retval = this.getComponentAfter(focusCycleRoot, retval);
                    }
                }

                return retval;
            }
        });
    }

    /**
     * Setup key-mappings for the panel. This currently consists of a mapping
     * for moving which observation is selected up and down in the table. Up
     * and down wraps at the top and bottom of the table.
     */
    private void mapKeys() {
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK),
                         "up-table");
        this.getActionMap().put("down-table", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = 1081157310975958639L;

            public void actionPerformed(final ActionEvent e) {

                final int numRows = observationTable.getRowCount();
                final int currentRow = observationTable.getSelectionModel().getLeadSelectionIndex();
                final int nextRow = (currentRow + 1 >= numRows) ? 0 : currentRow + 1;
                observationTable.getSelectionModel().setSelectionInterval(nextRow, nextRow);
                observationTable.scrollToVisible(nextRow, 0);
            }

        });
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                InputEvent.CTRL_DOWN_MASK), "down-table");
        this.getActionMap().put("up-table", new AbstractAction() {

            /**
             *
             */
            private static final long serialVersionUID = -5121776240964061423L;

            public void actionPerformed(final ActionEvent e) {

                final int numRows = observationTable.getRowCount();
                final int currentRow = observationTable.getSelectionModel().getLeadSelectionIndex();
                final int nextRow = (currentRow - 1 < 0) ? numRows - 1 : currentRow - 1;
                observationTable.getSelectionModel().setSelectionInterval(nextRow, nextRow);
                observationTable.scrollToVisible(nextRow, 0);
            }

        });
    }

    /**
     * Set this component to an enabled/disabled state.
     *
     * @param  shouldEnable The new enabled value
     */
    @Override
    public void setEnabled(final boolean shouldEnable) {
        super.setEnabled(shouldEnable);
        getNotesArea().setEnabled(shouldEnable);
        getListPanel().setEnabled(shouldEnable);
        getConceptComboBox().setEnabled(shouldEnable);
    }

    private void setObservation(final Observation observation) {

        this.selectedObservation = observation;
        final boolean isNull = (observation == null);
        setEnabled(!isNull);
        getListPanel().setObservation(observation);

        if (isNull) {
            try {
                getConceptComboBox().setSelectedItem(
                    KnowledgeBaseCache.getInstance().findRootConcept().getPrimaryConceptNameAsString());
            }
            catch (final DAOException e) {
                getConceptComboBox().setSelectedIndex(0);

                if (log.isErrorEnabled()) {
                    log.error("Failed to find root concept in the KnowledgeBaseCache", e);
                }
            }

            getNotesArea().setText("");
            getNotesArea().setEnabled(false);
        }
        else {
            getConceptComboBox().setSelectedItem(observation.getConceptName());
            getNotesArea().setText(observation.getNotes());
            getNotesArea().setEnabled(!getNotableConceptNames().contains(observation.getConceptName()));
        }

        getConceptComboBox().requestFocus();
    }
}
