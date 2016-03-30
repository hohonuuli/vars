/*
 * @(#)NamesEditorPanel.java   2009.10.29 at 12:43:22 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mbari.swing.ListListModel;
import org.mbari.text.IgnoreCaseToStringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.UserAccount;
import vars.knowledgebase.Concept;
import vars.knowledgebase.ConceptName;
import vars.knowledgebase.ConceptNameTypes;
import vars.shared.ui.ILockableEditor;

/**
 *
 */
public class NamesEditorPanel extends EditorPanel implements ILockableEditor {

    /**
     *
     */
    private static final long serialVersionUID = -3451562418328420320L;
    private static final Logger log = LoggerFactory.getLogger(NamesEditorPanel.class);
    private JTextField authorField = null;
    private JLabel authorLabel = null;
    private EditorButtonPanel buttonPanel = null;
    private JLabel commonLabel = null;
    private JRadioButton commonRb = null;
    private JLabel formerLabel = null;
    private JRadioButton formerRb = null;
    private JTextField nameField = null;
    private JLabel nameLabel = null;
    private JList namesList = null;
    private JPanel namesPanel = null;
    private JLabel primaryLabel = null;
    private JRadioButton primaryRb = null;
    private JPanel rbPanel = null;
    private JScrollPane scrollPane = null;
    private JLabel synonymLabel = null;
    private JRadioButton synonymRb = null;
    private JLabel typeLabel = null;
    private JPanel viewPanel = null;
    private Comparator stringComparator = new IgnoreCaseToStringComparator();
    private final NamesEditorPanelController controller;
    private ItemListener rbItemListener;

    /**
     * This is the default constructor
     *
     *
     * @param toolBelt
     */
    @Inject
    public NamesEditorPanel(ToolBelt toolBelt) {
        super(toolBelt);
        controller = new NamesEditorPanelController(this);
        initialize();
        setLocked(isLocked());
    }

    /**
     * This method initializes authorField
     * @return  javax.swing.JTextField
     */
    JTextField getAuthorField() {
        if (authorField == null) {
            authorField = new JTextField();
        }

        return authorField;
    }

    /**
     * This method initializes buttonPanel
     * @return  javax.swing.JPanel
     */
    private EditorButtonPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new EditorButtonPanel();

            JButton deleteButton = buttonPanel.getDeleteButton();
            deleteButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    final String selectedName = (String) getNamesList().getSelectedValue();
                    final Concept concept = getConcept();
                    final String primaryName = concept.getPrimaryConceptName().getName();
                    final ConceptName conceptName = concept.getConceptName(selectedName);
                    controller.deleteConceptName(conceptName);
                }
            });
            deleteButton.setEnabled(false);

            JButton updateButton = buttonPanel.getUpdateButton();
            updateButton.setEnabled(false);
            updateButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {


                    /*
                        * Get the name that's selected in the UI for the current concept.
                        */
                    final String selectedName = (String) getNamesList().getSelectedValue();
                    final Concept concept = getConcept();
                    ConceptName oldConceptName = concept.getConceptName(selectedName);
                    boolean okToProceed = true;

                    /*
                     * Retrieve the parameters from the interface
                     */
                    final String name = getNameField().getText();
                    int value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                        "Do want to change '" + selectedName + "' to '" + name + "'?", "VARS - Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    final String author = getAuthorField().getText();
                    String nameType = ConceptNameTypes.SYNONYM.toString();
                    if (getPrimaryRb().isSelected()) {
                        nameType = ConceptNameTypes.PRIMARY.toString();
                    }
                    else if (getCommonRb().isSelected()) {
                        nameType = ConceptNameTypes.COMMON.toString();
                    }
                    else if (getFormerRb().isSelected()) {
                        nameType = ConceptNameTypes.FORMER.toString();
                    }

                    /*
                     * Exit if the values are invalid
                     */
                    okToProceed = (value == JOptionPane.YES_OPTION) && !name.equals(selectedName) &&
                                  (name.equals("")) && (selectedName != null) && (oldConceptName != null);

                    final UserAccount userAccount = StateLookup.getUserAccount();

                    /*
                     * Warn users if they are trying to change the primary name.
                     */
                    if (okToProceed && nameType.equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString())) {
                        value = JOptionPane.showConfirmDialog(NamesEditorPanel.this,
                                "Are you really sure that you want\n" + "to change the primary name?",
                                "VARS - Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        okToProceed = (value == JOptionPane.YES_OPTION);
                    }


                    controller.updateConceptName(concept, name, author, nameType, selectedName, userAccount);
                }
            });

            JButton newButton = buttonPanel.getNewButton();
            newButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.newConceptName();
                }
            });
        }

        return buttonPanel;
    }

    /**
     * This method initializes commonRb
     * @return  javax.swing.JRadioButton
     */
    private JRadioButton getCommonRb() {
        if (commonRb == null) {
            commonRb = new JRadioButton();
            commonRb.addItemListener(getRbItemListener());
        }

        return commonRb;
    }

    private JRadioButton getFormerRb() {
        if (formerRb == null) {
            formerRb = new JRadioButton();
            formerRb.addItemListener(getRbItemListener());
        }
        return formerRb;
    }

    /**
     * This method initializes deleteButton
     *
     * @return javax.swing.JButton
     */
    private JButton getDeleteButton() {
        return getButtonPanel().getDeleteButton();
    }

    /**
     * This method initializes nameField
     * @return  javax.swing.JTextField
     */
    JTextField getNameField() {
        if (nameField == null) {
            nameField = new JTextField();
        }

        return nameField;
    }

    JList getNamesList() {
        if (namesList == null) {
            namesList = new JList();
            namesList.setPreferredSize(new java.awt.Dimension(0, 120));
            namesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            /*
             * We're using a custom model here because Swing's DefaultListModel
             * is a pain to work with.
             */
            namesList.setModel(new ListListModel(new ArrayList()));

            /*
             * When an item in the namesList is selected we need to update the
             * editor fields
             */
            namesList.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    String selectedName = (String) getNamesList().getSelectedValue();
                    if (log.isDebugEnabled()) {
                        log.debug("Selected '" + selectedName + "' in the name editor panel");
                    }

                    setSelectedConceptName(selectedName);
                }

            });

        }

        return namesList;
    }

    private JPanel getNamesPanel() {
        if (namesPanel == null) {
            namesPanel = new JPanel();
            namesPanel.setLayout(new BorderLayout());
            namesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Names",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            namesPanel.add(getViewPanel(), java.awt.BorderLayout.SOUTH);
            namesPanel.add(getScrollPane(), BorderLayout.CENTER);
        }

        return namesPanel;
    }

    private JButton getNewButton() {
        return getButtonPanel().getNewButton();
    }

    private JRadioButton getPrimaryRb() {
        if (primaryRb == null) {
            primaryRb = new JRadioButton();
            primaryRb.addItemListener(getRbItemListener());
        }

        return primaryRb;
    }

    /**
     * The primary radiobutton toggles the state of other components. For example we don't want to allow people to delete the primary conceptname.
     * @return
     */
    private ItemListener getRbItemListener() {
        if (rbItemListener == null) {
            rbItemListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (!isLocked()) {
                        JRadioButton rb = getPrimaryRb();
                        boolean enable = !rb.isSelected();
                        boolean enableDelete = (enable && (getNamesList().getSelectedIndex() > -1));
                        getDeleteButton().setEnabled(enableDelete);
                        boolean enableUpdate = (getNamesList().getSelectedIndex() > -1);
                        getUpdateButton().setEnabled(enableUpdate);
                        getCommonRb().setEnabled(enable);
                        getSynonymRb().setEnabled(enable);
                        getFormerRb().setEnabled(enable);
                        rb.setEnabled(!enable);
                    }
                }
            };
        }

        return rbItemListener;
    }

    private JPanel getRbPanel() {
        if (rbPanel == null) {
            primaryLabel = new JLabel();
            primaryLabel.setText("Primary");
            synonymLabel = new JLabel();
            synonymLabel.setText("Synonym");
            commonLabel = new JLabel();
            commonLabel.setText("Common");
            formerLabel = new JLabel("Former");
            rbPanel = new JPanel();
            rbPanel.setLayout(new BoxLayout(getRbPanel(), BoxLayout.X_AXIS));
            rbPanel.add(primaryLabel, null);
            rbPanel.add(getPrimaryRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(commonLabel, null);
            rbPanel.add(getCommonRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(synonymLabel, null);
            rbPanel.add(getSynonymRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(formerLabel, null);
            rbPanel.add(getFormerRb(), null);
            rbPanel.add(Box.createHorizontalStrut(10));
            rbPanel.add(Box.createHorizontalGlue());
        }

        return rbPanel;
    }

    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getNamesList());
        }

        return scrollPane;
    }

    private JRadioButton getSynonymRb() {
        if (synonymRb == null) {
            synonymRb = new JRadioButton();
            synonymRb.addItemListener(getRbItemListener());
        }

        return synonymRb;
    }

    private JButton getUpdateButton() {
        return getButtonPanel().getUpdateButton();
    }

    private JPanel getViewPanel() {
        if (viewPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new java.awt.Insets(4, 0, 0, 20);
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 20);
            gridBagConstraints.gridy = 0;
            typeLabel = new JLabel();
            typeLabel.setText("Type: ");
            authorLabel = new JLabel();
            authorLabel.setText("Author: ");
            nameLabel = new JLabel();
            nameLabel.setText("Name: ");
            viewPanel = new JPanel();
            viewPanel.setLayout(new GridBagLayout());
            viewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            viewPanel.add(nameLabel, gridBagConstraints);
            viewPanel.add(authorLabel, gridBagConstraints1);
            viewPanel.add(getNameField(), gridBagConstraints2);
            viewPanel.add(getAuthorField(), gridBagConstraints3);
            viewPanel.add(typeLabel, gridBagConstraints4);
            viewPanel.add(getRbPanel(), gridBagConstraints5);
        }

        return viewPanel;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getNamesPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(getPrimaryRb());
        buttonGroup.add(getCommonRb());
        buttonGroup.add(getSynonymRb());
        buttonGroup.add(getFormerRb());
    }

    /**
     * @param concept The concept to set.
     */
    @Override
    public void setConcept(Concept concept) {
        super.setConcept(concept);

        /*
         * Update the list of 'other' names
         */
        List<ConceptName> conceptNames = new ArrayList<ConceptName>();
        if (concept != null) {
            conceptNames.addAll(concept.getConceptNames());
        }

        List<String> names = new ArrayList<String>(conceptNames.size());
        for (ConceptName conceptName : conceptNames) {
            names.add(conceptName.getName());
        }

        Collections.sort(names, stringComparator);

        /*
         * We remove the ListSelectionListener here. If we don't we get errors
         * when the list is cleared.
         */
        ListListModel listModel = (ListListModel) getNamesList().getModel();
        listModel.clear();
        listModel.addAll(names);

        if (concept != null) {
            getNamesList().setSelectedValue(concept.getPrimaryConceptName().getName(), true);
        }
    }

    /**
     * @param locked The locked to set.
     */
    @Override
    public void setLocked(boolean locked) {
        super.setLocked(locked);
        getNameField().setEnabled(!locked);
        getAuthorField().setEnabled(!locked);
        getNewButton().setEnabled(!locked);
        getUpdateButton().setEnabled(!locked);
        getDeleteButton().setEnabled(!locked);
        getPrimaryRb().setEnabled(!locked);
        getCommonRb().setEnabled(!locked);
        getSynonymRb().setEnabled(!locked);
        getFormerRb().setEnabled(!locked);

        /*
         * Important to call this to keep the UI consistant
         */
        getRbItemListener().itemStateChanged(null);
    }

    /**
     * Actions that are taking when a name is selected in the namesList (JList)
     *
     * @param name
     */
    private void setSelectedConceptName(String name) {
        String author = "";
        String type = null;
        boolean enableButtons = (name != null) && !isLocked();

        if (name == null) {
            name = "";
            type = ConceptNameTypes.SYNONYM.toString();
        }
        else {
            ConceptName conceptName = getConcept().getConceptName(name);
            author = conceptName.getAuthor();
            type = conceptName.getNameType();
        }

        boolean isPrimaryName = type.equalsIgnoreCase(ConceptNameTypes.PRIMARY.toString());

        getNameField().setText(name);
        getAuthorField().setText(author);

        if (isPrimaryName) {
            getPrimaryRb().setSelected(true);
        }
        else if (type.equalsIgnoreCase(ConceptNameTypes.COMMON.toString())) {
            getCommonRb().setSelected(true);
        }
        else if (type.equalsIgnoreCase(ConceptNameTypes.FORMER.toString())) {
            getFormerRb().setSelected(true);
        }
        else {
            getSynonymRb().setSelected(true);
        }

        /*
         * We don't allow changing the authro here for primary names. If it's a
         * primary name the update action will try
         */
        getAuthorField().setEditable(!isPrimaryName);
        getUpdateButton().setEnabled(enableButtons);
        getDeleteButton().setEnabled(enableButtons && !isPrimaryName);
    }
}
