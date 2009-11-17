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
Created on Mar 11, 2005
 */
package org.mbari.vars.annotation.ui.table;

import foxtrot.Task;
import foxtrot.Worker;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.vars.annotation.model.Association;
import vars.annotation.AssociationList;
import vars.annotation.ISimpleConcept;
import org.mbari.vars.annotation.model.Observation;
import org.mbari.vars.dao.DAOEventQueue;
import org.mbari.vars.dao.DAOException;
import org.mbari.vars.dao.IDataObject;
import org.mbari.vars.knowledgebase.model.Concept;
import org.mbari.vars.knowledgebase.model.ConceptName;
import vars.knowledgebase.IConceptName;
import org.mbari.vars.knowledgebase.model.LinkTemplate;
import org.mbari.vars.knowledgebase.model.dao.KnowledgeBaseCache;
import org.mbari.vars.ui.HierachicalConceptNameComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.annotation.IAssociation;
import vars.annotation.IObservation;
import vars.knowledgebase.ILinkTemplate;

/**
 * This panel is for editing or adding an association to an existing observation.
 *
 * @author     brian
 * @created    March 17, 2005
 */
public class AssociationEditorPanelLite extends JPanel {

    private static final long serialVersionUID = -1613402111784588999L;
    private static final Logger log = LoggerFactory.getLogger(AssociationEditorPanelLite.class);
    private final static Concept CONCEPT_SELF = new Concept();
    private final static Concept CONCEPT_NIL = new Concept();

    static {

        /*
         * Initialize the default concepts
         */
        CONCEPT_SELF.addConceptName(new ConceptName(Association.SELF, IConceptName.NAMETYPE_PRIMARY));
        CONCEPT_NIL.addConceptName(new ConceptName(Association.NIL, IConceptName.NAMETYPE_PRIMARY));
    }

    /**
     * @uml.property  name="association"
     * @uml.associationEnd
     */
    private IAssociation association;

    /**
     * @uml.property  name="btnCancel"
     * @uml.associationEnd
     */
    private JButton btnCancel = null;

    /**
     * @uml.property  name="btnExcept"
     * @uml.associationEnd
     */
    private JButton btnExcept = null;    // @jve:decl-index=0:

    /**
     * @uml.property  name="cbFromConceptName"
     * @uml.associationEnd
     */
    private JComboBox cbFromConceptName = null;

    /**
     * @uml.property  name="cbLinkTemplates"
     * @uml.associationEnd
     */
    private JComboBox cbLinkTemplates = null;

    /**
     * @uml.property  name="cbToConceptName"
     * @uml.associationEnd
     */
    private HierachicalConceptNameComboBox cbToConceptName = null;

    /**
     * Key = concept name as a string Value = ISimpleConcept
     * @uml.property  name="fromConceptNameMap"
     * @uml.associationEnd  qualifier="toConcept:java.lang.String org.mbari.vars.annotation.model.Association"
     */
    private final HashMap fromConceptNameMap = new HashMap();

    /**
     * @uml.property  name="lblFor"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lblFor = null;

    /**
     * @uml.property  name="lblLinkName"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lblLinkName = null;

    /**
     * @uml.property  name="lblLinkValue"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lblLinkValue = null;

    /**
     * @uml.property  name="lblSearch"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lblSearch = null;

    /**
     * @uml.property  name="lblToConceptName"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private JLabel lblToConceptName = null;

    /**
     * @uml.property  name="observation"
     * @uml.associationEnd
     */
    private IObservation observation;

    /**
     * @uml.property  name="pButtons"
     * @uml.associationEnd
     */
    private JPanel pButtons = null;

    /**
     * This contains information from the assciation that is set with setAssociation
     * @uml.property  name="refLinkTemplate"
     * @uml.associationEnd  multiplicity="(1 1)"
     */
    private final ILinkTemplate refLinkTemplate = new LinkTemplate();

    /**
     * @uml.property  name="tfLinkName"
     * @uml.associationEnd
     */
    private JTextField tfLinkName = null;

    /**
     * @uml.property  name="tfLinkValue"
     * @uml.associationEnd
     */
    private JTextField tfLinkValue = null;

    /**
     * @uml.property  name="tfSearch"
     * @uml.associationEnd
     */
    private JTextField tfSearch = null;

    /**
     * This is the default constructor
     */
    public AssociationEditorPanelLite() {
        super();
        initialize();
    }

    /**
     * Adds an <code>ActionListener</code>. The listener will receive an
     * action event the user finishes making a selection.
     *
     * @param  l  the <code>ActionListener</code> that is to be notified
     */
    public void addActionListener(final ActionListener l) {
        getBtnExcept().addActionListener(l);
        getBtnCancel().addActionListener(l);
    }

    /**
     * This method initializes jButton1
     * @return     javax.swing.JButton
     * @uml.property  name="btnCancel"
     */
    private JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new JFancyButton();
            btnCancel.setPreferredSize(new Dimension(30, 23));
            btnCancel.setToolTipText("Cancel");
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/stop.png")));
            btnCancel.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    resetDisplay();
                }
            });
        }

        return btnCancel;
    }

    /**
     * This method initializes jButton
     * @return     javax.swing.JButton
     * @uml.property  name="btnExcept"
     */
    private JButton getBtnExcept() {
        if (btnExcept == null) {
            btnExcept = new JFancyButton();
            btnExcept.setPreferredSize(new Dimension(30, 23));
            btnExcept.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            btnExcept.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    if (observation != null) {

                        /*
                         * The parent can be either an observation or
                         * another association. We find the parent by
                         * searching for a match in the fromConceptNameMap
                         */
                        final ISimpleConcept parent =
                            (ISimpleConcept) fromConceptNameMap.get(getCbFromConceptName().getSelectedItem());

                        /*
                         * If an association was provided we modify it and
                         * set it's new parent. If none was provided we
                         * create a new association.
                         */
                        if (association == null) {
                            association = new Association();
                        }

                        association.setLinkName(getTfLinkName().getText());
                        association.setToConcept((String) getCbToConceptName().getSelectedItem());
                        association.setLinkValue(getTfLinkValue().getText());
                        parent.addAssociation(association);

                        /*
                         * Store the change in the database by updating the
                         * parent of the association. This call will update
                         * or insert the association as needed.
                         */
                        ((Association) association).validateToConceptName();
                        DAOEventQueue.updateVideoArchiveSet((IDataObject) parent);
                    }
                }

            });
        }

        return btnExcept;
    }

    /**
     * This method initializes jComboBox
     * @return     javax.swing.JComboBox
     * @uml.property  name="cbFromConceptName"
     */
    private JComboBox getCbFromConceptName() {
        if (cbFromConceptName == null) {
            cbFromConceptName = new JComboBox();
            cbFromConceptName.setToolTipText("From Concept");

            /*
             *  When an item is selected it updates the contents of
             *  cbLinkTemplates
             */
            cbFromConceptName.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final String fromConcept = cbFromConceptName.getSelectedItem().toString();
                        final ISimpleConcept simpleConcept = (ISimpleConcept) fromConceptNameMap.get(fromConcept);
                        try {
                            setFromConceptName(simpleConcept.getConceptName());
                        }
                        catch (final DAOException e1) {

                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }

            });
        }

        return cbFromConceptName;
    }

    /**
     * This method initializes jComboBox
     * @return     javax.swing.JComboBox
     * @uml.property  name="cbLinkTemplates"
     */
    private JComboBox getCbLinkTemplates() {
        if (cbLinkTemplates == null) {
            cbLinkTemplates = new JComboBox();
            cbLinkTemplates.setModel(new SearchableComboBoxModel());
            cbLinkTemplates.setToolTipText("Associations in Knowledgebase");
            cbLinkTemplates.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    setLinkTemplate((LinkTemplate) cbLinkTemplates.getSelectedItem());
                }
            });
        }

        return cbLinkTemplates;
    }

    /**
     * This method initializes jComboBox1
     * @return     javax.swing.JComboBox
     * @uml.property  name="cbToConceptName"
     */
    private HierachicalConceptNameComboBox getCbToConceptName() {
        if (cbToConceptName == null) {
            Concept rootConcept = null;
            try {
                rootConcept = KnowledgeBaseCache.getInstance().findRootConcept();
            }
            catch (final DAOException e) {
                rootConcept = new Concept();
                rootConcept.setPrimaryConceptName(new ConceptName("Object", IConceptName.NAMETYPE_PRIMARY));
            }

            cbToConceptName = new HierachicalConceptNameComboBox(rootConcept);
        }

        return cbToConceptName;
    }

    /**
     * This method initializes jPanel
     * @return     javax.swing.JPanel
     * @uml.property  name="pButtons"
     */
    private JPanel getPButtons() {
        if (pButtons == null) {
            pButtons = new JPanel();
            pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
            pButtons.add(Box.createHorizontalGlue());    // this will expand/contract
            pButtons.add(getBtnExcept(), null);

            // pButtons.add(Box.createHorizontalStrut(23));
            pButtons.add(getBtnCancel(), null);
        }

        return pButtons;
    }

    /**
     * This method initializes jTextField
     * @return     javax.swing.JTextField
     * @uml.property  name="tfLinkName"
     */
    private JTextField getTfLinkName() {
        if (tfLinkName == null) {
            tfLinkName = new JTextField();
            tfLinkName.setToolTipText("Link Name");
        }

        return tfLinkName;
    }

    /**
     * This method initializes jTextField
     * @return     javax.swing.JTextField
     * @uml.property  name="tfLinkValue"
     */
    private JTextField getTfLinkValue() {
        if (tfLinkValue == null) {
            tfLinkValue = new JTextField();
        }

        return tfLinkValue;
    }

    /**
     * The tfSearch object is a textfield which searches for substring matches in values in the cbLinkTemplates model
     * @return     javax.swing.JTextField
     * @uml.property  name="tfSearch"
     */
    private JTextField getTfSearch() {
        if (tfSearch == null) {
            tfSearch = new JTextField();
            tfSearch.addActionListener(new ActionListener() {

                /*
                 *  Search through the cbLinkTemplate widget to see if we find any
                 *  matches here.
                 *  @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed(final ActionEvent e) {
                    tfSearch.setEnabled(false);
                    final JComboBox cb = getCbLinkTemplates();
                    final int startIndex = cb.getSelectedIndex() + 1;
                    final SearchableComboBoxModel model = (SearchableComboBoxModel) cb.getModel();
                    int index = model.searchForItemContaining(tfSearch.getText(), startIndex);
                    if (index > -1) {

                        // Handle if match was found
                        cb.setSelectedIndex(index);
                        cb.hidePopup();
                    }
                    else {

                        // If no match was found search from the start of the
                        // list.
                        if (startIndex > 0) {
                            index = model.searchForItemContaining(tfSearch.getText());

                            if (index > -1) {

                                // Handle if match was found
                                cb.setSelectedIndex(index);
                                cb.hidePopup();
                            }
                        }
                    }

                    tfSearch.setEnabled(true);
                }
            });
            tfSearch.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    tfSearch.setSelectionStart(0);
                    tfSearch.setSelectionEnd(tfSearch.getText().length());
                }
            });
        }

        return tfSearch;
    }

    /**
     * This method initializes and does the layout
     */
    private void initialize() {
        final GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
        lblSearch = new JLabel();
        lblFor = new JLabel();
        lblLinkName = new JLabel();
        lblToConceptName = new JLabel();
        lblLinkValue = new JLabel();
        final GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
        this.setSize(500, 149);
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.ipadx = 2;
        gridBagConstraints1.ipady = 2;
        gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
        lblSearch.setText("Search");
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.ipadx = 4;
        gridBagConstraints3.ipady = 4;
        gridBagConstraints3.insets = new java.awt.Insets(2, 2, 2, 2);
        lblFor.setText("for");
        lblFor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints4.gridx = 4;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.gridy = 1;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridwidth = 4;
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints5.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints6.gridx = 0;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.insets = new java.awt.Insets(2, 2, 2, 2);
        lblLinkName.setText("Link");
        lblLinkName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLinkName.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lblSearch.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints8.gridx = 2;
        gridBagConstraints8.gridy = 2;
        gridBagConstraints8.insets = new java.awt.Insets(2, 2, 2, 2);
        lblToConceptName.setText("To");
        gridBagConstraints10.gridx = 0;
        gridBagConstraints10.gridy = 3;
        gridBagConstraints10.insets = new java.awt.Insets(2, 2, 2, 2);
        lblLinkValue.setText("Value");
        gridBagConstraints11.gridx = 1;
        gridBagConstraints11.gridy = 3;
        gridBagConstraints11.weightx = 1.0;
        gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints11.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints14.gridx = 4;
        gridBagConstraints14.gridy = 3;
        gridBagConstraints14.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints14.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints9.gridx = 4;
        gridBagConstraints9.gridy = 2;
        gridBagConstraints9.weightx = 1.0;
        gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints9.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.ipady = 2;
        gridBagConstraints2.ipadx = 2;
        gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints15.gridx = 1;
        gridBagConstraints15.gridy = 2;
        gridBagConstraints15.weightx = 1.0;
        gridBagConstraints15.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints15.insets = new java.awt.Insets(2, 2, 2, 2);
        this.add(getTfSearch(), gridBagConstraints4);
        this.add(lblSearch, gridBagConstraints1);
        this.add(getCbFromConceptName(), gridBagConstraints2);
        this.add(lblFor, gridBagConstraints3);
        this.add(getCbLinkTemplates(), gridBagConstraints5);
        this.add(lblLinkName, gridBagConstraints6);
        this.add(lblToConceptName, gridBagConstraints8);
        this.add(lblLinkValue, gridBagConstraints10);
        this.add(getCbToConceptName(), gridBagConstraints9);
        this.add(getTfLinkValue(), gridBagConstraints11);
        this.add(getPButtons(), gridBagConstraints14);
        this.add(getTfLinkName(), gridBagConstraints15);
    }

    /*
     * This frees the set association and observation and sets the UI into
     * a non-editing state. The editor can be restored by calling setEditValues()
     * with vaid arguments.
     */

    /**
     * <p><!-- Method description --></p>
     *
     */
    public void resetDisplay() {

        /*
         * Clear the values set by the observation
         */
        this.observation = null;
        fromConceptNameMap.clear();
        getCbFromConceptName().removeAllItems();
        getCbLinkTemplates().removeAllItems();
        getCbToConceptName().removeAllItems();

        /*
         * Clear the values set by the association.
         */
        setAssociation(null);
        getBtnExcept().setEnabled(false);
    }

    /**
     * When called it creates a <code>LinkTemplate</code> object that corresponds to the <code>Association</code> and sets the cbLinkTemplate selectedItem to the new LinkTemplate.
     * @param association   The new association value. It can be null, in which  case a LinkTemplate of NIL | NIL | NIL is created.
     * @uml.property  name="association"
     */
    private void setAssociation(final Association association) {
        this.association = association;

        /*
         *  Set the values in our reference linktemplate
         */
        String linkName = null;
        String toConcept = null;
        String linkValue = null;
        if (association != null) {
            linkName = association.getLinkName();
            toConcept = association.getToConcept();
            linkValue = association.getLinkValue();
        }

        refLinkTemplate.setLinkName(linkName);
        refLinkTemplate.setLinkValue(linkValue);
        refLinkTemplate.setToConcept(toConcept);

        /*
         *  Select the refLinkTemplate in the cbLinkTemplates
         */
        final JComboBox cb = getCbLinkTemplates();
        final SearchableComboBoxModel model = (SearchableComboBoxModel) cb.getModel();
        if (!model.contains(refLinkTemplate)) {
            model.addElement(refLinkTemplate);
        }

        cb.setSelectedItem(refLinkTemplate);
    }

    /**
     * Sets the observation and the association to be edited.
     *
     * @param  observation  Can not be <b>null</b>
     * @param  association  The new editValues value
     */
    public void setEditValues(final Observation observation, final Association association) {
        try {
            getBtnExcept().setEnabled(false);
            setObservation(observation);
            setAssociation(association);
            getBtnExcept().setEnabled(true);
        }
        catch (final DAOException e) {
            getBtnExcept().setEnabled(false);
            log.error("Call to setEditValues(" + observation + ", " + association + ") failed.", e);
        }
    }

    /**
     * When called this method loads all the linkTemplates that are appropriate
     * for the particular concept-name and populates cbLinkTemplates with them.
     * This method should not be called directly except by the itemm listener
     * attached to the cbFromConceptName ComboBox.
     *
     * @param  fromConceptName   The new fromConceptName value
     * @exception  DAOException  Thrown if unable to retrieve the linkTemplates
     *  from the database.
     */
    private void setFromConceptName(final String fromConceptName) throws DAOException {
        final Concept concept = KnowledgeBaseCache.getInstance().findConceptByName(fromConceptName);
        getCbFromConceptName().setEnabled(false);

        /*
         * This can be a long running task. Execute on non-event dispatch thread
         * using foxtrot API
         */
        LinkTemplate[] linkTemplates;
        try {
            linkTemplates = (LinkTemplate[]) Worker.post(new Task() {

                public Object run() throws Exception {
                    return concept.getHierarchicalLinkTemplates();
                }
            });
        }
        catch (final Exception e) {
            throw(DAOException) e;
        }

        getCbFromConceptName().setEnabled(true);

        /*
         *  When a 'from' concept-name is set we need to set the UI to only show
         *  LinkTemplates that are available for that concept
         */
        final JComboBox cbLT = getCbLinkTemplates();
        cbLT.setToolTipText("Associations available for " + fromConceptName);
        cbLT.removeAllItems();

        for (int i = 0; i < linkTemplates.length; i++) {
            cbLT.addItem(linkTemplates[i]);
        }
    }

    /**
     *  This method is called by the ItemListener on the cbLinkTemplates
     * ComboBox. It should not be called directly anywhere else. If you want to
     * trigger this method call then you should call either:
     * <pre>
     *  getCbLinkTemplates().setSelectedItem(someLinkTemplate);
     *  // or
     *  getCbLinkTemplates().setSelectedIndex(someInt);
     * </pre>
     *
     * @param  linkTemplate  The new linkTemplate value
     */
    private void setLinkTemplate(final LinkTemplate linkTemplate) {

        /*
         *  Set the text value in tfLinkValue as the LinkValue
         */
        final JTextField tfLV = getTfLinkName();
        tfLV.setText(linkTemplate.getLinkName());

        /*
         *  Set the selected concept in the toConceptName combobox. If it's NIL or
         *  SELF then that should be the only value in the combo box
         */
        final HierachicalConceptNameComboBox cb = getCbToConceptName();
        Concept concept = null;
        if (linkTemplate.getToConcept().equalsIgnoreCase(Association.NIL)) {
            concept = CONCEPT_NIL;
        }
        else if (linkTemplate.getToConcept().equalsIgnoreCase(Association.SELF)) {
            concept = CONCEPT_SELF;
        }
        else {
            try {

                /*
                 *  Set the root concept of the cbToConceptName widget.
                 */
                concept = KnowledgeBaseCache.getInstance().findConceptByName(linkTemplate.getToConcept());
            }
            catch (final DAOException e) {
                concept = CONCEPT_NIL;
                e.printStackTrace();
            }
        }

        // if (!model.contains(concept.getPrimaryConceptNameAsString())) {
        cb.setConcept(concept);

        // }
        cb.setSelectedItem(concept.getPrimaryConceptNameAsString());

        /*
         *  Set the text value in tfLinkValue as the LinkValue
         */
        getTfLinkValue().setText(linkTemplate.getLinkValue());
    }

    /**
     * Sets the observation attribute. This should only be called from setEditValue().
     * @param observation        The new observation value. <b>null</b> is not allowed
     * @exception DAOException   Thrown if unable to retrieve the correct linkTemplates
     * @uml.property  name="observation"
     */
    private void setObservation(final IObservation observation) throws DAOException {
        this.observation = observation;

        /*
         *  We have to map the conceptName to the correct ISImpleConcept so we
         *  can retrieve it later. This involves some name munging so that
         *  everything has a unique name for example a second nanomia would be
         *  added as" nanomia (1)"
         */
        fromConceptNameMap.clear();
        fromConceptNameMap.put(observation.getConceptName(), observation);
        final AssociationList associationList = observation.getAssociationList();
        for (final Iterator i = associationList.iterator(); i.hasNext(); ) {
            final IAssociation association = (IAssociation) i.next();
            String toConcept = association.getToConcept();
            if (!((toConcept == null) || (toConcept.toLowerCase().equals(Association.SELF)) ||
                    (toConcept.equals("")) || (toConcept.toLowerCase().equals(Association.NIL)))) {
                String key = toConcept;
                int n = 0;
                while (fromConceptNameMap.containsKey(key)) {
                    n++;
                    key = toConcept + " (" + n + ")";
                }

                toConcept = key;
                fromConceptNameMap.put(toConcept, association);
            }
        }

        /*
         *  Update the values in cbFromConceptName with the values we just stored
         *  in the fromConceptNameMap
         */
        final JComboBox cb = getCbFromConceptName();
        cb.removeAllItems();

        for (final Iterator i = fromConceptNameMap.keySet().iterator(); i.hasNext(); ) {
            cb.addItem(i.next());
        }

        cb.setSelectedIndex(0);
    }
}

//@jve:decl-index=0:visual-constraint="10,10"
