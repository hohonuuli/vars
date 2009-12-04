/*
 * @(#)AssociationEditorPanel.java   2009.11.18 at 04:22:36 PST
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



package vars.annotation.ui;

import com.google.common.collect.ImmutableList;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.bushe.swing.event.EventBus;
import org.mbari.awt.event.ActionAdapter;
import org.mbari.swing.FancyComboBox;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.swing.SortedComboBoxModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.CacheClearedEvent;
import vars.CacheClearedListener;
import vars.ILink;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.AssociationDAO;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.knowledgebase.LinkTemplate;
import vars.knowledgebase.SimpleConceptNameBean;
import vars.shared.ui.HierachicalConceptNameComboBox;

/**
 * @author  <a href="http://www.mbari.org">MBARI</a>
 */
public class AssociationEditorPanel extends JPanel {

    private javax.swing.JButton btnAdd = null;
    private javax.swing.JButton btnCancel = null;
    private javax.swing.JComboBox cbFromConcept = null;
    private javax.swing.JComboBox cbLinks = null;
    private HierachicalConceptNameComboBox cbToConcept = null;
    private javax.swing.JLabel lbl3a = null;
    private javax.swing.JLabel lbl4a = null;
    private javax.swing.JLabel lbl5a = null;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final javax.swing.JLabel forLabel = new JLabel("for");
    private final Map<String, Observation> conceptMap = new HashMap<String, Observation>();
    private final String nil = ILink.VALUE_NIL;
    private final javax.swing.JLabel searchLabel = new JLabel("Search");
    private javax.swing.JTextField tfLinkName = null;
    private javax.swing.JTextField tfLinkValue = null;
    private javax.swing.JTextField tfSearch = null;
    private vars.annotation.Association association;
    private ActionAdapter cancelAction;
    private final String nil3;

    /**
     *     The root observation of the Associations to be edited. Need to store this
     *     in order to set the selctedItem in cbFromConcept
     */
    private Observation observation;
    private final ToolBelt toolBelt;

    /**
     * This is the default constructor
     *
     * @param toolBelt
     */
    public AssociationEditorPanel(ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        nil3 = nil + " | " + nil + " | " + nil;
        initialize();
    }

    /**
     * Adds an <code>ActionListener</code>. The listener will receive an
     * action event the user finishes making a selection.
     *
     *
     * @param  l the <code>ActionListener</code> that is to be notified
     */
    public void addActionListener(final ActionListener l) {
        getBtnAdd().addActionListener(l);
        getBtnCancel().addActionListener(l);
    }

    /**
     *
     * @param  parent The feature to be added to the Association attribute
     */
    public void addAssociation(final Observation parent) {

        // Gather the parts of the Link from the GUI
        final String linkName = getTfLinkName().getText();
        final String toConcept = (String) getCbToConcept().getSelectedItem();
        final String linkValue = getTfLinkValue().getText();
        Association a = null;

        // If the current Association is being edited rather than created
        // update its particulars
        if (association != null) {

            /*
             *  DAOTX Remove reference to old parent
             */
            AssociationDAO associationDAO = toolBelt.getAnnotationDAOFactory().newAssociationDAO();
            associationDAO.startTransaction();
            association = associationDAO.merge(association);
            final Observation oldParent = association.getObservation();
            oldParent.removeAssociation(association);

            // Add new parent
            parent.addAssociation(association);
            association.setLinkName(linkName);
            association.setToConcept(toConcept);
            association.setLinkValue(linkValue);
            associationDAO.validateName(association);
            associationDAO.endTransaction();

            Collection<Observation> changedObservations = ImmutableList.of(oldParent, parent);
            toolBelt.getPersistenceController().updateObservations(changedObservations);

        }

        // here a new Association is being created
        else {
            a = toolBelt.getAnnotationFactory().newAssociation(linkName, toConcept, linkValue);
            Collection<Observation> obs = ImmutableList.of(parent);
            toolBelt.getPersistenceController().insertAssociations(obs, a);
        }
    }

    /**
     * @param  fromConcept
     */
    private void changeFromConcept(final String fromConcept) {

        // Get the currently selected link
        final JComboBox cbLinks_ = getCbLinks();
        final String link = (String) cbLinks_.getSelectedItem();

        // Clear the model and set the default
        final SortedComboBoxModel linksModel = (SortedComboBoxModel) getCbLinks().getModel();
        linksModel.clear();
        linksModel.addElement(nil3);

        if (log.isDebugEnabled()) {
            log.debug("Changing fromConcept to " + fromConcept);
        }

        final Collection<String> linkTemplatesAsStrings = new TreeSet<String>();
        try {
            Collection<LinkTemplate> linkTemplates = null;

            // Get the possible linkTemplates specific to a particular concept
            AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();
            if ((fromConcept != null) && (!fromConcept.equals(ILink.VALUE_NIL))) {

                final Concept c = service.findConceptByName(fromConcept);
                linkTemplates = service.findLinkTemplatesFor(c);
            }
            else {

                // If there is no toConcept, get all the linkTemplates
                Concept rootConcept = service.findRootConcept();
                linkTemplates = toolBelt.getAnnotationPersistenceService().findLinkTemplatesFor(rootConcept);
            }

            // Convert the link templates to their string representations
            for (final Iterator i = linkTemplates.iterator(); i.hasNext(); ) {
                final LinkTemplate lt = (LinkTemplate) i.next();
                linkTemplatesAsStrings.add(lt.stringValue());
            }
        }
        catch (final Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }

        if (linkTemplatesAsStrings != null) {
            linksModel.addAll(linkTemplatesAsStrings);
        }

        /*
         *  If the new model contains the previously selected link use the link. Otherwise set it to nil3
         */

        // 
        if (linksModel.contains(link)) {
            cbLinks_.setSelectedItem(link);
        }
        else {
            cbLinks_.setSelectedItem(nil3);
        }

        if (link != null) {
            setLink(link);
        }
    }

    /**
     *     @return   javax.swing.JButton
     */
    public javax.swing.JButton getBtnAdd() {
        if (btnAdd == null) {
            btnAdd = new JFancyButton();
            btnAdd.setPreferredSize(new Dimension(30, 23));
            btnAdd.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            btnAdd.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    // Get the fromConcept from the Observation
                    if (observation != null) {
                        final Observation parent = (Observation) conceptMap.get(getCbFromConcept().getSelectedItem());
                        addAssociation(parent);
                    }
                }

            });
        }

        return btnAdd;
    }

    /**
     *     This method initializes and returns the Cancel Button
     *     @return   javax.swing.JButton
     */
    public javax.swing.JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new JFancyButton();
            btnCancel.setPreferredSize(new Dimension(30, 23));
            btnCancel.setToolTipText("Cancel");
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/stop.png")));
            btnCancel.addActionListener(getCancelAction());
        }

        return btnCancel;
    }

    /**
     *     Gets the cancelAction attribute of the AssociationEditorPanel object
     *     @return   The cancelAction value
     */
    public ActionAdapter getCancelAction() {
        if (cancelAction == null) {
            cancelAction = new ActionAdapter() {

                public void doAction() {
                    resetDisplay();
                }
            };
        }

        return cancelAction;
    }

    /**
     *     This method initializes cbToConcept. It's underlying model is the <code>ListComboBoxModel</code>
     *     @return   javax.swing.JComboBox
     */
    private javax.swing.JComboBox getCbFromConcept() {
        if (cbFromConcept == null) {
            cbFromConcept = new FancyComboBox();
            cbFromConcept.setToolTipText("From Concept");
            cbFromConcept.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final String displayedFromConcept = cbFromConcept.getSelectedItem().toString();
                        final Observation concept = (Observation) conceptMap.get(displayedFromConcept);
                        changeFromConcept(concept.getConceptName());
                    }
                }

            });
        }

        return cbFromConcept;
    }

    private javax.swing.JComboBox getCbLinks() {
        if (cbLinks == null) {
            cbLinks = new JComboBox();
            cbLinks.setModel(new SearchableComboBoxModel());
            cbLinks.setToolTipText("Links in Knowledgebase");
            cbLinks.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setLink((String) e.getItem());
                    }
                }

            });
        }

        return cbLinks;
    }

    private HierachicalConceptNameComboBox getCbToConcept() {
        if (cbToConcept == null) {
            cbToConcept = new HierachicalConceptNameComboBox(toolBelt.getAnnotationPersistenceService());
            cbToConcept.setToolTipText("To Concept");
            cbToConcept.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent e) {
                    cbToConcept.getEditor().selectAll();
                }
            });
        }

        return cbToConcept;
    }

    private javax.swing.JLabel getForLabel() {
        return forLabel;
    }

    private javax.swing.JLabel getLinkLabel() {
        if (lbl3a == null) {
            lbl3a = new javax.swing.JLabel();
            lbl3a.setHorizontalAlignment(SwingConstants.RIGHT);
            lbl3a.setText("Link");
        }

        return lbl3a;
    }

    private javax.swing.JLabel getSearchLabel() {
        return searchLabel;
    }

    private javax.swing.JTextField getTfLinkName() {
        if (tfLinkName == null) {
            tfLinkName = new javax.swing.JTextField();
            tfLinkName.setText("");
            tfLinkName.setToolTipText("Link Name");
            tfLinkName.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    tfLinkName.setSelectionStart(0);
                    tfLinkName.setSelectionEnd(tfLinkName.getText().length());
                }
            });
        }

        return tfLinkName;
    }

    private javax.swing.JTextField getTfLinkValue() {
        if (tfLinkValue == null) {
            tfLinkValue = new javax.swing.JTextField();
            tfLinkValue.setText("");
            tfLinkValue.setToolTipText("Link Value");
            tfLinkValue.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    tfLinkValue.setSelectionStart(0);
                    tfLinkValue.setSelectionEnd(tfLinkValue.getText().length());
                }

            });
        }

        return tfLinkValue;
    }

    private javax.swing.JTextField getTfSearch() {
        if (tfSearch == null) {
            tfSearch = new javax.swing.JTextField();
            tfSearch.setText("");
            tfSearch.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {

                    /*
                     *  FIXME 20040907 brian: There is a known bug here that occurs
                     *  when enter is pressed repeatedly when tfSearch has focus. This
                     *  bug causes the UI to hang.
                     */
                    final JComboBox cb = getCbLinks();

                    final SearchableComboBoxModel linksModel = (SearchableComboBoxModel) cb.getModel();
                    int startIndex = cb.getSelectedIndex() + 1;
                    if (startIndex >= linksModel.getSize()) {
                        startIndex = 0;
                    }

                    int index = linksModel.searchForItemContaining(tfSearch.getText(), startIndex);
                    if (index > -1) {

                        // Handle if match was found
                        cb.setSelectedIndex(index);
                        cb.hidePopup();
                    }
                    else {

                        // If no match was found search from the start of the
                        // list.
                        if (startIndex > 0) {
                            index = linksModel.searchForItemContaining(tfSearch.getText());

                            if (index > -1) {

                                // Handle if match was found
                                cb.setSelectedIndex(index);
                                cb.hidePopup();
                            }
                        }
                    }
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
     * This method initializes jLabel
     *
     *
     * @return  javax.swing.JLabel
     */
    private javax.swing.JLabel getToLabel() {
        if (lbl4a == null) {
            lbl4a = new javax.swing.JLabel();
            lbl4a.setHorizontalAlignment(SwingConstants.RIGHT);
            lbl4a.setText("To");
        }

        return lbl4a;
    }

    /**
     * This method initializes jLabel1
     *
     *
     * @return  javax.swing.JLabel
     */
    private javax.swing.JLabel getValueLabel() {
        if (lbl5a == null) {
            lbl5a = new javax.swing.JLabel();
            lbl5a.setHorizontalAlignment(SwingConstants.RIGHT);
            lbl5a.setText("Value");
        }

        return lbl5a;
    }

    /**
     * This method initializes this
     *
     *
     */
    private void initialize() {
        final String vgap = "2dlu";
        final String hgap = "2dlu";
        final String columns = "0dlu, right:pref, " + hgap + ", left:pref:grow, " + hgap + ", right:pref, " + hgap +
                               ", left:pref:grow, " + " left:pref";

        final String rows = "0dlu, center:pref, " + vgap + ", center:pref, " + vgap + ", center:pref, " + vgap +
                            ", center:pref";
        final FormLayout layout = new FormLayout(columns, rows);
        setLayout(layout);

        // layout.setRowGroups(new int[][]{ {2, 4, 6, 8}});
        final CellConstraints cc = new CellConstraints();
        add(getSearchLabel(), cc.xy(2, 2));
        add(getCbFromConcept(), cc.xy(4, 2, "fill, default"));
        add(getForLabel(), cc.xy(6, 2));
        add(getTfSearch(), cc.xywh(8, 2, 2, 1, "fill, default"));
        add(getCbLinks(), cc.xywh(4, 4, 6, 1, "fill, default"));
        add(getLinkLabel(), cc.xy(2, 6));
        add(getTfLinkName(), cc.xy(4, 6, "fill, default"));
        add(getToLabel(), cc.xy(6, 6));
        add(getCbToConcept(), cc.xywh(8, 6, 2, 1, "fill, default"));
        add(getValueLabel(), cc.xy(2, 8));
        add(getTfLinkValue(), cc.xywh(4, 8, 3, 1, "fill, default"));
        add(getBtnAdd(), cc.xy(8, 8, "right, default"));
        add(getBtnCancel(), cc.xy(9, 8));
        setFocusPolicy();
        changeFromConcept(ILink.VALUE_NIL);
        addComponentListener(new ComponentAdapter() {

            public void componentShown(final ComponentEvent e) {
                setEnterBehavior();
                getTfSearch().requestFocus();
            }
        });

        /*
         * If the cache is cleared then we should update this editor
         */
        toolBelt.getPersistenceCache().addCacheClearedListener(new CacheClearedListener() {

            public void afterClear(CacheClearedEvent evt) {
                String conceptName = (observation == null) ? null : observation.getConceptName();
                changeFromConcept(conceptName);
            }
            public void beforeClear(CacheClearedEvent evt) {

                // Do nothing
            }

        });
    }

    /**
     * Removes an <code>ActionListener</code>.
     *
     * @param  l  the <code>ActionListener</code> to remove
     */
    public void removeActionListener(final ActionListener l) {
        getBtnAdd().removeActionListener(l);
        getBtnCancel().removeActionListener(l);
    }

    /**
     *  Reset the editor to it's default state
     */
    public void resetDisplay() {
        getTfSearch().setText("");
        setLink(this.nil3);
    }

    /**
     *  Sets the enabled attribute of the AssociationEditorPanel object
     *
     * @param  isEnabled The new enabled value
     */
    public void setEnabled(final boolean isEnabled) {
        getBtnCancel().setEnabled(isEnabled);
        getBtnAdd().setEnabled(isEnabled);
        getCbToConcept().setEnabled(isEnabled);
        getCbFromConcept().setEnabled(isEnabled);
        getCbLinks().setEnabled(isEnabled);
        getSearchLabel().setEnabled(isEnabled);
        getForLabel().setEnabled(isEnabled);
        getToLabel().setEnabled(isEnabled);
        getValueLabel().setEnabled(isEnabled);
        getLinkLabel().setEnabled(isEnabled);
        getTfSearch().setEnabled(isEnabled);
        getTfLinkName().setEnabled(isEnabled);
        getTfLinkValue().setEnabled(isEnabled);
    }

    private void setEnterBehavior() {
        getRootPane().setDefaultButton(getBtnAdd());
    }

    /**
     * Set the focus policy for moving focus with the keyboard in this component
     * <br/><em>NOTE:</em> When the focus cycle reaches the last component,
     * this focus policy will set the <tt>focusCycleRoot</tt> to false to get
     * the next or previous component to transfer focus out of the component.
     * Then, it sets the property back to true.
     *
     */
    private void setFocusPolicy() {
        final FocusTraversalPolicy policy = new FocusTraversalPolicy() {

            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                if ((aComponent == tfSearch) || (aComponent == cbLinks) || (aComponent == tfLinkName) ||
                        (aComponent == cbToConcept)) {
                    return btnAdd;
                }

                if (aComponent == tfLinkValue) {
                    return btnAdd;
                }

                if (aComponent == btnAdd) {
                    return btnCancel;
                }

                if (aComponent == btnCancel) {
                    setFocusCycleRoot(false);
                    Component next = getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentAfter(
                        getFocusCycleRootAncestor(), btnCancel);
                    setFocusCycleRoot(true);

                    return next;
                }

                return null;
            }
            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                if ((aComponent == tfSearch) || (aComponent == cbFromConcept)) {
                    setFocusCycleRoot(false);
                    Component next = getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentBefore(
                        getFocusCycleRootAncestor(), AssociationEditorPanel.this);
                    setFocusCycleRoot(true);

                    return next;
                }

                if ((aComponent == tfLinkValue) || (aComponent == cbToConcept) || (aComponent == tfLinkName) ||
                        (aComponent == cbLinks)) {
                    return tfSearch;
                }

                if (aComponent == btnAdd) {
                    return tfSearch;
                }

                if (aComponent == btnCancel) {
                    return btnAdd;
                }

                return null;
            }
            public Component getDefaultComponent(Container focusCycleRoot) {
                return tfSearch;
            }
            public Component getFirstComponent(Container focusCycleRoot) {
                return tfSearch;
            }
            public Component getLastComponent(Container focusCycleRoot) {
                return btnCancel;
            }
        };
        setFocusTraversalPolicy(policy);
        setFocusCycleRoot(true);
    }

    /**
     * <p><!-- Method description --></p>
     *
     *
     * @param link
     */
    private void setLink(final String link) {

        // reset the link, to, and value fields on the GUI
        final JTextField tfLinkName_ = getTfLinkName();
        final JTextField tfLinkValue_ = getTfLinkValue();
        final HierachicalConceptNameComboBox cbToConcept_ = getCbToConcept();

        tfLinkName_.setText(nil);

        cbToConcept_.setOpaque(true);
        cbToConcept_.setEnabled(true);
        tfLinkValue_.setText(nil);
        tfLinkValue_.setOpaque(false);
        tfLinkValue_.setEditable(false);

        // We need to handle both forms: "link to value" and "link | to | value"
        String delim = "|";
        final int ix = link.indexOf(delim);
        if (ix == -1) {

            // "|" was not found so use white space instead
            delim = " \t\n\r\f";
        }

        final StringTokenizer parser = new StringTokenizer(link, delim);
        final String linkName = parser.nextToken().trim();
        final String toConcept = parser.nextToken().trim();
        final String linkValue = parser.nextToken().trim();
        tfLinkName_.setText(linkName);
        tfLinkValue_.setText(linkValue);

        if (toConcept.equals(nil) || toConcept.equals("self")) {
            ConceptName conceptName = new SimpleConceptNameBean(toConcept, ConceptNameTypes.COMMON.getName());
            cbToConcept_.addItem(conceptName);
        }
        else {

            // Retrieve the child concepts and add to gui
            try {
                final Concept c = toolBelt.getAnnotationPersistenceService().findConceptByName(toConcept);
                cbToConcept_.setConcept(c);
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                ConceptName conceptName = new SimpleConceptNameBean(toConcept, ConceptNameTypes.COMMON.getName());
                cbToConcept_.addItem(conceptName);
            }
        }

        cbToConcept_.setSelectedItem(toConcept);
        cbToConcept_.setOpaque(true);
        cbToConcept_.setEnabled(true);
        tfLinkValue_.setOpaque(true);
        tfLinkValue_.setEditable(true);
    }

    /**
     * This sets the targets that are being edited
     *
     * @param observation The observation who's associations are being edited
     * @param association The association to be edited. If this null, then a new
     *  assocation will be added.
     */
    public void setTarget(final Observation observation, final Association association) {
        this.observation = observation;
        this.association = association;

        /*
         * If an association is set we're editing it. If not we're adding it.
         *
         * Here we figure out what the intial link and conceptName should be.
         */
        String defaultFromConcept = null;
        String defaultLink = nil3;
        if (observation != null) {
            updateCbFromConcepts();
            defaultFromConcept = observation.getConceptName();

            if (association != null) {
                defaultFromConcept = association.getFromConcept();
                defaultLink = association.getLinkName() + " | " + association.getToConcept() + " | " +
                              association.getLinkValue();
                getBtnAdd().setToolTipText("Accept Edits");
            }
            else {
                getBtnAdd().setToolTipText("Add Association");
            }
        }

        /*
         * Update the fromConcept
         */
        getCbFromConcept().setSelectedItem(defaultFromConcept);
        changeFromConcept(defaultFromConcept);

        /*
         * update the link
         */
        final SortedComboBoxModel linksModel = (SortedComboBoxModel) getCbLinks().getModel();
        if (!linksModel.contains(defaultLink)) {
            linksModel.addElement(defaultLink);
        }

        getCbLinks().setSelectedItem(defaultLink);
        setLink(defaultLink);
    }

    /**
     * Sets the strings of possible concept names in a cbFromConcept. The old way was to
     * add concept names from Association.getFromConcept() and
     * Observation.getConceptName(). The new ways is to only add observation.getConceptName()
     *
     *
     */
    private void updateCbFromConcepts() {
        final JComboBox cb = getCbFromConcept();
        final SortedComboBoxModel fromConceptModel = (SortedComboBoxModel) cb.getModel();
        fromConceptModel.clear();
        conceptMap.clear();

        if (observation != null) {
            String concept = observation.getConceptName();
            fromConceptModel.addElement(concept);
            conceptMap.put(concept, observation);

        }
    }
}
