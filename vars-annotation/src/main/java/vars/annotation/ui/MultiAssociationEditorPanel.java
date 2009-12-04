/*
 * @(#)MultiAssociationEditorPanel.java   2009.11.18 at 04:22:42 PST
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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.bushe.swing.event.EventBus;
import org.mbari.swing.JFancyButton;
import org.mbari.swing.ListListModel;
import org.mbari.swing.SearchableComboBoxModel;
import org.mbari.swing.SortedComboBoxModel;
import org.mbari.util.SortedArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.ILink;
import vars.annotation.AnnotationPersistenceService;
import vars.annotation.Association;
import vars.annotation.Observation;
import vars.annotation.ui.Lookup;
import vars.annotation.ui.ToolBelt;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptDAO;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.LinkTemplate;
import vars.shared.ui.ConceptNameComboBox;

/**
 * <p>More or less a copy and paste job reproducing the functionality of
 * <code>AssociationEditorPanel</code> with the side effects removed from the
 * UI code. Now works similar to a <code>JFileChooser</code>, use the
 * <code>showDialog</code> method to show the Association editor and then use
 * <code>getUserGeneratedAssociation</code> to get the association the user
 * made.</p>
 *
 * @author  <a href="http://www.mbari.org">MBARI</a>
 * @version  $Id: MultiAssociationEditorPanel.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class MultiAssociationEditorPanel extends JPanel {

    /**
     * Return value from <code>showDialog</code> if a new
     * <code>Association</code> is created.
     */
    public final static int ASSOCIATION_CREATED_OPTION = 234;

    /**
     * Return value from <code>showDialog</code> if the cancel/stop option is
     * chosen
     */
    public final static int CANCEL_OPTION = 500;

    /**
     * Return value from <code>showDialog</code> if an error occurs while
     * creating a new <code>Association</code>
     */
    public final static int ERROR_OPTION = 987;
    private final static String nil = "nil";
    private final static String nil3 = nil + " | " + nil + " | " + nil;
    private javax.swing.JButton addAssociationButton = null;
    private javax.swing.JButton cancelAssociationButton = null;
    private JDialog dialog = null;
    private javax.swing.JComboBox fromConceptComboBox = null;
    private javax.swing.JLabel linkLabel = null;
    private javax.swing.JTextField linkNameTextField = null;
    private javax.swing.JTextField linkValueTextField = null;
    private javax.swing.JComboBox linksComboBox = null;
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     *     Stores a list of links available in the KnowledgeBase for cbLinks
     */
    SearchableComboBoxModel linksModel = new SearchableComboBoxModel();
    private javax.swing.JTextField searchTextField = null;

    /**
     *     Drop down list of toConcepts to link an association/observation to
     */
    private javax.swing.JComboBox toConceptComboBox = null;
    private final javax.swing.JLabel searchLabel = new JLabel("Search");
    private int returnValue = ERROR_OPTION;

    /**
     *     Underlying storage for fromConceptModel and cbFromConcept. Stores Strings
     *     of the Observations 'fromConcept' and all child Associations 'toConcepts'.
     */
    private final java.util.List fromConceptList = new SortedArrayList();
    private final javax.swing.JLabel forLabel = new JLabel("for");
    private javax.swing.JLabel toLabel = null;
    private javax.swing.JLabel valueLabel = null;
    private Observation observation;
    private final ToolBelt toolBelt;
    private Association userGeneratedAssociation;

    /**
     * This is the default constructor
     *
     * @param toolBelt
     */
    public MultiAssociationEditorPanel(ToolBelt toolBelt) {
        this("physical object", toolBelt);
    }

    /**
     * Constructor for the MultiAssociationEditorPanel object
     *
     * @param  conceptName Description of the Parameter
     * @param toolBelt
     */
    public MultiAssociationEditorPanel(final String conceptName, ToolBelt toolBelt) {
        super();
        this.toolBelt = toolBelt;
        userGeneratedAssociation = toolBelt.getAnnotationFactory().newAssociation();
        initialize();
        addComponentListener(new ComponentAdapter() {

            public void componentShown(final ComponentEvent e) {
                setEnterBehavior();
                getSearchTextField().requestFocus();
            }
        });
        changeFromConcept(conceptName);
    }

    /**
     * Called by the UI when the user hits the Approve button This method causes
     * an action event to fire with the command string equal to
     * <code>APPROVE_SELECTION</code>.
     *
     * @see  #APPROVE_SELECTION
     */
    public void approveSelection() {
        returnValue = ASSOCIATION_CREATED_OPTION;

        if (dialog != null) {
            dialog.setVisible(false);
        }
    }

    /**
     * Called by the UI when the user chooses the Cancel button. This method
     * causes an action event to fire with the command string equal to
     * <code>CANCEL_SELECTION</code>.
     *
     * @see  #CANCEL_SELECTION
     */
    public void cancelSelection() {
        returnValue = CANCEL_OPTION;

        if (dialog != null) {
            dialog.setVisible(false);
        }
    }

    /**
     * @param  fromConcept
     */
    private void changeFromConcept(final String fromConcept) {

        // Get the currently selected link
        final JComboBox cbLinks_ = getLinksComboBox();
        final String link = (String) cbLinks_.getSelectedItem();

        // Clear the model and set the default
        linksModel.clear();
        linksModel.addElement(nil3);

        if (log.isDebugEnabled()) {
            log.debug("Changing fromConcept to " + fromConcept);
        }

        final Collection<String> linkTemplatesAsStrings = new TreeSet<String>();
        try {
            Collection<LinkTemplate> linkTemplates = null;
            AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();

            // Get the possible linkTemplates specific to a particular concept
            if ((fromConcept != null) && (!fromConcept.equals(ILink.VALUE_NIL))) {

                final Concept c = service.findConceptByName(fromConcept);

                if (c == null) {
                    linkTemplates = new ArrayList<LinkTemplate>();
                }
                else {
                    linkTemplates = service.findLinkTemplatesFor(c);
                }

            }
            else {
                final Concept c = service.findRootConcept();
                linkTemplates = new ArrayList<LinkTemplate>(c.getConceptMetadata().getLinkTemplates());
            }

            // Convert the link templates to their string representations
            for (final Iterator<LinkTemplate> i = linkTemplates.iterator(); i.hasNext(); ) {
                final LinkTemplate lt = i.next();
                linkTemplatesAsStrings.add(lt.stringValue());
            }
        }
        catch (final Exception e) {
            EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
        }

        if (linkTemplatesAsStrings != null) {
            linksModel.addAll(linkTemplatesAsStrings);
        }

        // If the new model contains the previously selected link use the link.
        // Otherwise set it to nil3
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
     *     This method initializes jButton
     *     @return   javax.swing.JButton
     */
    public javax.swing.JButton getAddAssociationButton() {
        if (addAssociationButton == null) {
            addAssociationButton = new JFancyButton();
            addAssociationButton.setPreferredSize(new Dimension(30, 23));
            addAssociationButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/add.png")));
            addAssociationButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    userGeneratedAssociation.setLinkName(getLinkNameTextField().getText());
                    userGeneratedAssociation.setToConcept((String) getToConceptComboBox().getSelectedItem());
                    userGeneratedAssociation.setLinkValue(getLinkValueTextField().getText());
                    approveSelection();
                }

            });
        }

        return addAssociationButton;
    }

    /**
     *     This method initializes jButton
     *     @return   javax.swing.JButton
     */
    public javax.swing.JButton getCancelAssociationButton() {
        if (cancelAssociationButton == null) {
            cancelAssociationButton = new JFancyButton();
            cancelAssociationButton.setPreferredSize(new Dimension(30, 23));
            cancelAssociationButton.setToolTipText("Cancel");
            cancelAssociationButton.setIcon(new ImageIcon(getClass().getResource("/images/vars/annotation/stop.png")));
            cancelAssociationButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    cancelSelection();
                    MultiAssociationEditorPanel.this.setVisible(false);
                }
            });
        }

        return cancelAssociationButton;
    }

    private javax.swing.JLabel getForLabel() {
        return forLabel;
    }

    private javax.swing.JComboBox getFromConceptComboBox() {
        if (fromConceptComboBox == null) {

            /*
             *  HACK ALERT!!! This code is MBARI specific.
             *
             *  Add the 1st 3 levels of the hierarchy from the knowledgebase. This
             *  will give us access to all the linkTemplates that MBARI uses.
             */
            final Set<String> conceptNames = new TreeSet<String>();

            AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();

            // Stores conceptNames as Strings
            String rootName = null;
            try {
                final Concept root = service.findRootConcept();
                rootName = root.getPrimaryConceptName().getName();
                conceptNames.add(rootName);

                for (Concept concept : new ArrayList<Concept>(root.getChildConcepts())) {
                    conceptNames.add(concept.getPrimaryConceptName().getName());

                    for (Concept grandChild : new ArrayList<Concept>(concept.getChildConcepts())) {
                        conceptNames.add(grandChild.getPrimaryConceptName().getName());
                    }
                }
            }
            catch (final Exception e) {
                conceptNames.clear();

                // TODO Try to get rid of this hard wired name
                rootName = "physical object";
                conceptNames.add(rootName);
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
            }

            fromConceptComboBox = new javax.swing.JComboBox();
            fromConceptComboBox.setToolTipText("From Concept");

            // Model for the fromConceptList
            for (final Iterator<String> i = conceptNames.iterator(); i.hasNext(); ) {
                final String name = i.next();
                fromConceptList.add(name);
            }

            changeFromConcept(rootName);
            fromConceptComboBox.setSelectedItem(rootName);
            fromConceptComboBox.addItemListener(new ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        final String conceptName = e.getItem().toString();
                        changeFromConcept(conceptName);
                    }
                }

            });

        }

        return fromConceptComboBox;
    }

    private javax.swing.JLabel getLinkLabel() {
        if (linkLabel == null) {
            linkLabel = new javax.swing.JLabel();
            linkLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            linkLabel.setText("Link");
        }

        return linkLabel;
    }

    private javax.swing.JTextField getLinkNameTextField() {
        if (linkNameTextField == null) {
            linkNameTextField = new javax.swing.JTextField();
            linkNameTextField.setText("");
            linkNameTextField.setToolTipText("Link Name");
            linkNameTextField.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    linkNameTextField.setSelectionStart(0);
                    linkNameTextField.setSelectionEnd(linkNameTextField.getText().length());
                }

            });
        }

        return linkNameTextField;
    }

    private javax.swing.JTextField getLinkValueTextField() {
        if (linkValueTextField == null) {
            linkValueTextField = new javax.swing.JTextField();
            linkValueTextField.setText("");
            linkValueTextField.setToolTipText("Link Value");
            linkValueTextField.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    linkValueTextField.setSelectionStart(0);
                    linkValueTextField.setSelectionEnd(linkValueTextField.getText().length());
                }

            });
        }

        return linkValueTextField;
    }

    private javax.swing.JComboBox getLinksComboBox() {
        if (linksComboBox == null) {
            linksComboBox = new javax.swing.JComboBox();
            linksComboBox.setToolTipText("Links in Knowledgebase");
            linksComboBox.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(final ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        setLink((String) e.getItem());
                    }
                }

            });
            linksComboBox.setModel(linksModel);
        }

        return linksComboBox;
    }

    private javax.swing.JLabel getSearchLabel() {
        return searchLabel;
    }

    private javax.swing.JTextField getSearchTextField() {
        if (searchTextField == null) {
            searchTextField = new javax.swing.JTextField();
            searchTextField.setText("");
            searchTextField.addKeyListener(new KeyAdapter() {

                // Search for a glob match as keys are typed.
                public void keyPressed(final KeyEvent e) {
                    final int keyCode = e.getKeyCode();

                    /*
                     * Ignore ENTERs. They're handled by the actionListener not
                     * the keylistener
                     */
                    if (keyCode != KeyEvent.VK_ENTER) {
                        String search = searchTextField.getText();

                        /* KeyEvents occur BEFORE tfSearch gets updated. So,
                         * we need to grab the character and append it to the
                         * existing text in tfSearch.
                         */
                        if (keyCode == KeyEvent.VK_BACK_SPACE) {
                            if (search.length() > 0) {
                                search = search.substring(0, search.length() - 1);
                            }
                        }
                        else if (keyCode == KeyEvent.VK_DELETE) {

                            // What to do?
                        }
                        else {
                            search = search + e.getKeyChar();
                        }

                        // Find the search string
                        final int index = linksModel.searchForItemContaining(search);
                        if (index > -1) {
                            final JComboBox cb = getLinksComboBox();
                            cb.setSelectedIndex(index);
                            cb.hidePopup();
                        }
                    }
                }
            });
            searchTextField.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    final JComboBox cb = getLinksComboBox();
                    final int startIndex = cb.getSelectedIndex() + 1;
                    int index = linksModel.searchForItemContaining(searchTextField.getText(), startIndex);
                    if (index > -1) {

                        // Handle if match was found
                        cb.setSelectedIndex(index);
                        cb.hidePopup();
                    }
                    else {

                        // If no match was found search from the start of the list.
                        if (startIndex > 0) {
                            index = linksModel.searchForItemContaining(searchTextField.getText());

                            if (index > -1) {

                                // Handle if match was found
                                cb.setSelectedIndex(index);
                                cb.hidePopup();
                            }
                        }
                    }
                }

            });
            searchTextField.addFocusListener(new FocusAdapter() {

                public void focusGained(final FocusEvent fe) {
                    searchTextField.setSelectionStart(0);
                    searchTextField.setSelectionEnd(searchTextField.getText().length());
                }

            });
        }

        return searchTextField;
    }

    private javax.swing.JComboBox getToConceptComboBox() {
        if (toConceptComboBox == null) {
            toConceptComboBox = new ConceptNameComboBox();
            toConceptComboBox.setToolTipText("To Concept");
        }

        return toConceptComboBox;
    }

    private javax.swing.JLabel getToLabel() {
        if (toLabel == null) {
            toLabel = new javax.swing.JLabel();
            toLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            toLabel.setText("To");
        }

        return toLabel;
    }

    /**
     *     @return   Returns the userGeneratedAssociation.
     */
    public Association getUserGeneratedAssociation() {
        return userGeneratedAssociation;
    }

    private javax.swing.JLabel getValueLabel() {
        if (valueLabel == null) {
            valueLabel = new javax.swing.JLabel();
            valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            valueLabel.setText("Value");
        }

        return valueLabel;
    }

    private void initialize() {
        final String vgap = "2dlu";
        final String hgap = "2dlu";
        final String columns = "0dlu, right:pref, " + hgap + ", left:pref:grow, " + hgap + ", right:pref, " + hgap +
                               ", left:pref:grow, " + " left:pref";

        // String columns = "0dlu, right:pref, " + hgap + ", left:pref, "
        // + hgap + ", right:pref, " + hgap + ", left:pref, "
        // + " left:pref";
        final String rows = "0dlu, center:pref, " + vgap + ", center:pref, " + vgap + ", center:pref, " + vgap +
                            ", center:pref";
        final FormLayout layout = new FormLayout(columns, rows);
        setLayout(layout);

        // layout.setRowGroups(new int[][]{ {2, 4, 6, 8}});
        final CellConstraints cc = new CellConstraints();
        add(getSearchLabel(), cc.xy(2, 2));
        add(getFromConceptComboBox(), cc.xy(4, 2, "fill, default"));
        add(getForLabel(), cc.xy(6, 2));
        add(getSearchTextField(), cc.xywh(8, 2, 2, 1, "fill, default"));
        add(getLinksComboBox(), cc.xywh(4, 4, 6, 1, "fill, default"));
        add(getLinkLabel(), cc.xy(2, 6));
        add(getLinkNameTextField(), cc.xy(4, 6, "fill, default"));
        add(getToLabel(), cc.xy(6, 6));
        add(getToConceptComboBox(), cc.xywh(8, 6, 2, 1, "fill, default"));
        add(getValueLabel(), cc.xy(2, 8));
        add(getLinkValueTextField(), cc.xywh(4, 8, 3, 1, "fill, default"));
        add(getAddAssociationButton(), cc.xy(8, 8, "right, default"));
        add(getCancelAssociationButton(), cc.xy(9, 8));
        setFocusPolicy();
        log.info("Finished initialization...");
    }

    /**
     *  Sets the enabled attribute of the MultiAssociationEditorPanel object
     *
     * @param  isEnabled The new enabled value
     */
    public void setEnabled(final boolean isEnabled) {
        getCancelAssociationButton().setEnabled(isEnabled);
        getAddAssociationButton().setEnabled(isEnabled);
        getToConceptComboBox().setEnabled(isEnabled);
        getFromConceptComboBox().setEnabled(isEnabled);
        getLinksComboBox().setEnabled(isEnabled);
        getSearchLabel().setEnabled(isEnabled);
        getForLabel().setEnabled(isEnabled);
        getToLabel().setEnabled(isEnabled);
        getValueLabel().setEnabled(isEnabled);
        getLinkLabel().setEnabled(isEnabled);
        getSearchTextField().setEnabled(isEnabled);
        getLinkNameTextField().setEnabled(isEnabled);
        getLinkValueTextField().setEnabled(isEnabled);
    }

    /**
     * <p><!-- Method description --></p>
     *
     */
    private void setEnterBehavior() {
        getRootPane().setDefaultButton(getAddAssociationButton());
    }

    /**
     * Set the focus policy for moving focus with the keyboard in this component
     * <br/><em>NOTE:</em> When the focus cycle reaches the last component,
     * this focus policy will set the <tt>focusCycleRoot</tt> to false to get
     * the next or previous component to transfer focus out of the component.
     * Then, it sets the property back to true.
     */
    private void setFocusPolicy() {
        final FocusTraversalPolicy policy = new FocusTraversalPolicy() {

            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                if ((aComponent == searchTextField) || (aComponent == linksComboBox) ||
                        (aComponent == linkNameTextField) || (aComponent == toConceptComboBox)) {
                    return addAssociationButton;
                }

                if (aComponent == linkValueTextField) {
                    return addAssociationButton;
                }

                if (aComponent == addAssociationButton) {
                    return cancelAssociationButton;
                }

                if (aComponent == cancelAssociationButton) {
                    setFocusCycleRoot(false);
                    Component next = getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentAfter(
                        getFocusCycleRootAncestor(), cancelAssociationButton);
                    setFocusCycleRoot(true);

                    return next;
                }

                return null;
            }
            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                if ((aComponent == searchTextField) || (aComponent == fromConceptComboBox)) {
                    setFocusCycleRoot(false);
                    Component next = getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentBefore(
                        getFocusCycleRootAncestor(), MultiAssociationEditorPanel.this);
                    setFocusCycleRoot(true);

                    return next;
                }

                if ((aComponent == linkValueTextField) || (aComponent == toConceptComboBox) ||
                        (aComponent == linkNameTextField) || (aComponent == linksComboBox)) {
                    return searchTextField;
                }

                if (aComponent == addAssociationButton) {
                    return searchTextField;
                }

                if (aComponent == cancelAssociationButton) {
                    return addAssociationButton;
                }

                return null;
            }
            public Component getDefaultComponent(Container focusCycleRoot) {
                return searchTextField;
            }
            public Component getFirstComponent(Container focusCycleRoot) {
                return searchTextField;
            }
            public Component getLastComponent(Container focusCycleRoot) {
                return cancelAssociationButton;
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
        final JTextField tfLinkName_ = getLinkNameTextField();
        final JTextField tfLinkValue_ = getLinkValueTextField();
        final JComboBox cbToConcept_ = getToConceptComboBox();
        final SortedComboBoxModel cbModel = (SortedComboBoxModel) cbToConcept_.getModel();
        tfLinkName_.setText(nil);
        cbModel.clear();
        cbToConcept_.setOpaque(true);
        cbToConcept_.setEnabled(true);
        tfLinkValue_.setText(nil);
        tfLinkValue_.setOpaque(false);
        tfLinkValue_.setEditable(false);

        // We need to handle both forms: "link to value" and "link | to |
        // value"
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
            cbModel.addElement(toConcept);
            cbToConcept_.setSelectedItem(toConcept);
            tfLinkValue_.setOpaque(true);
            tfLinkValue_.setEditable(true);
        }
        else {
            Collection<String> childList = new ArrayList<String>();

            // Retrieve the child concepts and add to GUI
            try {
                AnnotationPersistenceService service = toolBelt.getAnnotationPersistenceService();
                ConceptDAO dao = service.getReadOnlyConceptDAO();
                Collection<ConceptName> conceptNames = dao.findDescendentNames(dao.findByName(toConcept));
                for (ConceptName conceptName : conceptNames) {
                    childList.add(conceptName.getName());
                }
            }
            catch (final Exception e) {
                EventBus.publish(Lookup.TOPIC_NONFATAL_ERROR, e);
                childList.add(toConcept);
            }

            cbModel.addAll(childList);
        }

        if (cbModel.contains(toConcept)) {
            cbToConcept_.setSelectedItem(toConcept);
        }
        else {
            cbToConcept_.setSelectedIndex(0);
        }

        cbToConcept_.setOpaque(true);
        cbToConcept_.setEnabled(true);
        tfLinkValue_.setOpaque(true);
        tfLinkValue_.setEditable(true);
    }

    /**
     * Set the ListModel that contains a list of associations for an annotation.
     * This method sets the Concept to the observation. This method is called if
     * adding a new annotation.
     *
     * @param  associationListModel This <code>ListListModel</code> must use an
     *            <code>AssociationList</code> as the backing storage
     *            <code>List</code>
     */
    public void setListModel(final ListListModel associationListModel) {
        setListModel(associationListModel, null);
    }

    /**
     * Set the ListModel that contains a list of associations for an annotation.
     * This method sets the Concept to the Associations 'fromConcept'. This
     * method is called if editing an existing annotation.
     *
     * @param  associationListModel This <code>ListListModel</code> must use an
     *            <code>AssociationList</code> as the backing storage
     *            <code>List</code>
     * @param  association The association to set as the current concept. If null the
     *            current concept is set to the observation
     */
    public void setListModel(final ListListModel associationListModel, final Association association) {

        // Set the default fromConcept. If adding a new concept the default is
        // the observations conceptName. If editing an association, the default
        // is the association's fromConcept
        String defaultFromConcept = null;
        String link = null;
        if (association == null) {
            defaultFromConcept = observation.getConceptName();
            link = nil3;
            getAddAssociationButton().setToolTipText("Add Association");
        }
        else {
            defaultFromConcept = association.getFromConcept();
            link = association.getLinkName() + " | " + association.getToConcept() + " | " + association.getLinkValue();
            getAddAssociationButton().setToolTipText("Accept Edits");
        }

        getFromConceptComboBox().setSelectedItem(defaultFromConcept);
        changeFromConcept(defaultFromConcept);

        if (!linksModel.contains(link)) {
            linksModel.addElement(link);
        }

        getLinksComboBox().setSelectedItem(link);
        setLink(link);
    }

    /**
     *     @param userGeneratedAssociation  The userGeneratedAssociation to set.
     *     @uml.property  name="userGeneratedAssociation"
     */
    public void setUserGeneratedAssociation(final Association userGeneratedAssociation) {
        this.userGeneratedAssociation = userGeneratedAssociation;
    }

    /**
     *  Description of the Method
     *
     * @param  parent Description of the Parameter
     * @param  title Description of the Parameter
     * @param  displayLocation Description of the Parameter
     * @return  Description of the Return Value
     */
    public int showDialog(final Frame parent, final String title, final Point displayLocation) {
        if (userGeneratedAssociation != null) {

            /*
             *  TODO achase 20040802 I don't know how to set the proper value in
             *  the linksComboBox and not being able to set that value prevents
             *  the call to toConceptComboBox.setSelectedItem from working
             *  because the proper item is not in the combobox until the
             *  linksComboBox is set correctly
             */
            this.linkValueTextField.setText(userGeneratedAssociation.getLinkValue());
            this.linkNameTextField.setText(userGeneratedAssociation.getLinkName());
            this.toConceptComboBox.setSelectedItem(userGeneratedAssociation.getToConcept());
        }

        dialog = new JDialog(parent, title, true);
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocation(displayLocation);
        dialog.addWindowListener(new WindowAdapter() {

            public void windowClosing(final WindowEvent e) {
                returnValue = CANCEL_OPTION;
            }
        });
        returnValue = ERROR_OPTION;
        dialog.setVisible(true);
        dialog.dispose();
        dialog = null;

        return returnValue;
    }
}



//@jve:visual-info decl-index=0 visual-constraint="10,10"

