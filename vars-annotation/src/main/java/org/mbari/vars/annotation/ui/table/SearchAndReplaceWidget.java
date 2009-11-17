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


/**
 * @created  August 18, 2004
 */
package org.mbari.vars.annotation.ui.table;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.swing.SwingWorker;
import org.mbari.text.IgnoreCaseToStringComparator;

import org.mbari.vars.annotation.ui.VideoSetViewer;
import org.mbari.vars.annotation.ui.actions.DeleteObservationAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.Observation;
import vars.annotation.Association;
import vars.annotation.ui.table.IObservationTable;
import vars.annotation.ui.table.IObservationTableModel;
import vars.shared.ui.AllConceptNamesComboBox;
import vars.shared.ui.ConceptNameComboBox;

/**
 * <p>A UI component that provides access to search and replace functions for
 * an ObservationTable</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class SearchAndReplaceWidget extends JPanel {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private JButton addAssociationButton = null;

    private JLabel associationLabel = null;

    private JLabel conceptLabel = null;

    private transient volatile boolean isBeingUpdated = false;

    private JButton removeAssociationsButton = null;

    private JButton removeObservationButton = null;


    private JButton renameConceptButton = null;

 
    private AllConceptNamesComboBox renameConceptComboBox = null;

 
    private JButton replaceAssociationButton = null;

 
    private JCheckBox searchAssociationCheckBox = null;

    private JComboBox searchAssociationComboBox = null;

 
    private JButton searchButton = null;

    private JCheckBox searchConceptCheckBox = null;

    private ConceptNameComboBox searchConceptComboBox = null;


    private JLabel searchLabel = null;

    private JPanel searchPanel = null;

    private JLabel searchStatusLabel = null;

    private final IObservationTable table;

    private final VideoSetViewer videoSetViewer;


    private final String[] waitMessage = { "Retrieving Concept Names ..." };

    /**
     * Constructor for the SearchAndReplaceWidget object
     *
     * @param  videoSetViewer Description of the Parameter
     * @param  table Description of the Parameter
     */
    public SearchAndReplaceWidget(final VideoSetViewer videoSetViewer, final IObservationTable table) {
        this.videoSetViewer = videoSetViewer;
        this.table = table;
        listenForTableChanges();
        initialize();
    }

    private JButton getAddAssociationButton() {
        if (addAssociationButton == null) {
            addAssociationButton = new JButton("Add Association");
            addAssociationButton.setToolTipText("Add a new association to all selected Observations");
            addAssociationButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final MultiAssociationEditorPanel editorPanel = new MultiAssociationEditorPanel();
                    final Point buttonPoint = addAssociationButton.getLocationOnScreen();
                    final int returnStatus = editorPanel.showDialog(videoSetViewer, "Add Association", buttonPoint);
                    if (returnStatus != MultiAssociationEditorPanel.ASSOCIATION_CREATED_OPTION) {
                        return;
                    }

                    final Association association = editorPanel.getUserGeneratedAssociation();
                    final Observation[] observations = SearchAndReplace.getSelectedObservations(table);

                    /*
                     * Flush pending database transactions in order to simplify
                     * the error handiling here.
                     */
                    DAOEventQueue.flush();

                    for (int i = 0; i < observations.length; i++) {
                        final Association newAssociation = new Association(association);
                        final IObservation observation = observations[i];
                        observation.addAssociation(newAssociation);

                        try {
                            ObservationDAO.getInstance().updateVideoArchiveSet((IDataObject) observation);
                        }
                        catch (final DAOException e1) {
                            if (log.isErrorEnabled()) {
                                log.error("Failed to update " + observation +
                                          " in the database. Rolling back changes.", e1);
                            }

                            observation.removeAssociation(newAssociation);
                        }
                    }

                    updateContentsOfAssociationComboBox();
                    redrawSelectedRows();
                }

            });
            addAssociationButton.setEnabled(false);
        }

        return addAssociationButton;
    }


    private JLabel getAssociationLabel() {
        if (associationLabel == null) {
            associationLabel = new JLabel("Association");
        }

        return associationLabel;
    }

 
    private JLabel getConceptLabel() {
        if (conceptLabel == null) {
            conceptLabel = new JLabel("Concept Name");
        }

        return conceptLabel;
    }


    private JButton getRemoveAssociationsButton() {
        if (removeAssociationsButton == null) {
            removeAssociationsButton = new JButton("Remove Associations");
            removeAssociationsButton.setToolTipText("Remove associations matching the association searched for");
            removeAssociationsButton.setEnabled(false);
            removeAssociationsButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final Association deadAssoc = (Association) getSearchAssociationComboBox().getSelectedItem();
                    if (deadAssoc == null) {
                        return;
                    }

                    final Observation[] selectedObs = SearchAndReplace.getSelectedObservations(table);
                    for (int i = 0; i < selectedObs.length; i++) {
                        final Observation obs = selectedObs[i];
                        final Collection dead = obs.removeDescendantAssociation(deadAssoc);
                        DAOEventQueue.updateVideoArchiveSet((IDataObject) obs);

                        for (final Iterator j = dead.iterator(); j.hasNext(); ) {
                            final Association a = (Association) j.next();
                            DAOEventQueue.delete(a);
                        }

                        table.redrawAll();
                        updateContentsOfAssociationComboBox();
                    }
                }

            });
        }

        return removeAssociationsButton;
    }

    private JButton getRemoveObservationButton() {
        if (removeObservationButton == null) {
            removeObservationButton = new JButton("Remove Selected Observations");
            removeObservationButton.setEnabled(false);
            removeObservationButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    // Prompt user to make sure that they really want to do the delete
                    final int count = table.getSelectedRowCount();
                    final Object[] options = { "OK", "CANCEL" };
                    final int confirm = JOptionPane.showOptionDialog(AppFrameDispatcher.getFrame(),
                                            "Do you want to delete " + count + " observation(s)?",
                                            "VARS - Confirm Delete", JOptionPane.DEFAULT_OPTION,
                                            JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                    if (confirm != 0) {
                        return;
                    }

                    // Remove the association from the table and the database
                    final Observation[] observationsToBeRemoved = SearchAndReplace.getSelectedObservations(table);
                    for (int i = 0; i < observationsToBeRemoved.length; i++) {
                        final Observation deadObs = observationsToBeRemoved[i];
                        ((IObservationTableModel) table.getModel()).removeObservation(deadObs);
                        action.setObservation(deadObs);
                        action.doAction();
                    }

                    table.redrawAll();
                }
                DeleteObservationAction action = new DeleteObservationAction();

            });
        }

        return removeObservationButton;
    }

    private JButton getRenameConceptButton() {
        if (renameConceptButton == null) {
            renameConceptButton = new JButton("Rename Selected");
            renameConceptButton.setToolTipText("Rename all selected Observations in table");
            renameConceptButton.addActionListener(new RenameAction());
            renameConceptButton.setEnabled(false);
        }

        return renameConceptButton;
    }


    private ConceptNameComboBox getRenameConceptComboBox() {
        if (renameConceptComboBox == null) {
            renameConceptComboBox = new AllConceptNamesComboBox();
            renameConceptComboBox.addEditorActionListener(new RenameAction());
        }

        return renameConceptComboBox;
    }

    private JButton getReplaceAssociationButton() {
        if (replaceAssociationButton == null) {
            replaceAssociationButton = new JButton("Replace Matching Associations");
            replaceAssociationButton.setToolTipText("Replace associations in selected rows " +
                    "matching the association searched for");
            replaceAssociationButton.addActionListener(new ReplaceAssociationAction());
            replaceAssociationButton.setEnabled(false);
        }

        return replaceAssociationButton;
    }

    private JCheckBox getSearchAssociationCheckBox() {
        if (searchAssociationCheckBox == null) {
            searchAssociationCheckBox = new JCheckBox();
            searchAssociationCheckBox.setSelected(false);
            searchAssociationCheckBox.addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    final boolean shouldEnable = searchAssociationCheckBox.isSelected();
                    getSearchAssociationComboBox().setEnabled(shouldEnable);
                    updateEnabledStatusForAllAssociationComponents();

                    if (shouldEnable) {
                        updateContentsOfAssociationComboBox();
                    }
                }

            });
        }

        return searchAssociationCheckBox;
    }

    private JComboBox getSearchAssociationComboBox() {
        if (searchAssociationComboBox == null) {
            searchAssociationComboBox = new JComboBox();
            searchAssociationComboBox.getEditor().addActionListener(new SearchAction());
            updateContentsOfAssociationComboBox();
            searchAssociationComboBox.setEditable(false);
            searchAssociationComboBox.setEnabled(false);
        }

        return searchAssociationComboBox;
    }

    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton("Search");
            searchButton.setToolTipText("Search for a concept name, association, or both");
            searchButton.addActionListener(new SearchAction());
            class ButtonState {

                // Update the button state, looking at the searchConceptComboBox
                // and searchAssociationComboBox selected items, if both of
                // these
                // are blank, disable the button

                /**
                 *  Description of the Method
                 */
                public void updateButtonState() {

                    //final Object selectedConcept = getSearchConceptComboBox().getSelectedItem();
                    //final Object selectedAssociation = getSearchAssociationComboBox().getSelectedItem();
                }
            }
            ;
            final ButtonState buttonState = new ButtonState();
            class ComboBoxItemListener implements ItemListener {

                /**
                 *  Description of the Method
                 *
                 * @param  e Description of the Parameter
                 */
                public void itemStateChanged(final ItemEvent e) {
                    buttonState.updateButtonState();
                }
            }
            ;

            // make sure that at least one of search boxes has a searchable item
            // in it.
            getSearchConceptComboBox().addItemListener(new ComboBoxItemListener());
            getSearchAssociationComboBox().addItemListener(new ComboBoxItemListener());
        }

        return searchButton;
    }


    private JCheckBox getSearchConceptCheckBox() {
        if (searchConceptCheckBox == null) {
            searchConceptCheckBox = new JCheckBox();

            // searchConceptCheckBox.setSelected(true);
            searchConceptCheckBox.addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    boolean shouldEnable = searchConceptCheckBox.isSelected();
                    getSearchConceptComboBox().setEnabled(shouldEnable);
                    updateEnabledStatusForAllObservationComponents();
                    String selectedItem = "";
                    if (getSearchConceptComboBox().getSelectedItem() != null) {
                        selectedItem = getSearchConceptComboBox().getSelectedItem().toString();
                    }

                    updateContentsOfSearchConceptNameComboBox(selectedItem);

                    if (!shouldEnable) {
                        updateContentsOfAssociationComboBox();
                    }
                }

            });
        }

        return searchConceptCheckBox;
    }


    private ConceptNameComboBox getSearchConceptComboBox() {
        if (searchConceptComboBox == null) {
            searchConceptComboBox = new ConceptNameComboBox();
            searchConceptComboBox.setEditable(false);
            searchConceptComboBox.addEditorActionListener(new SearchAction());
            updateContentsOfSearchConceptNameComboBox(null);
            searchConceptComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    // System.out.println(e);
                    updateContentsOfAssociationComboBox();
                }
            });
        }

        return searchConceptComboBox;
    }


    private JLabel getSearchLabel() {
        if (searchLabel == null) {
            searchLabel = new JLabel("Search By");
        }

        return searchLabel;
    }

    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(getSearchButton(), BorderLayout.EAST);
            searchPanel.add(getSearchStatusLabel(), BorderLayout.WEST);
        }

        return searchPanel;
    }

    private JLabel getSearchStatusLabel() {
        if (searchStatusLabel == null) {
            searchStatusLabel = new JLabel("Search Status");
            searchStatusLabel.setToolTipText("Search results are displayed here");
        }

        return searchStatusLabel;
    }

    private void initialize() {
        final String border = "6dlu";
        final String padding = "4dlu";
        final String columns = border + ",right:pref, " + padding + ", pref, " + padding + ", pref:grow(.5), " +
                               padding + ", pref, " + padding + ", " + ",pref:grow(.5), " + padding + ", pref, " +
                               border;
        final String rows = border + ", pref, " + padding + ", pref, " + padding + ", pref, " + padding + ", pref, " +
                            border;
        final int row1 = 2;
        final int row2 = 4;
        final int row3 = 6;
        final int row4 = 8;
        final int col1 = 2;
        final int col2 = 4;
        final int col3 = 6;
        final int col4 = 10;
        final int col5 = 12;
        final FormLayout layout = new FormLayout(columns, rows);
        layout.setColumnGroups(new int[][] {
            { 10, 12 }
        });
        this.setLayout(layout);
        final CellConstraints cc = new CellConstraints();
        this.add(getSearchLabel(), cc.xy(col3, row1, CellConstraints.CENTER, CellConstraints.DEFAULT));
        this.add(getConceptLabel(), cc.xy(col1, row2));
        this.add(getSearchConceptCheckBox(), cc.xy(col2, row2));
        this.add(getSearchConceptComboBox(), cc.xy(col3, row2));
        this.add(getRenameConceptComboBox(), cc.xy(col4, row2));
        this.add(getRenameConceptButton(), cc.xy(col5, row2));
        this.add(getAssociationLabel(), cc.xy(col1, row3));
        this.add(getSearchAssociationCheckBox(), cc.xy(col2, row3));
        this.add(getSearchAssociationComboBox(), cc.xy(col3, row3));
        this.add(getReplaceAssociationButton(), cc.xy(col4, row3));
        this.add(getAddAssociationButton(), cc.xy(col5, row3));
        this.add(getSearchPanel(), cc.xy(col3, row4));
        this.add(getRemoveObservationButton(), cc.xy(col4, row4));
        this.add(getRemoveAssociationsButton(), cc.xy(col5, row4));

        // separate columns 1 and 2 from 3 and 4
        final JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        this.add(separator, cc.xywh(8, 2, 1, 8));
    }

    private void listenForTableChanges() {
        final JTable jTable = table.getJTable();
        jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(final ListSelectionEvent e) {
                updateEnabledStatusForAllAssociationComponents();
                updateEnabledStatusForAllObservationComponents();
            }
        });
        videoSetViewer.addPropertyChangeListener("tableChange", new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent e) {
                updateContentsOfSearchConceptNameComboBox(null);
                updateContentsOfAssociationComboBox();
            }
        });
    }

    public void redrawSelectedRows() {
        final JTable jTable = table.getJTable();
        final IObservationTableModel model = (IObservationTableModel) jTable.getModel();
        final int[] selectedRows = jTable.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            model.redrawRow(selectedRows[i]);
        }
    }

    private void updateContentsOfAssociationComboBox() {
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                Object[] associations = null;

                // if the concept name search box is open, only show
                // associations
                // matching the selected conceptname
                if (getSearchConceptCheckBox().isSelected()) {
                    int[] matchingRows = SearchAndReplace.getMatchingRows(table,
                                             (String) getSearchConceptComboBox().getSelectedItem(), null);
                    associations = SearchAndReplace.getAssociationsAtRows(table, matchingRows);
                }
                else {
                    associations = SearchAndReplace.getAssociationsInTable(table);
                }

                if (associations == null) {
                    associations = new String[] { "Error while looking for Associations in table" };
                }

                final SortedComboBoxModel model = new SortedComboBoxModel(Arrays.asList(associations),
                                                      new IgnoreCaseToStringComparator());
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        getSearchAssociationComboBox().setModel(model);
                    }
                });

                return null;
            }
        };
        worker.start();
    }

    /**
     * Update the searchConceptNameComboBox contents, synchronizing all the
     * concept names in the table with the concept names in the combobox.
     *
     * @param  selectedConceptName Description of the Parameter
     */
    public synchronized void updateContentsOfSearchConceptNameComboBox(final String selectedConceptName) {
        if (isBeingUpdated) {
            return;
        }

        isBeingUpdated = true;
        final String currentlySelectedConceptName = (String) getSearchConceptComboBox().getSelectedItem();

        // place a wait message in the combo box while the update happens
        getSearchConceptComboBox().updateModel(waitMessage);
        final SwingWorker worker = new SwingWorker() {

            public Object construct() {
                String[] conceptNames = null;
                conceptNames = SearchAndReplace.getConceptNamesInTable(table);

                if (conceptNames == null) {
                    conceptNames = new String[] { "Error while searching for concept names in table" };
                }

                final String[] finalConceptNames = conceptNames;
                String conceptNameToSelect = null;

                // sort the conceptNames array so that a binarySearch can be
                // performed on it.
                Arrays.sort(conceptNames);

                if (selectedConceptName != null) {

                    /*
                     *  If the selectedConceptName is not in the array, then a
                     *  different conceptName will need to be used as the
                     *  selected index.
                     */
                    int foundIndex = Arrays.binarySearch(conceptNames, selectedConceptName);
                    if (foundIndex > -1) {

                        /*
                         *  the selectedConceptName was found, use this as the
                         *  selectedItem in the combobox
                         */
                        conceptNameToSelect = selectedConceptName;
                    }
                }

                // if the selectedConceptName was null, or is no longer in the
                // combobox
                // then conceptNameToSelect will be null still. Find a value to
                // assign to it
                if (conceptNameToSelect == null) {

                    // first check if the currentlySelectedConceptName is still
                    // in the combobox
                    int foundIndex = -1;
                    if (currentlySelectedConceptName != null) {
                        foundIndex = Arrays.binarySearch(conceptNames, currentlySelectedConceptName);
                    }

                    if (foundIndex > -1) {
                        conceptNameToSelect = currentlySelectedConceptName;
                    }
                    else if (conceptNames.length > 0) {

                        // okay, nothing else to do but choose the first element
                        // in the array
                        // to use as the selected element
                        conceptNameToSelect = conceptNames[0];
                    }
                    else {
                        conceptNameToSelect = "";
                    }
                }

                final String finalConceptNameToSelect = conceptNameToSelect;
                getSearchConceptComboBox().updateModel(finalConceptNames);

                if (finalConceptNameToSelect != null) {
                    getSearchConceptComboBox().setSelectedItem(finalConceptNameToSelect);
                }

                /*
                 * SwingUtilities.invokeLater(
                 *   new Runnable() {
                 *
                 *       public void run() {
                 *           getSearchConceptComboBox().updateModel(
                 *                   finalConceptNames);
                 *           if (finalConceptNameToSelect != null) {
                 *               getSearchConceptComboBox().setSelectedItem(
                 *                       finalConceptNameToSelect);
                 *           }
                 *       }
                 *   });
                 */
                return null;
            }
            public void finished() {
                isBeingUpdated = false;
                updateContentsOfAssociationComboBox();
            }
        };
        worker.start();
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void updateEnabledStatusForAllAssociationComponents() {
        updateEnabledStatusForAssociationComponent(getReplaceAssociationButton());
        updateEnabledStatusForAssociationComponent(getRemoveAssociationsButton());
        updateEnabledStatusForAssociationComponent(getAddAssociationButton());
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void updateEnabledStatusForAllObservationComponents() {

        // updateEnabledStatusForObservationComponent(getRemoveObservationButton());
        updateEnabledStatusForObservationComponent(getRenameConceptComboBox());
        updateEnabledStatusForObservationComponent(getRenameConceptButton());
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param component
     */
    private void updateEnabledStatusForAssociationComponent(final JComponent component) {
        if (getSearchAssociationCheckBox().isSelected() && (table.getJTable().getSelectedRowCount() > 0)) {
            component.setEnabled(true);
        }
        else {
            component.setEnabled(false);
        }
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param component
     */
    private void updateEnabledStatusForObservationComponent(final JComponent component) {
        if (getSearchConceptCheckBox().isSelected() && (table.getJTable().getSelectedRowCount() > 0)) {
            component.setEnabled(true);
        }
        else {
            component.setEnabled(false);
        }
    }

    /**
     *  The main program for the SearchAndReplaceWidget class
     *
     * @param  args The command line arguments
     */
    public static void main(final String[] args) {

        // ClearLookManager.setMode(ClearLookMode.DEBUG);
        final JFrame frame = new JFrame("Test Search and Replace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(200, 200, 500, 600);
        frame.getContentPane().add(new SearchAndReplaceWidget(null, null));
        frame.setVisible(true);
    }

    private class RenameAction implements ActionListener {

        /**
         *  Description of the Method
         *
         * @param  e Description of the Parameter
         */
        public void actionPerformed(final ActionEvent e) {
            String newConceptName = (String) renameConceptComboBox.getSelectedItem();

            // Replace the selected name with the primary name.
            try {
                final Concept concept = KnowledgeBaseCache.getInstance().findConceptByName(newConceptName);
                if (concept != null) {
                    newConceptName = concept.getPrimaryConceptNameAsString();
                }
            }
            catch (final DAOException e1) {

                /*
                 *  If lookup fails its not a huge deal because we can still use
                 *  the newConceptName. Still we should log the error for now.
                 */
                if (log.isErrorEnabled()) {
                    log.error("Failed to retrive primary name for " + newConceptName + " from the knowledge base", e1);
                }
            }

            if (newConceptName != null) {
                SearchAndReplace.setConceptNameForSelectedObservations(table, newConceptName);
            }

            // Call this so the view reflects the model.
            redrawSelectedRows();

            // Update the conceptname combobox to reflect the new names.
            updateContentsOfSearchConceptNameComboBox(newConceptName);
        }
    }

    private class ReplaceAssociationAction implements ActionListener {

        /**
         *  Description of the Method
         *
         * @param  e Description of the Parameter
         */
        public void actionPerformed(final ActionEvent e) {
            final Point location = getReplaceAssociationButton().getLocationOnScreen();
            location.x = location.x + getReplaceAssociationButton().getWidth();
            final MultiAssociationEditorPanel editor = new MultiAssociationEditorPanel();
            editor.setUserGeneratedAssociation(
                new Association((Association) getSearchAssociationComboBox().getSelectedItem()));
            final int result = editor.showDialog(videoSetViewer, "Replace Association", location);
            if (result == MultiAssociationEditorPanel.ASSOCIATION_CREATED_OPTION) {
                final Association association = editor.getUserGeneratedAssociation();
                final Association associationSearchedFor =
                    (Association) SearchAndReplaceWidget.this.getSearchAssociationComboBox().getSelectedItem();

                // need to get the link value and copy it to a string because
                // the association will get changed
                // in one of the replace operations
                final int[] selectedRows = table.getSelectedRows();
                final Association[] possibleAssociations = SearchAndReplace.getAssociationsAtRows(table, selectedRows);
                for (int i = 0; i < possibleAssociations.length; i++) {
                    final Association currentAssoc = possibleAssociations[i];
                    if (currentAssoc.equals(associationSearchedFor)) {
                        currentAssoc.setLinkName(association.getLinkName());
                        currentAssoc.setToConcept(association.getToConcept());
                        currentAssoc.setLinkValue(association.getLinkValue());
                        DAOEventQueue.updateVideoArchiveSet((IDataObject) currentAssoc);
                    }
                }
            }

            redrawSelectedRows();
            updateContentsOfAssociationComboBox();
        }

        /**
         *  Gets the matchString attribute of the ReplaceAssociationAction object
         *
         * @param  association Description of the Parameter
         * @return  The matchString value
         */
        public String getMatchString(final Association association) {
            final String matchingString = association.getLinkValue() + association.getLinkName() +
                                          association.getToConcept();

            return matchingString;
        }
    }

    /**
     * ************* LISTENERS
     *
     * @author  brian
     * @version
     * @created  August 18, 2004
     */
    private class SearchAction implements ActionListener {

        /**
         *  Description of the Method
         *
         * @param  e Description of the Parameter
         */
        public void actionPerformed(final ActionEvent e) {
            getSearchStatusLabel().setText("Searching...");

            /*
             * SwingUtilities.invokeLater(
             *   new Runnable() {
             *
             *       public void run() {
             *           getSearchStatusLabel().setText("Searching...");
             *       }
             *   });
             */
            final String conceptName = getSearchConceptComboBox().isEnabled()
                                       ? (String) searchConceptComboBox.getSelectedItem() : null;
            final Object shouldBeAnAssociation = getSearchAssociationComboBox().isEnabled()
                                                 ? getSearchAssociationComboBox().getSelectedItem() : null;
            Association searchAssociation = null;
            if ((shouldBeAnAssociation != null) && (shouldBeAnAssociation instanceof Association)) {
                searchAssociation = (Association) shouldBeAnAssociation;
            }
            else {
                searchAssociation = null;
            }

            String resultMessage = "0 Matches";
            if ((conceptName != null) || (searchAssociation != null)) {
                SearchAndReplace.selectMatchingObservations(table, conceptName, searchAssociation);
                final int numberOfObservationsSelected = table.getSelectedRowCount();
                if (numberOfObservationsSelected == 1) {
                    resultMessage = numberOfObservationsSelected + " match";
                }
                else {
                    resultMessage = numberOfObservationsSelected + " matches";
                }
            }

            final String finalResultMessage = resultMessage;
            getSearchStatusLabel().setText(finalResultMessage);
        }
    }
}
